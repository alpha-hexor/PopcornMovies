package local.to.popcornmovies;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import local.to.popcornmovies.models.AnimeEpisode;
import local.to.popcornmovies.models.AnimeStreamingSource;
import local.to.popcornmovies.models.AnimeWishSearchResultModel;
import local.to.popcornmovies.models.Episode;
import local.to.popcornmovies.models.QualityParsedModel;
import local.to.popcornmovies.models.Season;
import local.to.popcornmovies.models.SeasonEpisode;
import local.to.popcornmovies.models.StreamingModel;
import local.to.popcornmovies.models.TrendingSearchWishResultModel;
import local.to.popcornmovies.rooom_database.MainDatabase;
import local.to.popcornmovies.rooom_database.entities.AnimeEpisodeEntity;
import local.to.popcornmovies.rooom_database.entities.AnimeWishEntity;
import local.to.popcornmovies.rooom_database.entities.EpisodeEntity;
import local.to.popcornmovies.rooom_database.entities.MovieEntity;
import local.to.popcornmovies.rooom_database.entities.SeasonEntity;
import local.to.popcornmovies.rooom_database.entities.SeriesEntity;
import local.to.popcornmovies.rooom_database.entities.TrendingEntity;
import local.to.popcornmovies.rooom_database.entities.WishEntity;
import local.to.popcornmovies.utils.AllAnimeLinkUtils;
import local.to.popcornmovies.utils.M3U8_QualityParser;
import local.to.popcornmovies.utils.OkHttpUtil;
import local.to.popcornmovies.utils.PopcornMoviesLinkUtils;
import local.to.popcornmovies.utils.SubtitleConfigurationUtils;

public class MainViewModel extends ViewModel {

    public static final String TAG = "test->MainVwMdl";

    public final ExecutorService
            executor = Executors.newFixedThreadPool(4);

    public MutableLiveData<Boolean> networkState
            = new MutableLiveData<Boolean>(false);

    public MutableLiveData<PopcornMoviesLinkUtils> popcornMoviesLinkUtil
            = new MutableLiveData<PopcornMoviesLinkUtils>();

    public MutableLiveData<AllAnimeLinkUtils> allAnimeLinkUtil
            = new MutableLiveData<AllAnimeLinkUtils>();

    public MutableLiveData<MainDatabase> mainDatabase
            = new MutableLiveData<MainDatabase>();

    public MutableLiveData<ArrayList<TrendingSearchWishResultModel>>
            searchResults = new MutableLiveData<ArrayList<TrendingSearchWishResultModel>>(),
            trendingResults = new MutableLiveData<ArrayList<TrendingSearchWishResultModel>>(),
            wish = new MutableLiveData<ArrayList<TrendingSearchWishResultModel>>();

    public MutableLiveData<ArrayList<AnimeWishSearchResultModel>>
            animeSearchResults = new MutableLiveData<ArrayList<AnimeWishSearchResultModel>>(),
            animeWish = new MutableLiveData<ArrayList<AnimeWishSearchResultModel>>();

    public MutableLiveData<MovieEntity> movieData
            = new MutableLiveData<MovieEntity>();

    public MutableLiveData<ArrayList<Season>> seasonData
            = new MutableLiveData<ArrayList<Season>>();

    public MutableLiveData<ArrayList<QualityParsedModel>> qualityParsedModelMutableLiveData
            = new MutableLiveData<ArrayList<QualityParsedModel>>();

    public MutableLiveData<ArrayList<AnimeEpisode>> animeEpisode
            = new MutableLiveData<ArrayList<AnimeEpisode>>();

    public MutableLiveData<ArrayList<AnimeStreamingSource>> animeStreamingLinks
            = new MutableLiveData<ArrayList<AnimeStreamingSource>>();

    public void search(String query) {
        if (this.popcornMoviesLinkUtil.getValue() == null || query.length() <= 0) return;
        executor.execute(() -> {
            try {
                ArrayList<TrendingSearchWishResultModel> searchResults = this.popcornMoviesLinkUtil.getValue().searchMedia(query);
                for (TrendingSearchWishResultModel searchResult : searchResults) {
                    searchResult.inWishList = Boolean.valueOf(this.mainDatabase.getValue().getWishList().doHave(searchResult.mediaLink));
                }
                this.searchResults.postValue(searchResults);
            } catch (Exception e) {
                Log.e(TAG, "Error getting search result", e);
                this.searchResults.postValue(null);
            }
        });
        executor.execute(() -> {
            try {
                ArrayList<AnimeWishSearchResultModel> searchResults = this.allAnimeLinkUtil.getValue().animeSearch(query);
                for (AnimeWishSearchResultModel searchResult : searchResults) {
                    searchResult.inWishList = Boolean.valueOf(this.mainDatabase.getValue().getAnimeWishListDAO().doHave(searchResult.id));
                }
                this.animeSearchResults.postValue(searchResults);
            } catch (Exception e) {
                Log.e(TAG, "Error getting anime search result", e);
                this.animeSearchResults.postValue(null);
            }
        });
    }

