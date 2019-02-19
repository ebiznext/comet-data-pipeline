package com.ebiznext.comet.job


import org.apache.spark.{SparkConf}
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions.col



/**  To record statistics with other information during ingestion.
  *
  */

object SaveStatistics {


    /** data Statistics information and status of the data
      *
      * @param domain                      : Domain name
      * @param schema                      : Schema
      * @param variableName                : Variable name from the attributes
      * @param minMetric                   : The minimum value associates with the variable
      * @param maxMetric                   : The maximum value associates with the variable
      * @param meanMetric                  : The mean value associates with the variable
      * @param countMetric                 : The count value associates with the variable
      * @param countMissValMetric          : The count value of missing values associates with the variable
      * @param varMetric                   : The variance value associates with the variable
      * @param stddevMetric                : The stddev value associates with the variable
      * @param sumMetric                   : The sum value associates with the variable
      * @param skewnessMetric              : The skewness value associates with the variable
      * @param kurtosisMetric              : The kurtosis value associates with the variable
      * @param percentile25Metric          : The percentile25 value associates with the variable
      * @param medianMetric                : The median value associates with the variable
      * @param percentile75Metric          : The percentile25 value associates with the variable
      * @param categoryMetric              : The category value associates with the variable (in the case of discrete variable)
      * @param countDiscreteMetric         : The count value associates with the variable (in the case of discrete variable)
      * @param frequenciesMetric           : The frequencies values associates with the variable (in the case of discrete variable)
      * @param CountMissValuesDiscrete     : The count value of missing values value associates with the variable (in the case of discrete variable)
      * @param timesIngestion              : The time at ingestion
      * @param stageState                  : The stage state  (consolidated or Ingested)
      */
    case class dataStatCaseClass( domain: String,
                                  schema: String,
                                  variableName: String,

                                  minMetric: Option[Double],
                                  maxMetric: Option[Double],
                                  meanMetric: Option[Double],
                                  countMetric: Option[Long],
                                  countMissValMetric: Option[Long],
                                  varMetric: Option[Double],
                                  stddevMetric: Option[Double],
                                  sumMetric: Option[Double],
                                  skewnessMetric: Option[Double],
                                  kurtosisMetric: Option[Double],
                                  percentile25Metric: Option[Double],
                                  medianMetric: Option[Double],
                                  percentile75Metric: Option[Double],

                                  categoryMetric: Option[List[String]],
                                  countDiscreteMetric: Option[Map[String, Long]],
                                  frequenciesMetric: Option[Map[String, Double]],

                                  CountMissValuesDiscrete: Option[Long],

                                  timesIngestion: String,
                                  stageState: String)


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

  import spark.implicits._




  /** Function that retrieves class names for each variable (case discrete variable)
    * @param dataInit     : Dataframe that contains all the computed metrics
    * @param nameVariable : name of the variable
    * @return             : list of all class associates to the variable
    */

  def getListCategory(dataInit: DataFrame , nameVariable : String) : List[String] = {
    val dataReduiceCategory: DataFrame = dataInit.filter(col("Variables").isin(nameVariable))
    dataReduiceCategory.select("Category").collect.map(_.getString(0)).toList
  }

  /** Function that retrieves the values of the CountDiscrete metric (the distinct count) for one category  associate to one variable (case discrete variable)
    * @param dataInit      : Dataframe that contains all the computed metrics
    * @param nameVariable  : name of the variable
    * @param nameCategory  : name of the category
    * @return              : the value of the distinct count
    */

  def getMapCategoryCountDiscrete(dataInit: DataFrame , nameVariable : String, nameCategory : String): Long = {
    val metricName : String = "CountDiscrete"
    val dataReduiceCategory: DataFrame = dataInit.filter(col("Variables").isin(nameVariable))
    val dataCategory: DataFrame = dataReduiceCategory.filter(col("Category").isin(nameCategory))
    dataCategory.select(col(metricName)).first().getLong(0)
  }


  /** Function that retrieves the values of the Frequencies metric (frequency) for one category  associate to one variable (case discrete variable)
    * @param dataInit      : Dataframe that contains all the computed metrics
    * @param nameVariable  : name of the variable
    * @param nameCategory  : name of the category
    * @return              : the value of the frequency
    */

  def getMapCategoryFrequencies(dataInit: DataFrame , nameVariable : String, nameCategory : String): Double = {
    val metricName : String = "Frequencies"
    val dataReduiceCategory: DataFrame = dataInit.filter(col("Variables").isin(nameVariable))
    val dataCategory: DataFrame = dataReduiceCategory.filter(col("Category").isin(nameCategory))
    dataCategory.select(col(metricName)).first().getDouble(0)
  }

  /** Function that retrieves the value of the metric associate to the variable (case continuous variable)
    * @param dfStatistics  :  Dataframe that contains all the computed metrics
    * @param nameVariable  :  name of the variable
    * @param metricName    :  name of the metric
    * @return              :  the value of the metric
    */


