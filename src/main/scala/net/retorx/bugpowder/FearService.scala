package net.retorx.bugpowder

import javax.ws.rs._
import org.springframework.stereotype.Service

@Service
@Path("/fear")
class FearService(fearBuilder: FearBuilder, megaphone: Megaphone, ebcs : EBCS, ebcsFreshener : EBCSFreshener) {

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