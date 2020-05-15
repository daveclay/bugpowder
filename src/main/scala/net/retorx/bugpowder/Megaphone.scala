package net.retorx.bugpowder

import com.google.inject.Singleton
import scala.util.Random
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import scala.jdk.CollectionConverters._
import scala.collection.immutable.HashSet
import com.sun.syndication.io.{XmlReader, SyndFeedInput}
import java.net.URL
import com.sun.syndication.feed.synd.SyndEntry
import java.util

@Singleton
class Megaphone {

    private val random = new Random()

    def getCutups = {
        val voices = getVoices
        Ticker(random.shuffle(getCutupPieces(voices)).foldLeft("")((string, part) => {
            string + " " + part
        }))
    }

    def getCutupPieces(voices:List[Voice]) = {
        voices.foldLeft(List[String]())((list, voice) => {
            val words = (voice.title.split(" ") ++ voice.description.split(" ")).map(word => {
                word.replaceAll("/[^a-zA-Z0-9\\s\\p{P}]/", "")
            })
            list ++ words
        })
    }

    def getVoices = {
        val urls = Array("http://www.huffingtonpost.com/feeds/verticals/politics/index.xml")
        urls.foldLeft(List[Voice]())((list, url) => {
            list ++ Feed.load(url)
        })
    }
}

case class Voice(title: String, description: String)
case class Ticker(text: String)

object Feed {

    def load(url: String):Iterator[Voice] = {
        val feedSource = new URL(url)
        val input = new SyndFeedInput()
        val feed = input.build(new XmlReader(feedSource))
        val arrayList = feed.getEntries.asInstanceOf[util.ArrayList[SyndEntry]]
        arrayList.iterator().asScala.map(entry => {
            val title = entry.getTitle
            val desc = entry.getDescription.getValue
            Voice(title, desc)
        })
    }
}