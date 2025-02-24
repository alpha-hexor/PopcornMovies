package local.to.popcornmovies.rooom_database.entities;

import androidx.room.Entity;

import local.to.popcornmovies.models.AnimeWishSearchResultModel;

@Entity(tableName = "anime_wish", primaryKeys = {"id"}, ignoredColumns = {"inWishList"})
public class AnimeWishEntity extends AnimeWishSearchResultModel {
    public AnimeWishEntity(String id, String poster, String title) {
        super(id, poster, title);
    }
}
