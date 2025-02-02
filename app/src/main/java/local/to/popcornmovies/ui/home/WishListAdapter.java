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
import local.to.popcornmovies.databinding.WishResultCardBinding;
import local.to.popcornmovies.models.TrendingSearchWishResultModel;
import local.to.popcornmovies.utils.OkHttpUtil;

public class WishListAdapter extends ListAdapter<TrendingSearchWishResultModel, WishListAdapter.WishResultViewHolder>{

    private final MainViewModel _mainViewModel;
    public final OnWishItemClick onWishItemClick;
    private final Handler _handler;

    public WishListAdapter(MainViewModel mainViewModel, OnWishItemClick onWishItemClick, Handler handler) {
        super(new SearchResultsDiffutil());
        this._mainViewModel = mainViewModel;
        this.onWishItemClick = onWishItemClick;
        this._handler = handler;
    }

    @NonNull
    @Override
    public WishResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WishResultViewHolder(WishResultCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull WishResultViewHolder holder, int position) {
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


    public class WishResultViewHolder extends RecyclerView.ViewHolder {

        protected final WishResultCardBinding _binding;

        public WishResultViewHolder(@NonNull WishResultCardBinding binding) {
            super(binding.getRoot());
            this._binding = binding;
            this._binding.setWishAdapter(WishListAdapter.this);
            this._binding.setWishViewHolder(this);
        }

        public void bind(TrendingSearchWishResultModel data) {
            if(data.inWishList == null) data.inWishList = Boolean.valueOf(true);
            _binding.setData(data);
            _binding.searchResultsCardThreeDotsLoader.loader.setVisibility(View.VISIBLE);
            _binding.searchResultPosterImageView.setVisibility(View.INVISIBLE);
            WishListAdapter.this._mainViewModel.executor.execute(()-> {
                Bitmap image = OkHttpUtil.getInstance(this._binding.getRoot().getContext()).getBitmap(data.poster);
                WishListAdapter.this._handler.post(()-> {
                    _binding.searchResultsCardThreeDotsLoader.loader.setVisibility(View.GONE);
                    if (image != null) _binding.searchResultPosterImageView.setImageBitmap(image);
                    _binding.searchResultPosterImageView.setVisibility(View.VISIBLE);
                });
            });
        }

        public void removeFromWish(TrendingSearchWishResultModel data){
            WishListAdapter.this._mainViewModel.removeFromWish(data);
        }
    }

    public interface OnWishItemClick{public void apply(TrendingSearchWishResultModel model);}
}
