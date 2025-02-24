package local.to.popcornmovies.fragments.anime_details;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.app.Dialog;
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

import java.io.Serializable;
import java.util.ArrayList;

import local.to.popcornmovies.MainViewModel;
import local.to.popcornmovies.R;
import local.to.popcornmovies.databinding.FragmentAnimeDetailsBinding;
import local.to.popcornmovies.models.AnimeEpisode;
import local.to.popcornmovies.models.AnimeWishSearchResultModel;
import local.to.popcornmovies.ui.anime_details.AnimeEpisodeListAdapter;
import local.to.popcornmovies.utils.OkHttpUtil;

public class AnimeDetails extends Fragment {

    public static final String TAG = "test->AnimeDetails";

    private FragmentAnimeDetailsBinding _binding;
    private MainViewModel _mainViewModel;
    private NavController _navController;
    private AnimeWishSearchResultModel _show;
    private Handler handler;
    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this._binding = FragmentAnimeDetailsBinding.inflate(inflater, container, false);
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
        this._mainViewModel.getAnimeEpisode(this._show.id);
    }

    private void initVariables() {
        this._binding.setFragmentAnimeDetails(this);
        this._navController = findNavController(this);
        Serializable serializableShow = getArguments().getSerializable("show");
        if(serializableShow instanceof AnimeWishSearchResultModel) this.initShow((AnimeWishSearchResultModel) serializableShow);
        else {
            this._navController.navigateUp();
            return;
        }
        this._mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        this.handler = new Handler(getActivity().getMainLooper());
    }

    private void initObservers() {
        this._mainViewModel.animeEpisode.observe(getViewLifecycleOwner(),this::onEpisodes);
    }

    private void initListeners() {

    }

    private void initViews() {
        this._mainViewModel.executor.execute(()->{
            if(this._binding == null)
                return;
            Bitmap image = OkHttpUtil.getInstance(this._binding.getRoot().getContext()).getBitmap(this._show.poster);
            this.handler.post(()->{
                if(this._binding == null) return;
                this._binding.animeDetailsRootLayout.setBackground(new BitmapDrawable(getResources(),image));
            });
        });

        this.dialog = new Dialog(getContext());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
        dialog.setContentView(R.layout.sub_dub_dialog_view);
        dialog.setTitle(R.string.select_type);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }

    private void onEpisodes(ArrayList<AnimeEpisode> animeEpisodes) {
        if(animeEpisodes == null) return;
        if(animeEpisodes.isEmpty()) {
            Toast.makeText(getContext(),getString(R.string.no_data_available),Toast.LENGTH_LONG).show();
            this._navController.navigateUp();
        }
        this._binding.animeDetailsThreeDotsLoader.loader.setVisibility(View.GONE);
        this._binding.animeEpisodeListRecyclerView.setAdapter(new AnimeEpisodeListAdapter(animeEpisodes,this::onEpisodeClick));
    }

    private void onEpisodeClick(AnimeEpisode animeEpisode) {
        Log.d(TAG,"Clicked :: "+animeEpisode.toString());
        if(!animeEpisode.isDubAvailable) {
            this.goToPlayer(animeEpisode.id, animeEpisode.episode, "sub",animeEpisode.watchPercentage);
            return;
        }
        dialog.findViewById(R.id.sub_button).setOnClickListener((v)->{
            goToPlayer(animeEpisode.id,animeEpisode.episode,"sub",animeEpisode.watchPercentage);
            dialog.dismiss();
            return;
        });

        dialog.findViewById(R.id.dub_button).setOnClickListener((v)->{
            goToPlayer(animeEpisode.id,animeEpisode.episode,"sub",animeEpisode.watchPercentage);
            dialog.dismiss();
            return;
        });

        dialog.show();
    }

    private void goToPlayer(String id, String episode, String sub, float watchPercentage) {
        NavDirections action = AnimeDetailsDirections.actionAnimeDetailsToAnimePlayer(id,episode,sub,watchPercentage);
        this._navController.navigate(action);
    }

    private void initShow(AnimeWishSearchResultModel animeWishSearchResultModel){
        this._show = animeWishSearchResultModel;
    }

    @Override
    public void onDestroyView() {
        this._binding = null;
        this.removeObservers();
        super.onDestroyView();
    }

    private void removeObservers() {
        if(this._mainViewModel == null) return;
        this._mainViewModel.animeEpisode.removeObservers(getViewLifecycleOwner());
    }
}