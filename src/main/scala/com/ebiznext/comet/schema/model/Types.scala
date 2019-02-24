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

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

import com.ebiznext.comet.schema.model.PrimitiveType.{date, timestamp}
import org.apache.spark.sql.types.StructField
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._

import scala.util.Try

/**
  * List of globally defined types
  *
  * @param types : Type list
  */
case class Types(types: List[Type])

/**
  * Semantic Type
  *
  * @param name          : Type name
  * @param format        : Pattern use to check that the input data matches the pattern
  * @param primitiveType : Spark Column Type of the attribute
  */
case class Type(
  name: String,
  pattern: String,
  primitiveType: PrimitiveType = PrimitiveType.string,
  sample: Option[String] = None,
  comment: Option[String] = None,
  stat: Option[Stat] = None
) {
  // Used only when object is not a date nor a timestamp
  private lazy val textPattern = Pattern.compile(pattern)

  def matches(value: String): Boolean = {
    primitiveType match {
      case PrimitiveType.struct => true
      case PrimitiveType.date =>
        Try(date.fromString(value, pattern)).isSuccess
      case PrimitiveType.timestamp =>
        Try(timestamp.fromString(value, pattern)).isSuccess
      case _ =>
        textPattern.matcher(value).matches()
    }
  }

  def sparkValue(value: String): Any = {
    primitiveType.fromString(value, pattern)
  }

  def sparkType(fieldName: String, nullable: Boolean, comment: Option[String]): StructField = {
    StructField(fieldName, primitiveType.sparkType, nullable)
      .withComment(comment.getOrElse(""))
  }
}

/**
  * Companion object used to check the types validation
  */
object Types {

  def checkValidity(types: Types): ValidationResult[Types] = {
    val valDup = checkDuplicates(types.types.map(_.name))
    val valInnerTypes = types.types.traverse(Type.checkValidity)
    val globalCheck: ValidationResult[List[Type]] = valDup *> valInnerTypes

    globalCheck.map(Types.apply)
  }
}

/**
  * Companion object used to check the type validation
  */
object Type {

  /**
    * Check type definition correctness :
    *   - pattern should be valid foreach type
    *   - sample parameter should be match with his defined pattern
    *
    * @param types : List of globally defined types
    * @return The type if is valid
    */
  def checkValidity(types: Type): ValidationResult[Type] = {
    def validatePattern(primitiveType: PrimitiveType, name: String, pattern: String) : ValidationResult[String] = {
      val patternIsValid = Try {
        primitiveType match {
          case PrimitiveType.struct =>
          case PrimitiveType.date =>
            new SimpleDateFormat(pattern)
          case PrimitiveType.timestamp =>
            pattern match {
              case "epoch_second" | "epoch_milli" =>
              case _ if PrimitiveType.formatters.keys.toList.contains(pattern) =>
              case _ =>
                DateTimeFormatter.ofPattern(pattern)
            }
          case _ =>
            Pattern.compile(pattern)
        }
      }
      if (patternIsValid.isFailure)
        Invalid(List(s"Invalid Pattern $pattern in type $name"))
      else
        Valid(pattern)
    }

    def validateSample(sample: Option[String],types: Type): ValidationResult[Option[String]] ={
      val ok = sample.forall(types.matches)
      val pattern = types.pattern
      val name = types.name
      if (!ok)
        Invalid(List(s"Sample $sample does not match pattern $pattern in type $name"))
      else
        Valid(sample)
    }


    val sampleVal = validatePattern(types.primitiveType, types.name, types.pattern)
    val typeErrorVal = validateSample(types.sample, types)

    (sampleVal,typeErrorVal).mapN((res1,res2) => Type(types.name,res1,types.primitiveType,res2,types.comment,types.stat))
  }
}
