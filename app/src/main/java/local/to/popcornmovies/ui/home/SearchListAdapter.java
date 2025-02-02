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
import local.to.popcornmovies.databinding.SearchResultCardBinding;
import local.to.popcornmovies.models.TrendingSearchWishResultModel;
import local.to.popcornmovies.utils.OkHttpUtil;

public class SearchListAdapter extends ListAdapter<TrendingSearchWishResultModel, SearchListAdapter.SearchResultViewHolder>{

    private final MainViewModel _mainViewModel;
    public final OnSearchItemClick onSearchItemClick;
    private final Handler _handler;

    public SearchListAdapter(MainViewModel mainViewModel, OnSearchItemClick onSearchItemClick, Handler handler) {
        super(new SearchResultsDiffutil());
        this._mainViewModel = mainViewModel;
        this.onSearchItemClick = onSearchItemClick;
        this._handler = handler;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchResultViewHolder(SearchResultCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    private static class SearchResultsDiffutil extends  DiffUtil.ItemCallback<TrendingSearchWishResultModel> {

        @Override
        public boolean areItemsTheSame(@NonNull TrendingSearchWishResultModel oldItem, @NonNull TrendingSearchWishResultModel newItem) {
            return oldItem.mediaLink.equals(newItem.mediaLink);
        }

        @Override
        public boolean areContentsTheSame(@NonNull TrendingSearchWishResultModel oldItem, @NonNull TrendingSearchWishResultModel newItem) {
            return this.areItemsTheSame(oldItem, newItem);
        }
    };

    public class SearchResultViewHolder extends RecyclerView.ViewHolder {

        protected final SearchResultCardBinding _binding;

        public SearchResultViewHolder(@NonNull SearchResultCardBinding binding) {
            super(binding.getRoot());
            this._binding = binding;
            this._binding.setSearchAdapter(SearchListAdapter.this);
            this._binding.setSearchViewHolder(this);
        }

        public void bind(TrendingSearchWishResultModel data) {
            _binding.setData(data);
            if(data.inWishList == null) this._binding.searchResultAddToWish.setVisibility(View.GONE);
            else this._binding.searchResultAddToWish.setVisibility(data.inWishList ? View.GONE : View.VISIBLE);
            _binding.searchResultsCardThreeDotsLoader.loader.setVisibility(View.VISIBLE);
            _binding.searchResultPosterImageView.setVisibility(View.INVISIBLE);
            SearchListAdapter.this._mainViewModel.executor.execute(()-> {
                Bitmap image = OkHttpUtil.getInstance(this._binding.getRoot().getContext()).getBitmap(data.poster);
                SearchListAdapter.this._handler.post(()-> {
                    _binding.searchResultsCardThreeDotsLoader.loader.setVisibility(View.GONE);
                    if (image != null) _binding.searchResultPosterImageView.setImageBitmap(image);
                    _binding.searchResultPosterImageView.setVisibility(View.VISIBLE);
                });
            });
        }

        public void addToWish(TrendingSearchWishResultModel data) {
            SearchListAdapter.this._mainViewModel.addToWish(data);
            this._binding.searchResultAddToWish.setVisibility(View.GONE);
        }
    }

    public interface OnSearchItemClick{public void apply(TrendingSearchWishResultModel model);}
}
