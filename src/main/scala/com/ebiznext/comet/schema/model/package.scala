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

package com.ebiznext.comet.schema

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._

package object model {

  type ValidationResult[A] = Validated[List[String], A]
  /**
    * Utility to extract duplicates and their number of occurrences for a unique value
    *
    * @param value : A string value
    * @param values : List of strings
    * @return The value if it's valid
    */
  def checkDuplicate(value: String, values: List[String]): ValidationResult[String] = {
    val occurences = values.filter(_ == value)
    if (occurences.size > 1) Invalid(List(s"Not good ! I have ${occurences.size} for this occurence")) else Valid(value)
  }


  /**
    * Utility to extract duplicates and their number of occurrences for a list of values
    *
    * @param values : List of strings
    * @return The list of values if it's valid
    */
  def checkDuplicates(values: List[String]): ValidationResult[List[String]] =
    values.traverse(checkDuplicate(_, values))
}
