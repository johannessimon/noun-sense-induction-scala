package de.uhh.lt.jst.dt

import de.uhh.lt.jst.Job
import de.uhh.lt.jst.utils.Util
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object WordFeatureFilter  extends Job {

  case class Config(
    wordFeatureCSV: String = "",
    wordsCSV: String = "",
    outputWordFeatureCSV: String = ""
    )

  type ConfigType = Config
  override val config = Config()

  override val command: String = "WordFeatureFilter"
  override val description = "Remove all rows where the word is not in the VOC_FILE"

  override val parser = new Parser {
    arg[String]("WORD_FEATURE_COUNT_FILE").action((x, c) =>
      c.copy(wordFeatureCSV = x)).required().hidden()

    arg[String]("VOC_FILE").action((x, c) =>
      c.copy(wordsCSV = x)).required().hidden()

    arg[String]("OUTPUT_DIR").action((x, c) =>
      c.copy(outputWordFeatureCSV = x)).required().hidden()
  }

  def run(config: Config): Unit = oldMain(config.productIterator.map(_.toString).toArray)

  // ------ unchanged old logic ------- //

  def oldMain(args: Array[String]) {
    if (args.size < 3) {
      println("Usage: WordFeatureFilter <word-feature-csv> <words-csv> <output-word-feature-csv>")
      println("<word-feature-csv>\tpath to a csv with word counts 'word<TAB>feature<TAB>freq'")
      println("<words-csv>\tpath to a list of words that will be kept in the output. " +
        "words in all registers will be kept if present in the vocabulary.")
      println("<output-word-feature-csv>\tpath to output with the filtered word counts")
      return
    }

    // Input parameters
    val inputPath = args(0)
    val inputWordVocPath = args(1)
    val outputPath = args(2)
    val outFeatureVocPath = outputPath + "-voc.csv"
    println("Input word-features: " + inputPath)
    println("Input word vocabulary:" + inputWordVocPath)
    println("Output word-features: " + outputPath)
    println("Output feature vocabulary: " + outFeatureVocPath)
    Util.delete(outputPath)
    Util.delete(outFeatureVocPath)

    // Set Spark configuration
    val conf = new SparkConf().setAppName("FreqFilter")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sc: SparkContext = new SparkContext(conf)

    // Filter
    val voc = Util.loadVocabulary(sc, inputWordVocPath)
    val (wordFeatureFreq, featureVoc) = run(inputPath, voc, sc)

    // Save result
    featureVoc
      .saveAsTextFile(outFeatureVocPath)

    wordFeatureFreq
      .map({ case (word, feature, freq) => word + "\t" + feature + "\t" + freq })
      .saveAsTextFile(outputPath)
  }

  def run(inputPath: String, voc: Set[String], sc: SparkContext): (RDD[(String, String, String)], RDD[String]) = {
    // Filter

    val wordFeatureFreq = sc.textFile(inputPath)
      .map(line => line.split("\t"))
      .map({ case Array(word, feature, freq) => (word, feature, freq) case _ => ("?", "?", "?") })
      .filter({ case (word, feature, freq) => (voc.contains(word.toLowerCase())) })
      .cache()

    val features = wordFeatureFreq
      .map({ case (word, feature, freq) => (feature) })
      .distinct()
      .sortBy({ case feature => feature })
      .cache()
    (wordFeatureFreq, features)
  }
}