    public void getTrending() {
        if (this.popcornMoviesLinkUtil.getValue() == null) return;
        this.executor.execute(() -> {
            try {
                ArrayList<TrendingSearchWishResultModel> trendingResults = this.popcornMoviesLinkUtil.getValue().getTrending();
                for (TrendingSearchWishResultModel trendingResult : trendingResults) {
                    trendingResult.inWishList = Boolean.valueOf(this.mainDatabase.getValue().getWishList().doHave(trendingResult.mediaLink));
                }
                this.trendingResults.postValue(trendingResults);
                if (this.mainDatabase.getValue() == null) return;
                this.mainDatabase.getValue().getTrendingTable().deleteAll();
                ArrayList<TrendingEntity> temp = new ArrayList<TrendingEntity>(trendingResults.size());
                for (int i = 0; i < trendingResults.size(); i++) {
                    TrendingSearchWishResultModel data = trendingResults.get(i);
                    temp.add(new TrendingEntity(data.title, data.mediaLink, data.poster));
                }
                this.mainDatabase.getValue().getTrendingTable().addAll(temp);
            } catch (Exception e) {
                Log.e(TAG, "Error getting trending", e);
                if (this.mainDatabase.getValue() == null) this.trendingResults.postValue(null);
                else {
                    List<TrendingEntity> trendingResults = this.mainDatabase.getValue().getTrendingTable().getAll();
                    ArrayList<TrendingSearchWishResultModel> trendingResults2 = new ArrayList<TrendingSearchWishResultModel>(trendingResults.size());
                    for (int i = 0; i < trendingResults.size(); i++) {
                        TrendingEntity trendingResult = trendingResults.get(i);
                        TrendingSearchWishResultModel trendingResult2 = new TrendingSearchWishResultModel(trendingResult.title, trendingResult.mediaLink, trendingResult.poster);
                        trendingResults2.add(trendingResult2);
                        trendingResult2.inWishList = Boolean.valueOf(this.mainDatabase.getValue().getWishList().doHave(trendingResult2.mediaLink));
                    }
                    this.trendingResults.postValue(trendingResults2.size() <= 0 ? null : trendingResults2);
                }
            }
        });
    }

    public void getAllWish() {
        if (this.mainDatabase.getValue() == null) return;
        this.executor.execute(() -> {
            List<WishEntity> tempWish = this.mainDatabase.getValue().getWishList().getAll();
            ArrayList<TrendingSearchWishResultModel> wishData = new ArrayList<TrendingSearchWishResultModel>(tempWish.size());
            for (int i = 0; i < tempWish.size(); i++) {
                WishEntity wishEntity = tempWish.get(i);
                TrendingSearchWishResultModel trendingSearchWishResultModel = new TrendingSearchWishResultModel(wishEntity.title, wishEntity.mediaLink, wishEntity.poster);
                wishData.add(trendingSearchWishResultModel);
            }
            this.wish.postValue(wishData);
        });
        this.executor.execute(() -> {
            List<AnimeWishEntity> tempWish = this.mainDatabase.getValue().getAnimeWishListDAO().getAll();
            ArrayList<AnimeWishSearchResultModel> wishData = new ArrayList<AnimeWishSearchResultModel>(tempWish.size());
            for (int i = 0; i < tempWish.size(); i++) {
                AnimeWishEntity wishEntity = tempWish.get(i);
                AnimeWishSearchResultModel trendingSearchWishResultModel = new AnimeWishSearchResultModel(wishEntity.id, wishEntity.poster, wishEntity.title);
                wishData.add(trendingSearchWishResultModel);
            }
            this.animeWish.postValue(wishData);
        });
    }

