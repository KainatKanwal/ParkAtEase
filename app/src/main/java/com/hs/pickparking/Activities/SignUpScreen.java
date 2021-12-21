package com.hs.pickparking.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.internal.ShowFirstParty;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hs.pickparking.R;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class SignUpScreen extends AppCompatActivity {

    private Dialog dialog1;
    private TextInputLayout regName, regPhone, regPassword, regConfirmPassword;
    private Button reg_btn;
    private String phone_no;

    @Override
    protected void onStart() {
        super.onStart();
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
        setContentView(R.layout.activity_sign_up_screen);

        regName = findViewById(R.id.regName);
        regPhone = findViewById(R.id.regPhone);
        regPassword = findViewById(R.id.regPassword);
        regConfirmPassword=findViewById(R.id.regConfirmPassword);
        reg_btn=findViewById(R.id.regBtn);
    }

    private Boolean ValidateName() {
        String val = regName.getEditText().getText().toString();
        if (val.isEmpty()) {
            regName.setError("Field cannot be empty");
            return false;
        } else {
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean ValidatePhone() {
        final String val = regPhone.getEditText().getText().toString();
        phone_no=val;
        if (val.isEmpty()) {
            regPhone.setError("Field cannot be empty");
            return false;
        } else if (val.length() != 11) {
            regPhone.setError("Invalid number");
            return false;
        }else {
            regPhone.setError(null);
            regPhone.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean ValidatePassword() {
        String val = regPassword.getEditText().getText().toString();
        String val2 = regConfirmPassword.getEditText().getText().toString();
        String passwordVal = "^" +
//                "(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
//                "(?=.*[a-zA-Z])" +      //any letter
//                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{6,}" +               //at least 4 characters
                "$";
        if (val.isEmpty()) {
            regPassword.setError("Field cannot be empty");
            regConfirmPassword.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            regPassword.setError("No white space and grater then 6 character");
            regConfirmPassword.setError("No white space and grater then 6 character");
            return false;
        } else if (!val.equals(val2)){
            regPassword.setError("Incorrect confirm password");
            regConfirmPassword.setError("Incorrect confirm password");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            regConfirmPassword.setError(null);
            regConfirmPassword.setErrorEnabled(false);
            return true;
        }
    }




    public void Signup(View view) {
        if (!ValidatePhone() | !ValidatePassword() | !ValidateName()) {
//            dialog1.dismiss();
        }else {
//            dialog1.dismiss();
            CheckNumber();
        }
    }

    private void CheckNumber(){
        ShowLoadingDialog();
        boolean check=false;
        Query checkUser= FirebaseDatabase.getInstance().getReference("users").orderByChild("phone").equalTo(phone_no);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Toast.makeText(getApplicationContext(),"This user is already exist",Toast.LENGTH_LONG).show();
                }else{
                    SendData();
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

    private void SendData() {
        String phone_no = regPhone.getEditText().getText().toString();
        String name = regName.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();

        Intent intent = new Intent(SignUpScreen.this, OPTCodeScreen.class);
        intent.putExtra("phone_no",phone_no);
        intent.putExtra("name",name);
        intent.putExtra("password",password);
        intent.putExtra("whatToDo", "signUp");
        startActivity(intent);
        finish();
    }

    public void BackToLogin(View view) {
        finish();
    }

    private void ShowLoadingDialog(){
        dialog1=new Dialog(SignUpScreen.this);
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