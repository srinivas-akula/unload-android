package com.sringa.unload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.R;

public class VehicleListActivity extends BaseActivity implements View.OnClickListener {

    private FloatingActionButton fabVehicle;
    private VehicleListAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);
        fabVehicle = (FloatingActionButton) findViewById(R.id.fabAddVehicle);
        fabVehicle.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.rvVehicleList);
        adapter = new VehicleListAdapter(this, recyclerView);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        AppDataBase appDb = AppDataBase.INSTANCE;
        if (appDb.getUserMode().equals("Driver") && appDb.getVehicleCount() == 1) {
            fabVehicle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void modifyMenu(Menu menu) {
        menu.findItem(R.id.vehicles).setVisible(false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fabAddVehicle) {
            Intent intent = new Intent(this, AddVehicleActivity.class);
            intent.putExtra("mode", "NEW");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
