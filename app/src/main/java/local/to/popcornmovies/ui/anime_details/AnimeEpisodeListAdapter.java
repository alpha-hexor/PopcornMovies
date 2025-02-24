package local.to.popcornmovies.ui.anime_details;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import local.to.popcornmovies.R;
import local.to.popcornmovies.databinding.EpisodeListItemBinding;
import local.to.popcornmovies.models.AnimeEpisode;

public class AnimeEpisodeListAdapter extends RecyclerView.Adapter<AnimeEpisodeListAdapter.EpisodeListViewHolder>{

    private final ArrayList<AnimeEpisode> list;
    private final OnAnimeEpisodeClick _onAnimeEpisodeClick;

    public AnimeEpisodeListAdapter(ArrayList<AnimeEpisode> list, OnAnimeEpisodeClick onAnimeEpisodeClick) {
        this.list = list;
        this._onAnimeEpisodeClick = onAnimeEpisodeClick;
    }

    @NonNull
    @Override
    public EpisodeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EpisodeListViewHolder(EpisodeListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeListViewHolder holder, int position) {
        holder.bind(this.list.get(position));
    }

    @Override
    public int getItemCount() {
        return this.list == null ? 0 : this.list.size();
    }

    protected class EpisodeListViewHolder extends RecyclerView.ViewHolder {

        private final EpisodeListItemBinding _binding;

        public EpisodeListViewHolder(@NonNull EpisodeListItemBinding binding) {
            super(binding.getRoot());
            this._binding = binding;
        }

        public void bind(AnimeEpisode animeEpisode) {
            this._binding.episodeTextView.setText(_binding.getRoot().getContext().getResources().getString(R.string.episode_str, animeEpisode.episode));
            this._binding.getRoot().setOnClickListener(v->{
                AnimeEpisodeListAdapter.this._onAnimeEpisodeClick.apply(animeEpisode);
            });
            LinearLayout.LayoutParams progressNegativeParams = (LinearLayout.LayoutParams) this._binding.progress.progressNegative.getLayoutParams();
            progressNegativeParams.weight = 1- animeEpisode.watchPercentage;
            this._binding.progress.progressNegative.setLayoutParams(progressNegativeParams);

            LinearLayout.LayoutParams progressPositiveParams = (LinearLayout.LayoutParams) this._binding.progress.progressPositive.getLayoutParams();
            progressPositiveParams.weight = animeEpisode.watchPercentage;
            this._binding.progress.progressPositive.setLayoutParams(progressPositiveParams);
        }
    }

    public interface OnAnimeEpisodeClick{public void apply(AnimeEpisode episode);}
}
