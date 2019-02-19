package com.ebiznext.comet.job

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame}


object  StatDescCode {


  trait MetricContinuous {
    /**
      *
      * @return       : the name of the variable
      */
    def name: String

    /**
      *
      * @return       : the metric function
      */
    def function: Column => Column
  }



  /**  Case Continuous Variable  with all Metrics
    *
    * @param name       : the name of the variable
    * @param function   : the metric function
    */


  case class ContinuousMetric(name: String, function: Column => Column) extends MetricContinuous

  object Min extends ContinuousMetric("Min", min)

  object Max extends ContinuousMetric("Max", max)

  object Count extends ContinuousMetric("Count", count)

  object Sum extends ContinuousMetric("Sum", sum)

  object Skewness extends ContinuousMetric("Skewness", skewness)

  object Kurtosis extends ContinuousMetric("Kurtosis", kurtosis)


  /** Customize function metric  in the case continuous variabes : percentile, median, mean, var, stddev
    *
    */

  import org.apache.spark.sql.functions.{callUDF, lit}

  def customCallUDF75(e: Column): Column = callUDF("percentile_approx", e, lit(0.75)).as("percentile75")

  def customCallUDF50(e: Column): Column = callUDF("percentile_approx", e, lit(0.50)).as("percentile50")

  def customCallUDF25(e: Column): Column = callUDF("percentile_approx", e, lit(0.25)).as("percentile25")

  def customCallUDF10(e: Column): Column = callUDF("percentile_approx", e, lit(0.10)).as("percentile10")


  /** customize mean of the column e
    *
    * @param e        : the name of the column
    * @return Integer : the computed  value of the mean
    */

  def customMean(e: Column): Column = {
    val nameCol = e.toString()
    val aliasMean: String = "Mean" + "(" + nameCol + ")"
    mean(e).as(aliasMean)
  }

  /** customize variance of the column e
    *
    * @param e        : the name of the column
    * @return Integer : the computed  value of the variance
    */


  def customVariance(e: Column): Column = {
    val nameCol = e.toString()
    val aliasVariance: String = "Var" + "(" + nameCol + ")"
    variance(e).as(aliasVariance)
  }

  /** customize Stddev of the column e
    *
    * @param e        : the name of the column
    * @return Integer : the computed  value of the Stddev
    */

  def customStddev(e: Column): Column = {
    val nameCol = e.toString()
    val aliasStddev: String = "Stddev" + "(" + nameCol + ")"
    stddev(e).as(aliasStddev)
  }

  /** customize Median of the column e
    *
    * @param e        : the name of the column
    * @return Integer : the computed  value of the Median
    */

  def customMedian(e: Column): Column = {
    val nameCol = e.toString()
    val aliasMedian: String = "Median" + "(" + nameCol + ")"
    callUDF("percentile_approx", e, lit(0.50)).as(aliasMedian)
  }

  /** customize percentile of order 0.75 of the column e
    *
    * @param e        : the name of the column
    * @return Integer : the computed  value of the percentile of order 0.75
    */


  def percentile75(e: Column): Column = {
    val nameCol = e.toString()
    val aliasPercentil75: String = "Percentile75" + "(" + nameCol + ")"
    callUDF("percentile_approx", e, lit(0.75)).as(aliasPercentil75)
  }

  /** customize percentile of order 0.25 of the column e
    *
    * @param e        : the name of the column
    * @return Integer : the computed  value of the percentile of order 0.25
    */

  def percentile25(e: Column): Column = {
    val nameCol = e.toString()
    val aliasPercentil25: String = "Percentile25" + "(" + nameCol + ")"
    callUDF("percentile_approx", e, lit(0.25)).as(aliasPercentil25)
  }

  /** costumize missing values
    *
    * @param e   : the name of the column
    * @return Integer : the number of missing values, NaN  values and null values
    */

  def customCountMissValues(e: Column): Column = {
    val nameCol = e.toString()
    val aliasCountMissValues: String = "CountMissValues" + "(" + nameCol + ")"
    val unionMissingValues = sum(when(e.isNull
      || e === ""
      || e === " "
      || e.isNaN,1).otherwise(0))
    unionMissingValues.as(aliasCountMissValues)
  }


  object Percentile75 extends ContinuousMetric("Percentile75", percentile75)

  object Percentile25 extends ContinuousMetric("Percentile25", percentile25)

  object Median extends ContinuousMetric("Median", customMedian)

  object Mean extends ContinuousMetric("Mean", customMean)

  object Variance extends ContinuousMetric("Var", customVariance)

  object Stddev extends ContinuousMetric("Stddev", customStddev)

  object CountMissValues extends ContinuousMetric("CountMissValues", customCountMissValues)

  /** List of all available metrics
    *
    */

