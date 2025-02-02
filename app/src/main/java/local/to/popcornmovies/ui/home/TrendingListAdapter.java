package local.to.popcornmovies.ui.home;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import local.to.popcornmovies.MainViewModel;
import local.to.popcornmovies.databinding.TrendingResultCardBinding;
import local.to.popcornmovies.models.TrendingSearchWishResultModel;
import local.to.popcornmovies.utils.OkHttpUtil;

public class TrendingListAdapter extends RecyclerView.Adapter<TrendingListAdapter.TrendingResultViewHolder>{

    private final MainViewModel _mainViewModel;
    private final OnTrendingItemClick onSearchItemClick;
    private ArrayList<TrendingSearchWishResultModel> _list;
    private final Handler _handler;

    public TrendingListAdapter(MainViewModel mainViewModel, OnTrendingItemClick onSearchItemClick, Handler handler) {
        super();
        this._mainViewModel = mainViewModel;
        this.onSearchItemClick = onSearchItemClick;
        this._handler = handler;
    }

    @NonNull
    @Override
    public TrendingResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrendingResultViewHolder(TrendingResultCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingResultViewHolder holder, int position) {
        if(this._list == null) return;
        else if(this._list.isEmpty()) return;
        holder.bind(this._list.get(position%this._list.size()));
    }


    @Override
    public int getItemCount() {
        return this._list == null?0:this._list.size();
    }


    protected class TrendingResultViewHolder extends RecyclerView.ViewHolder {

        protected final TrendingResultCardBinding _binding;

        public TrendingResultViewHolder(@NonNull TrendingResultCardBinding binding) {
            super(binding.getRoot());
            this._binding = binding;
        }

        public void bind(TrendingSearchWishResultModel model) {
            if(model.inWishList ==null) this._binding.trendingWishImageView.setVisibility(View.GONE);
            else this._binding.trendingWishImageView.setVisibility(model.inWishList ? View.GONE:View.VISIBLE);
            _binding.trendingResultsCardThreeDotsLoaderContainer.loader.setVisibility(View.VISIBLE);
            _binding.trendingResultPosterImageView.setVisibility(View.INVISIBLE);
            this._binding.trendingWishImageView.setOnClickListener(view->{
                if(!model.inWishList) TrendingListAdapter.this._mainViewModel.addToWish(model);
                model.inWishList = true;
                _binding.trendingWishImageView.setVisibility(View.GONE);
            });
            this._binding.trendingResultCardView.setOnClickListener(v-> TrendingListAdapter.this.onSearchItemClick.apply(model));
            TrendingListAdapter.this._mainViewModel.executor.execute(()-> {
                Bitmap image = OkHttpUtil.getInstance(this._binding.getRoot().getContext()).getBitmap(model.poster);
                TrendingListAdapter.this._handler.post(()-> {
                    _binding.trendingResultPosterImageView.setVisibility(View.VISIBLE);
                    _binding.trendingResultsCardThreeDotsLoaderContainer.loader.setVisibility(View.GONE);
                    if (image != null) _binding.trendingResultPosterImageView.setImageBitmap(image);
                });
            });
        }
    }


    public void submitList(ArrayList<TrendingSearchWishResultModel> list) {
        this._list = list;
        this.notifyDataSetChanged();
    }


    public interface OnTrendingItemClick{public void apply(TrendingSearchWishResultModel model);}
}
