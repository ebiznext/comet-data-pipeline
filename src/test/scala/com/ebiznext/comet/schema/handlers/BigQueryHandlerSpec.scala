package com.ebiznext.comet.schema.handlers

import com.ebiznext.comet.TestHelper
import com.ebiznext.comet.config.{Settings, StorageArea}
import com.ebiznext.comet.schema.model.{AutoJobDesc, AutoTaskDesc, BigQuerySink, Engine, WriteMode}
import com.ebiznext.comet.utils.BigQueryUtils
import com.ebiznext.comet.workflow.{IngestionWorkflow, TransformConfig}
import org.apache.hadoop.fs.Path

class BigQueryHandlerSpec extends TestHelper {
  val doTest = BigQueryUtils.unitTestLoadCredentials().nonEmpty

  new WithSettings() {
    "A DSV File" should "be ingested in BigQuery" in {
      if (doTest) {
        new SpecTrait(
          domainOrJobFilename = "BQ.comet.yml",
          sourceDomainOrJobPathname = s"/sample/BQ.comet.yml",
          datasetDomainName = "COMET_TEST",
          sourceDatasetPathName = "/sample/employee.csv"
        ) {
          cleanMetadata
          cleanDatasets
          loadPending
        }
      }
    }
    "trigger AutoJob with no parameters on SQL statement" should "generate a dataset in business" in {
      if (doTest) {
        val businessTask1 = AutoTaskDesc(
          None,
          "select name, age, 'dData Eng.' from COMET_TEST.employee",
          "COMET_TEST",
          "employee_job",
          WriteMode.OVERWRITE,
          area = Some(StorageArea.fromString("business")),
          sink = Some(BigQuerySink())
        )
        val businessJob =
          AutoJobDesc(
            "employee_job",
            List(businessTask1),
            None,
            Some("parquet"),
            Some(false),
            engine = Some(Engine.BQ)
          )
        val schemaHandler = new SchemaHandler(storageHandler)

        val businessJobDef = mapper
          .writer()
          .withAttribute(classOf[Settings], settings)
          .writeValueAsString(businessJob)

        lazy val pathBusiness = new Path(cometMetadataPath + "/jobs/employee_job.comet.yml")

        val workflow =
          new IngestionWorkflow(storageHandler, schemaHandler, new SimpleLauncher())
        storageHandler.write(businessJobDef, pathBusiness)

        workflow.autoJob(TransformConfig("employee_job"))
      }
    }
  }
}
