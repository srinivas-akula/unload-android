package in.sapota.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import in.sapota.db.AppDataBase;
import in.sapota.db.VehicleDetail;
import in.sapota.unload.R;

public class AddVehicleActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button btnSubmit;
    private EditText vNumber;
    private EditText vTonnage;
    private Spinner typeSpinner;
    private Spinner modelSpinner;
    private String vtype;
    private String vmodel;
    private String mode = "NEW";
    private VehicleDetail vDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        vNumber = (EditText) findViewById(R.id.vnumber);
        vTonnage = (EditText) findViewById(R.id.vtonnage);
        typeSpinner = (Spinner) findViewById(R.id.vtype);
        modelSpinner = (Spinner) findViewById(R.id.vmodel);
        btnSubmit = (Button) findViewById(R.id.btnUpdate);

        typeSpinner.setOnItemSelectedListener(this);
        modelSpinner.setOnItemSelectedListener(this);
        btnSubmit.setOnClickListener(this);
        fillValues();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fillValues() {

        String mode = getIntent().getStringExtra("mode");
        if (null != mode && "EDIT".equalsIgnoreCase(mode)) {
            this.mode = mode;
            vDetail = (VehicleDetail) getIntent().getSerializableExtra("vehicle");
            if (null != vDetail) {
                vNumber.setText(vDetail.getNumber());
                vNumber.setClickable(false);
                vNumber.setFocusable(false);
                vNumber.setKeyListener(null);
                vTonnage.setText(Integer.toString(vDetail.getTonnage()));
                typeSpinner.setSelection(getIndex(typeSpinner, vDetail.getType()));
                modelSpinner.setSelection(getIndex(modelSpinner, vDetail.getModel()));
                vtype = vDetail.getType();
                vmodel = vDetail.getModel();
            }
        }
    }

    private int getIndex(Spinner spinner, String value) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                index = i;
                break;
            }
        }
        return index;
    }

    protected void modifyMenu(Menu menu) {
        //Do nothing.
    }

    @Override
    public void onClick(View v) {

        if (!isDataValid()) {
            return;
        }
        VehicleDetail vDetail = null;
        if ("EDIT".equalsIgnoreCase(mode) && null != this.vDetail) {
            vDetail = this.vDetail;
        } else {
            vDetail = new VehicleDetail();
            vDetail.setNumber(prepareNumber(vNumber.getText().toString()));
        }
        vDetail.setTonnage(Integer.valueOf(vTonnage.getText().toString()));
        vDetail.setModel(vmodel);
        vDetail.setType(vtype);
        boolean success = AppDataBase.INSTANCE.addOrUpadate(vDetail);
        if (success) {
            startActivity(new Intent(this, VehicleListActivity.class));
        }
    }

    private boolean isDataValid() {

        boolean valid = true;
        String number = vNumber.getText().toString();
        if (TextUtils.isEmpty(number)) {
            vNumber.setError("Vehicle Number is required.");
            valid = false;
        } else if ("NEW".equalsIgnoreCase(mode)) {
            number = prepareNumber(number);
            VehicleDetail detail = AppDataBase.INSTANCE.getVehicle(number);
            if (null != detail) {
                vNumber.setError("Vehicle already exists with this number.");
                return false;
            }
        }

        if (TextUtils.isEmpty(vTonnage.getText().toString())) {
            vTonnage.setError("Maximum Tonnage is required.");
            valid = false;
        }

        if (null == vtype) {
            vtype = getResources().getStringArray(R.array.vehicle_type_values)[0];
        }

        if (null == vmodel) {
            vmodel = getResources().getStringArray(R.array.vehicle_model_values)[0];
        }
        return valid;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        if (parent.getId() == R.id.vmodel) {
            vmodel = parent.getItemAtPosition(pos).toString();
        } else if (parent.getId() == R.id.vtype) {
            vtype = parent.getItemAtPosition(pos).toString();
        }
    }

    public void onNothingSelected(AdapterView parent) {
        // Do nothing.
    }

    private String prepareNumber(String number) {
        return number.trim().toUpperCase();
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
