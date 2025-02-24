package local.to.popcornmovies.rooom_database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import local.to.popcornmovies.rooom_database.entities.UrlCacheEntity;

@Dao
public interface UrlCacheDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UrlCacheEntity data);

    @Query("delete from url_cache")
    public int deleteAll();

    @Query("select * from url_cache where url=:url")
    UrlCacheEntity get(String url);
}
