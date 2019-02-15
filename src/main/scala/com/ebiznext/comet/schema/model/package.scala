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
