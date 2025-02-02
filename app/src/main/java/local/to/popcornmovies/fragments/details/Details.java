package local.to.popcornmovies.fragments.details;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.io.Serializable;
import java.util.ArrayList;

import local.to.popcornmovies.MainViewModel;
import local.to.popcornmovies.R;
import local.to.popcornmovies.databinding.FragmentDetailsBinding;
import local.to.popcornmovies.models.Episode;
import local.to.popcornmovies.models.Season;
import local.to.popcornmovies.models.TrendingSearchWishResultModel;
import local.to.popcornmovies.rooom_database.entities.MovieEntity;
import local.to.popcornmovies.ui.details.EpisodeViewPagerAdapter;
import local.to.popcornmovies.ui.details.SeasonListAdapter;
import local.to.popcornmovies.utils.OkHttpUtil;

public class Details extends Fragment {

    public static final String TAG = "test->DetailsFrag";

    private FragmentDetailsBinding _binding;
    private MainViewModel _mainViewModel;
    private Handler handler;
    private NavController _navController;
    private TrendingSearchWishResultModel _show;

    private EpisodeViewPagerAdapter _episodeViewPagerAdapter;
    private SeasonListAdapter _seasonListAdapter;
    private RecyclerView.State _seasonRecyclerViewState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this._binding = FragmentDetailsBinding.inflate(inflater, container, false);
        return this._binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        this.initVariables();
        this.initObservers();
        this.initListeners();
        this.initViews();
        this._mainViewModel.getMovieOrSeasonData(this._show.mediaLink);
    }

    private void initVariables() {
        this._binding.setFragmentDetails(this);
        Serializable serializableShow = getArguments().getSerializable("show");
        if(serializableShow instanceof TrendingSearchWishResultModel) this.initShow((TrendingSearchWishResultModel) serializableShow);
        else {
            this._navController.navigateUp();
            return;
        }
        this._mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        this._navController = findNavController(this);
        this.handler = new Handler(getActivity().getMainLooper());
        this._seasonRecyclerViewState = new RecyclerView.State();
    }

    private void initObservers() {
        if(this._mainViewModel.linkUtils.getValue().isMovie(this._show.mediaLink)) this._mainViewModel.movieData.observe(getViewLifecycleOwner(),this::onMovieData);
        else this._mainViewModel.seasonData.observe(getViewLifecycleOwner(),this::onSeasonData);
    }

    private void initListeners() {
    }

    private void initViews() {
        this._mainViewModel.executor.execute(()->{
            Bitmap image = OkHttpUtil.getInstance(this._binding.getRoot().getContext()).getBitmap(this._show.poster);
            this.handler.post(()->{
                this._binding.detailsRootLayout.setBackground(new BitmapDrawable(getResources(),image));
            });
        });
    }

    private void initShow(TrendingSearchWishResultModel show){
        this._show = show;
    }

    private void onSeasonData(ArrayList<Season> seasonData) {
        if(seasonData == null) return;
        if(seasonData.isEmpty()) {
            if(this._mainViewModel.networkState.getValue())
                Toast.makeText(getContext(),getString(R.string.no_data_available),Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(getContext(), getString(R.string.internet_down), Toast.LENGTH_LONG).show();
                this._mainViewModel.networkState.observe(getViewLifecycleOwner(),state->{
                    if(state) {
                        this._mainViewModel.networkState.removeObservers(getViewLifecycleOwner());
                        this._mainViewModel.getMovieOrSeasonData(this._show.mediaLink);
                    }
                });
            }
            return;
        }
        this._mainViewModel.networkState.removeObservers(getViewLifecycleOwner());
        this.initSeasonView(seasonData);
        this._binding.detailsThreeDotsLoader.loader.setVisibility(View.GONE);
        this._binding.seriesContainer.getRoot().setVisibility(View.VISIBLE);
    }

    private void onMovieData(MovieEntity movieData) {
        this._binding.movieContainer.detailsMovieWatchButton.setOnClickListener(v->this.onWatchMovie(movieData));
        this._binding.detailsThreeDotsLoader.loader.setVisibility(View.GONE);
        this._binding.movieContainer.getRoot().setVisibility(View.VISIBLE);
    }

    private void onWatchMovie(MovieEntity movie) {
        if(movie == null) {
            Toast.makeText(getContext(),"Unable to find tmdbId",Toast.LENGTH_LONG).show();
            return;
        }
        NavDirections action = DetailsDirections.actionDetailsToPlayer(false,0,0,movie.tmdbId,movie.watchPercentage);
        this._navController.navigate(action);
    }

    private void initSeasonView(ArrayList<Season> seasonData){
        this._episodeViewPagerAdapter = new EpisodeViewPagerAdapter(this,seasonData,this::onSeasonEpisodeSelected);
        this._seasonListAdapter = new SeasonListAdapter(this::onSelectSeason,seasonData.size());
        this._binding.seriesContainer.detailsEpisodeViewPager.setAdapter(this._episodeViewPagerAdapter);
        this._binding.seriesContainer.detailsSeasonDataRecyclerView.setAdapter(this._seasonListAdapter);
        this._binding.seriesContainer.detailsEpisodeViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Details.this._seasonListAdapter.setSelected(position);
                Details.this._binding.seriesContainer.detailsSeasonDataRecyclerView.getLayoutManager().smoothScrollToPosition(
                        Details.this._binding.seriesContainer.detailsSeasonDataRecyclerView,
                        Details.this._seasonRecyclerViewState,
                        position
                );
            }
        });
    }

    private void onSeasonEpisodeSelected(Season season, Episode episode) {
        Log.d(TAG,"Selected series : "+season+"\n"+episode);
        NavDirections action = DetailsDirections.actionDetailsToPlayer(true,season.seasonNumber,episode.episodeNumber,season.tmdbId,episode.watchPercentage);
        this._navController.navigate(action);
    }

    private void onSelectSeason(int season) {
        this._seasonListAdapter.setSelected(season);
        this._binding.seriesContainer.detailsEpisodeViewPager.setCurrentItem(season-1);
    }

    @Override
    public void onDestroyView() {
        this._binding = null;
        this.removeObservers();
        super.onDestroyView();
    }

    private void removeObservers() {
        if(this._mainViewModel == null) return;
        this._mainViewModel.movieData.removeObservers(getViewLifecycleOwner());
        this._mainViewModel.seasonData.removeObservers(getViewLifecycleOwner());
    }
}