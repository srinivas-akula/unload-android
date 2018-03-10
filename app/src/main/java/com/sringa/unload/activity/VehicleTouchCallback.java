package com.sringa.unload.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.sringa.unload.R;
import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.db.Constants;
import com.sringa.unload.db.VehicleDetail;
import com.sringa.unload.service.IRequestManager;
import com.sringa.unload.service.ProtocolFormatter;

import org.json.JSONObject;

import java.util.List;

public class VehicleTouchCallback extends ItemTouchHelper.SimpleCallback {

    private VehicleListAdapter adapter;
    private List<VehicleDetail> vehicleDetails;
    private Context context;

    VehicleTouchCallback(VehicleListAdapter adapter, Context context, List<VehicleDetail> vehicleDetails) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.context = context;
        this.vehicleDetails = vehicleDetails;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition(); //get position which is swipe

        if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setMessage(R.string.confirm_text);
            builder.setPositiveButton(R.string.menu_delete_vehicle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    delete(position);
                    return;
                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemRemoved(position + 1);
                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                    return;
                }
            }).show();
        }
    }

    private void delete(final int position) {

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Processing...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        final VehicleDetail vDetail = vehicleDetails.get(position);
        JSONObject jsonObject = ProtocolFormatter.toJson(vDetail);
        final String url = String.format(Constants.VEHICLE_ID_RESOURCE, vDetail.getNumber());
        AppDataBase.INSTANCE.getRequestManager().sendAsyncRequest(IRequestManager.Method.DELETE, url, jsonObject, new IRequestManager.IRequestHandler() {

            @Override
            public void onComplete(IRequestManager.Response response) {
                if (response.isSuccess()) {
                    if (AppDataBase.INSTANCE.deleteVehicle(vDetail)) {
                        vehicleDetails.remove(position);
                        if (vehicleDetails.isEmpty()) {
                            context.startActivity(new Intent(context, VehicleListActivity.class));
                        } else {
                            adapter.notifyItemRemoved(position);
                        }
                    } else {
                        adapter.notifyItemRemoved(position + 1);
                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                    }
                } else {
                    adapter.notifyItemRemoved(position + 1);
                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                }
                dialog.dismiss();
            }
        });
    }
}
