package com.ebiznext.comet.utils

import com.ebiznext.comet.utils.repackaged.BigQuerySchemaConverters
import com.google.auth.oauth2.ServiceAccountCredentials
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.{DataType, StructType}
import com.google.cloud.bigquery.{BigQueryOptions, Schema => BQSchema}

import java.io.ByteArrayInputStream
import scala.io.Source

/** [X] whatever
  * Conversion between [X] Schema and BigQuery Schema
  */
object BigQueryUtils {

  val sparkToBq: DataFrame => BQSchema = (df: DataFrame) => bqSchema(df.schema)

  /** Compute BigQuery Schema from Spark or PArquet Schema while Schema.bqSchema compute it from YMl File
    * @param schema Spark DataType
    * @return
    */

  def bqSchema(schema: DataType): BQSchema = {
    BigQuerySchemaConverters.toBigQuerySchema(schema.asInstanceOf[StructType])
  }

  def bqOptions(): BigQueryOptions = {
    unitTestLoadGcpCredentials() match {
      case Some(unitTestGcpCredentials) =>
        val credentials = ServiceAccountCredentials.fromStream(
          new ByteArrayInputStream(unitTestGcpCredentials.getBytes())
        )
        BigQueryOptions.newBuilder().setCredentials(credentials).build()
      case None =>
        BigQueryOptions.getDefaultInstance
    }
  }

  def unitTestLoadGcpCredentials(): Option[String] = {
    val credFile = Option(System.getenv("COMET_TEST_GCP_CREDENTIALS_FILE"))
    val cred = Option(System.getenv("COMET_TEST_GCP_CREDENTIALS"))
    val projectId = Option(System.getenv("COMET_TEST_GCP_PROJECT_ID"))
    (credFile, projectId) match {
      case (_, None) => None
      case (Some(credFile), Some(_)) =>
        val source = Source.fromFile(credFile)
        val cometGcpCredentials = source.getLines.mkString
        source.close()
        Some(cometGcpCredentials)
      case (None, Some(_)) => cred
    }
  }
}
