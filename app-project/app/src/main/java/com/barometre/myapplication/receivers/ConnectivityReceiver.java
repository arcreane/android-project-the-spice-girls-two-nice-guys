package com.barometre.myapplication.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.barometre.myapplication.viewmodel.BarViewModel;

public class ConnectivityReceiver extends BroadcastReceiver {

    private final BarViewModel barViewModel;

    public ConnectivityReceiver(BarViewModel barViewModel) {
        this.barViewModel = barViewModel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        barViewModel.setOfflineMode(!isOnline(context));
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}