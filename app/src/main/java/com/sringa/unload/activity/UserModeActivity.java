package com.sringa.unload.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sringa.unload.R;
import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.db.AppUser;
import com.sringa.unload.db.VehicleDetail;
import com.sringa.unload.service.IRequestManager;
import com.sringa.unload.service.ProtocolFormatter;
import com.sringa.unload.service.TrackingService;

import org.json.JSONObject;

import static com.sringa.unload.db.Constants.APP_USER_RESOURCE;
import static com.sringa.unload.db.Constants.USER_PASSWORD;

public class UserModeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView driverIcon;
    private ImageView ownerIcon;
    private TextView errorText;
    private String mode = "O";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppDataBase.init(this.getBaseContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mode);
        driverIcon = (ImageView) findViewById(R.id.driverIcon);
        driverIcon.setOnClickListener(this);
        ownerIcon = (ImageView) findViewById(R.id.ownerIcon);
        ownerIcon.setOnClickListener(this);
        errorText = (TextView) findViewById(R.id.errorText);
    }

    @Override
    public void onClick(View v) {

        final ImageView imageView = (ImageView) v;
        if (R.id.driverIcon == imageView.getId()) {
            mode = "D";
            final int hasPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        111);
                return;
            } else {
                processMode();
            }
        } else {
            processMode();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 111:
                processMode();
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Permission Denied
                    Toast.makeText(UserModeActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void processMode() {

        final ProgressDialog dialog = new ProgressDialog(UserModeActivity.this);
        dialog.setMessage("Processing...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        AppUser user = AppDataBase.INSTANCE.updateAppUser(USER_PASSWORD, mode);
        final JSONObject jsonObject = ProtocolFormatter.toJson(user);
        AppDataBase.INSTANCE.getRequestManager().sendAsyncRequest(IRequestManager.Method.POST, APP_USER_RESOURCE,
                jsonObject, new IRequestManager.IRequestHandler() {

                    @Override
                    public void onComplete(IRequestManager.Response response) {
                        if (response.isSuccess()) {
                            createVehiclesFromResponse(response.getResponse());
                            if (!AppDataBase.INSTANCE.isServiceStarted()
                                    && AppDataBase.INSTANCE.getVehicleCount() > 0
                                    && AppDataBase.INSTANCE.getAppUser().isDriverMode()) {
                                startService();
                                AppDataBase.INSTANCE.serviceStarted();
                            }
                            errorText.setVisibility(View.GONE);
                            goToListView();
                        } else {
                            errorText.setVisibility(View.VISIBLE);
                        }
                        dialog.dismiss();
                    }
                });
    }

    private void goToListView() {
        startActivity(new Intent(this, VehicleListActivity.class));
    }

    private void startService() {
        startService(new Intent(this, TrackingService.class));
    }

    @Override
    public void onBackPressed() {
        //Do nothing.
    }

    private void createVehiclesFromResponse(String response) {

        if (null != response) {
            try {
                Gson gson = new Gson();
                JsonObject jsonObject = (new JsonParser()).parse(response).getAsJsonObject();
                if (jsonObject.has("user")) {
                    JsonElement user = jsonObject.get("user");
                    AppUser appUser = gson.fromJson(user, AppUser.class);
                    if (jsonObject.has("devices")) {
                        JsonArray devicesElem = jsonObject.get("devices").getAsJsonArray();
                        if (null != devicesElem && devicesElem.size() > 0) {
                            for (int i = 0; i < devicesElem.size(); i++) {
                                final JsonObject element = devicesElem.get(i).getAsJsonObject();
                                final VehicleDetail detail = new VehicleDetail();
                                detail.setNumber(element.get("uniqueId").getAsString());
                                detail.setModel(element.get("model").getAsString());
                                JsonObject attributes = element.get("attributes").getAsJsonObject();
                                if (attributes.has("tonnage"))
                                    detail.setTonnage(attributes.get("tonnage").getAsInt());
                                if (attributes.has("axle"))
                                    detail.setAxle(attributes.get("axle").getAsString());
                                AppDataBase.INSTANCE.add(detail);
                                if ("D".equalsIgnoreCase(appUser.getMode())) {
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Read Response", e.toString());
            }
        }
    }
}
