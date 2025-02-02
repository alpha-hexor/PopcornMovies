package local.to.popcornmovies.rooom_database.entities;

import androidx.room.Entity;

import local.to.popcornmovies.models.TrendingSearchWishResultModel;

@Entity(tableName = "trending", primaryKeys = {"mediaLink"}, ignoredColumns = {"inWishList"})
public class TrendingEntity extends TrendingSearchWishResultModel {
    public TrendingEntity(String title, String mediaLink, String poster) {
        super(title, mediaLink, poster);
    }
}
