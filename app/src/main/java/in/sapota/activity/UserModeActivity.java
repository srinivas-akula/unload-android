package in.sapota.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import in.sapota.db.AppDataBase;
import in.sapota.unload.R;

public class UserModeActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup radioModeGroup;
    private RadioButton radioSelected;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        AppDataBase.INSTANCE.updateAppUser(radioSelected.getText().toString());
        startActivity(new Intent(this, VehicleListActivity.class));
    }

    @Override
    public void onBackPressed() {
        //Do nothing.
    }
}
