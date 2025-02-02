package local.to.popcornmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private OnNetworkChangeListener _listener;


    public NetworkChangeReceiver(){
        super();
    }


    public NetworkChangeReceiver(OnNetworkChangeListener listener){
        super();
        this._listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(this._listener!=null)
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                this._listener.onChange(true);
            } else {
                this._listener.onChange(false);
            }
    }

    public interface OnNetworkChangeListener{
        public void onChange(boolean state);
    }

}
