package com.hs.pickparking.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hs.pickparking.R;

public class ForgetPasswordScreen extends AppCompatActivity {

    private Dialog dialog1;
    private TextInputLayout forgetPhone;
    private Button forgetPhoneBtn;
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
        setContentView(R.layout.activity_forget_password_screen);
        forgetPhone=findViewById(R.id.forgetPhone);
        forgetPhoneBtn=findViewById(R.id.forgetPhoneBtn);
    }

    public void CheckPhoneNo(View view) {
        ShowLoadingDialog();
        String phone_no=forgetPhone.getEditText().getText().toString().trim();
        Query checkUser= FirebaseDatabase.getInstance().getReference("users").orderByChild("phone").equalTo(phone_no);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    forgetPhone.setError(null);
                    forgetPhone.setErrorEnabled(false);
                    Intent intent=new Intent(ForgetPasswordScreen.this,OPTCodeScreen.class);
                    intent.putExtra("phone_no",phone_no);
                    intent.putExtra("whatToDo","forgetPass");
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"This user does not exist",Toast.LENGTH_LONG).show();
                    forgetPhone.setError("invalid phone no");
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
    private void ShowLoadingDialog(){
        dialog1=new Dialog(ForgetPasswordScreen.this);
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