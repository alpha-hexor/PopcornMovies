package local.to.popcornmovies.rooom_database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.File;

import local.to.popcornmovies.rooom_database.daos.UrlCacheDAO;
import local.to.popcornmovies.rooom_database.entities.UrlCacheEntity;

@Database(entities = {
        UrlCacheEntity.class
        }, version = 12, exportSchema = false)
public abstract class CacheDatabase extends RoomDatabase {


    public abstract UrlCacheDAO getUrlCacheDAO();

    private static volatile CacheDatabase Instance = null;

    public static CacheDatabase getDataBase(Context context){
        if(Instance!=null) return Instance;
        Instance = Room.databaseBuilder(
                context.getApplicationContext(),
                CacheDatabase.class,
                new File(context.getCacheDir(),"local_to_popcorn_movies_cache").getAbsolutePath()
        )
                .fallbackToDestructiveMigration()
                .build();
        return Instance;
    }

}
