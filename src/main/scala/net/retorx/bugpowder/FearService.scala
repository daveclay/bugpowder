package net.retorx.bugpowder

import javax.ws.rs._
import com.google.inject.{Inject, Singleton}

@Singleton
@Path("/fear")
class FearService @Inject() (fearBuilder: FearBuilder, megaphone: Megaphone, ebcs : EBCS, ebcsFreshener : EBCSFreshener) {

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
    
    @GET
    @Path("/audioClip/{fileName}.ogg")
    @Produces(Array("audio/ogg"))
    def getOggAudioClip(@PathParam(value="fileName") clipPath : String) = {
      ebcs.getAudioClip(clipPath + ".ogg")
    }

    @GET
    @Path("/audioClip/{fileName}.mp3")
    @Produces(Array("audio/mpeg"))
    def getMP3AudioClip(@PathParam(value="fileName") clipPath : String) = {
      ebcs.getAudioClip(clipPath + ".mp3")
    }

    @GET
    @Path("/audioClip/{fileName}.wav")
    @Produces(Array("audio/wave"))
    def getWAVAudioClip(@PathParam(value="fileName") clipPath : String) = {
      ebcs.getAudioClip(clipPath + ".wav")
    }
    
    @POST
    @Path("/freshenEBCS")
    def freshenEBCS {
      ebcsFreshener.freshen()
    }
    
    @GET
    @Path("/freshenerStatus")
    def freshenerStatus = {
      ebcsFreshener.status
    }
}