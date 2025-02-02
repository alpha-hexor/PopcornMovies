package local.to.popcornmovies.rooom_database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "series", primaryKeys = {"tmdbId"})
public class SeriesEntity {
    @NonNull
    public final String mediaLink, tmdbId;

    public SeriesEntity(@NonNull String mediaLink, @NonNull String tmdbId) {
        this.mediaLink = mediaLink;
        this.tmdbId = tmdbId;
    }
}

