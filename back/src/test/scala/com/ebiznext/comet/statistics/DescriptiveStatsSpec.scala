package com.ebiznext.comet.statistics

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.scalatest.FlatSpec

import scala.reflect.runtime.universe._

class DescriptiveStatsSpec extends FlatSpec {


  /**
    * Custom Log Levels
    */

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)
  Logger.getLogger("org.apache.spark").setLevel(Level.OFF)



  /**
    * Spark configuration
    */

  val conf = new SparkConf()          // set the configuration
    .setAppName("Statistic Summary")
    .setMaster("local[*]")


  val spark = SparkSession           // init sparksession
    .builder
    .config(conf)
    .appName("readxlsx")
    .getOrCreate()


  /** Function to get the Type
    *
    * @param a
    * @tparam T
    * @return
    */
  def getType[T: TypeTag](a: T): Type = typeOf[T]


  /**  Read the data .csv (iris.csv and titanic.csv)
    *
    */

  val dataIris: DataFrame = spark.read
   .format("csv")
   .option("header", "true") //reading the headers
   .option("mode", "DROPMALFORMED")
   .load(path = "./src/test/ressources/iris.csv")

  val dataTitanic = spark.read
   .format("csv")
   .option("header", "true") //reading the headers
   .option("mode", "DROPMALFORMED")
   .load(path ="./src/test/ressources/titanic.csv")

  /**
    *  Inputs for the test :  Header (list of the variable) and  statMetrics (Metrics to use)
    */

  val irisContnuousAttributes: List[String] = Seq("SepalLength", "SepalWidth","PetalLength","PetalWidth").toList
  val irisDiscreteAttributes: List[String] = Seq("Name").toList

  val titanicContinuousAttributes: List[String] = Seq("Fare", "Age").toList
  val titanicDiscreteAttributes: List[String] = Seq("Survived", "Pclass","Siblings","Parents","Sex").toList


  val partialContnuousAttributes: List[String] = Seq("Fare", "Age").toList
  val partialContinuousMetric : List[DescriptiveStats.ContinuousMetric]= List(DescriptiveStats.Min, DescriptiveStats.Max)


  /**
    * Descriptive statistics of the Iris dataframe for Quantitative variable:
    */

  val result0 = DescriptiveStats.computeContinuiousMetric(dataIris, irisContnuousAttributes, DescriptiveStats.allContinuousMetrics)

  val result1 = DescriptiveStats.computeContinuiousMetric(dataIris, irisContnuousAttributes, partialContinuousMetric)


  /**
    *  Descriptive statistics of the Iris dataframe for Qualitative variable:
    */


  val result2 = DescriptiveStats.computeDiscretMetric(dataTitanic, titanicDiscreteAttributes, DescriptiveStats.allDiscreteMetrics)


  val result3 = DescriptiveStats.computeDiscretMetric(dataTitanic, List("Survived", "Pclass","Sex"), List(DescriptiveStats.Category, DescriptiveStats.Frequencies))


  /**
    *  1- test : Test on the mean of the dimension
    */


  val dimensionTable = ((partialContinuousMetric.size)+1)*((irisContnuousAttributes.size)+1)


  val dimensionDataframe = (result1.columns.size)*((result1.select(col("Variables")).collect().map(_.getString(0)).toList.size)+1)


  "The size  of the Table " should "be tested" in {
    assert(dimensionTable - dimensionDataframe  == 0 )
  }

  /**
    *  2- test : Test for all values of the Mean
    */


  val meanList: List[Double] = irisContnuousAttributes.map(name => dataIris.select(avg(name)).first().getDouble(0))

  val meanListTable : List[Double]= result0.select(col("Mean")).collect().map(_.getDouble(0)).toList


  "All values of The Mean " should "be tested" in {
    assert(meanList.zip(meanListTable).map(x => x._1 - x._2).sum  <= 0.00001 )
  }

  /**
    *  3- test : Test for all values of the Min
    */


  val minList: List[Double] = irisContnuousAttributes.map(name => dataIris.select(min(name)).first().getString(0).toDouble)

  val minListTable : List[Double]= result0.select(col("Min")).collect().map(_.getDouble(0)).toList

  "All values of The Min" should "be tested" in {
    assert(minList.zip(minListTable).map(x => x._1 - x._2).sum  <= 0.00001 )
  }

  /**
    *  4- test : Test for all values of the Max
    */


   val maxList: List[Double] = irisContnuousAttributes.map(name => dataIris.select(max(name)).first().getString(0).toDouble)

   val maxListTable : List[Double]= result0.select(col("Max")).collect().map(_.getDouble(0)).toList

   "All values of The Max" should "be tested" in {
     assert(maxList.zip(maxListTable).map(x => x._1 - x._2).sum  <= 0.00001 )
   }


  /**
    *  5- test : Test for all values of the Stddev
    */


  val stddevList: List[Double] = irisContnuousAttributes.map(name => dataIris.select(stddev(name)).first().getDouble(0))

  val stddevListTable : List[Double]= result0.select(col("Stddev")).collect().map(_.getDouble(0)).toList


  "All values of The Stddev" should "be tested" in {
    assert(stddevList.zip(stddevListTable).map(x => x._1 - x._2).sum  <= 0.001 )
  }

  /**
    * 6- test : Test for all values of the Skewness
    */


  val skewnessList: List[Double] = irisContnuousAttributes.map(name => dataIris.select(skewness(name)).first().getDouble(0))

  val skewnessListTable : List[Double]= result0.select(col("Skewness")).collect().map(_.getDouble(0)).toList


  "All values of The Skewness" should "be tested" in {
    assert(skewnessList.zip(skewnessListTable).map(x => x._1 - x._2).sum  <= 0.001 )
  }

  /**
    * 7- test : Test for all values of the kurtosis
    */

  val kurtosisList: List[Double] = irisContnuousAttributes.map(name => dataIris.select(kurtosis(name)).first().getDouble(0))

  val kurtosisListTable : List[Double]= result0.select(col("Kurtosis")).collect().map(_.getDouble(0)).toList

  "All values of The Kurtosis" should "be tested" in {
    assert(kurtosisList.zip(kurtosisListTable).map(x => x._1 - x._2).sum  <= 0.001 )
  }


  /**
    * 8- test : Test for the case
    */


  val result10 = DescriptiveStats.computeContinuiousMetric(dataIris, partialContnuousAttributes, DescriptiveStats.allContinuousMetrics)
  result10.show()




}

