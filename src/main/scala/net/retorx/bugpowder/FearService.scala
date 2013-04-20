package net.retorx.bugpowder

import javax.ws.rs._
import com.google.inject.{Inject, Singleton}

@Singleton
@Path("/fear")
class FearService @Inject() (fearBuilder: FearBuilder, megaphone: Megaphone, ebcs : EBCS) {

    @GET
    @Path("/images")
    @Produces(Array("text/json"))
    def getImages = {
        fearBuilder.getImages
    }

    @GET
    @Path("/feeds")
    @Produces(Array("text/json"))
    def getFeeds = {
        megaphone.getCutups
    }
    
    @GET
    @Path("/audio")
    @Produces(Array("text/json"))
    def getAudio = {
        ebcs.getAudioClips
    }
}