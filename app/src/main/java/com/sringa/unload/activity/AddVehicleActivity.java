package com.sringa.unload.activity;

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

import com.sringa.unload.R;
import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.db.VehicleDetail;
import com.sringa.unload.utils.Utils;

public class AddVehicleActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button btnSubmit;
    private EditText vNumber;
    private EditText vTonnage;
    private Spinner axleSpinner;
    private Spinner modelSpinner;
    private String axleType;
    private String vmodel;
    private String mode = "NEW";
    private VehicleDetail vDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        vNumber = (EditText) findViewById(R.id.vnumber);
        vTonnage = (EditText) findViewById(R.id.vtonnage);
        axleSpinner = (Spinner) findViewById(R.id.axleType);
        modelSpinner = (Spinner) findViewById(R.id.vmodel);
        btnSubmit = (Button) findViewById(R.id.btnUpdate);

        axleSpinner.setOnItemSelectedListener(this);
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
                vNumber.setText(vDetail.getId());
                vNumber.setClickable(false);
                vNumber.setFocusable(false);
                vNumber.setKeyListener(null);
                vTonnage.setText(Integer.toString(vDetail.getTonnage()));
                axleSpinner.setSelection(getIndex(axleSpinner, vDetail.getAxle()));
                modelSpinner.setSelection(getIndex(modelSpinner, vDetail.getModel()));
                axleType = vDetail.getAxle();
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
            vDetail.setId(prepareNumber(vNumber.getText().toString()));
        }
        vDetail.setTonnage(Integer.valueOf(vTonnage.getText().toString()));
        vDetail.setModel(vmodel);
        vDetail.setAxle(axleType);
        boolean success = "EDIT".equalsIgnoreCase(mode) ? AppDataBase.INSTANCE.update(vDetail) : AppDataBase.INSTANCE.add(vDetail);
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

        if (null == axleType || "Select Axle Type".equals(axleType)) {
            Utils.setSpinnerError(axleSpinner, "Please Select Axle Type.");
            valid = false;
        }

        if (null == vmodel || "Select Model".equals(vmodel)) {
            Utils.setSpinnerError(modelSpinner, "Please Select Model.");
            valid = false;
        }
        return valid;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        if (parent.getId() == R.id.vmodel) {
            vmodel = parent.getItemAtPosition(pos).toString();
        } else if (parent.getId() == R.id.axleType) {
            axleType = parent.getItemAtPosition(pos).toString();
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
