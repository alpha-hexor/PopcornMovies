package local.to.popcornmovies.rooom_database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "seasons",
        primaryKeys = {"tmdbId"},
        foreignKeys = @ForeignKey(entity = SeriesEntity.class,
                                    parentColumns = "tmdbId",
                                    childColumns = "tmdbId",
                                    onDelete = ForeignKey.RESTRICT,
                                    onUpdate = ForeignKey.CASCADE)
)
public class SeasonEntity {
    @NonNull
    public final String tmdbId;
    public final int seasonCount;

    public SeasonEntity(@NonNull String tmdbId, int seasonCount) {
        this.tmdbId = tmdbId;
        this.seasonCount = seasonCount;
    }
}


