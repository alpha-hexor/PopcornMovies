package local.to.popcornmovies.models;

import androidx.annotation.NonNull;

public class Episode {
    public final int episodeNumber;
    public float watchPercentage;

    public Episode(int episodeNumber, float watchPercentage) {
        this.episodeNumber = episodeNumber;
        this.watchPercentage = watchPercentage;
    }

    @NonNull
    @Override
    public String toString() {
        return "Episode{" +
                "episodeNumber=" + episodeNumber +","+
                "watchPercentage="+ watchPercentage+","+
                "}";
    }
}