package local.to.popcornmovies.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Season {
    public final String tmdbId;
    public int seasonNumber;
    public ArrayList<Episode> episodes;

    public Season(String tmdbId, int seasonNumber){
        this.tmdbId = tmdbId;
        this.seasonNumber = seasonNumber;
        this.episodes = new ArrayList<Episode>(0);
    }

    @NonNull
    @Override
    public String toString() {
        return "Season{"+ "tmdbId=" + tmdbId +","+
                "seasonNumber=" + seasonNumber +","+
                "episodes=" + episodes +","+
                '}';
    }
}
