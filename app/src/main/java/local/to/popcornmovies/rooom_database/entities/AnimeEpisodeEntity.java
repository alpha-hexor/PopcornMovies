package local.to.popcornmovies.rooom_database.entities;

import androidx.room.Entity;

import local.to.popcornmovies.models.AnimeEpisode;

@Entity(tableName = "anime_episodes", primaryKeys = {"id","episode"})
public class AnimeEpisodeEntity extends AnimeEpisode {
    public AnimeEpisodeEntity(String id, String episode, boolean isDubAvailable) {
        super(id, episode, isDubAvailable);
    }
}
