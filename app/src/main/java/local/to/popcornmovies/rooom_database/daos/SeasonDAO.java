package local.to.popcornmovies.rooom_database.daos;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import local.to.popcornmovies.models.SeasonEpisode;
import local.to.popcornmovies.rooom_database.entities.SeasonEntity;

@Dao
public interface SeasonDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SeasonEntity season);

    @Query("UPDATE seasons SET seasonCount = :newSeasonCount WHERE tmdbId = :tmdbId")
    int update(String tmdbId, int newSeasonCount);

    @Query("SELECT * FROM seasons WHERE tmdbId = :tmdbId")
    SeasonEntity get(String tmdbId);

    @Query("SELECT seasons.tmdbId as tmdbId," +
            "        episodes.seasonNumber as seasonNumber," +
            "        episodes.episodeNumber as episodeNumber," +
            "        episodes.watchPercentage as watchPercentage" +
            " FROM seasons join episodes WHERE " +
            "       seasons.tmdbId = episodes.tmdbId AND" +
            "        seasons.tmdbId = :seriesTmdbId")
    List<SeasonEpisode> getSeasonBySeriesTmdbId(String seriesTmdbId);

    @Query("Select seasonCount from seasons where tmdbId = :tmdbId")
    int getSeasonCount(String tmdbId);

}
