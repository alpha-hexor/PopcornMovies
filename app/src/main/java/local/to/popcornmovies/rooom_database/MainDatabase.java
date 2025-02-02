package local.to.popcornmovies.rooom_database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import local.to.popcornmovies.rooom_database.daos.EpisodeDAO;
import local.to.popcornmovies.rooom_database.daos.MovieDAO;
import local.to.popcornmovies.rooom_database.daos.SeasonDAO;
import local.to.popcornmovies.rooom_database.daos.SeriesDAO;
import local.to.popcornmovies.rooom_database.daos.TrendingDAO;
import local.to.popcornmovies.rooom_database.daos.WishListDAO;
import local.to.popcornmovies.rooom_database.entities.EpisodeEntity;
import local.to.popcornmovies.rooom_database.entities.MovieEntity;
import local.to.popcornmovies.rooom_database.entities.SeasonEntity;
import local.to.popcornmovies.rooom_database.entities.SeriesEntity;
import local.to.popcornmovies.rooom_database.entities.TrendingEntity;
import local.to.popcornmovies.rooom_database.entities.WishEntity;

@Database(entities = {
        TrendingEntity.class,
        WishEntity.class,
        MovieEntity.class,
        SeriesEntity.class,
        SeasonEntity.class,
        EpisodeEntity.class
        }, version = 9, exportSchema = false)
public abstract class MainDatabase extends RoomDatabase {

    public abstract TrendingDAO getTrendingTable();
    public abstract WishListDAO getWishList();
    public abstract MovieDAO getMovieDAO();
    public abstract SeriesDAO getSeriesDAO();
    public abstract SeasonDAO getSeasonDAO();
    public abstract EpisodeDAO getEpisodeDAO();

    private static volatile MainDatabase Instance = null;

    public static MainDatabase getDataBase(Context context){
        if(Instance!=null) return Instance;
        Instance = Room.databaseBuilder(
                context.getApplicationContext(),
                MainDatabase.class,
                "local_to_popcorn_movies"
        )
                .fallbackToDestructiveMigration()
                .build();
        return Instance;
    }

}
