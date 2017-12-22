package com.sringa.unload.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.db.VehicleDetail;
import com.sringa.unload.service.R;

public class VehicleListAdapter extends
        RecyclerView.Adapter<VehicleViewHolder> {

    private List<VehicleDetail> vehicleDetails;
    private final RecyclerView recyclerView;
    private final Context context;

    public VehicleListAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        vehicleDetails = AppDataBase.INSTANCE.getVehicleDetails();
        final ItemTouchHelper.SimpleCallback callback = new VehicleTouchCallback(this, context, vehicleDetails);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View vehicleView = inflater.inflate(R.layout.vehicle_card, parent, false);
        // Return a new holder instance
        return new VehicleViewHolder(this, vehicleView, this.context);
    }

    @Override
    public void onBindViewHolder(VehicleViewHolder holder, int position) {
        final VehicleDetail vDetail = vehicleDetails.get(position);
        holder.initView(vDetail);
    }

    @Override
    public int getItemCount() {
        return vehicleDetails.size();
    }
}
