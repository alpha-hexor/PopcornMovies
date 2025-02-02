package local.to.popcornmovies;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import local.to.popcornmovies.databinding.ActivityMainBinding;
import local.to.popcornmovies.rooom_database.MainDatabase;
import local.to.popcornmovies.utils.LinkUtils;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "test->MnAct";

    private ActivityMainBinding _binding;
    private NavController _navController;
    private MainViewModel _viewModel;
    private NetworkChangeReceiver _networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(this._binding.getRoot());
        this.hideSystemUI();
        EdgeToEdge.enable(this);
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager()
                .findFragmentById(this._binding.navHost.getId());

        this._navController = navHostFragment.getNavController();

        this._viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        this._viewModel.executor.execute(()-> {
            this._viewModel.linkUtils.postValue(new LinkUtils(this));
            this._viewModel.mainDatabase.postValue(MainDatabase.getDataBase(this));
        });

        this._networkChangeReceiver = new NetworkChangeReceiver(this::onNetworkChange);

        registerNetworkChangeReceiver();
    }

    private void onNetworkChange(boolean state) {
        this._viewModel.networkState.postValue(state);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return this._navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void registerNetworkChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(_networkChangeReceiver, intentFilter);
    }

    private void hideSystemUI() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // rehiding on visible
        this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener((l)->{
            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(_networkChangeReceiver);
    }

}