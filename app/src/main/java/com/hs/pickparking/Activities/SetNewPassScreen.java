package com.hs.pickparking.Activities;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hs.pickparking.R;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class SetNewPassScreen extends AppCompatActivity {

    private String mobile;
    private Dialog dialog1;
    private TextInputLayout passOne,passTwo;
    private String pass;

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
        setContentView(R.layout.activity_set_new_pass_screen);
//        Intent intent = getIntent();
//        mobile = intent.getStringExtra("mobile");
        mobile="03487345567";
        passOne=findViewById(R.id.passOne);
        passTwo=findViewById(R.id.passTwo);
    }

    private Boolean ValidatePassword() {
        String val = passOne.getEditText().getText().toString();
        pass=val;
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
//                "(?=.*[a-zA-Z])" +      //any letter
//                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        if (val.isEmpty() && passTwo.getEditText().getText().toString().isEmpty()) {
            passOne.setError("Field cannot be empty");
            passTwo.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            passOne.setError("No white space and grater then 6 character");
            return false;
        } else if (!passOne.getEditText().getText().toString().equals(passTwo.getEditText().getText().toString())){
            passOne.setError("Password is not same");
            passTwo.setError("Password is not same");
            return false;
        }
        else {
            passOne.setError(null);
            passTwo.setError(null);
            passOne.setErrorEnabled(false);
            passTwo.setErrorEnabled(false);
            return true;
        }
    }

    public void SetNewPass(View view) {
        if (!ValidatePassword()){
            return;
        }
        ShowLoadingDialog();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.child(mobile).child("password").setValue(pass);
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