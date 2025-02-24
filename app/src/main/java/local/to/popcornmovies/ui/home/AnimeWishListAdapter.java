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
import local.to.popcornmovies.databinding.AnimeWishResultCardBinding;
import local.to.popcornmovies.models.AnimeWishSearchResultModel;
import local.to.popcornmovies.utils.OkHttpUtil;

public class AnimeWishListAdapter extends ListAdapter<AnimeWishSearchResultModel, AnimeWishListAdapter.AnimeWishResultViewHolder>{

    private final MainViewModel _mainViewModel;
    public final OnWishItemClick onWishItemClick;
    private final Handler _handler;

    public AnimeWishListAdapter(MainViewModel mainViewModel, OnWishItemClick onWishItemClick, Handler handler) {
        super(new SearchResultsDiffutil());
        this._mainViewModel = mainViewModel;
        this.onWishItemClick = onWishItemClick;
        this._handler = handler;
    }

    @NonNull
    @Override
    public AnimeWishResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnimeWishResultViewHolder(AnimeWishResultCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeWishResultViewHolder holder, int position) {
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


    public class AnimeWishResultViewHolder extends RecyclerView.ViewHolder {

        protected final AnimeWishResultCardBinding _binding;

        public AnimeWishResultViewHolder(@NonNull AnimeWishResultCardBinding binding) {
            super(binding.getRoot());
            this._binding = binding;
            this._binding.setWishAdapter(AnimeWishListAdapter.this);
            this._binding.setWishViewHolder(this);
        }

        public void bind(AnimeWishSearchResultModel data) {
            if(data.inWishList == null) data.inWishList = Boolean.valueOf(true);
            _binding.setData(data);
            _binding.searchResultsCardThreeDotsLoader.loader.setVisibility(View.VISIBLE);
            _binding.searchResultPosterImageView.setVisibility(View.INVISIBLE);
            AnimeWishListAdapter.this._mainViewModel.executor.execute(()-> {
                Bitmap image = OkHttpUtil.getInstance(this._binding.getRoot().getContext()).getBitmap(data.poster);
                AnimeWishListAdapter.this._handler.post(()-> {
                    _binding.searchResultsCardThreeDotsLoader.loader.setVisibility(View.GONE);
                    if (image != null) _binding.searchResultPosterImageView.setImageBitmap(image);
                    _binding.searchResultPosterImageView.setVisibility(View.VISIBLE);
                });
            });
        }

        public void removeFromWish(AnimeWishSearchResultModel data){
            AnimeWishListAdapter.this._mainViewModel.removeFromWish(data);
        }
    }

    public interface OnWishItemClick{public void apply(AnimeWishSearchResultModel model);}
}
