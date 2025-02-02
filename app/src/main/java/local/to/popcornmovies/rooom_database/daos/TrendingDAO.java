package local.to.popcornmovies.rooom_database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import local.to.popcornmovies.rooom_database.entities.TrendingEntity;

@Dao
public interface TrendingDAO {

    @Query("select * from trending")
    public List<TrendingEntity> getAll();

    @Query("delete from trending")
    public int deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addAll(List<TrendingEntity> trending);
}