  val allContinuousMetrics: List[MetricContinuous] = List(Min, Max, Mean, Count,CountMissValues,Variance, Stddev, Sum, Skewness, Kurtosis, Percentile25, Median, Percentile75)



  /**  Function to split the DataFrame metric (metricFrame)  to get a sequence of partial DataFrame metric by variable.
    *
    * @param namecol     : the name of the column.
    * @param metricFrame : the DataFrame of all the computed metrics for each variable by columns.
    * @return            : the DataFrame metric  associated to the variable (namecol).
    */

  def splitdata(namecol: String, metricFrame: DataFrame): DataFrame = {
    val listHead = metricFrame.schema.filter(_.name.contains(namecol)).map(x => x.name).sorted // to get all the header of test with SepalLength
    val splitHead = metricFrame.select(listHead.head, listHead.tail: _*)
    val dataSplit: DataFrame = splitHead.select(splitHead.columns.map(c => bround(col(c), 3).alias(c)): _*) // Reduice decimal values to 3
    val dataSplitPartiel = dataSplit.withColumn("Variables", lit(namecol)) // add  column of name Variables
    val dataSplitPartielHeader = dataSplitPartiel.columns.toList.map(str => str.replaceAll("\\("+namecol+"\\)", "")).map(_.capitalize)
    dataSplitPartiel.toDF(dataSplitPartielHeader: _*)
  }


  /**  Function to combine all the partial DataFrame metric by variable (to get one DataFrame by row).
    *
    * @param indexToDataFrame : function that link the index to  each partial DataFrame metric by variable.
    * @param indexDataFrame   : index associated  with partial DataFrame metric by variable.
    * @param variableList     : list of variable.
    * @return                 : DataFrame metric  of all variables by row.
    */

  def unionFrame(indexToDataFrame: Int => DataFrame)(indexDataFrame: Int, variableList: List[String]): DataFrame = {
    def iter(indexDataFrame: Int, result: DataFrame): DataFrame = {
      if (indexDataFrame == variableList.length - 1) result
      else iter(indexDataFrame + 1, result.union(indexToDataFrame(indexDataFrame + 1)))
    }
    iter(indexDataFrame, indexToDataFrame(indexDataFrame))
  }


  /** Function to check if all the attributes are compatible to the names of variables
    *
    * @param dataUse    : initial DataFrame.
    * @param attributes : name list of all variables.
    * @return
    */


  def checkTestAttributes(dataUse: DataFrame, attributes: List[String]): Either[Either[List[String], List[String]], List[String]] = {
    val headerDataUse  = dataUse.columns.toList
    val listDifference = attributes.filterNot(headerDataUse.contains)
    val  intersectionHeaderAttributes = headerDataUse.intersect(attributes)
    if (listDifference.isEmpty) Right(attributes)
    else Left( if(intersectionHeaderAttributes.nonEmpty) Right(intersectionHeaderAttributes)  else Left(listDifference))
  }



  /** Function to compute the DataFrame metrics by row
    *
    * @param dataUse    : initial DataFrame.
    * @param attributes : name list of all variables.
    * @param operations : list of metrics you want to calculate.
    * @return DataFrame : DataFrame metric  of all variables by row.
    */


  def computeContinuiousMetric(dataUse: DataFrame, attributes: List[String], operations: List[MetricContinuous]) : DataFrame = {
    val headerDataUse  = dataUse.columns.toList
    val intersectionHeaderAttributes = headerDataUse.intersect(attributes)
    val listDifference = attributes.filterNot(headerDataUse.contains)
    val testAttributes = checkTestAttributes(dataUse,attributes)

    val attributeChecked =  testAttributes match {
      case Right(attributeChecked) => attributes
      case Left(Right(attributeChecked)) => {
        println("These attributes are not part of the variable names: " + listDifference.mkString(","))
        intersectionHeaderAttributes
      }
      case Left(Left(attributeChecked))=> println("No attributes are matching the columns names")
        headerDataUse
    }

    val colRenamed: List[String] = "Variables" :: operations.map(_.name)
    val metrics: List[Column] = attributeChecked.flatMap(name => operations.map(metric => metric.function(col(name))))
    val metricFrame: DataFrame = dataUse.agg(metrics.head, metrics.tail: _*)
    val matrixMetric = unionFrame(x => splitdata(attributeChecked(x), metricFrame))(0, attributeChecked)
    matrixMetric.select(colRenamed.head, colRenamed.tail: _*)

  }



  trait MetricDiscrete {

    /**
      *
      * @return     :  the name of the variable
      */

    def name: String;

    /**
      *
      * @return     : metric function
      */

    def function: (DataFrame , String) => DataFrame
  }


