package in.sapota.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import in.sapota.unload.ProtocolFormatter;
import in.sapota.unload.R;
import in.sapota.unload.RequestManager;

public class UserPhoneAuthActivity extends AppCompatActivity implements
        View.OnClickListener {

    private EditText mPhoneNumberField;
    private Button mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mStartButton = (Button) findViewById(R.id.button_start_verification);
        mStartButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        final String phoneNumber = mPhoneNumberField.getText().toString();
        if (view.getId() == R.id.button_start_verification
                && validatePhoneNumber(phoneNumber)) {
            final String request = ProtocolFormatter.formatRequest(phoneNumber);
            if (RequestManager.sendGetRequest(this, request, true)) {
                Toast.makeText(getApplicationContext(), "OTP request sent. Please wait for the message.",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, OtpActivity.class);
                intent.putExtra("phoneNumber", mPhoneNumberField.getText().toString());
                startActivity(intent);
            }
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {

            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //Do nothing.
    }
}
