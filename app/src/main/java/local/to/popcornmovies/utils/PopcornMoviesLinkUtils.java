package local.to.popcornmovies.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import local.to.popcornmovies.models.StreamingModel;
import local.to.popcornmovies.models.TrendingSearchWishResultModel;
import okhttp3.Request;
import okhttp3.Response;

public class PopcornMoviesLinkUtils {
    
    private static final String
            TAG = "test->PopcornMoviesLinkUtils",WORKER_URL,HOME_URL,CDN_GETTER_URL,SEARCH_REGEX,SEARCH_URL,
            TMDB_SERIES_REFERER,TMDB_MOVIES_REFERER,TMDB_URL,TMDB_REGEX,TRENDING_REGEX,
            AUTHORIZATION_BEARER,AUTHORIZATION;

    static {
        // https://popcornmovies.to/home
        HOME_URL = HexDecoder.fromHex("68747470733a2f2f706f70636f726e6d6f766965732e746f2f686f6d65");
        // https://tom.autoembed.cc/api/getVideoSource?type=
        CDN_GETTER_URL = HexDecoder.fromHex("68747470733a2f2f746f6d2e6175746f656d6265642e63632f6170692f676574566964656f536f757263653f747970653d");
        // <div class=\"relative group overflow-hidden\">\\s+<a href=\"(.*?)\"\\s+class=\".*?\">\\s+<picture>\\s+<img .+? data-src=\"(.*?)\" alt=\"(.*?)\"
        SEARCH_REGEX = HexDecoder.fromHex("3c64697620636c6173733d2272656c61746976652067726f7570206f766572666c6f772d68696464656e223e5c732b3c6120687265663d22282e2a3f29225c732b636c6173733d222e2a3f223e5c732b3c706963747572653e5c732b3c696d67202e2b3f20646174612d7372633d22282e2a3f292220616c743d22282e2a3f2922");
        // https://popcornmovies.to/search/
        SEARCH_URL = HexDecoder.fromHex("68747470733a2f2f706f70636f726e6d6f766965732e746f2f7365617263682f");
        // https://flixscrape.popcornmovies.to
        WORKER_URL = HexDecoder.fromHex("68747470733a2f2f666c69787363726170652e706f70636f726e6d6f766965732e746f");
        // \s+tmdbId:\s+&#039;([0-9]+)&#039;
        TMDB_REGEX = HexDecoder.fromHex("5c732b746d646249643a5c732b26233033393b285b302d395d2b2926233033393b");
        // https://api.tmdb.org/3/tv/
        TMDB_URL = HexDecoder.fromHex("68747470733a2f2f6170692e746d64622e6f72672f332f74762f");
        // https://tom.autoembed.cc/tv/
        TMDB_SERIES_REFERER = HexDecoder.fromHex("68747470733a2f2f746f6d2e6175746f656d6265642e63632f74762f");
        // https://tom.autoembed.cc/movie/
        TMDB_MOVIES_REFERER = HexDecoder.fromHex("68747470733a2f2f746f6d2e6175746f656d6265642e63632f6d6f7669652f");
        // \\s+<div class=\"swiper-slide\">\\s+<div class=\"relative group\">\\s+<a href=\"(.*?)\"\\s+class=\".*?\">\\s+<img src=\"(.*?)\" alt=\"(.*?)\"
        TRENDING_REGEX = HexDecoder.fromHex("5c732b3c64697620636c6173733d227377697065722d736c696465223e5c732b3c64697620636c6173733d2272656c61746976652067726f7570223e5c732b3c6120687265663d22282e2a3f29225c732b636c6173733d222e2a3f223e5c732b3c696d67207372633d22282e2a3f292220616c743d22282e2a3f2922");
        // Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmYWNkMmE1YWE0YmMwMzAyZjNhZmRlYTIwZGQ2YWRhZSIsInN1YiI6IjY1OTEyNjU1NjUxZmNmNWYxMzhlMWRjNyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.5boG-w-nlk-SWB8hvFeWq_DNRbrU6n5XEXleVQ1L1Sg
        AUTHORIZATION_BEARER = HexDecoder.fromHex("4265617265722065794a68624763694f694a49557a49314e694a392e65794a68645751694f694a6d59574e6b4d6d453159574530596d4d774d7a41795a6a4e685a6d526c595449775a475132595752685a534973496e4e3159694936496a59314f5445794e6a55314e6a55785a6d4e6d4e5759784d7a686c4d57526a4e794973496e4e6a6233426c6379493657794a6863476c66636d56685a434a644c434a325a584a7a61573975496a6f7866512e35626f472d772d6e6c6b2d535742386876466557715f444e52627255366e355845586c655651314c315367");
        // Authorization
        AUTHORIZATION = HexDecoder.fromHex("417574686f72697a6174696f6e");
    }

    private final OkHttpUtil okHttpUtil;
    private final Pattern SEARCH_REGEX_pattern, TMDB_REGEX_pattern, TRENDING_REGEX_pattern;
    private final Gson gson;

