package local.to.popcornmovies.rooom_database.daos;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import local.to.popcornmovies.rooom_database.entities.WishEntity;

@Dao
public interface WishListDAO {

    @Query("select * from wish_list")
    public List<WishEntity> getAll();

    @Query("delete from wish_list")
    public int deleteAll();

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public long add(WishEntity data);

    @Delete
    public int delete(WishEntity data);

    @Query("select COUNT(*) > 0 from wish_list" +
            " where mediaLink=:mediaLink")
    boolean doHave(String mediaLink);

}
