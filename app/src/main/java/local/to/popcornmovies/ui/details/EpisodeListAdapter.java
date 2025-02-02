package local.to.popcornmovies.ui.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import local.to.popcornmovies.R;
import local.to.popcornmovies.databinding.EpisodeListItemBinding;
import local.to.popcornmovies.models.Episode;
import local.to.popcornmovies.models.Season;

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.EpisodeListViewHolder>{

    private final OnEpisodeClickListener _onEpisodeClickListener;
    private final Season season;

    public EpisodeListAdapter(OnEpisodeClickListener onEpisodeClickListener, Season season) {
        _onEpisodeClickListener = onEpisodeClickListener;
        this.season = season;
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
        holder.bind(this.season.episodes.get(position));
    }

    @Override
    public int getItemCount() {
        return this.season == null ? 0 : this.season.episodes.size();
    }

    protected class EpisodeListViewHolder extends RecyclerView.ViewHolder{
        EpisodeListItemBinding _binding;
        public EpisodeListViewHolder(@NonNull EpisodeListItemBinding binding) {
            super(binding.getRoot());
            this._binding = binding;
        }

        public void bind(Episode episode) {
            this._binding.episodeTextView.setText(_binding.getRoot().getContext().getResources().getString(R.string.episode,episode.episodeNumber));
            this._binding.getRoot().setOnClickListener(v->{
                EpisodeListAdapter.this._onEpisodeClickListener.apply(episode);
            });
            LinearLayout.LayoutParams progressNegativeParams = (LinearLayout.LayoutParams) this._binding.progress.progressNegative.getLayoutParams();
            progressNegativeParams.weight = 1-episode.watchPercentage;
            this._binding.progress.progressNegative.setLayoutParams(progressNegativeParams);

            LinearLayout.LayoutParams progressPositiveParams = (LinearLayout.LayoutParams) this._binding.progress.progressPositive.getLayoutParams();
            progressPositiveParams.weight = episode.watchPercentage;
            this._binding.progress.progressPositive.setLayoutParams(progressPositiveParams);
        }
    }

    public interface OnEpisodeClickListener{public void apply(Episode episode);}
}
