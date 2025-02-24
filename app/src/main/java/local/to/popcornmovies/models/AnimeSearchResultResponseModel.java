package local.to.popcornmovies.models;

import com.google.gson.annotations.SerializedName;
import java.util.*;

public class AnimeSearchResultResponseModel {
   
   @SerializedName("data")
   public Data data;

   public static class Data {
      @SerializedName("shows")
      public Shows shows;
   }

   public static class Shows {
      @SerializedName("edges")
      public List<Edge> edges;
   }

   public static class Edge {
      @SerializedName("availableEpisodes")
      public AvailableEpisodes availableEpisodes;

      @SerializedName("thumbnail")
      public String thumbnail;

      @SerializedName("__typename")
      public String typename;

      @SerializedName("name")
      public String name;

      @SerializedName("_id")
      public String id;
   }

   public static class AvailableEpisodes {
      @SerializedName("dub")
      public int dub;

      @SerializedName("sub")
      public int sub;

      @SerializedName("raw")
      public int raw;
   }
}