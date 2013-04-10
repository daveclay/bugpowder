package net.retorx.bugpowder

import com.google.inject.Singleton
import scala.util.Random
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import scala.collection.JavaConversions._
import scala.collection.immutable.HashSet

@Singleton
class FearBuilder {

    val fearSources = Array(
        new CNN(),
        new FoxNews(),
        new HuffingtonPost(),
        new Townhall())

    def getImages = {
        fearSources.foldLeft(new HashSet[String]())((list, fearSource) => {
            list ++ fearSource.getImages
        })
    }
}

trait FearSource {
    def getImages:List[String]
}

@Singleton
class HuffingtonPost extends FearSource {
    def getImages = {
        val urls = Array(
            "http://www.huffingtonpost.com/politics/")

        urls.foldLeft(List[String]())((list,url) => {
            val images = grab(url)
            list ++ images
        })
    }

    private def grab(url: String) = {
        val doc = Jsoup.connect(url).get
        val images = doc.select("img")
        images.iterator().map(element => {
            element.attr("src")
        }).filter(src => {
            (src.contains("i.huffington"))
        })
    }

}

@Singleton
class Townhall extends FearSource {
    def getImages = {
        val urls = Array(
            "http://townhall.com/")

        urls.foldLeft(List[String]())((list,url) => {
            val images = grab(url)
            list ++ images
        })
    }

    private def grab(url: String) = {
        val doc = Jsoup.connect(url).get
        val images = doc.select("img")
        images.iterator().map(element => {
            element.attr("src")
        }).filter(src => {
            (src.contains("media")) && (! src.contains("comments.png") || ! src.contains("partner"))
        })
    }

}

@Singleton
class FoxNews extends FearSource {
    def getImages = {
        val urls = Array(
            "http://www.foxnews.com",
            "http://www.foxnews.com/politics/index.html")

        urls.foldLeft(List[String]())((list,url) => {
            val images = grab(url)
            list ++ images
        })
    }

    private def grab(url: String) = {
        val doc = Jsoup.connect(url).get
        val images = doc.select("img")
        images.iterator().map(element => {
            element.attr("src")
        }).filter(src => {
            (src.contains("root_images") || src.contains("ucat") || src.contains("managed")) && (! src.contains("logo"))
        })
    }

}

@Singleton
class CNN extends FearSource {

    def getImages = {
        val urls = Array(
            "http://www.cnn.com",
            "http://www.cnn.com/POLITICS/",
            "http://politicalticker.blogs.cnn.com/")

        urls.foldLeft(List[String]())((list,url) => {
            val images = grab(url)
            list ++ images
        })
    }

    private def grab(url: String) = {
        val doc = Jsoup.connect(url).get
        val images = doc.select("img")
        images.iterator().map(element => {
            element.attr("src")
        }).filter(src => {
            src.contains("asset")
        })
    }

}