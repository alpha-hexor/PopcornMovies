package local.to.popcornmovies.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AnimeEpisodeResponseModel {
   @SerializedName("data")
   public Data data;

   public static class Data {
      @SerializedName("show")
      public Show show;
   }

   public static class Show {
      @SerializedName("availableEpisodesDetail")
      public AvailableEpisodesDetail availableEpisodesDetail;

      @SerializedName("_id")
      public String id;
   }

   public static class AvailableEpisodesDetail {
      @SerializedName("dub")
      public List<String> dub;

      @SerializedName("sub")
      public List<String> sub;
   }
}
