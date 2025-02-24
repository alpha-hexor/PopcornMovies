package local.to.popcornmovies.rooom_database.daos;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import local.to.popcornmovies.rooom_database.entities.AnimeEpisodeEntity;

@Dao
public interface AnimeEpisodeDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(AnimeEpisodeEntity episode);

    @Query("update anime_episodes set isDubAvailable=:isDubAvailable where id=:id and episode=:episode")
    int update(String id, String episode, boolean isDubAvailable);

    @Query("update anime_episodes set watchPercentage=:watchPercentage where id=:id and episode=:episode")
    int update(String id, String episode, float watchPercentage);

    @Query("select * from anime_episodes where id=:id and episode=:episode")
    AnimeEpisodeEntity get(@NonNull String id, String episode);

    @Query("select * from anime_episodes where id=:id order by cast(episode AS INTEGER)")
    List<AnimeEpisodeEntity> get(@NonNull String id);

}
