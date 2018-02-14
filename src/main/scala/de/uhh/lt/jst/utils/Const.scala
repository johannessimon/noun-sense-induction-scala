package de.uhh.lt.jst.utils

object Const {

  val LIST_SEP = "  "
  val SCORE_SEP = ':'
  val POS_SEP = "#"
  val SENSE_SEP = "#"
  val HOLE = "@"
  val HOLE_DEPRECATED = "@@"
  val NO_FEATURE_LABEL = "-1"
  val NO_FEATURE_CONF = 0.0
  val PRIOR_PROB = 0.000010

  object FeatureExtractionTests {
    val conll = "/conll-large.csv.gz"
    val warc = "/cc-test.warc.gz"
  }

  object Resources {
    val STOPWORDS = "/stoplist_en.csv"
    val STOP_DEPENDENCIES = Set("dep", "punct", "cc", "possessive")
  }

  object CoNLL {
    // local huge files for large scale testing
    val largeConllPath = "/Users/sasha/Desktop/p"
    val xlargeConllPath = "/Users/sasha/Desktop/part-m-03787.gz"
  }

  object PRJ_TEST {
    val FEATURES = "/prj/dt/F.csv"
    val SENSES = "/prj/senses.csv"
    val WORDS = "/prj/dt/W.csv"
    val WORD_FEATURES = "/prj/WF.csv"

    // these large files can downloaded from http://panchenko.me/data/joint/josimtext/
    // and placed into the src/test/resources/wsd-test-data/
    object WSD_RES {
      val dir = "/wsd-test-data"
      val clusters = dir + "/clusters.csv"
      val coocs = dir + "/depwords.csv"
      val trigrams = dir + "/trigrams.csv"
      val deps = dir + "/deps.csv"
      val contexts = dir + "/contexts.csv"
    }

  }

}
