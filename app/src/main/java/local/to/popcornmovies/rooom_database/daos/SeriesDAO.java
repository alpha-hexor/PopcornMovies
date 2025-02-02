package local.to.popcornmovies.rooom_database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import local.to.popcornmovies.rooom_database.entities.SeriesEntity;

@Dao
public interface SeriesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SeriesEntity series);

    @Query("SELECT * FROM series WHERE mediaLink = :mediaLink")
    SeriesEntity getSeriesByMediaLink(String mediaLink);
}