    public PopcornMoviesLinkUtils(Context context){
        this.okHttpUtil = OkHttpUtil.getInstance(context);
        this.TMDB_REGEX_pattern = Pattern.compile(TMDB_REGEX);
        this.SEARCH_REGEX_pattern =  Pattern.compile(SEARCH_REGEX);
        this.TRENDING_REGEX_pattern =  Pattern.compile(TRENDING_REGEX);
        this.gson = new Gson();
    }

    public String getTmdbId(String mediaLink) throws Exception {
        if(!this.isMovie(mediaLink)){
            mediaLink = mediaLink.replace("/tv-show/","/episode/");
            mediaLink += "/1-1";
        }
        Response response = okHttpUtil.getHttpRequest(mediaLink);
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        
        String responseBody = response.body().string();
        Matcher matcher = TMDB_REGEX_pattern.matcher(responseBody);
        String tmdbId = null;
        if (matcher.find()) {
            tmdbId = matcher.group(1);
        } else {
            throw new IOException("TMDB ID not found");
        }
        return tmdbId;
    }

    public int[] getSeriesSeasonEpisodes(String tmdbId) throws Exception {
        String tmdbUrl = TMDB_URL + tmdbId + "?append_to_response=external_ids";
        Request.Builder tmdbRequestBuilder = new Request.Builder()
                .url(tmdbUrl)
                .header(AUTHORIZATION,AUTHORIZATION_BEARER);

        Response tmdbResponse = okHttpUtil.getHttpRequest(tmdbRequestBuilder);
        if (!tmdbResponse.isSuccessful()) throw new IOException("Unexpected code " + tmdbResponse);

        String tmdbResponseBody = tmdbResponse.body().string();
        JSONObject jsonResponse = new JSONObject(tmdbResponseBody);
        JSONArray seasons = jsonResponse.getJSONArray("seasons");
        int[] seasonEps = new int[seasons.length()-1];
        for (int i = 1; i < seasons.length(); i++) {
            JSONObject season = seasons.getJSONObject(i);
            seasonEps[i-1] = season.getInt("episode_count");
        }
        return seasonEps;
    }

    public StreamingModel getSeriesStreamingLink(int season, int episode, String tmdbId) throws Exception {
        String apiUrl = CDN_GETTER_URL + "tv&&id=" + tmdbId + "/" + season + "/" + episode;
        Request.Builder streamRequestBuilder = new Request.Builder()
                .url(apiUrl)
                .header("Accept", "application/json")
                .header("Referer", TMDB_SERIES_REFERER+ tmdbId + "/" + season + "/" + episode);

        if(streamRequestBuilder==null) return null;

        Response streamResponse = okHttpUtil.getHttpRequest(streamRequestBuilder);
        if (!streamResponse.isSuccessful()) throw new IOException("Unexpected code " + streamResponse);

        String streamResponseBody = streamResponse.body().string();
        
        return gson.fromJson(streamResponseBody, StreamingModel.class);
    }

    public StreamingModel getMovieStreamingLink(String tmdbId) throws Exception {
        String apiUrl;
        apiUrl = CDN_GETTER_URL+"movie&id="+tmdbId;
        
        Request.Builder streamRequestBuilder = new Request.Builder()
                .url(apiUrl)
                .header("Accept", "application/json")
                .header("Referer", TMDB_MOVIES_REFERER+ tmdbId);

                
        if(streamRequestBuilder==null) return null;

        Response streamResponse = okHttpUtil.getHttpRequest(streamRequestBuilder);
        if (!streamResponse.isSuccessful()) throw new IOException("Unexpected code " + streamResponse);

        String streamResponseBody = streamResponse.body().string();
        
        return gson.fromJson(streamResponseBody, StreamingModel.class);
    }

    public boolean isMovie(String mediaLink){
        return !mediaLink.contains("/tv-show/");
    }

    public ArrayList<TrendingSearchWishResultModel> searchMedia(String query) throws Exception {
        String searchUrl = SEARCH_URL + query.replace(" ", "%20");

        Response response = okHttpUtil.getHttpRequest(searchUrl);
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        String responseBody = response.body().string();
        Matcher matcher = SEARCH_REGEX_pattern.matcher(responseBody);

        ArrayList<TrendingSearchWishResultModel> result = new ArrayList<TrendingSearchWishResultModel>();
        while (matcher.find()) {
            String link = matcher.group(1);
            String image = matcher.group(2);
            String title = matcher.group(3);
            result.add(new TrendingSearchWishResultModel(title, link, image));
        }

        return result;
    }

    public ArrayList<TrendingSearchWishResultModel> getTrending() throws Exception{

        Response response = null;
        try {
            response = okHttpUtil.getHttpRequest(HOME_URL);
        } catch (Exception e) {
            Log.e(TAG,"Error getting trending",e);
            throw e;
        }
        String responseBody = response.body().string();
        Matcher matcher = TRENDING_REGEX_pattern.matcher(responseBody);

        ArrayList<TrendingSearchWishResultModel> result = new ArrayList<TrendingSearchWishResultModel>();
        while (matcher.find()) {
            String link = matcher.group(1);
            String image = matcher.group(2);
            String title = matcher.group(3);
            result.add(new TrendingSearchWishResultModel(title, link, image));
        }

        return result;
    }
}