    public void addToWish(TrendingSearchWishResultModel data) {
        if (this.mainDatabase.getValue() == null) return;
        this.executor.execute(() -> {
            long status = this.mainDatabase.getValue().getWishList().add(new WishEntity(data.title, data.mediaLink, data.poster));
            boolean isAtleast1UpdatedOnTrending = false;
            ArrayList<TrendingSearchWishResultModel> tempTrending = this.trendingResults.getValue();
            if (tempTrending != null) {
                if (!tempTrending.isEmpty()) {
                    for (int i = 0; i < tempTrending.size(); i++) {
                        TrendingSearchWishResultModel tempTrendingData = tempTrending.get(i);
                        if (tempTrendingData.mediaLink.equals(data.mediaLink)) {
                            tempTrendingData.inWishList = true;
                            isAtleast1UpdatedOnTrending = true;
                        }
                    }
                }
            }
            ArrayList<TrendingSearchWishResultModel> tempSearch = this.searchResults.getValue();
            boolean isAtleast1UpdatedOnSearch = false;
            if (tempSearch != null) {
                if (!tempSearch.isEmpty()) {
                    for (int i = 0; i < tempSearch.size(); i++) {
                        TrendingSearchWishResultModel tempSearchData = tempSearch.get(i);
                        if (tempSearchData.mediaLink.equals(data.mediaLink)) {
                            tempSearchData.inWishList = true;
                            isAtleast1UpdatedOnSearch = true;
                        }
                    }
                }
            }
            List<WishEntity> allWish = this.mainDatabase.getValue().getWishList().getAll();
            ArrayList<TrendingSearchWishResultModel> tempAllWish = new ArrayList<TrendingSearchWishResultModel>(allWish.size());
            for (int i = 0; i < allWish.size(); i++) {
                TrendingSearchWishResultModel trendingSearchWishResultModel = new TrendingSearchWishResultModel(allWish.get(i).title, allWish.get(i).mediaLink, allWish.get(i).poster);
                tempAllWish.add(trendingSearchWishResultModel);
            }
            if (isAtleast1UpdatedOnSearch) this.searchResults.postValue(tempSearch);
            if (isAtleast1UpdatedOnTrending) this.trendingResults.postValue(tempTrending);
            this.wish.postValue(tempAllWish);
        });
    }

    public void addToWish(AnimeWishSearchResultModel data) {
        if (this.mainDatabase.getValue() == null) return;
        this.executor.execute(() -> {
            long status = this.mainDatabase.getValue().getAnimeWishListDAO().add(new AnimeWishEntity(data.id, data.poster, data.title));
            ArrayList<AnimeWishSearchResultModel> tempSearch = this.animeSearchResults.getValue();
            boolean isAtleast1UpdatedOnSearch = false;
            if (tempSearch != null) {
                if (!tempSearch.isEmpty()) {
                    for (int i = 0; i < tempSearch.size(); i++) {
                        AnimeWishSearchResultModel tempSearchData = tempSearch.get(i);
                        if (tempSearchData.id.equals(data.id)) {
                            tempSearchData.inWishList = true;
                            isAtleast1UpdatedOnSearch = true;
                        }
                    }
                }
            }
            List<AnimeWishEntity> allWish = this.mainDatabase.getValue().getAnimeWishListDAO().getAll();
            ArrayList<AnimeWishSearchResultModel> tempAllWish = new ArrayList<AnimeWishSearchResultModel>(allWish.size());
            for (int i = 0; i < allWish.size(); i++) {
                AnimeWishSearchResultModel trendingSearchWishResultModel = new AnimeWishSearchResultModel(allWish.get(i).id, allWish.get(i).poster, allWish.get(i).title);
                tempAllWish.add(trendingSearchWishResultModel);
            }
            if (isAtleast1UpdatedOnSearch) this.animeSearchResults.postValue(tempSearch);
            this.animeWish.postValue(tempAllWish);
        });
    }

