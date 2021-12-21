package com.hs.pickparking.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hs.pickparking.Activities.ConfirmParkingScreen;
import com.hs.pickparking.R;
import com.hs.pickparking.Utils.ParkingUtils;

import java.util.List;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder>{
    private Context context;
    private List<ParkingUtils> parkingDetails;

    public ParkingAdapter(Context context, List<ParkingUtils> parkingDetails) {
        this.context = context;
        this.parkingDetails = parkingDetails;
    }

    @NonNull
    @Override
    public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.parking_item, parent, false);
        return new ParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingViewHolder holder, int position) {
        ParkingUtils parkingUtils=parkingDetails.get(position);
        holder.text_yellow.setText(parkingUtils.getParking_name());
        holder.text_red.setText(parkingUtils.getParking_name());
        String check=parkingUtils.getCheck();

        if (check.equals("0")){
            holder.parking_card_yellow.setVisibility(View.VISIBLE);
            holder.parking_card_red.setVisibility(View.GONE);
            holder.itemView.setEnabled(true);
            holder.itemView.setClickable(true);
        }else {
            holder.parking_card_yellow.setVisibility(View.GONE);
            holder.parking_card_red.setVisibility(View.VISIBLE);
            holder.itemView.setEnabled(false);
            holder.itemView.setClickable(false);
        }

            holder.parking_card_yellow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, ConfirmParkingScreen.class);
                    intent.putExtra("parking",parkingUtils.getParking_name());
                    context.startActivity(intent);
                }
            });


    }

    @Override
    public int getItemCount() {
        return parkingDetails.size();
    }

    public class ParkingViewHolder extends RecyclerView.ViewHolder{
        TextView text_yellow,text_red;
        CardView parking_card_yellow,parking_card_red;

        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            text_yellow=itemView.findViewById(R.id.parking_text_yellow);
            parking_card_yellow=itemView.findViewById(R.id.parking_card_yellow);
            text_red=itemView.findViewById(R.id.parking_text_red);
            parking_card_red=itemView.findViewById(R.id.parking_card_red);
        }
    }
}
