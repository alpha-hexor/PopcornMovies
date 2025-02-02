package local.to.popcornmovies.rooom_database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class MovieEntity {

    @NonNull
    @PrimaryKey
    public final String mediaLink;
    public final String tmdbId;
    public float watchPercentage;

    public MovieEntity(@NonNull String mediaLink, String tmdbId, float watchPercentage) {
        this.mediaLink = mediaLink;
        this.tmdbId = tmdbId;
        this.watchPercentage = watchPercentage;
    }

    @NonNull
    @Override
    public String toString() {
        return "{\n" +
                "mediaLink : " + this.mediaLink +"\n"+
                "tmdbId : " + this.tmdbId +"\n"+
                "watchPercentage : " + this.watchPercentage +"\n"+
                "}";
    }
}
