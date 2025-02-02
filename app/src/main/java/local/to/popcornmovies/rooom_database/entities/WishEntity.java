package local.to.popcornmovies.rooom_database.entities;

import androidx.room.Entity;

import local.to.popcornmovies.models.TrendingSearchWishResultModel;

@Entity(tableName = "wish_list", primaryKeys = {"mediaLink"}, ignoredColumns = {"inWishList"})
public class WishEntity extends TrendingSearchWishResultModel {
    public WishEntity(String title, String mediaLink, String poster) {
        super(title, mediaLink, poster);
    }
}
