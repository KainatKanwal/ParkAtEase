package com.hs.pickparking.Activities;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hs.pickparking.R;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class LoginScreen extends AppCompatActivity {

    private TextInputLayout phoneNo,password;
    private String phone_no,pass;
    private Dialog dialog1;

    @Override
    protected void onStart() {
        super.onStart();
        SessionManager sessionManager=new SessionManager(LoginScreen.this);
        if(sessionManager.CheckLogin()){
            Intent intent = new Intent(LoginScreen.this, DashboardScreen.class);
            startActivity(intent);
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
        setContentView(R.layout.activity_login_screen);

        phoneNo=findViewById(R.id.phone);
        password=findViewById(R.id.password);
    }
    private Boolean ValidatePhoneNo(){
        String val=phoneNo.getEditText().getText().toString();
        phone_no=val;
        if (val.isEmpty()){
            phoneNo.setError("Field cannot be empty");
            return false;
        }else if (val.length() != 11) {
            phoneNo.setError("Invalid number");
            return false;
        }
        else {
            phoneNo.setError(null);
            phoneNo.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean ValidatePassword(){
        String val=password.getEditText().getText().toString();
        pass=val;
        if (val.isEmpty()){
            password.setError("Field cannot be empty");
            return false;
        }else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }



    public void LoginUser(View view) {
        ShowLoadingDialog();
        if (!ValidatePhoneNo() | !ValidatePassword()){
            return;
        }else {
            Query checkUser= FirebaseDatabase.getInstance().getReference("users").orderByChild("phone").equalTo(phone_no);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        phoneNo.setError(null);
                        phoneNo.setErrorEnabled(false);
                        String pas=snapshot.child(phone_no).child("password").getValue(String.class);
                        if (pas.equals(pass)){
                            password.setError(null);
                            password.setErrorEnabled(false);
                            String name=snapshot.child(phone_no).child("name").getValue(String.class);
                            String check=snapshot.child(phone_no).child("check").getValue(String.class);
                            SessionManager sessionManager=new SessionManager(LoginScreen.this);

                            if (check.equals("0")){
                                sessionManager.CreateLoginSessions("0");
                            }else {
                                Query checkUser1= FirebaseDatabase.getInstance().getReference("parking");
                                checkUser1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String check1=snapshot.child(check).child("check").getValue(String.class);
                                        SessionManager sessionManager=new SessionManager(LoginScreen.this);
                                        sessionManager.CreateLoginSessions(check1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }




                            sessionManager.CreateLoginSession(name,phone_no,pas,check);

                            Intent intent = new Intent(LoginScreen.this, DashboardScreen.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(),"Password does not match",Toast.LENGTH_LONG).show();
                            password.setError("invalid password");
                        }

                    }else {
                        Toast.makeText(getApplicationContext(),"This user is not exit",Toast.LENGTH_LONG).show();
                        phoneNo.setError("invalid Phone no");

                    }
                    dialog1.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    dialog1.dismiss();
                }
            });
        }
    }

    public void ForgetPasswordScreen(View view) {
        startActivity(new Intent(getApplicationContext(),ForgetPasswordScreen.class));
    }

    public void Signup(View view) {
        startActivity(new Intent(getApplicationContext(),SignUpScreen.class));
    }

    private void ShowLoadingDialog(){
        dialog1=new Dialog(LoginScreen.this);
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