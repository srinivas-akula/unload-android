package com.sringa.unload.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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

import java.util.HashMap;
import java.util.Map;

import static com.sringa.unload.db.Constants.APP_USER_RESOURCE;
import static com.sringa.unload.db.Constants.USER_PASSWORD;

public class UserModeActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup radioModeGroup;
    private RadioButton radioSelected;
    private Button btnSubmit;

    private final Map<String, String> modeMap = new HashMap<String, String>() {
        {
            put("Owner", "O");
            put("Driver", "D");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppDataBase.init(this.getBaseContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mode);
        radioModeGroup = (RadioGroup) findViewById(R.id.radioUserMode);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        final ProgressDialog dialog = new ProgressDialog(UserModeActivity.this);
        dialog.setMessage("Processing...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        // get selected radio button from radioGroup
        int selectedId = radioModeGroup.getCheckedRadioButtonId();
        radioSelected = (RadioButton) findViewById(selectedId);
        final String mode = modeMap.get(radioSelected.getText().toString());
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
                            goToListView();
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
