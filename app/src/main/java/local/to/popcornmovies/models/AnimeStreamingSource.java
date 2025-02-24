package local.to.popcornmovies.models;

public class AnimeStreamingSource {
   public final String sourceName, url;
   public final boolean isMp4;

   public AnimeStreamingSource(String sourceName, String url, boolean isMp4) {
      this.sourceName = sourceName;
      this.url = url;
      this.isMp4 = isMp4;
   }

   @Override
   public String toString() {
      return "{"+
      "sourceName : " + this.sourceName + "\n" +
      "url : " + this.url + "\n" +
      "isMp4 : " + this.isMp4 + "\n" +
      "}";
   }
}
