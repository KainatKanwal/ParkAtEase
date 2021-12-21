package com.hs.pickparking.Activities;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hs.pickparking.Adapters.ParkingAdapter;
import com.hs.pickparking.R;
import com.hs.pickparking.Utils.ParkingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardScreen extends AppCompatActivity {

    private ImageView logout;

    private RecyclerView parking_list;
    private ParkingAdapter parkingAdapter;
    private ArrayList<ParkingUtils> parkingDetails;
    private DatabaseReference reference;
    private Button check_out_btn,check_in_btn;

    @Override
    protected void onStart() {
        super.onStart();

        SessionManager sessionManager=new SessionManager(this);
        if(!sessionManager.CheckLogin()){
            finish();
        }
        if (!(new CheckInternet().isConnected(getApplicationContext()))){
            //initialize dialog box
            final Dialog dialog=new Dialog(this);
            //set content view
            dialog.setContentView(R.layout.item_internet_dialog);
//            set outside touch
            dialog.setCanceledOnTouchOutside(false);
//            set dialog box width and height
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
//            set transparent background
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            set animation
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
//            initialize dialog variable btn
            Button try_btn=dialog.findViewById(R.id.dialog_try_btn);
            try_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    recreate();

                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);

        logout=findViewById(R.id.logout);
        parking_list=findViewById(R.id.parking_list);
        check_in_btn=findViewById(R.id.check_in_btn);
        check_out_btn=findViewById(R.id.check_out_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });





        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> usersDetails = sessionManager.GetUserDetailFromSession();
        String check = usersDetails.get(SessionManager.KEY_CHECK);
        String stage = usersDetails.get(SessionManager.KEY_STAGE);

        assert stage != null;
        if(stage.equals("0")){
            check_in_btn.setVisibility(View.GONE);
            check_out_btn.setVisibility(View.GONE);
            parking_list.setVisibility(View.VISIBLE);
        }else if (stage.equals("book")){
            check_in_btn.setVisibility(View.VISIBLE);
            check_out_btn.setVisibility(View.GONE);
            parking_list.setVisibility(View.GONE);
        }else if (stage.equals("checkin")){
            check_in_btn.setVisibility(View.GONE);
            check_out_btn.setVisibility(View.VISIBLE);
            parking_list.setVisibility(View.GONE);
        }










        parking_list.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(DashboardScreen.this, 2);
        parking_list.setLayoutManager(mLayoutManager);
        parkingDetails = new ArrayList<>();
        parkingAdapter=new ParkingAdapter(this,parkingDetails);
        parking_list.setAdapter(parkingAdapter);


        reference= FirebaseDatabase.getInstance().getReference("parking");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                parkingDetails.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ParkingUtils parkingUtils=dataSnapshot.getValue(ParkingUtils.class);
                    parkingDetails.add(parkingUtils);
                }
                parkingAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }





    public void signOut() {
        SessionManager sessionManager=new SessionManager(getApplicationContext());
        sessionManager.LogoutUserFromSession();
        Intent intent = new Intent(DashboardScreen.this, LoginScreen.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void CHeckOut(View view) {
        Intent intent = new Intent(DashboardScreen.this, ParkingInfoScreen.class);
        startActivity(intent);
    }

    public void CheckIn(View view) {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> usersDetails = sessionManager.GetUserDetailFromSession();
        String parking = usersDetails.get(SessionManager.KEY_CHECK);
        DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("parking");
        reference1.child(parking).child("check").setValue("checkin");
        sessionManager.CreateLoginSessions("checkin");
        check_in_btn.setVisibility(View.GONE);
        check_out_btn.setVisibility(View.VISIBLE);
        parking_list.setVisibility(View.GONE);
    }
}