package com.sringa.unload.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

import com.sringa.unload.activity.BaseActivity;
import com.sringa.unload.activity.UserPhoneAuthActivity;
import com.sringa.unload.activity.VehicleListActivity;
import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.service.TrackingService;

public class MainActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;

    protected void modifyMenu(Menu menu) {
        //DO nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDataBase.init(this.getBaseContext());
        if (null == AppDataBase.INSTANCE.getAppUser()) {
            startActivity(new Intent(this, UserPhoneAuthActivity.class));
        } else {
            startActivity(new Intent(this, VehicleListActivity.class));
        }
        //TODO: start only once in background.
        startService();
    }

    @Override
    protected void onDestroy() {
        //TODO: re visit this.
        stopService();
        super.onDestroy();
    }

    public void startService() {
        startService(new Intent(getBaseContext(), TrackingService.class));
    }

    // Method to stop the service
    public void stopService() {
        stopService(new Intent(getBaseContext(), TrackingService.class));
    }

    @Override
    public void onBackPressed() {
        //Do nothing.
    }
}
