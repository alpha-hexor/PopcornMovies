package local.to.popcornmovies.rooom_database.daos;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import local.to.popcornmovies.rooom_database.entities.AnimeWishEntity;

@Dao
public interface AnimeWishListDAO {

    @Query("select * from anime_wish")
    public List<AnimeWishEntity> getAll();

    @Query("delete from anime_wish")
    public int deleteAll();

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public long add(AnimeWishEntity data);

    @Delete
    public int delete(AnimeWishEntity data);

    @Query("select COUNT(*) > 0 from anime_wish" +
            " where id=:id")
    boolean doHave(String id);

}
