package net.retorx.bugpowder

import javax.ws.rs._
import com.google.inject.{Inject, Singleton}

@Singleton
@Path("/fear")
class FearService @Inject() (fearBuilder: FearBuilder) {

    @GET
    @Path("/images")
    @Produces(Array("text/json"))
    def getImages = {
        fearBuilder.getImages
    }

}