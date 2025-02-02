package local.to.popcornmovies.models;

import java.util.ArrayList;

public class StreamingModel {

   public final String videoSource;
   public final ArrayList<Subtitle> subtitles;

   public StreamingModel(String videoSource, ArrayList<Subtitle> subtitles) {
      this.videoSource = videoSource;
      this.subtitles = subtitles;
   }

   @Override
   public String toString(){
      return "{"+
         "videoSource : "+this.videoSource+",\t"+
         "subtitles : "+this.subtitles.toString()+",\t"+
      "}";
   }

}
