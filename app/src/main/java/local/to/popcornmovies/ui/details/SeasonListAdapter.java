package local.to.popcornmovies.ui.details;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import local.to.popcornmovies.R;
import local.to.popcornmovies.databinding.SeasonListItemBinding;

public class SeasonListAdapter extends RecyclerView.Adapter<SeasonListAdapter.SeasonListViewHolder>{

    private final OnSeasonClickListener _onSeasonClickListener;
    private final int seasonCount;
    private int selected = 1;

    public SeasonListAdapter(OnSeasonClickListener onSeasonClickListener, int seasonCount) {
        _onSeasonClickListener = onSeasonClickListener;
        this.seasonCount = seasonCount;
    }

    @NonNull
    @Override
    public SeasonListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SeasonListViewHolder(SeasonListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonListViewHolder holder, int position) {
        holder.bind(position+1);
    }

    @Override
    public int getItemCount() {
        return this.seasonCount;
    }

    public void setSelected(int position) {
        int PreviousSelected = this.selected;
        this.selected = position+1;
        this.notifyItemChanged(this.selected-1);
        this.notifyItemChanged(PreviousSelected-1);
    }

    protected class SeasonListViewHolder extends RecyclerView.ViewHolder {

        SeasonListItemBinding _binding;

        public SeasonListViewHolder(@NonNull SeasonListItemBinding binding) {
            super(binding.getRoot());
            this._binding = binding;
        }

        public void bind(int episodeNumber) {
            this._binding.seasonUnderlineDecoration.setVisibility(SeasonListAdapter.this.selected == episodeNumber?View.VISIBLE: View.GONE);
            this._binding.seasonTextView.setText(_binding.getRoot().getContext().getResources().getString(R.string.season,episodeNumber));
            this._binding.getRoot().setOnClickListener(v->{
                SeasonListAdapter.this._onSeasonClickListener.apply(episodeNumber);
            });
        }
    }

    public interface OnSeasonClickListener{public void apply(int season);}
}
