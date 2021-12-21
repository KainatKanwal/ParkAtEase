package com.hs.pickparking.Activities;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hs.pickparking.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class ConfirmParkingScreen extends AppCompatActivity {

    private ImageView logout;
    private String parking;
    private TextView parking_tv;
    private TextView start_time_tv;
    private Dialog dialog1;
    private long minutes;

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
        setContentView(R.layout.activity_confirm_parking_screen);

        parking_tv=findViewById(R.id.parking_text);
        start_time_tv=findViewById(R.id.start_time_tv);
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });


        Intent intent = getIntent();
        parking = intent.getStringExtra("parking");

        parking_tv.setText(parking);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String strDate =mdformat.format(calendar.getTime());

        start_time_tv.setText(strDate);

        long millis = System.currentTimeMillis();
        long seconds = millis / 1000;
        minutes = seconds / 60;


    }
    public void signOut() {
        SessionManager sessionManager=new SessionManager(getApplicationContext());
        sessionManager.LogoutUserFromSession();
        Intent intent = new Intent(ConfirmParkingScreen.this, LoginScreen.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void Confirm(View view) {
        ShowLoadingDialog();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> usersDetails = sessionManager.GetUserDetailFromSession();
        String mobile = usersDetails.get(SessionManager.KEY_PHONE);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.child(mobile).child("check").setValue(parking);
        reference.child(mobile).child("date").setValue(String.valueOf(minutes));


        DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("parking");
        reference1.child(parking).child("check").setValue("book");


        sessionManager.CreateLoginSession(parking);
        sessionManager.CreateLoginSessions("book");
        dialog1.dismiss();


        Intent intent = new Intent(ConfirmParkingScreen .this, DashboardScreen.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    private void ShowLoadingDialog(){
        dialog1=new Dialog(this);
        dialog1.setContentView(R.layout.item_loading_dialog);
//            set outside touch
        dialog1.setCanceledOnTouchOutside(false);
//            set dialog box width and height
        dialog1.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
//            set transparent background
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            set animation
        dialog1.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
//            initialize dialog variable btn

        dialog1.show();
    }

}