  def getValueMetricContinuous(dfStatistics: DataFrame, nameVariable : String, metricName : String ): Option[Any]= {
    dfStatistics.filter(col("Variables").isin(nameVariable)).count() match {
      case 1 => metricName match {
        case "Min" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Min").first().getDouble(0))
        case "Max" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Max").first().getDouble(0))
        case "Mean" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Mean").first().getDouble(0))
        case "Count" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Count").first().getLong(0))
        case "CountMissValues" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("CountMissValues").first().getLong(0))
        case "Var" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Var").first().getDouble(0))
        case "Stddev" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Stddev").first().getDouble(0))
        case "Sum" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Sum").first().getDouble(0))
        case "Skewness" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Skewness").first().getDouble(0))
        case "Kurtosis" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Kurtosis").first().getDouble(0))
        case "Percentile25" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Percentile25").first().getDouble(0))
        case "Median" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Median").first().getDouble(0))
        case "Percentile75" => Some(dfStatistics.filter(col("Variables").isin(nameVariable )).select("Percentile75").first().getDouble(0))
      }

      case _ => None
    }
  }


  /** Function that retrieves the value of the metric associate to the variable (case discrete variable)
    * @param dfStatistics  :  Dataframe that contains all the computed metrics
    * @param nameVariable  :  name of the variable
    * @param metricName    :  name of the metric
    * @return              :  the value of the metric
    */


  def getValueMetricDiscret(dfStatistics: DataFrame, nameVariable : String, nameCategory: String, metricName : String): Option[Any] = {
    dfStatistics.filter(col("Variables").isin(nameVariable)).count() match {
      case 1 => None
      case _ =>  metricName match {
          case "Category" => Some(dfStatistics.filter(col("Variables").isin(nameVariable)).select("Category").collect.map(_.getString(0)).toList)
          case "CountDiscrete" => Some(Map(getListCategory(dfStatistics, nameVariable).map(x => (x -> getMapCategoryCountDiscrete(dfStatistics, nameVariable,x))) : _*))
          case "Frequencies" => Some(Map(getListCategory(dfStatistics, nameVariable).map(x => (x -> getMapCategoryFrequencies(dfStatistics, nameVariable,x))) : _*))
          case "CountMissValuesDiscrete" => Some(dfStatistics.filter(col("Variables").isin(nameVariable)).select("CountMissValuesDiscrete").first().getLong(0))
        }

    }
  }

  /** Function that retrieves the value of the metric associate to the variable (case continuous variable)
    * @param dfStatistics   : Dataframe that contains all the computed metrics
    * @param Domain         : name of the domain
    * @param schema         : schema of the initial data
    * @param timeIngestion  : time which correspond to the ingestion
    * @param stage          : stage
    */


      def writeDataStatToParquet(dfStatistics: DataFrame, domain: String, schema: String, timeIngestion : String, stageState : String) :Dataset[SaveStatistics.dataStatCaseClass] = {


        val listVariable : List[String]= dfStatistics.select("Variables").select("Variables").collect.map(_.getString(0)).toList.distinct
        val listRowByVariable : List[SaveStatistics.dataStatCaseClass]= listVariable.map(c => dataStatCaseClass(
          domain,
          schema,
          c,

          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Min").first().getDouble(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Max").first().getDouble(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Mean").first().getDouble(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Count").first().getLong(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("CountMissValues").first().getLong(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Var").first().getDouble(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Stddev").first().getDouble(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Sum").first().getDouble(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Skewness").first().getDouble(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Kurtosis").first().getDouble(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Percentile25").first().getDouble(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Median").first().getDouble(0))
          case _ => None},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => Some(dfStatistics.filter(col("Variables").isin(c)).select("Percentile75").first().getDouble(0))
          case _ => None},

          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => None
          case _ => Some(dfStatistics.filter(col("Variables").isin(c)).select("Category").collect.map(_.getString(0)).toList)},

          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => None
          case _ => Some(Map(getListCategory(dfStatistics, c).map(x => (x -> getMapCategoryCountDiscrete(dfStatistics, c,x))) : _*))},
          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => None
          case _ => Some(Map(getListCategory(dfStatistics, c).map(x => (x -> getMapCategoryFrequencies(dfStatistics, c,x))) : _*))},


          dfStatistics.filter(col("Variables").isin(c)).count()  match { case 1 => None
          case _ => Some(dfStatistics.filter(col("Variables").isin(c)).select("CountMissValuesDiscrete").first().getLong(0))},

          timeIngestion,
          stageState))

        val dataByVariable = listRowByVariable.toDS
        dataByVariable.coalesce(1).write.format("parquet").mode("append").save("./src/test/ressources/statDataParquet.parquet")

        dataByVariable

      }





  }


