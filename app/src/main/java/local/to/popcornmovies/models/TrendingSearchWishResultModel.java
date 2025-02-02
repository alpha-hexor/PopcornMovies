package local.to.popcornmovies.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TrendingSearchWishResultModel implements Serializable {

   @NonNull
   public final String title, mediaLink, poster;

   public Boolean inWishList = null;

   public TrendingSearchWishResultModel(String title, String mediaLink, String poster){
      this.title = title;
      this.mediaLink = mediaLink;
      this.poster = poster;
   }

   @Override
   public String toString(){
      return "{\n"+
      "title : "+this.title+"\n"+
      "mediaLink : "+this.mediaLink+"\n"+
      "poster : "+this.poster+"\n"+
      "}";
   }
}
