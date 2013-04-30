package net.retorx.bugpowder

import com.google.inject.Singleton
import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import scala.collection.immutable.HashSet

@Singleton
class HuffingtonPost extends LogoFilteringFearSourceImp(
    Array("http://www.huffingtonpost.com/politics/"),
    src => src.contains("i.huffington"))

@Singleton
class Townhall extends LogoFilteringFearSourceImp(
    Array("http://townhall.com/"),
    src => src.contains("media") && (! src.contains("comments.png") || ! src.contains("partner")))


@Singleton
class FoxNews extends LogoFilteringFearSourceImp(
    Array("http://www.foxnews.com",
        "http://www.foxnews.com/politics/index.html"),
    src => (src.contains("root_images") || src.contains("ucat") || src.contains("managed")) && (! src.contains("logo")))

@Singleton
class CNN extends LogoFilteringFearSourceImp(
    Array("http://www.cnn.com",
        "http://www.cnn.com/POLITICS/",
        "http://politicalticker.blogs.cnn.com/"),
    src => src.contains("asset"))


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

class LogoFilteringFearSourceImp(urls: Array[String], filter:String => Boolean)
        extends FilteringFearSource(urls, src => { filter(src) && (src.indexOf("logo") < 0) })

class FilteringFearSource(urls: Array[String], filter:String => Boolean) extends FearSource {

    def getImages = {
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
        }).filter(src => { filter(src.toLowerCase) } )
    }
}
