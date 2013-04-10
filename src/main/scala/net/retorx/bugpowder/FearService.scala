package net.retorx.bugpowder

import javax.ws.rs._
import com.google.inject.{Inject, Singleton}

@Singleton
@Path("/fear")
class FearService @Inject() (cnn: CNN) {

    @GET
    @Path("/images")
    @Produces(Array("text/json"))
    def getImages = {
        cnn.getImageUrls
    }

}