package local.to.popcornmovies.fragments.home;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.HeroCarouselStrategy;

import java.util.ArrayList;

import local.to.popcornmovies.MainViewModel;
import local.to.popcornmovies.R;
import local.to.popcornmovies.databinding.FragmentHomeBinding;
import local.to.popcornmovies.models.TrendingSearchWishResultModel;
import local.to.popcornmovies.ui.home.WishListAdapter;
import local.to.popcornmovies.ui.home.SearchListAdapter;
import local.to.popcornmovies.ui.home.TrendingListAdapter;

public class Home extends Fragment {

    private static final String TAG = "test->HomeFrag";

    private FragmentHomeBinding _binding;
    private SearchListAdapter _searchListAdapter;
    private TrendingListAdapter _trendingListAdapter;
    private WishListAdapter _wishListAdapter;
    private MainViewModel _mainViewModel;
    private Handler handler;
    private InputMethodManager _inputMethodManager;
    private NavController _navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _binding = FragmentHomeBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initVariables();
        this.initObservers();
        this.initListeners();
    }

    private void initVariables() {
        this._binding.setFragmentHome(this);
        this._mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        this.handler = new Handler(getActivity().getMainLooper());
        this._searchListAdapter = new SearchListAdapter(this._mainViewModel,this::onSearchItemClick, this.handler);
        this._wishListAdapter = new WishListAdapter(this._mainViewModel,this::onWishItemClick, this.handler);
        this._trendingListAdapter = new TrendingListAdapter(this._mainViewModel,this::onTrendingItemClick, this.handler);
        this._binding.searchResultListRecyclerView.setAdapter(this._searchListAdapter);
        this._binding.trendingResultListRecyclerView.setAdapter(this._trendingListAdapter);
        this._binding.wishListRecyclerView.setAdapter(this._wishListAdapter);
        this._binding.trendingResultListRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager trendingLayoutManager = this._binding.trendingResultListRecyclerView.getLayoutManager();
        if(trendingLayoutManager instanceof CarouselLayoutManager)
            this.setTrendingCarouselLayoutManager((CarouselLayoutManager) trendingLayoutManager);
        this._inputMethodManager = getSystemService(getActivity(), InputMethodManager.class);
        this._navController = findNavController(this);
    }

    private void initObservers() {
        this._mainViewModel.linkUtils.observe(getViewLifecycleOwner(),linkUtils -> {
            if(linkUtils == null) return;
            if(this._mainViewModel.trendingResults.getValue() == null) this._mainViewModel.getTrending();
            else if(this._mainViewModel.trendingResults.getValue().isEmpty()) this._mainViewModel.getTrending();
        });
        this._mainViewModel.mainDatabase.observe(getViewLifecycleOwner(),mainDatabase -> {
            if(mainDatabase==null) return;
            this._mainViewModel.getAllWish();
        });
        this._mainViewModel.searchResults.observe(getViewLifecycleOwner(),this::onSearchResult);
        this._mainViewModel.trendingResults.observe(getViewLifecycleOwner(),this::onTrendingResult);
        this._mainViewModel.networkState.observe(getViewLifecycleOwner(),this::onNetworkAvailable);
        this._mainViewModel.wish.observe(getViewLifecycleOwner(),this::onWishResult);
        this._mainViewModel.movieData.postValue(null);
        this._mainViewModel.seasonData.postValue(null);
    }

    private void initListeners() {
        this._binding.searchEditTextView.setOnKeyListener((v,keyCode,event)-> {
            this.onSearchButtonClick();
            return true;
        });
    }


    private void onNetworkAvailable(Boolean aBoolean) {
        if(!aBoolean) return;
        if(this._mainViewModel.trendingResults.getValue() == null)  this._mainViewModel.getTrending();
        else if(this._mainViewModel.trendingResults.getValue().isEmpty()) this._mainViewModel.getTrending();
        if(this._binding.searchEditTextView.getText().toString().length()>0 && this._mainViewModel.searchResults.getValue() == null)
            this.onSearchButtonClick();
        else if(this._binding.searchEditTextView.getText().toString().length()>0 && this._mainViewModel.searchResults.getValue().isEmpty())
            this.onSearchButtonClick();
    }

    private void setTrendingCarouselLayoutManager(CarouselLayoutManager trendingLayoutManager) {
        HeroCarouselStrategy heroCarouselStrategy = new HeroCarouselStrategy();
        trendingLayoutManager.setCarouselStrategy(heroCarouselStrategy);
    }

    public void onSearchButtonClick(){
        this.hideKeyboard(this._binding.searchEditTextView);
        if(this._binding.searchEditTextView.getText().toString().length()<=0) return;
        this._binding.searchResultListContainer.setVisibility(View.VISIBLE);
        this._binding.searchHeaderTextView.setText(R.string.searching);
        this._binding.searchingLoader.loader.setVisibility(View.VISIBLE);
        this._mainViewModel.search(this._binding.searchEditTextView.getText().toString());
    }

    private void onSearchResult(ArrayList<TrendingSearchWishResultModel> searchResult) {
        if(this._searchListAdapter ==null || searchResult == null) {
            this._binding.searchResultListContainer.setVisibility(View.GONE);
            return;
        }
        this._binding.searchResultListContainer.setVisibility(View.VISIBLE);
        if(searchResult.size()<=0) this._binding.searchHeaderTextView.setText(R.string.no_search_results);
        else this._binding.searchHeaderTextView.setText(R.string.search_results);
        this._binding.searchingLoader.loader.setVisibility(View.GONE);
        this._searchListAdapter.submitList(searchResult,this._searchListAdapter::notifyDataSetChanged);
    }

    private void onTrendingResult(ArrayList<TrendingSearchWishResultModel> trendingResult) {
        if(this._trendingListAdapter ==null || trendingResult == null) return;
        this._trendingListAdapter.submitList(trendingResult);
        this._binding.trendingLoader.loader.setVisibility(View.GONE);
    }

    private void onWishResult(ArrayList<TrendingSearchWishResultModel> wishResult){
        if(wishResult == null) {
            this._binding.wishListContainer.setVisibility(View.GONE);
            return;
        }
        else if(wishResult.isEmpty()) {
            this._binding.wishListContainer.setVisibility(View.GONE);
            return;
        }
        this._binding.wishListContainer.setVisibility(View.VISIBLE);
        this._wishListAdapter.submitList(wishResult,this._wishListAdapter::notifyDataSetChanged);
    }

    private void onSearchItemClick(TrendingSearchWishResultModel searchResultModel) {
        this.goToDetailsScreen(searchResultModel);
    }

    private void onTrendingItemClick(TrendingSearchWishResultModel trendingResultModel) {
        if(trendingResultModel.inWishList==null) trendingResultModel.inWishList = Boolean.valueOf(true);
        this.goToDetailsScreen(trendingResultModel);
    }

    private void onWishItemClick(TrendingSearchWishResultModel wishlistResultModel) {
        this.goToDetailsScreen(wishlistResultModel);
    }

    private void goToDetailsScreen(TrendingSearchWishResultModel trendingSearchWishResultModel) {
        NavDirections action = HomeDirections.actionHomeToDetails(trendingSearchWishResultModel);
        this._navController.navigate(action);
    }

    private void hideKeyboard(View view) {
        this._inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onDestroyView() {
        this._binding = null;
        this.removeObservers();
        super.onDestroyView();
    }

    private void removeObservers() {
        this._mainViewModel.linkUtils.removeObservers(getViewLifecycleOwner());
        this._mainViewModel.searchResults.removeObservers(getViewLifecycleOwner());
        this._mainViewModel.trendingResults.removeObservers(getViewLifecycleOwner());
        this._mainViewModel.wish.removeObservers(getViewLifecycleOwner());
        this._mainViewModel.mainDatabase.removeObservers(getViewLifecycleOwner());
    }
}