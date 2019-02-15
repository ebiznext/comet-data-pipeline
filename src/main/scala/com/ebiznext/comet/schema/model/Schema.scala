package com.ebiznext.comet.schema.model

import java.util.regex.Pattern

import org.apache.spark.sql.types.{StructField, StructType}

import cats.data.Validated.{Invalid, Valid}
import cats.implicits._


/**
  * How dataset are merge
  * @param key list of attributes to join existing with incoming data. Use renamed columns here.
  * @param delete Optional valid sql condition on the incoming dataset. Use renamed column here.
  */
case class MergeOptions(key: List[String], delete: Option[String] = None)

/**
  * Dataset Schema
  *
  * @param name       : Schema name, must be unique in the domain. Will become the hive table name
  * @param pattern    : filename pattern to which this schema must be applied
  * @param attributes : datasets columns
  * @param metadata   : Dataset metadata
  * @param comment    : free text
  * @param presql     :  SQL code executed before the file is ingested
  * @param postsql    : SQL code executed right after the file has been ingested
  */
case class Schema(
  name: String,
  pattern: Pattern,
  attributes: List[Attribute],
  metadata: Option[Metadata],
  merge: Option[MergeOptions],
  comment: Option[String],
  presql: Option[List[String]],
  postsql: Option[List[String]]
) {

  /**
    * @return Are the parittions columns defined in the metadata valid column names
    */
  def validatePartitionColumns(): Boolean = {
    metadata.forall(
      _.getPartition().forall(
        attributes
          .map(_.getFinalName())
          .union(Metadata.CometPartitionColumns)
          .contains
      )
    )
  }

  /**
    * This Schema as a Spark Catalyst Schema
    *
    * @param types : globally defined types
    * @return Spark Catalyst Schema
    */
  def sparkType(types: Types): StructType = {
    val fields = attributes.map { attr =>
      StructField(attr.name, attr.sparkType(types), !attr.required)
    }
    StructType(fields)
  }

  /**
    * return the list of renamed attributes
    *
    * @return list of tuples (oldname, newname)
    */
  def renamedAttributes(): List[(String, String)] = {
    attributes.filter(attr => attr.name != attr.getFinalName()).map { attr =>
      (attr.name, attr.getFinalName())
    }
  }
}

/**
  * Companion object used to check the schema validation
  */
object Schema {

  /**
    * Check attribute definition correctness :
    *   - schema name should be a valid table identifier
    *   - attribute name should be a valid Hive column identifier
    *   - attribute name can occur only once in the schema
    *
    * @param types : List of globally defined types
    * @param schema : A schema
    * @return The schema if is valid
    */
  def checkValidity(schema: Schema, types: List[Type]) : ValidationResult[Schema] = {
    def validateTableName(name: String) : ValidationResult[String] = {
      val tableNamePattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]{1,256}")
      if (!tableNamePattern.matcher(name).matches())
        Invalid(List(s"Schema with name $name should respect the pattern ${tableNamePattern.pattern()}"))
      else
        Valid(name)
    }

    def validateAttributes(attributes: List[Attribute], types: List[Type]): ValidationResult[List[Attribute]] = {
      attributes.traverse(Attribute.checkValidity(_,types))

    }

    val tableNameVal = validateTableName(schema.name)
    val attributeVal = validateAttributes(schema.attributes, types)

    (tableNameVal,attributeVal).mapN((tableName,attribute) => Schema(tableName,schema.pattern,attribute,schema.metadata, schema.merge,
      schema.comment,schema.presql,schema.postsql))

  }
}
