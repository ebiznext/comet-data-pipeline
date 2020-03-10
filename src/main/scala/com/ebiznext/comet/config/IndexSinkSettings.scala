package com.ebiznext.comet.config

import com.ebiznext.comet.schema.model.IndexSink
import com.ebiznext.comet.utils.CometJacksonModule
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import configs.Configs

/**
  * Configuration for [[IndexSink]]
  *
  * This is used to define an auxiliary output for Audit or Metrics data, in addition to the Parquets
  * The default Index Sink is None, but additional types exists (such as BigQuery or Jdbc)
  *
  */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
sealed abstract class IndexSinkSettings(val `type`: String) {
  def indexSinkType: IndexSink
}

object IndexSinkSettings {

  /**
    * A no-operation Index Output (disabling external output beyond the business area parquets)
    */
  @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
  @JsonDeserialize(builder = classOf[None.NoneBuilder])
  case object None
    extends IndexSinkSettings("None")
      with CometJacksonModule.JacksonProtectedSingleton {
    override def indexSinkType: IndexSink.None.type = IndexSink.None

    class NoneBuilder extends CometJacksonModule.ProtectedSingletonBuilder[None.type]
  }

  /**
    * Describes an Index Output delivering values into a BigQuery dataset
    */
  final case class BigQuery(bqDataset: String) extends IndexSinkSettings("BigQuery") {
    override def indexSinkType: IndexSink.BQ.type = IndexSink.BQ
  }

  /**
    * Describes an Index Output delivering values into a JDBC-accessible SQL database
    */
  final case class Jdbc(jdbcConnection: String, partitions: Int = 1, batchSize: Int = 1000)
    extends IndexSinkSettings("Jdbc") {
    override def indexSinkType: IndexSink.JDBC.type = IndexSink.JDBC
  }

  /**
    * Describes an Index Output delivering values into an ElasticSearch index
    */
  final case class ElasticSearch(indexName: Option[String]) extends IndexSinkSettings("ElasticSearch") {
    override def indexSinkType: IndexSink.ES.type = IndexSink.ES
  }

  // TODO: Maybe later; additional sink types (e.g. Kafka/Pulsar)?

  implicit val indexSinkSettingsConfigs: Configs[IndexSinkSettings] =
    Configs.derive[IndexSinkSettings]
}

