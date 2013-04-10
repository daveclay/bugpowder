package net.retorx.bugpowder

import com.google.inject.Singleton
import scala.util.Random
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import scala.collection.JavaConversions._

@Singleton
class CNN {

    val random = new Random()

    def getImageUrls = {
        val url = "http://www.cnn.com"
        val doc = Jsoup.connect(url).get
        val images = doc.select("img")
        images.iterator().map(element => {
            element.attr("src")
        }).filter(src => {
            src.contains("asset")
        })
    }

}