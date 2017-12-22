package in.sapota.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import in.sapota.db.AppDataBase;
import in.sapota.db.VehicleDetail;
import in.sapota.unload.R;
import in.sapota.utils.AssetsPropertyReader;

public class VehicleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView numberView;
    private TextView modelTypeText;
    private TextView tonnageText;
    private ImageButton imgBtn;
    private Button submitBtn, cancelBtn;
    private VehicleDetail vDetail;
    private Spinner stateSpinner, citySpinner;
    private final VehicleListAdapter mAdapter;
    private final Context context;
    private final LinearLayout expandableLayout;
    private boolean isExpanded = false;
    private String state, city;
    private Properties cityStates;

    public VehicleViewHolder(VehicleListAdapter mAdapter, View itemView, Context context) {
        super(itemView);
        cityStates = AssetsPropertyReader.getProperties(context, "StateAndCities.properties");
        this.mAdapter = mAdapter;
        numberView = (TextView) itemView.findViewById(R.id.vehicleNo);
        modelTypeText = (TextView) itemView.findViewById(R.id.vehicleModel);
        tonnageText = (TextView) itemView.findViewById(R.id.tonnage);
        imgBtn = (ImageButton) itemView.findViewById(R.id.toggleLoadBtn);
        submitBtn = (Button) itemView.findViewById(R.id.submitLoad);
        cancelBtn = (Button) itemView.findViewById(R.id.btn_cancel);
        expandableLayout = (LinearLayout) itemView.findViewById(R.id.layout_expandable);
        stateSpinner = (Spinner) itemView.findViewById(R.id.state);
        citySpinner = (Spinner) itemView.findViewById(R.id.city);
        imgBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        this.context = context;
        itemView.setOnClickListener(this);

        loadStateSpinner();
        loadCitySpinner(state);
        stateSpinner.setOnItemSelectedListener(this);
        citySpinner.setOnItemSelectedListener(this);
    }

    public void initView(VehicleDetail detail) {
        this.vDetail = detail;
        numberView.setText(vDetail.getNumber());
        modelTypeText.setText(vDetail.getModel());
        tonnageText.setText(String.valueOf(vDetail.getTonnage()));
        imgBtn.setImageResource(vDetail.getLoad() == 1 ? R.mipmap.ic_loaded
                : R.mipmap.ic_unloaded_truck);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitLoad:
                if (!validate()) {
                    return;
                }
                vDetail.setLoad(1 - vDetail.getLoad());
                if (AppDataBase.INSTANCE.addOrUpadate(vDetail))
                    mAdapter.notifyDataSetChanged();
                expandableLayout.setVisibility(View.GONE);
                isExpanded = false;
                break;
            case R.id.toggleLoadBtn:
                if (!isExpanded) {
                    expandableLayout.setVisibility(View.VISIBLE);
                    isExpanded = true;
                }
                break;
            case R.id.btn_cancel:
                if (isExpanded) {
                    expandableLayout.setVisibility(View.GONE);
                    isExpanded = false;
                }
                break;
            default:
                Intent intent = new Intent(context, AddVehicleActivity.class);
                intent.putExtra("mode", "EDIT");
                intent.putExtra("vehicle", vDetail);
                context.startActivity(intent);
        }
    }

    private boolean validate() {
        if("Select State".equals(state)){
            Toast.makeText(CallWs.this, "Error", Toast.LENGTH_SHORT).show();
        }
        if("Select City".equals(city)){

        }
    }

    private void loadStateSpinner() {
        state = "Select State";
        Set<String> states = cityStates.stringPropertyNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, new ArrayList<>(states));
        stateSpinner.setAdapter(adapter);
    }

    private void loadCitySpinner(String state) {
        String cityStr = cityStates.getProperty(state);
        String[] cities = cityStr.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, Arrays.asList(cities));
        citySpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getId() == R.id.state) {
            state = adapterView.getItemAtPosition(i).toString();
            loadCitySpinner(state);
        } else if (adapterView.getId() == R.id.city) {
            city = adapterView.getItemAtPosition(i).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}