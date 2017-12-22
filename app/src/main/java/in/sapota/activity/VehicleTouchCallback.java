package in.sapota.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.List;

import in.sapota.db.AppDataBase;
import in.sapota.db.VehicleDetail;
import in.sapota.unload.R;

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
                    VehicleDetail vDetail = vehicleDetails.get(position);
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
}
