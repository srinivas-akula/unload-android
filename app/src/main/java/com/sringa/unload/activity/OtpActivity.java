package com.sringa.unload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.db.AppUser;
import com.sringa.unload.service.ProtocolFormatter;
import com.sringa.unload.service.R;
import com.sringa.unload.service.RequestManager;

public class OtpActivity extends AppCompatActivity implements
        View.OnClickListener {

    private String phoneNumber;
    private EditText mVerificationField;
    private Button mVerifyButton;
    private Button mResendButton;
    private TextView phoneTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        mVerificationField = findViewById(R.id.field_verification_code);
        mVerifyButton = findViewById(R.id.button_verify_phone);
        mResendButton = findViewById(R.id.button_resend);
        phoneTxt = (TextView) findViewById(R.id.phoneNumber);
        phoneTxt.setText(" Phone: " + phoneNumber);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }
                final String request = ProtocolFormatter.formatRequest(phoneNumber, code);
                if (RequestManager.sendGetRequest(this, request, true)) {
                    Toast.makeText(getApplicationContext(), "Your phone successfully verified.",
                            Toast.LENGTH_SHORT).show();
                    AppDataBase.INSTANCE.addAppUser(new AppUser(phoneNumber));
                    Intent intent = new Intent(this, UserModeActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.button_resend:
                break;
        }
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
