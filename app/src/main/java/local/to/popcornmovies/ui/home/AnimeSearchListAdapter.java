package local.to.popcornmovies.ui.home;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import local.to.popcornmovies.MainViewModel;
import local.to.popcornmovies.databinding.AnimeSearchResultCardBinding;
import local.to.popcornmovies.models.AnimeWishSearchResultModel;
import local.to.popcornmovies.utils.OkHttpUtil;

public class AnimeSearchListAdapter extends ListAdapter<AnimeWishSearchResultModel, AnimeSearchListAdapter.AnimeSearchResultViewHolder>{

    private final MainViewModel _mainViewModel;
    public final OnSearchItemClick onSearchItemClick;
    private final Handler _handler;

    public AnimeSearchListAdapter(MainViewModel mainViewModel, OnSearchItemClick onSearchItemClick, Handler handler) {
        super(new SearchResultsDiffutil());
        this._mainViewModel = mainViewModel;
        this.onSearchItemClick = onSearchItemClick;
        this._handler = handler;
    }

    @NonNull
    @Override
    public AnimeSearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnimeSearchResultViewHolder(AnimeSearchResultCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeSearchResultViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    private static class SearchResultsDiffutil extends  DiffUtil.ItemCallback<AnimeWishSearchResultModel> {

        @Override
        public boolean areItemsTheSame(@NonNull AnimeWishSearchResultModel oldItem, @NonNull AnimeWishSearchResultModel newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull AnimeWishSearchResultModel oldItem, @NonNull AnimeWishSearchResultModel newItem) {
            return this.areItemsTheSame(oldItem, newItem);
        }
    };

    public class AnimeSearchResultViewHolder extends RecyclerView.ViewHolder {

        protected final AnimeSearchResultCardBinding _binding;

        public AnimeSearchResultViewHolder(@NonNull AnimeSearchResultCardBinding binding) {
            super(binding.getRoot());
            this._binding = binding;
            this._binding.setSearchAdapter(AnimeSearchListAdapter.this);
            this._binding.setSearchViewHolder(this);
        }

        public void bind(AnimeWishSearchResultModel data) {
            _binding.setData(data);
            if(data.inWishList == null) this._binding.searchResultAddToWish.setVisibility(View.GONE);
            else this._binding.searchResultAddToWish.setVisibility(data.inWishList ? View.GONE : View.VISIBLE);
            _binding.searchResultsCardThreeDotsLoader.loader.setVisibility(View.VISIBLE);
            _binding.searchResultPosterImageView.setVisibility(View.INVISIBLE);
            AnimeSearchListAdapter.this._mainViewModel.executor.execute(()-> {
                Bitmap image = OkHttpUtil.getInstance(this._binding.getRoot().getContext()).getBitmap(data.poster);
                AnimeSearchListAdapter.this._handler.post(()-> {
                    _binding.searchResultsCardThreeDotsLoader.loader.setVisibility(View.GONE);
                    if (image != null) _binding.searchResultPosterImageView.setImageBitmap(image);
                    _binding.searchResultPosterImageView.setVisibility(View.VISIBLE);
                });
            });
        }

        public void addToWish(AnimeWishSearchResultModel data) {
            AnimeSearchListAdapter.this._mainViewModel.addToWish(data);
            this._binding.searchResultAddToWish.setVisibility(View.GONE);
        }
    }

    public interface OnSearchItemClick{public void apply(AnimeWishSearchResultModel model);}
}
