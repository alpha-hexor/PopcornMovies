package local.to.popcornmovies.fragments.anime_player;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.util.EventLogger;
import androidx.media3.ui.PlayerView;
import androidx.navigation.NavController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import local.to.popcornmovies.MainViewModel;
import local.to.popcornmovies.R;
import local.to.popcornmovies.databinding.FragmentAnimePlayerBinding;
import local.to.popcornmovies.models.AnimeStreamingSource;
import local.to.popcornmovies.models.QualityParsedModel;
import local.to.popcornmovies.ui.player.QualitySpinnerAdapter;
import local.to.popcornmovies.ui.player.SourceSpinnerAdapter;
import local.to.popcornmovies.utils.AllAnimeLinkUtils;
import local.to.popcornmovies.utils.M3U8_QualityParser;
import local.to.popcornmovies.utils.OkHttpUtil;

@UnstableApi
public class AnimePlayer extends Fragment {

    private static final String TAG = "test->AnmPlayer";

    private FragmentAnimePlayerBinding binding;
    private MainViewModel _mainViewModel;
    private ExoPlayer _player;
    private NavController _navController;
    private float watchPercentage;
    private boolean isMp4 = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAnimePlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @UnstableApi
    @Override
    public void  onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            return;
        }
        this.initVariables();
        this.initObservers();
        this.initializations();
    }

    private void initVariables() {
        this._mainViewModel = new ViewModelProvider(this.getActivity()).get(MainViewModel.class);
        Map<String, String> headers = new HashMap<>();
        headers.put("Referer", AllAnimeLinkUtils.ANIME_REFERER);
        headers.put("User-Agent", OkHttpUtil.USER_AGENT);
        DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setDefaultRequestProperties(headers);
        this._player = new ExoPlayer.Builder(this.getContext())
                .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory))
                .build();
        this._navController = findNavController(this);
    }

    private void initObservers() {
        this._mainViewModel.animeStreamingLinks.observe(getViewLifecycleOwner(), this::onStreamingData);
    }

    private void initializations() {
        this.binding.animeExoplayerView.setPlayer(this._player);
        this._player.setPlayWhenReady(true);
        this.binding.animeExoplayerView.setControllerVisibilityListener(new PlayerView.ControllerVisibilityListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
                binding.animeQualitySelector.setVisibility(visibility == View.VISIBLE && !isMp4 ? View.VISIBLE : View.GONE);
                binding.animeSourceSelector.setVisibility(visibility == View.VISIBLE ? View.VISIBLE : View.GONE);
            }
        });
        this._player.addAnalyticsListener(new EventLogger());
        this.binding.animeQualitySelector.setVisibility(View.GONE);
        this._mainViewModel.getAnimeStreamingLink(
                getArguments().getString("id"),
                getArguments().getString("episode"),
                getArguments().getString("subDub"));

        this.watchPercentage = getArguments().getFloat("watchPercentage");
    }

    private void goBack() {
        try {
            this._navController.navigateUp();
        } catch(Exception e) {
            return;
        }
    }

    private void onStreamingData(ArrayList<AnimeStreamingSource> qualityParsedModelList) {
        if(qualityParsedModelList == null) {
            Toast.makeText(getContext(),getString(R.string.link_not_found), Toast.LENGTH_LONG).show();
            this.goBack();
            return;
        }
        else if(qualityParsedModelList.isEmpty()) {
            Toast.makeText(getContext(),"Error in fetching streaming link(s).",Toast.LENGTH_LONG).show();
            this.goBack();
            return;
        }

        SourceSpinnerAdapter sourceSelectorAdapter = new SourceSpinnerAdapter(getContext(), qualityParsedModelList);
        this.binding.animeSourceSelector.setAdapter(sourceSelectorAdapter);
        this.binding.animeSourceSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                long current = AnimePlayer.this._player.getCurrentPosition();
                Log.d(TAG,"Selected : "+i+" :: "+qualityParsedModelList.get(i).toString());
                onSourceChange(qualityParsedModelList.get(i));
                AnimePlayer.this._player.seekTo(current);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        this.onSourceChange(qualityParsedModelList.get(0));
    }

    public void setPlayer(QualityParsedModel qualityParsedModel) {
        if (qualityParsedModel.videoSource == null) return;
        Log.d(TAG,"playing :: "+qualityParsedModel.videoSource);
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(qualityParsedModel.videoSource)
                .setSubtitleConfigurations(
                        qualityParsedModel.subtitleConfigurations == null?
                                new ArrayList<>():qualityParsedModel.subtitleConfigurations
                )
                .build();
        this._player.addListener(new androidx.media3.common.Player.Listener() {

            @Override
            public void onEvents(androidx.media3.common.Player player, androidx.media3.common.Player.Events events) {
                float progress = (float)player.getCurrentPosition()/(float)player.getDuration();
                if(AnimePlayer.this.watchPercentage == progress) return;
                else {
                    AnimePlayer.this.watchPercentage = progress;
                    AnimePlayer.this._mainViewModel.updateAnimeWatchPercentage(
                            getArguments().getString("id"),
                            getArguments().getString("episode"),
                            progress
                    );
                }
            }
        });
        this._player.setMediaItem(mediaItem);
        this._player.prepare();
        this._player.seekTo((long)(this.watchPercentage*this._player.getDuration()));
    }

    private void onSourceChange(AnimeStreamingSource animeStreamingSource) {
        this.isMp4 = animeStreamingSource.isMp4;
        if(animeStreamingSource.isMp4) {
            this.setPlayer(new QualityParsedModel("",animeStreamingSource.url));
            this.binding.animeQualitySelector.setVisibility(View.GONE);
        } else {
            this.binding.animeQualitySelector.setVisibility(View.VISIBLE);
            this._mainViewModel.executor.execute(() -> {
                ArrayList<QualityParsedModel> qualityParsedModels = M3U8_QualityParser.processMainUrl(animeStreamingSource.url, OkHttpUtil.getInstance(super.getContext()));
                this.binding.animeQualitySelector.setAdapter(new QualitySpinnerAdapter(getContext(), qualityParsedModels));
                this.setPlayer(qualityParsedModels.get(0));
                QualitySpinnerAdapter qualitySelectorAdapter = new QualitySpinnerAdapter(getContext(), qualityParsedModels);
                this.binding.animeQualitySelector.setAdapter(qualitySelectorAdapter);
                this.binding.animeQualitySelector.setVisibility(View.VISIBLE);
                this.binding.animeQualitySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        long current = AnimePlayer.this._player.getCurrentPosition();
                        Log.d(TAG,"Selected : "+i+" :: "+qualityParsedModels.get(i).toString());
                        setPlayer(qualityParsedModels.get(i));
                        AnimePlayer.this._player.seekTo(current);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            });
        }
    }

    @Override
    public void onDestroy() {
        this.releasePlayer();
        binding = null;
        Log.d(TAG,"Destroy");
        super.onDestroy();
    }

    private void releasePlayer(){
        if(this._player!=null)        this._player.release();
        this._player = null;
    }

}