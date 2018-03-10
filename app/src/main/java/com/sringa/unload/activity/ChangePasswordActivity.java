package com.sringa.unload.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sringa.unload.R;
import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.db.AppUser;
import com.sringa.unload.db.Constants;
import com.sringa.unload.service.IRequestManager;
import com.sringa.unload.service.ProtocolFormatter;
import com.sringa.unload.service.TrackingService;

import org.json.JSONObject;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private Button btnSubmit;
    private Button btnCancel;
    private TextView errorText;
    private EditText confirmPwd;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_change);

        password = (EditText) findViewById(R.id.password);
        confirmPwd = (EditText) findViewById(R.id.confirmPwd);
        errorText = (TextView) findViewById(R.id.errorText);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void modifyMenu(Menu menu) {
        menu.findItem(R.id.changePwd).setVisible(false);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnCancel) {
            proceed();
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(ChangePasswordActivity.this);
        dialog.setMessage("Processing...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        if (!AppDataBase.INSTANCE.isOnline() || !isDataValid()) {
            dialog.dismiss();
            return;
        }
        final IRequestManager.Method method = IRequestManager.Method.POST;
        final String url = Constants.APP_USER_CHANGE_PWD;
        AppUser appUser = AppDataBase.INSTANCE.getAppUser();
        final String pwd = password.getText().toString();
        AppUser user = new AppUser();
        user.setPhone(appUser.getPhone());
        user.setMode(appUser.getMode());
        user.setPassword(pwd);
        final JSONObject jsonObject = ProtocolFormatter.toJson(user);
        AppDataBase.INSTANCE.getRequestManager().sendAsyncRequest(method, url, jsonObject, new IRequestManager.IRequestHandler() {

            @Override
            public void onComplete(IRequestManager.Response response) {
                if (response.isSuccess()) {
                    boolean success = AppDataBase.INSTANCE.updatePassword(pwd);
                    if (success) {
                        proceed();
                    }
                    errorText.setVisibility(View.GONE);
                } else {
                    errorText.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }
        });
    }

    private void proceed() {

        if (!AppDataBase.INSTANCE.isServiceStarted()
                && AppDataBase.INSTANCE.getAppUser().isDriverMode()) {
            startService(new Intent(this, TrackingService.class));
            AppDataBase.INSTANCE.serviceStarted();
        }
        startActivity(new Intent(this, VehicleListActivity.class));
    }

    private boolean isDataValid() {

        boolean valid = true;
        String pwd = password.getText().toString();
        String confirmed = confirmPwd.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            password.setError("Password is required.");
            valid = false;
        }
        if (TextUtils.isEmpty(confirmed)) {
            confirmPwd.setError("Please confirm password");
            valid = false;
        }

        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(confirmed)) {
            if (!TextUtils.equals(pwd, confirmed)) {
                confirmPwd.setError("Password do not match with above.");
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
