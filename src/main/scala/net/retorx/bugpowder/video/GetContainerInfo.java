package net.retorx.bugpowder.video;

import com.xuggle.xuggler.Configuration;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

public class GetContainerInfo {
    /**
     * Takes a media container (file) as the first argument, opens it, and tells you what's inside the container.
     * @param args Must contain one string which represents a filename
     */
    public static void main(String[] args)
    {
      // If the user passes -Dxuggle.options, then we print
      // out all possible options as well.
      String optionString = System.getProperty("xuggle.options");
      if (optionString != null)
      {
        Configuration.printHelp(System.out);
      }

      if (args.length <= 0)
        throw new IllegalArgumentException("must pass in a filename as the first argument");

      String filename = args[0];
      // Create a Xuggler container object
      IContainer container = IContainer.make();

      // Open up the container
      if (container.open(filename, IContainer.Type.READ, null) < 0)
        throw new IllegalArgumentException("could not open file: " + filename);

      // query how many streams the call to open found
      int numStreams = container.getNumStreams();
      System.out.printf("file \"%s\": %d stream%s; ",
          filename,
          numStreams,
          numStreams == 1 ? "" : "s");
      System.out.printf("duration (ms): %s; ", container.getDuration() == Global.NO_PTS ? "unknown" : "" + container.getDuration()/1000);
      System.out.printf("start time (ms): %s; ", container.getStartTime() == Global.NO_PTS ? "unknown" : "" + container.getStartTime()/1000);
      System.out.printf("file size (bytes): %d; ", container.getFileSize());
      System.out.printf("bit rate: %d; ", container.getBitRate());
      System.out.printf("\n");

      // and iterate through the streams to print their meta data
      for(int i = 0; i < numStreams; i++)
      {
        // Find the stream object
        IStream stream = container.getStream(i);
        // Get the pre-configured decoder that can decode this stream;
        IStreamCoder coder = stream.getStreamCoder();


        // and now print out the meta data.
        System.out.printf("stream %d: ",    i);
        System.out.printf("type: %s; ",     coder.getCodecType());
        System.out.printf("codec: %s; ",    coder.getCodecID());
        System.out.printf("duration: %s; ", stream.getDuration() == Global.NO_PTS ? "unknown" : "" + stream.getDuration());
        System.out.printf("start time: %s; ", container.getStartTime() == Global.NO_PTS ? "unknown" : "" + stream.getStartTime());
        System.out.printf("language: %s; ", stream.getLanguage() == null ? "unknown" : stream.getLanguage());
        System.out.printf("timebase: %d/%d; ", stream.getTimeBase().getNumerator(), stream.getTimeBase().getDenominator());
        System.out.printf("coder tb: %d/%d; ", coder.getTimeBase().getNumerator(), coder.getTimeBase().getDenominator());

        if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
        {
          System.out.printf("sample rate: %d; ", coder.getSampleRate());
          System.out.printf("channels: %d; ",    coder.getChannels());
          System.out.printf("format: %s",        coder.getSampleFormat());
        } else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
        {
          System.out.printf("width: %d; ",  coder.getWidth());
          System.out.printf("height: %d; ", coder.getHeight());
          System.out.printf("format: %s; ", coder.getPixelType());
          System.out.printf("frame-rate: %5.2f; ", coder.getFrameRate().getDouble());
        }
        System.out.printf("\n");
      }

    }
}
