package de.uhh.lt.conll

import org.scalatest._

import scala.io.Source

class CoNLLParserSpec extends FlatSpec with Matchers {

  it should "parse the rows for one sentence in CoNLL format" in {
    val path = getClass.getResource("/conll.csv").getPath
    val text = Source.fromFile(path).mkString

    val sentence = CoNLLParser.parseSingleSentence(text)

    sentence.deps.head should be(
      (0, Dependency(
        id = 0,
        form = "Website",
        lemma = "Website",
        upostag = "NNP",
        xpostag = "NNP",
        feats = "",
        head = 2,
        deprel = "nn",
        deps = "2:nn",
        ner = "O"
      ))
    )
    sentence.deps.size should be(8)
  }
}




