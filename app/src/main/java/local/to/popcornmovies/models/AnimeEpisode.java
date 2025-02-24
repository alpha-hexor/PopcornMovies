package local.to.popcornmovies.models;

import androidx.annotation.NonNull;

public class AnimeEpisode {

    @NonNull
    public final String id, episode;
    public final boolean isDubAvailable;
    public float watchPercentage = 0f;

    public AnimeEpisode(String id, String episode, boolean isDubAvailable) {
        this.id = id;
        this.episode = episode;
        this.isDubAvailable = isDubAvailable;
    }

    public AnimeEpisode(String id, String episode, boolean isDubAvailable, float watchPercentage) {
        this.id = id;
        this.episode = episode;
        this.isDubAvailable = isDubAvailable;
        this.watchPercentage=watchPercentage;
    }

    @Override
    public String toString() {
        return "{\n" +
                "id : " + this.id + "\n" +
                "episode : " + this.episode + "\n" +
                "isDubAvailable : " + this.isDubAvailable + "\n" +
                "watchPercentage : " + this.watchPercentage + "\n" +
                "}";
    }
}