    public void removeFromWish(TrendingSearchWishResultModel data) {
        if (this.mainDatabase.getValue() == null) return;
        this.executor.execute(() -> {
            long status = this.mainDatabase.getValue().getWishList().delete(new WishEntity(data.title, data.mediaLink, data.poster));
            ArrayList<TrendingSearchWishResultModel> tempTrending = this.trendingResults.getValue();
            boolean isAtleast1UpdatedTrending = false;
            if (tempTrending != null) {
                if (!tempTrending.isEmpty()) {
                    for (int i = 0; i < tempTrending.size(); i++) {
                        TrendingSearchWishResultModel tempTrendingData = tempTrending.get(i);
                        if (tempTrendingData.mediaLink.equals(data.mediaLink)) {
                            tempTrendingData.inWishList = false;
                            isAtleast1UpdatedTrending = true;
                        }
                    }
                }
            }
            ArrayList<TrendingSearchWishResultModel> tempSearch = this.searchResults.getValue();
            boolean isAtleast1UpdatedSearch = false;
            if (tempSearch != null) {
                if (!tempSearch.isEmpty()) {
                    for (int i = 0; i < tempSearch.size(); i++) {
                        TrendingSearchWishResultModel tempSearchData = tempSearch.get(i);
                        if (tempSearchData.mediaLink.equals(data.mediaLink)) {
                            tempSearchData.inWishList = false;
                            isAtleast1UpdatedSearch = true;
                        }
                    }
                }
            }
            List<WishEntity> allWish = this.mainDatabase.getValue().getWishList().getAll();
            ArrayList<TrendingSearchWishResultModel> tempAllWish = new ArrayList<TrendingSearchWishResultModel>(allWish.size());
            for (int i = 0; i < allWish.size(); i++) {
                TrendingSearchWishResultModel trendingSearchWishResultModel = new TrendingSearchWishResultModel(allWish.get(i).title, allWish.get(i).mediaLink, allWish.get(i).poster);
                tempAllWish.add(trendingSearchWishResultModel);
            }
            if (isAtleast1UpdatedSearch) this.searchResults.postValue(tempSearch);
            if (isAtleast1UpdatedTrending) this.trendingResults.postValue(tempTrending);
            this.wish.postValue(tempAllWish);
        });
    }

    public void removeFromWish(AnimeWishSearchResultModel data) {
        if (this.mainDatabase.getValue() == null) return;
        this.executor.execute(() -> {
            long status = this.mainDatabase.getValue().getAnimeWishListDAO().delete(new AnimeWishEntity(data.id, data.poster, data.title));
            ArrayList<AnimeWishSearchResultModel> tempSearch = this.animeSearchResults.getValue();
            boolean isAtleast1UpdatedSearch = false;
            if (tempSearch != null) {
                if (!tempSearch.isEmpty()) {
                    for (int i = 0; i < tempSearch.size(); i++) {
                        AnimeWishSearchResultModel tempSearchData = tempSearch.get(i);
                        if (tempSearchData.id.equals(data.id)) {
                            tempSearchData.inWishList = false;
                            isAtleast1UpdatedSearch = true;
                        }
                    }
                }
            }
            List<AnimeWishEntity> allWish = this.mainDatabase.getValue().getAnimeWishListDAO().getAll();
            ArrayList<AnimeWishSearchResultModel> tempAllWish = new ArrayList<AnimeWishSearchResultModel>(allWish.size());
            for (int i = 0; i < allWish.size(); i++) {
                AnimeWishSearchResultModel trendingSearchWishResultModel = new AnimeWishSearchResultModel(allWish.get(i).id, allWish.get(i).poster, allWish.get(i).title);
                tempAllWish.add(trendingSearchWishResultModel);
            }
            if (isAtleast1UpdatedSearch) this.animeSearchResults.postValue(tempSearch);
            this.animeWish.postValue(tempAllWish);
        });
    }

    public void getMovieOrSeasonData(String mediaLink) {
        this.executor.execute(() -> {
            if (this.popcornMoviesLinkUtil.getValue().isMovie(mediaLink)) {
                MovieEntity movieEntity = this.mainDatabase.getValue().getMovieDAO().getMovie(mediaLink);
                if (movieEntity != null) {
                    this.movieData.postValue(movieEntity);
                } else {
                    try {
                        String tmdbId = this.popcornMoviesLinkUtil.getValue().getTmdbId(mediaLink);
                        movieEntity = new MovieEntity(mediaLink, tmdbId, 0f);
                        this.mainDatabase.getValue().getMovieDAO().add(movieEntity);
                        this.movieData.postValue(movieEntity);
                    } catch (Exception e) {
                        Log.e(TAG, "Error getting movie tmdb id", e);
                        this.movieData.postValue(new MovieEntity(mediaLink, null, 0));
                    }
                }
            } else {
                String tmdbId = getTmdbIdOffline(mediaLink);
                if(tmdbId==null && this.networkState.getValue()) {
                    try {
                        tmdbId = this.popcornMoviesLinkUtil.getValue().getTmdbId(mediaLink);
                        this.mainDatabase.getValue().getSeriesDAO().insert(new SeriesEntity(mediaLink, tmdbId));
                    } catch (Exception e) {
                        this.seasonData.postValue(new ArrayList<Season>(0));
                        return;
                    }
                } else if(tmdbId==null && !this.networkState.getValue()){
                    this.seasonData.postValue(new ArrayList<Season>(0));
                    return;
                }
                if(this.networkState.getValue()) {
                    try {
                        this.seasonData.postValue(getAndStoreSeasonDataOnline(tmdbId));
                    } catch (Exception e) {
                        this.seasonData.postValue(getSeasonDataOffline(tmdbId));
                    }
                } else {
                    this.seasonData.postValue(getSeasonDataOffline(tmdbId));
                }
            }
        });
    }

