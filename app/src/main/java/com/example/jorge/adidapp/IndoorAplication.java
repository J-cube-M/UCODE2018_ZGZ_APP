package com.example.jorge.adidapp;

import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.estimote.cloud_plugin.common.EstimoteCloudCredentials;
import com.estimote.indoorsdk.IndoorLocationManagerBuilder;
import com.estimote.indoorsdk_module.algorithm.OnPositionUpdateListener;
import com.estimote.indoorsdk_module.algorithm.ScanningIndoorLocationManager;
import com.estimote.indoorsdk_module.cloud.CloudCallback;
import com.estimote.indoorsdk_module.cloud.EstimoteCloudException;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManager;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManagerFactory;
import com.estimote.indoorsdk_module.cloud.Location;
import com.estimote.indoorsdk_module.cloud.LocationPosition;

/**
 * Created by jorge on 10/03/2018.
 */

public class IndoorAplication {
    private EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials("hackaton-59f", "95ebfc2422dea64a367c224a0cb31c2d");
    private IndoorCloudManager cloudManager;
    private Location location;
    private ScanningIndoorLocationManager indoorLocationManager;
    private Notification notification;

    public void start(String map, final Context context, final MyCallback callback) {
        cloudManager = new IndoorCloudManagerFactory().create(context, cloudCredentials);

        cloudManager.getLocation(map, new CloudCallback<Location>() {
            @Override
            public void success(Location loc) {
                // do something with your Location object here.
                // You will need it to initialise IndoorLocationManager!
                location = loc;
                Log.d("LOCSu", "bien");
                setupLocation(context, callback);
            }

            @Override
            public void failure(EstimoteCloudException e) {
                // oops!
                Log.d("LOCFa", "fail");
            }
        });
    }

    private void setupLocation(Context context, final MyCallback callback) {
        final Context context_ = context;
        Log.d("SUL", "entro");
        indoorLocationManager =
                new IndoorLocationManagerBuilder(context, location, cloudCredentials)
                        .withDefaultScanner()
                        // .withScannerInForegroundService(notification)
                        .build();
        Log.d("SUL", "creo");
        indoorLocationManager.setOnPositionUpdateListener(new OnPositionUpdateListener() {
            @Override
            public void onPositionUpdate(LocationPosition locationPosition) {
                Log.d("cb", "es");
                callback.execute(locationPosition.getX(), locationPosition.getY());

            }

            @Override
            public void onPositionOutsideLocation() {
                // Think
                Log.d("Fuera", "estas fuera");
                Toast.makeText(context_, "Estas fuera", 5).show();
            }
        });
        Log.d("SUL", "busco");
        indoorLocationManager.startPositioning();
    }

    public void end() {
        indoorLocationManager.stopPositioning();
    }
}
