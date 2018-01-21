package com.sringa.unload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sringa.unload.R;
import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.db.AppUser;
import com.sringa.unload.db.Constants;
import com.sringa.unload.service.ProtocolFormatter;
import com.sringa.unload.service.RequestManager;
import com.sringa.unload.service.TrackingService;

import java.util.HashMap;
import java.util.Map;

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
        // get selected radio button from radioGroup
        int selectedId = radioModeGroup.getCheckedRadioButtonId();
        radioSelected = (RadioButton) findViewById(selectedId);
        String mode = modeMap.get(radioSelected.getText().toString());
        AppUser user = AppDataBase.INSTANCE.updateAppUser("unload", mode);
//        RequestManager.Response response = RequestManager.sendPostRequest(Constants.APP_USER_RESOURCE, ProtocolFormatter.toJson(user));
//        if (response.isSuccess()) {
//            startActivity(new Intent(this, VehicleListActivity.class));
//            if (AppDataBase.INSTANCE.getAppUser().isDriverMode()) {
//                startService(new Intent(getBaseContext(), TrackingService.class));
//            }
//        }
    }

    @Override
    public void onBackPressed() {
        //Do nothing.
    }
}