    private String getTmdbIdOffline(String mediaLink) {

        SeriesEntity series = this.mainDatabase.getValue().getSeriesDAO().getSeriesByMediaLink(mediaLink);
        if(series == null) {
            this.seasonData.postValue(new ArrayList<>(0));
            return null;
        } else {
            return series.tmdbId;
        }
    }

    private ArrayList<Season> getSeasonDataOffline(String tmdbId) {
        List<SeasonEpisode> se = this.mainDatabase.getValue().getSeasonDAO().getSeasonBySeriesTmdbId(tmdbId);
        Season[] season = new Season[this.mainDatabase.getValue().getSeasonDAO().getSeasonCount(tmdbId)];
        for(int s=0;s<se.size();s++){
            Episode ep = new Episode(se.get(s).episodeNumber,se.get(s).watchPercentage);
            if(season[se.get(s).seasonNumber-1]==null)
                season[se.get(s).seasonNumber-1] = new Season(tmdbId,se.get(s).seasonNumber);
            season[se.get(s).seasonNumber-1].episodes.add(ep);
        }
        return new ArrayList<>(Arrays.asList(season));
    }

    private ArrayList<Season> getAndStoreSeasonDataOnline(String tmdbId) throws Exception {
        int[] seasonEpisodeData = this.popcornMoviesLinkUtil.getValue().getSeriesSeasonEpisodes(tmdbId);
        if(this.mainDatabase.getValue().getSeasonDAO().get(tmdbId)==null)
            this.mainDatabase.getValue().getSeasonDAO().insert(new SeasonEntity(tmdbId,seasonEpisodeData.length));
        else
            this.mainDatabase.getValue().getSeasonDAO().update(tmdbId,seasonEpisodeData.length);
        for(int season=0;season<seasonEpisodeData.length;season++){
            for(int episode=0;episode<seasonEpisodeData[season];episode++) {
                EpisodeEntity tempEpisodeEntity = this.mainDatabase.getValue().getEpisodeDAO().get(tmdbId, season+1, episode+1);
                if(tempEpisodeEntity==null)
                    this.mainDatabase.getValue().getEpisodeDAO().insert(new EpisodeEntity(tmdbId, season+1, episode+1, 0f));
            }
        }
        List<SeasonEpisode> se = this.mainDatabase.getValue().getSeasonDAO().getSeasonBySeriesTmdbId(tmdbId);
        Season[] season = new Season[this.mainDatabase.getValue().getSeasonDAO().getSeasonCount(tmdbId)];
        for(int s=0;s<se.size();s++){
            Episode ep = new Episode(se.get(s).episodeNumber,se.get(s).watchPercentage);
            if(season[se.get(s).seasonNumber-1]==null)
                season[se.get(s).seasonNumber-1] = new Season(tmdbId,se.get(s).seasonNumber);
            season[se.get(s).seasonNumber-1].episodes.add(ep);
        }
        return new ArrayList<>(Arrays.asList(season));
    }

