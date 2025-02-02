package local.to.popcornmovies.ui.details;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import local.to.popcornmovies.fragments.details.Episodes;
import local.to.popcornmovies.models.Episode;
import local.to.popcornmovies.models.Season;

public class EpisodeViewPagerAdapter extends FragmentStateAdapter {

    private final ArrayList<Season> seasonData;
    private final OnSeasonEpisodeClickListener onEpisodeClickListener;

    public EpisodeViewPagerAdapter(@NonNull Fragment fragment, ArrayList<Season> seasonData, OnSeasonEpisodeClickListener onEpisodeClickListener) {
        super(fragment);
        this.seasonData = seasonData;
        this.onEpisodeClickListener = onEpisodeClickListener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new Episodes(seasonData.get(position),episode -> {
            this.onEpisodeClickListener.apply(seasonData.get(position),episode);
        });
    }

    @Override
    public int getItemCount() {
        return seasonData.size();
    }

    public interface OnSeasonEpisodeClickListener{public void apply(Season season, Episode episode);}
}
