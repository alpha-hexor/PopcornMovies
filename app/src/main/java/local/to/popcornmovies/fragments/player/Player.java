package local.to.popcornmovies.fragments.player;

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
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.util.EventLogger;
import androidx.media3.ui.PlayerView;
import androidx.navigation.NavController;

import java.util.ArrayList;

import local.to.popcornmovies.MainViewModel;
import local.to.popcornmovies.databinding.FragmentPlayerBinding;
import local.to.popcornmovies.models.QualityParsedModel;
import local.to.popcornmovies.ui.player.QualitySpinnerAdapter;

@UnstableApi
public class Player extends Fragment {
    private static final String TAG = "test->Player";

    private FragmentPlayerBinding binding;
    private MainViewModel _mainViewModel;
    private ExoPlayer _player;
    private NavController _navController;
    private float watchPercentage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @UnstableApi
    @Override
    public void  onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            return;
        }
        Log.d(TAG,"Started");
        this.initVariables();
        this.initObservers();
        this.initializations();
    }

    private void initVariables() {
        this._mainViewModel = new ViewModelProvider(this.getActivity()).get(MainViewModel.class);
        this._player = new ExoPlayer.Builder(this.getContext()).build();
        this._navController = findNavController(this);
    }

    private void initObservers() {
        this._mainViewModel.qualityParsedModelMutableLiveData.observe(getViewLifecycleOwner(), this::onStreamingData);
    }

    private void initializations(){
        this.binding.exoplayerView.setPlayer(this._player);
        this._player.setPlayWhenReady(true);
        this.binding.exoplayerView.setControllerVisibilityListener(new PlayerView.ControllerVisibilityListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
                if (visibility == View.VISIBLE) {
                    binding.qualitySelector.setVisibility(View.VISIBLE); // Show spinner with controls
                } else {
                    binding.qualitySelector.setVisibility(View.GONE); // Hide spinner
                }
            }
        });
        this._player.addAnalyticsListener(new EventLogger());
        this.binding.qualitySelector.setVisibility(View.GONE);
        this._mainViewModel.getQualityParsedModel(
                getArguments().getBoolean("isSeries"),
                getArguments().getInt("seasonNumber"),
                getArguments().getInt("episodeNumber"),
                getArguments().getString("tmdbId"),
                getContext());

        this.watchPercentage = getArguments().getFloat("watchPercentage");
    }

    private void goBack() {
        try {
            this._navController.navigateUp();
        } catch(Exception e) {
            return;
        }
    }

    private void onStreamingData(ArrayList<QualityParsedModel> qualityParsedModelList) {
        if(qualityParsedModelList == null) return;
        else if(qualityParsedModelList.isEmpty()) {
            Toast.makeText(getContext(),"Error in fetching streaming link(s).",Toast.LENGTH_LONG).show();
            this.goBack();
        }
        Log.d(TAG,"Streaming details :\n"+qualityParsedModelList.toString());
        this.binding.qualitySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                long current = Player.this._player.getCurrentPosition();
                Log.d(TAG,"Selected : "+i+" :: "+qualityParsedModelList.get(i).toString());
                setPlayer(qualityParsedModelList.get(i));
                Player.this._player.seekTo(current);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        QualitySpinnerAdapter qualitySelectorAdapter = new QualitySpinnerAdapter(getContext(), qualityParsedModelList);
        this.binding.qualitySelector.setAdapter(qualitySelectorAdapter);
        setPlayer(qualityParsedModelList.get(0));
    }

    public void setPlayer(QualityParsedModel qualityParsedModel) {
        if (qualityParsedModel.videoSource == null) return;

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
                if(Player.this.watchPercentage == progress) return;
                else {
                    Player.this.watchPercentage = progress;
                    Player.this._mainViewModel.updateWatchPercentage(
                            getArguments().getBoolean("isSeries"),
                            getArguments().getInt("seasonNumber"),
                            getArguments().getInt("episodeNumber"),
                            getArguments().getString("tmdbId"),
                            progress
                    );
                }
            }
        });
        this._player.setMediaItem(mediaItem);
        this._player.prepare();
        this._player.seekTo((long)(this.watchPercentage*this._player.getDuration()));
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