    public void getQualityParsedModel(boolean isSeries, int seasonNumber, int episodeNumber, String tmdbId, Context context){
        this.executor.execute(()->{
            StreamingModel streamingModel = null;
            try {
                if(isSeries)
                    streamingModel = this.popcornMoviesLinkUtil
                            .getValue()
                            .getSeriesStreamingLink(seasonNumber, episodeNumber, tmdbId);
                else
                    streamingModel = this.popcornMoviesLinkUtil.getValue().getMovieStreamingLink(tmdbId);
            } catch (Exception e) {
                Log.e(TAG,"Error is getting streaming details",e);
                this.qualityParsedModelMutableLiveData.postValue(null);
                return;
            }
            if(streamingModel == null) {
                this.qualityParsedModelMutableLiveData.postValue(new ArrayList<QualityParsedModel>(0));
                return ;
            }
            Log.d(TAG,"Parsed : "+ streamingModel);
            ArrayList<QualityParsedModel> qualityList = M3U8_QualityParser.processMainUrl(streamingModel.videoSource, OkHttpUtil.getInstance(context));
            if(qualityList == null || qualityList.isEmpty()){
                this.qualityParsedModelMutableLiveData.postValue(new ArrayList<QualityParsedModel>(0));
            }
            qualityList.get(0).rootUrl = streamingModel.videoSource;
            qualityList.get(0).subtitleConfigurations = SubtitleConfigurationUtils.getSubititleConfigurations(streamingModel.subtitles);

            this.qualityParsedModelMutableLiveData.postValue(qualityList);
        });
    }

    public void updateWatchPercentage(boolean isSeries, int seasonNumber, int episodeNumber, String tmdbId, float watch_percentage) {
        this.executor.execute(()->{
            if(isSeries)
                this.mainDatabase.getValue().getEpisodeDAO().update(tmdbId,seasonNumber,episodeNumber,watch_percentage);
            else
                this.mainDatabase.getValue().getMovieDAO().update(tmdbId,watch_percentage);
        });

    }

    public void getAnimeEpisode(String id) {
        if(this.allAnimeLinkUtil.getValue() == null) return;
        this.executor.execute(()->{
            try {
                if(this.networkState.getValue()) this.getAnimeEpisodeOnline(id);
                else this.getAnimeEpisodeOffline(id);
            } catch (Exception e) {
                Log.e(TAG,"Error in getting anime episode",e);
                if(!this.networkState.getValue()) this.getAnimeEpisodeOffline(id);
                else this.animeEpisode.postValue(new ArrayList<>(0));
            }
        });
    }

    private void getAnimeEpisodeOnline(String id) throws Exception {
        ArrayList<AnimeEpisode> animeEpisodes = this.allAnimeLinkUtil.getValue().getAnimeEpisodes(id);
        if(animeEpisodes == null) return;
        for (AnimeEpisode episode : animeEpisodes) {
            AnimeEpisodeEntity animeEpisodeEntity = this.mainDatabase.getValue().getAnimeEpisodeDAO().get(id,episode.episode);
            if(animeEpisodeEntity==null)
                this.mainDatabase.getValue().getAnimeEpisodeDAO().insert(new AnimeEpisodeEntity(id,episode.episode,episode.isDubAvailable));
            else if(animeEpisodeEntity.isDubAvailable != episode.isDubAvailable)
                this.mainDatabase.getValue().getAnimeEpisodeDAO().update(id,episode.episode,episode.isDubAvailable);
        }
        getAnimeEpisodeOffline(id);
    }

    private void getAnimeEpisodeOffline(String id) {
        List<AnimeEpisodeEntity> animeEpisodeEntities = this.mainDatabase.getValue().getAnimeEpisodeDAO().get(id);
        ArrayList<AnimeEpisode> animeEpisodes = new ArrayList<AnimeEpisode>(animeEpisodeEntities.size());
        for (AnimeEpisodeEntity animeEpisodeEntity : animeEpisodeEntities)
            animeEpisodes.add(new AnimeEpisode(animeEpisodeEntity.id,animeEpisodeEntity.episode,animeEpisodeEntity.isDubAvailable,animeEpisodeEntity.watchPercentage));
        this.animeEpisode.postValue(animeEpisodes);
    }

    public void getAnimeStreamingLink(String id, String episode, String subDub) {
        if(!this.networkState.getValue()) return;
        this.executor.execute(()-> {
            try {
                ArrayList<AnimeStreamingSource> streamingModels = this.allAnimeLinkUtil.getValue().getAnimeVideoLink(id,episode,subDub);
                this.animeStreamingLinks.postValue(streamingModels);
            } catch (Exception e) {
                Log.e(TAG,"Error in getting anime streaming link",e);
                this.animeStreamingLinks.postValue(new ArrayList<>(0));
            }
        });
    }

    public void updateAnimeWatchPercentage(String id, String episode, float progress) {
        if(this.allAnimeLinkUtil.getValue()==null) return;
        this.executor.execute(()-> {
            this.mainDatabase.getValue().getAnimeEpisodeDAO().update(id,episode,progress);
        });
    }
}
