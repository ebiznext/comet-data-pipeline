package com.ebiznext.comet.job.jdbcload

import com.ebiznext.comet.config.Settings
import com.ebiznext.comet.config.IndexSink
import com.ebiznext.comet.utils.{SparkJob, Utils}
import com.google.cloud.bigquery.JobInfo.WriteDisposition
import org.apache.spark.sql.types.{ArrayType, DataType, DataTypes, MapType, StructType}
import org.apache.spark.sql.{functions, DataFrame, SaveMode, SparkSession}

import scala.util.{Success, Try}

class JdbcLoadJob(
  cliConfig: JdbcLoadConfig
)(implicit val settings: Settings)
    extends SparkJob {

  override def name: String = s"jdbcload-JDBC-${cliConfig.outputTable}"

  val conf = session.sparkContext.hadoopConfiguration
  logger.info(s"JDBC Config $cliConfig")
  val driver = cliConfig.driver
  val url = cliConfig.url
  val user = cliConfig.user
  val password = cliConfig.password
  Class.forName(driver)

  def runJDBC(): Try[SparkSession] = {
    val inputPath = cliConfig.sourceFile
    logger.info(s"Input path $inputPath")
    Try {
      val sourceDF =
        inputPath match {
          case Left(path) => session.read.parquet(path)
          case Right(df)  => df
        }

      val flattenedDF = flattenDataframeFields(sourceDF)

      flattenedDF.write
        .format("jdbc")
        .option("numPartitions", cliConfig.partitions)
        .option("batchsize", cliConfig.batchSize)
        .option("truncate", cliConfig.writeDisposition == WriteDisposition.WRITE_TRUNCATE)
        .option("driver", driver)
        .option("url", url)
        .option("dbtable", cliConfig.outputTable)
        .option("user", user)
        .option("password", password)
        .mode(SaveMode.Append)
        .save()
      session
    }
  }

  /**
    * JDBC Databases typically cannot deal with complex types. Those that can, typically consider
    * that anything can be JSON if we want to talk using JSON (e.g. Cassandra, Postgres)
    *
    * So, our strategy at this point is to just hammer anything that doesn't fit a fundamental type into JSON
    * and let the database engine either manage it as a string, or take advantage of the JSON.
    *
    * @param sourceDF
    * @return
    */
  private def flattenDataframeFields(sourceDF: DataFrame) = {
    sourceDF.columns.foldLeft(sourceDF) {
      case (dfBuffer, colName) =>
        val col = dfBuffer.col(colName)
        val dataType = col.expr.dataType
        logger.info(s"column: ${colName} dataType=$dataType")

        dataType match {
          case _: StructType | _: ArrayType | _: MapType =>
            dfBuffer.withColumn(colName, functions.to_json(col))

          case _ =>
            dfBuffer
        }
    }
  }

  /**
    * Just to force any spark job to implement its entry point using within the "run" method
    *
    * @return : Spark Session used for the job
    */
  override def run(): Try[SparkSession] = {
    val res = runJDBC()

    Utils.logWhenFailure(res, logger)
  }
}
