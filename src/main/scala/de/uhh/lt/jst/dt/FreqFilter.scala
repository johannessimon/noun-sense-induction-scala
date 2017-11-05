package de.uhh.lt.jst.dt

import de.uhh.lt.jst.Job
import de.uhh.lt.jst.utils.Util
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object FreqFilter extends Job {

  case class Config(
    freqCSV: String = "",
    vocabularyCSV: String = "",
    outputFreqCSV: String = "",
    keepSingleWords: Boolean = true
  )

  type ConfigType = Config
  override val config = Config()

  override val command: String = "FreqFilter"
  override val description = "Remove all rows where the term is not in the VOC_FILE"

  override val parser = new Parser {

    opt[Unit]('s', "remove-single").action( (x, c) =>
      c.copy(keepSingleWords = false) ).
      text("remove all single word terms")

    arg[String]("TERM_REPR_FILE").action( (x, c) =>
      c.copy(freqCSV = x) ).required().hidden()

    arg[String]("VOC_FILE").action( (x, c) =>
      c.copy(vocabularyCSV = x) ).required().hidden()

    arg[String]("OUTPUT_DIR").action( (x, c) =>
      c.copy(outputFreqCSV = x) ).required().hidden()
  }

  def run(config: Config): Unit = {

    // Set Spark configuration
    val sparkConf = new SparkConf().setAppName("FreqFilter")
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    //conf.set("spark.dynamicAllocation.enabled", "true")
    //conf.set("spark.shuffle.service.enabled", "true") // required to enable dynamicAllocation
    val sc = new SparkContext(sparkConf)

    // Filter
    val voc = Util.loadVocabulary(sc, config.vocabularyCSV)
    val freqFiltered = run(sc, config.freqCSV, voc, config.keepSingleWords)

    // Save the result
    freqFiltered
      .map({ case (word, freq) => word + "\t" + freq })
      .saveAsTextFile(config.outputFreqCSV)
  }

  def run(sc: SparkContext, freqPath: String, voc: Set[String], keepSingleWords: Boolean): RDD[(String, String)] = {
    val freq = sc.textFile(freqPath)
      .map(line => line.split("\t"))
      .map({ case Array(word, freq) => (word, freq) case _ => ("?", "?") })

    val freqFiltered =
      if (keepSingleWords) {
        freq.filter({ case (word, freq) => (!word.contains(" ") || voc.contains(word.toLowerCase())) })
      } else {
        freq.filter({ case (word, freq) => (voc.contains(word.toLowerCase())) })
      }
    freqFiltered
  }
}