package com.ebiznext.comet.schema.handlers

import com.ebiznext.comet.TestHelper
import com.ebiznext.comet.config.DatasetArea
import com.ebiznext.comet.schema.model.{BigQuerySink, Metadata, Mode, Schema, WriteMode}
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.types.StructField

import java.net.URL

class SchemaHandlerSpec extends TestHelper {
  new WithSettings() {
    "Writing types" should "work" in {

      val typesPath = new Path(DatasetArea.types, "types.comet.yml")

      deliverTestFile("/sample/types.comet.yml", typesPath)

      readFileContent(typesPath) shouldBe loadTextFile("/sample/types.comet.yml")
    }

    "Mapping Schema" should "produce valid template" in {
      new SpecTrait(
        domainOrJobFilename = "locations.comet.yml",
        sourceDomainOrJobPathname = "/sample/simple-json-locations/locations.comet.yml",
        datasetDomainName = "locations",
        sourceDatasetPathName = "/sample/simple-json-locations/locations.json"
      ) {
        cleanMetadata
        cleanDatasets

        val schemaHandler = new SchemaHandler(storageHandler)

        val schema: Option[Schema] = schemaHandler.domains
          .find(_.name == "locations")
          .flatMap(_.schemas.find(_.name == "locations"))
        val expected: String =
          """
            |{
            |  "index_patterns": ["locations.locations", "locations.locations-*"],
            |  "settings": {
            |    "number_of_shards": "1",
            |    "number_of_replicas": "0"
            |  },
            |  "mappings": {
            |    "_doc": {
            |      "_source": {
            |        "enabled": true
            |      },
            |
            |"properties": {
            |
            |"id": {
            |  "type": "keyword"
            |},
            |"name": {
            |  "type": "keyword"
            |},
            |"name_upper_case": {
            |  "type": "keyword"
            |},
            |"source_file_name": {
            |  "type": "keyword"
            |}
            |}
            |    }
            |  }
            |}
        """.stripMargin.trim
        val mapping =
          schema.map(_.mapping(None, "locations", schemaHandler)).map(_.trim).getOrElse("")
        logger.info(mapping)
        mapping.replaceAll("\\s", "") shouldBe expected.replaceAll("\\s", "")
      }

    }
    "JSON Schema" should "produce valid template" in {
      new SpecTrait(
        domainOrJobFilename = "locations.comet.yml",
        sourceDomainOrJobPathname = s"/sample/simple-json-locations/locations.comet.yml",
        datasetDomainName = "locations",
        sourceDatasetPathName = "/sample/simple-json-locations/locations.json"
      ) {
        cleanMetadata
        cleanDatasets

        val schemaHandler = new SchemaHandler(storageHandler)

        val ds: URL = getClass.getResource("/sample/mapping/dataset")

        logger.info(
          Schema.mapping(
            "domain",
            "schema",
            StructField("ignore", sparkSession.read.parquet(ds.toString).schema),
            schemaHandler
          )
        )

        // TODO: we aren't actually testing anything here are we?
      }
    }

    "Custom mapping in Metadata" should "be read as a map" in {
      val sch = new SchemaHandler(storageHandler)
      val content =
        """mode: FILE
          |withHeader: false
          |encoding: ISO-8859-1
          |format: POSITION
          |sink:
          |  type: BQ
          |  timestamp: _PARTITIONTIME
          |write: OVERWRITE
          |""".stripMargin
      val metadata = sch.mapper.readValue(content, classOf[Metadata])

      metadata shouldBe Metadata(
        mode = Some(Mode.FILE),
        format = Some(com.ebiznext.comet.schema.model.Format.POSITION),
        encoding = Some("ISO-8859-1"),
        withHeader = Some(false),
        sink = Some(BigQuerySink(timestamp = Some("_PARTITIONTIME"))),
        write = Some(WriteMode.OVERWRITE)
      )
    }
  }
}
