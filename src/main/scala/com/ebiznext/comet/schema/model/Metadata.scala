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

package com.ebiznext.comet.schema.model

import com.ebiznext.comet.config.IndexSink
import com.ebiznext.comet.schema.model.Format.DSV
import com.ebiznext.comet.schema.model.Mode.FILE
import com.ebiznext.comet.schema.model.WriteMode.APPEND
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties}
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer, JsonNode}

import scala.language.postfixOps

/**
  * Specify Schema properties.
  * These properties may be specified at the schema or domain level
  * Any property non specified at the schema level is taken from the
  * one specified at the domain level or else the default value is returned.
  *
  * @param mode       : FILE mode by default
  * @param format     : DSV by default
  * @param encoding   : UTF-8 by default
  * @param multiline  : are json objects on a single line or multiple line ? Single by default.  false means single. false also means faster
  * @param array      : Is a json stored as a single object array ? false by default
  * @param withHeader : does the dataset has a header ? true bu default
  * @param separator  : the column separator,  ';' by default
  * @param quote      : The String quote char, '"' by default
  * @param escape     : escaping char '\' by default
  * @param write      : Write mode, APPEND by default
  * @param partition  : Partition columns, no partitioning by default
  * @param index      : should the dataset be indexed in elasticsearch after ingestion ?
  *
  */
@JsonIgnoreProperties(Array( /* Can ignore old properties for backcompat of domain files: */ /*"dateFormat", "timestampFormat" */))
case class Metadata(
                     mode: Option[Mode] = None,
                     format: Option[Format] = None,
                     encoding: Option[String] = None,
                     multiline: Option[Boolean] = None,
                     array: Option[Boolean] = None,
                     withHeader: Option[Boolean] = None,
                     separator: Option[String] = None,
                     quote: Option[String] = None,
                     escape: Option[String] = None,
                     write: Option[WriteMode] = None,
                     partition: Option[Partition] = None,
                     index: Option[IndexSink] = None,
                     properties: Option[Map[String, String]] = None
                   ) {
  override def toString: String =
    s"""
       |mode:${getIngestMode()}
       |format:${getFormat()}
       |encoding:${getEncoding()}
       |multiline:${getMultiline()}
       |array:${isArray()}
       |withHeader:${isWithHeader()}
       |separator:${getSeparator()}
       |quote:${getQuote()}
       |escape:${getEscape()}
       |write:${getWriteMode()}
       |partition:${getPartitionAttributes()}
       |index:${getIndexSink()}
       |properties:${properties}
       """.stripMargin

  /* Note: the reason we have "Java-style accessors" here is that these accessors supply the defaults;
   * while the constructor arguments may be defined or not */

  @JsonIgnore
  def getIngestMode(): Mode = mode.getOrElse(FILE)

  @JsonIgnore
  def getFormat(): Format = format.getOrElse(DSV)

  @JsonIgnore
  def getEncoding(): String = encoding.getOrElse("UTF-8")

  @JsonIgnore
  def getMultiline(): Boolean = multiline.getOrElse(false)

  @JsonIgnore
  def isArray(): Boolean = array.getOrElse(false)

  @JsonIgnore
  def isWithHeader(): Boolean = withHeader.getOrElse(true)

  @JsonIgnore
  def getSeparator(): String = separator.getOrElse(";")

  @JsonIgnore
  def getQuote(): String = quote.getOrElse("\"")

  @JsonIgnore
  def getEscape(): String = escape.getOrElse("\\")

  @JsonIgnore
  def getWriteMode(): WriteMode = write.getOrElse(APPEND)

  @JsonIgnore
  def getPartitionAttributes(): List[String] = partition.map(_.attributes).getOrElse(Nil)

  @JsonIgnore
  def getSamplingStrategy(): Double = partition.map(_.sampling).getOrElse(0.0)

  @JsonIgnore
  def getIndexSink(): Option[IndexSink] = index

  @JsonIgnore
  def getProperties(): Map[String, String] = properties.getOrElse(Map.empty)

  /**
    * Merge a single attribute
    *
    * @param parent : Domain level metadata attribute
    * @param child  : Schema level metadata attribute
    * @return attribute if merge, the domain attribute otherwise.
    */
  protected def merge[T](parent: Option[T], child: Option[T]): Option[T] =
    child.orElse(parent)

  /**
    * Merge this metadata with its child.
    * Any property defined at the child level overrides the one defined at this level
    * This allow a schema to override the domain metadata attribute
    * Applied to a Domain level metadata
    *
    * @param child : Schema level metadata
    * @return the metadata resulting of the merge of the schema and the domain metadata.
    */
  def `import`(child: Metadata): Metadata = {
    Metadata(
      mode = merge(this.mode, child.mode),
      format = merge(this.format, child.format),
      encoding = merge(this.encoding, child.encoding),
      multiline = merge(this.multiline, child.multiline),
      array = merge(this.array, child.array),
      withHeader = merge(this.withHeader, child.withHeader),
      separator = merge(this.separator, child.separator),
      quote = merge(this.quote, child.quote),
      escape = merge(this.escape, child.escape),
      write = merge(this.write, child.write),
      partition = merge(this.partition, child.partition),
      index = merge(this.index, child.index),
      properties = merge(this.properties, child.properties)
    )
  }
}

object Metadata {

  /**
    * Predefined partition columns.
    */
  val CometPartitionColumns =
    List("comet_year", "comet_month", "comet_day", "comet_hour", "comet_minute")

  def Dsv(
    separator: Option[String],
    quote: Option[String],
    escape: Option[String],
    write: Option[WriteMode]
  ) = new Metadata(
    Some(Mode.FILE),
    Some(Format.DSV),
    None,
    Some(false),
    Some(false),
    Some(true),
    separator,
    quote,
    escape,
    write,
    None,
    None
  )
}
