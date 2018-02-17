package com.sringa.unload.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sringa.unload.activity.BaseActivity;
import com.sringa.unload.activity.UserModeActivity;
import com.sringa.unload.activity.VehicleListActivity;
import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.db.AppUser;
import com.sringa.unload.service.TrackingService;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;

    private static final int RC_SIGN_IN = 123;

    protected void modifyMenu(Menu menu) {
        //DO nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Init DB.
        AppDataBase.init(this.getBaseContext());
        //Check if user exists??
        AppUser user = AppDataBase.INSTANCE.getAppUser();
//        if(null == user) {
//            user = new AppUser();
//            user.setUid("123456");
//            user.setPhone("9989364978");
//            user = AppDataBase.INSTANCE.addAppUser(user);
//        }
        if (null == user) {
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if (null == fbUser) {
                //User not yet registered. Start Phone verification.
                // Choose authentication providers
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
                );
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            } else {
                user = createAppUser(fbUser);
            }
        }
        if (null != user) {
            if (null == user.getMode()) {
                startActivity(new Intent(this, UserModeActivity.class));
            } else {
                startActivity(new Intent(this, VehicleListActivity.class));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == ResultCodes.OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                createAppUser(user);
                startActivity(new Intent(this, UserModeActivity.class));
            } else {
                Log.e("Phone Verification", "Phone verification failed.");
                Toast.makeText(getApplicationContext(), "Something went wrong in verification process.",
                        Toast.LENGTH_SHORT).show();
                System.exit(1);
            }
        }
    }

    private AppUser createAppUser(FirebaseUser user) {
        final AppUser appUser = new AppUser();
        appUser.setPhone(user.getPhoneNumber());
        appUser.setDisplayname(user.getDisplayName());
        appUser.setProviderid(user.getProviderId());
        appUser.setUid(user.getUid());
        return AppDataBase.INSTANCE.addAppUser(appUser);
    }

    @Override
    protected void onDestroy() {

        if (AppDataBase.INSTANCE.isServiceStarted()) {
            stopService(new Intent(getBaseContext(), TrackingService.class));
            AppDataBase.INSTANCE.serviceStopped();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //Do nothing.
    }
}
