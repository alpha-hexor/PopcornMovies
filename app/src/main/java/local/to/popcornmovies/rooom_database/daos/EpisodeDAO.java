package local.to.popcornmovies.rooom_database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import local.to.popcornmovies.rooom_database.entities.EpisodeEntity;

@Dao
public interface EpisodeDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(EpisodeEntity episode);

    @Query("update episodes set watchPercentage=:watchPercentage where tmdbId=:tmdbId and seasonNumber=:seasonNumber and episodeNumber=:episodeNumber")
    int update(String tmdbId, int seasonNumber, int episodeNumber, float watchPercentage);

    @Query("select * from episodes where tmdbId=:tmdbId and seasonNumber=:seasonNumber and episodeNumber=:episodeNumber")
    EpisodeEntity get(String tmdbId, int seasonNumber, int episodeNumber);
}
