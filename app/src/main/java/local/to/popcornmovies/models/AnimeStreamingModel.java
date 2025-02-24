package local.to.popcornmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AnimeStreamingModel {

   @SerializedName("links")
   public ArrayList<Data> links;

   public static class Data{

      @SerializedName("mp4")
      public boolean mp4;

      @SerializedName("src")
      public String src;
      
      @SerializedName("link")
      public String link;
      
      @SerializedName("resolutionStr")
      public String resolutionStr;
      

      @Override
      public String toString(){
         return "{"+
         "mp4 : "+this.mp4+"\n"+
         "src : "+this.src+"\n"+
         "link : "+this.link+"\n"+
         "resolutionStr : "+this.resolutionStr+"\n"+
         "}";
      }
   }
   @Override
   public String toString(){
      return this.links.toString();
   }
}
