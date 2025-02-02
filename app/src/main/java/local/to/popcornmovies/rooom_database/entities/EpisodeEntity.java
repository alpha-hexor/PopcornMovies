package local.to.popcornmovies.rooom_database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "episodes",
        primaryKeys = {"tmdbId", "seasonNumber", "episodeNumber"},
        foreignKeys = @ForeignKey(entity = SeasonEntity.class,
                                    parentColumns = {"tmdbId"},
                                    childColumns = {"tmdbId"},
                                    onDelete = ForeignKey.CASCADE,
                                    onUpdate = ForeignKey.CASCADE)
)
public class EpisodeEntity {
    @NonNull
    public final String tmdbId;
    public final int seasonNumber;
    public final int episodeNumber;
    public final float watchPercentage;

    public EpisodeEntity(@NonNull String tmdbId, int seasonNumber, int episodeNumber, float watchPercentage) {
        this.tmdbId = tmdbId;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.watchPercentage = watchPercentage;
    }
}
