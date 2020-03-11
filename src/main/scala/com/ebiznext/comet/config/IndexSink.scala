package com.ebiznext.comet.config

import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.module.SimpleModule
import configs.Configs

/**
  * Configuration for [[IndexSink]]
  *
  * This is used to define an auxiliary output for Audit or Metrics data, in addition to the Parquets
  * The default Index Sink is None, but additional types exists (such as BigQuery or Jdbc)
  *
  */
sealed abstract class IndexSink( /*val*/ `type`: String) {}

object IndexSink {

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
  @JsonSubTypes(
    Array(
      /* it's a bit sad we need to repeat the ADT here for Jackson's benefit, but on the other hand
       we decide *here* how we expose Scala's type system to users who're going to fill in configuration files
       */
      new JsonSubTypes.Type(value = classOf[None], name = "None"),
      new JsonSubTypes.Type(value = classOf[ElasticSearch], name = "ElasticSearch"),
      new JsonSubTypes.Type(value = classOf[BigQuery], name = "BigQuery"),
      new JsonSubTypes.Type(value = classOf[Jdbc], name = "Jdbc")
    )
  )
  abstract class JacksonDummy

  /* We need to detach the annotations from IndexSinkSettings to put them on the JacksonDummy as we cannot
  have a cyclic reference between IndexSinkSettings (the public trait) and IndexSinkSettings$ (the object), which
  putting the annotations up there would cause. Fortunately, Jackson allows us a way out, the price is to never forget
  to register this "module":
   */
  lazy val JacksonModule: SimpleModule = new SimpleModule()
    .setMixInAnnotation(classOf[IndexSink], classOf[JacksonDummy])

  /**
    * A no-operation Index Output (disabling external output beyond the business area parquets)
    */
//   @JsonDeserialize(builder = classOf[None.NoneBuilder])
  case class None()
      extends IndexSink("None")
      // with CometJacksonModule.JacksonProtectedSingleton
      {
    /* Note: this is semantically a case object; but due to the combination of Jackson's lack of ability
        to deal with scala case objects (workaround possible, in this code base) and the eleven-year-old-and-counting
         scala bug https://github.com/scala/bug/issues/2453, it is easier to just dodge it and swallow a couple extra
         instances
     */
    //  class NoneBuilder extends CometJacksonModule.ProtectedSingletonBuilder[None.type]
  }

  /**
    * Describes an Index Output delivering values into a BigQuery dataset
    */
  final case class BigQuery(bqDataset: String = "") extends IndexSink("BigQuery") {}

  /**
    * Describes an Index Output delivering values into a JDBC-accessible SQL database
    */
  final case class Jdbc(jdbcConnection: String, partitions: Int = 1, batchSize: Int = 1000)
      extends IndexSink("Jdbc") {}

  /**
    * Describes an Index Output delivering values into an ElasticSearch index
    */
  final case class ElasticSearch(indexName: Option[String]) extends IndexSink("ElasticSearch") {}

  // TODO: Maybe later; additional sink types (e.g. Kafka/Pulsar)?

  implicit val indexSinkSettingsConfigs: Configs[IndexSink] =
    Configs.derive[IndexSink]
}
