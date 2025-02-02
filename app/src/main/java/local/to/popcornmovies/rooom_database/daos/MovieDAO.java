package local.to.popcornmovies.rooom_database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import local.to.popcornmovies.rooom_database.entities.MovieEntity;

@Dao
public interface MovieDAO {

    @Query("select * from movies where mediaLink=:mediaLink")
    MovieEntity getMovie(String mediaLink);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long add(MovieEntity movieEntity);

    @Query("update movies set watchPercentage=:watchPercentage where tmdbId=:tmdbId")
    int update(String tmdbId, float watchPercentage);
}
