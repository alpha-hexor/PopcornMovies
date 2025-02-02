package local.to.popcornmovies.fragments.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import local.to.popcornmovies.databinding.FragmentEpisodesBinding;
import local.to.popcornmovies.models.Season;
import local.to.popcornmovies.ui.details.EpisodeListAdapter;

public class Episodes extends Fragment {

    private final String TAG = "test->EpisodesFrag";
    FragmentEpisodesBinding _binding;
    private Season episodeCount;
    EpisodeListAdapter _episodeListAdapter;
    private EpisodeListAdapter.OnEpisodeClickListener onEpisodeClickListener;

    public Episodes(){
    }

    public Episodes(Season season, EpisodeListAdapter.OnEpisodeClickListener onEpisodeClickListener) {
        this.episodeCount = season;
        this.onEpisodeClickListener = onEpisodeClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this._binding = FragmentEpisodesBinding.inflate(inflater, container, false);
        return this._binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this._episodeListAdapter = new EpisodeListAdapter(this.onEpisodeClickListener,this.episodeCount);
        this._binding.episodeListRecyclerView.setAdapter(this._episodeListAdapter);
    }

}