  case class DiscreteMetric(name: String, function: (DataFrame , String) => DataFrame) extends MetricDiscrete

  /** customize count for discrete variable
    *
    * @param dataInit    : initial DataFrame
    * @param e           : name of the column
    * @return  DataFrame : whit the name of category and the value of the count
    */


  def customCountDiscrete(dataInit: DataFrame , e: String): DataFrame= {
    val valueCount: DataFrame = dataInit.groupBy(e).count()
    valueCount.toDF("Category", "CountDiscrete")
  }


  /** customize Category for discrete variable
    *
    * @param dataInit    : initial DataFrame
    * @param e           : name of the column
    * @return  DataFrame : whit the name of category
    */

  def customCategory(dataInit: DataFrame , e: String): DataFrame= {
    val valueCategory: DataFrame = dataInit.groupBy(e).count()
    valueCategory.toDF("Category", "CountDiscrete").select("Category")
  }

  /** customize Frequencies for discrete variable
    *
    * @param dataInit    : initial DataFrame
    * @param e           : name of the column
    * @return  DataFrame : whit the name of category and the values of the Frequencies
    */

  def customFrequencies(dataInit: DataFrame , e: String): DataFrame= {
    val subColFrame: DataFrame = customCountDiscrete(dataInit, e)
    val sumValues: Long = subColFrame.agg(sum("CountDiscrete")).first.getAs[Long](0)
    val valueFrequencies: DataFrame= subColFrame.withColumn("Frequencies", bround(subColFrame("CountDiscrete") / sumValues, 3))
    valueFrequencies.select("Category","Frequencies")
  }


  /**
    *
    * @param dataInit   : initial DataFrame
    * @param e          : the name of the column
    * @return DataFrame : whit the number of missing values, NaN  values and null values
    */
  def customCountMissValuesDiscrete(dataInit: DataFrame , e: String):   DataFrame= {
    val numMissValues =  dataInit.filter(dataInit(e).isNull || dataInit(e) === " " || dataInit(e).isNaN).count()
    val subColFrame: DataFrame = customCategory(dataInit, e).withColumn("CountMissValuesDiscrete",lit(numMissValues))
    subColFrame.toDF("Category", "CountMissValuesDiscrete")
  }





  object Category extends DiscreteMetric("Category", customCategory)

  object CountDiscrete extends DiscreteMetric("CountDiscrete", customCountDiscrete)

  object Frequencies extends DiscreteMetric("Frequencies", customFrequencies)

  object CountMissValuesDiscrete extends DiscreteMetric("CountMissValuesDiscrete", customCountMissValuesDiscrete)



  /** List of all available metrics.
    *
    */

  val allDiscreteMetrics: List[MetricDiscrete] = List(Category, CountDiscrete, Frequencies, CountMissValuesDiscrete)

  /** Function to compute each partial DataFrame metric by variable.
    *
    * @param dataInit   : initial DataFrame.
    * @param name       : name of the variable.
    * @param operations : list of metrics you want to calculate.
    * @return DataFrame : partial DataFrame metric by variables (name).
    */

  def subFrame(dataInit: DataFrame, name: String, operations: List[MetricDiscrete]): DataFrame = {
    val metrics: List[DataFrame] =  operations.map(metric => metric.function(dataInit, name))
    val metricFrame: DataFrame = metrics.reduce((a, b) => a.join(b,"Category"))
    metricFrame.withColumn("Variables", lit(name))
  }

  /** Function to combine all the partial DataFrame metric by variable (to get one DataFrame by row).
    *
    * @param dataUse    : initial DataFrame.
    * @param attributes : name of the variable.
    * @param operations : list of metrics you want to calculate.
    * @return DataFrame : DataFrame with alle the metric by variable by row
    */



  def computeDiscretMetric(dataUse: DataFrame, attributes: List[String], operations: List[MetricDiscrete]):  DataFrame= {

    val headerDataUse  = dataUse.columns.toList
    val intersectionHeaderAttributes = headerDataUse.intersect(attributes)
    val listDifference = attributes.filterNot(headerDataUse.contains)
    val testAttributes = checkTestAttributes(dataUse,attributes)

    val attributeChecked =  testAttributes match {
      case Right(attributeChecked) => attributes
      case Left(Right(attributeChecked)) => {
        println("These attributes are not part of the variable names: " + listDifference.mkString(","))
        intersectionHeaderAttributes
      }
      case Left(Left(attributeChecked))=> println("No attributes are matching the columns names")
        headerDataUse
    }

    val colRenamed: List[String] = "Variables" :: operations.map(_.name)
    val matrixMetric = unionFrame(x => subFrame(dataUse, attributeChecked(x),operations))(0, attributeChecked)
    matrixMetric.select(colRenamed.head, colRenamed.tail: _*)
  }



}
