package de.uhh.lt.conll

// Using Seq instead of List because of SPARK-16792
case class Sentence(comments: Seq[String],
                    deps: Seq[Row],
                    documentID:String = "",
                    sentenceID:Int = -1,
                    text:String = "")
