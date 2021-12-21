package com.hs.pickparking.Activities;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hs.pickparking.R;

import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ParkingInfoScreen extends AppCompatActivity {

    private ImageView logout;
    private Dialog dialog1;
    private String check,phone_no;
    private SessionManager sessionManager;
    private TextView parking_tv,current_time_tv,duration_tv,payment_tv;
    private long store_minutes,final_minutes,hours,days,minutes;
    private String st_minutes,st_hours,st_days,payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_info_screen);


        parking_tv=findViewById(R.id.parking_text);
        current_time_tv=findViewById(R.id.current_time_tv);
        duration_tv=findViewById(R.id.duration);
        payment_tv=findViewById(R.id.payment);

        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        sessionManager= new SessionManager(getApplicationContext());
        HashMap<String, String> usersDetails = sessionManager.GetUserDetailFromSession();
        phone_no = usersDetails.get(SessionManager.KEY_PHONE);










        long millis = System.currentTimeMillis();
        long seconds = millis / 1000;
        minutes = seconds / 60;

        ShowLoadingDialog();
        Query checkUser= FirebaseDatabase.getInstance().getReference("users").orderByChild("phone").equalTo(phone_no);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String s_mint=snapshot.child(phone_no).child("date").getValue(String.class);
                    store_minutes= Long.parseLong(s_mint);
                    check=snapshot.child(phone_no).child("check").getValue(String.class);
                    ShowData();
                }else {
                    Toast.makeText(getApplicationContext(), "Server Down", Toast.LENGTH_SHORT).show();
                    finish();
                    dialog1.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Server Down", Toast.LENGTH_SHORT).show();
                finish();
                dialog1.dismiss();
            }
        });
        Toast.makeText(getApplicationContext(),String.valueOf(store_minutes),Toast.LENGTH_LONG).show();



    }

    private void ShowData(){
        final_minutes=minutes-store_minutes;
        if (final_minutes<60){
            st_days="0";
            st_hours="0";
            st_minutes= String.valueOf(final_minutes);
        }else {
            hours=final_minutes/60;
            st_minutes=String.valueOf(final_minutes%60);
            if (hours<24){
                st_days="0";
                st_hours=String.valueOf(hours);
            }else {
                st_days=String.valueOf(hours/24);
                st_hours=String.valueOf(hours%24);
            }
        }

        if (hours==0){
            payment="10";
        }else{
            payment=String.valueOf(hours*10);
        }




        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String strDate =mdformat.format(calendar.getTime());

        current_time_tv.setText(strDate);
        parking_tv.setText(check);
        duration_tv.setText("D:"+st_days+"  H:"+st_hours+"  M:"+st_minutes);
        payment_tv.setText(payment);
        dialog1.dismiss();
    }
    public void signOut() {
        SessionManager sessionManager=new SessionManager(getApplicationContext());
        sessionManager.LogoutUserFromSession();
        Intent intent = new Intent(ParkingInfoScreen.this, LoginScreen.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void CheckOut(View view) {
        ShowLoadingDialog();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.child(phone_no).child("check").setValue("0");
        reference.child(phone_no).child("date").setValue("0");

        DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("parking");
        reference1.child(check).child("check").setValue("0");

        sessionManager.CreateLoginSession("0");
        sessionManager.CreateLoginSessions("0");
        dialog1.dismiss();


        Intent intent = new Intent(ParkingInfoScreen .this, DashboardScreen.class);
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