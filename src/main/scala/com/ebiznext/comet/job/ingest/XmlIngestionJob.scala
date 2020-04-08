/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.ebiznext.comet.job.ingest

import com.ebiznext.comet.config.{DatasetArea, Settings, StorageArea}
import com.ebiznext.comet.schema.handlers.{SchemaHandler, StorageHandler}
import com.ebiznext.comet.schema.model._
import com.ebiznext.comet.utils.Utils
import org.apache.hadoop.fs.Path
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.execution.datasources.json.JsonIngestionUtil
import org.apache.spark.sql.execution.datasources.json.JsonIngestionUtil.{compareTypes, factory}
import org.apache.spark.sql.types.{DataType, StructType}
import org.apache.spark.sql.{DataFrame, Encoders, Row}

import scala.util.{Failure, Success, Try}

/**
  * Main class to complex json delimiter separated values file
  * If your json contains only one level simple attribute aka. kind of dsv but in json format please use SIMPLE_JSON instead. It's way faster
  *
  * @param domain         : Input Dataset Domain
  * @param schema         : Input Dataset Schema
  * @param types          : List of globally defined types
  * @param path           : Input dataset path
  * @param storageHandler : Storage Handler
  */
class XmlIngestionJob(
  val domain: Domain,
  val schema: Schema,
  val types: List[Type],
  val path: List[Path],
  val storageHandler: StorageHandler,
  val schemaHandler: SchemaHandler
)(implicit val settings: Settings)
    extends IngestionJob {

  /**
    * load the json as an RDD of String
    *
    * @return Spark Dataframe loaded using metadata options
    */
  def loadDataSet(): Try[DataFrame] = {

    try {
      val df = session.read
        .format("com.databricks.spark.csv")
        .option("inferSchema", value = false)
        .option("encoding", metadata.getEncoding())
        .text(path.map(_.toString): _*)
      df.printSchema()
      Success(df)
    } catch {
      case e: Exception =>
        Failure(e)
    }
  }

  lazy val schemaSparkType: StructType = schema.sparkType(schemaHandler)

  /**
    * Where the magic happen
    *
    * @param dataset input dataset as a RDD of string
    */
  def ingest(dataset: DataFrame): (RDD[_], RDD[_]) = {
    val rdd = dataset.rdd
    dataset.printSchema()
    val checkedRDD = XmlIngestionJob.parseRDD(rdd, schemaSparkType).cache()
    val acceptedRDD: RDD[String] = checkedRDD.filter(_.isRight).map(_.right.get)
    val rejectedRDD: RDD[String] =
      checkedRDD.filter(_.isLeft).map(_.left.get.mkString("\n"))
    val acceptedDF = session.read.json(session.createDataset(acceptedRDD)(Encoders.STRING))
    saveRejected(rejectedRDD)
    val (df, path) = saveAccepted(acceptedDF) // prefer to let Spark compute the final schema
    index(df)
    (rejectedRDD, acceptedRDD)
  }

  /**
    * Use the schema we used for validation when saving
    *
    * @param acceptedRDD
    */
  @deprecated("We let Spark compute the final schema", "")
  def saveAccepted(acceptedRDD: RDD[Row]): Path = {
    val writeMode = metadata.getWriteMode()
    val acceptedPath = new Path(DatasetArea.accepted(domain.name), schema.name)
    saveRows(
      session.createDataFrame(acceptedRDD, schemaSparkType),
      acceptedPath,
      writeMode,
      StorageArea.accepted,
      schema.merge.isDefined
    )
    acceptedPath
  }

  override def name: String = "JsonJob"
}

object XmlIngestionJob {
  import com.databricks.spark.xml._

  def parseString(content: String, schemaSparkType: StructType): Try[DataType] = {
    Try {
      val row = from_xml_string(content, schemaSparkType)
      row.schema
    }
  }

  def parseRDD(
    inputRDD: RDD[Row],
    schemaSparkType: StructType
  ): RDD[Either[List[String], String]] = {
    inputRDD.mapPartitions { partition =>
      partition.map { row =>
        val rowAsString = row.getAs[String](0)
        parseString(rowAsString, schemaSparkType) match {
          case Success(datasetType) =>
            val errorList = compareTypes(schemaSparkType, datasetType)
            if (errorList.isEmpty)
              Right(rowAsString)
            else
              Left(errorList)

          case Failure(exception) =>
            Left(List(exception.toString))
        }
      }
    }
  }

}