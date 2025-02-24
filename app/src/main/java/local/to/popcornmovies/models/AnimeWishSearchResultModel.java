package local.to.popcornmovies.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AnimeWishSearchResultModel implements Serializable {

   @NonNull
   public final String id, poster, title;
   public Boolean inWishList = null;

   public AnimeWishSearchResultModel(String id, String poster, String title){
      this.id = id;
      this.poster = poster;
      this.title = title;
   }

   public String toString(){
      return "{"+
         "id : " + this.id + "\n" +
         "thumbnail : " + this.poster + "\n" +
         "name : " + this.title + "\n" +
      "}";
   }
}
