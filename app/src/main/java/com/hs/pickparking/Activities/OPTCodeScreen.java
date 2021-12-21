package com.hs.pickparking.Activities;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OPTCodeScreen extends AppCompatActivity {

    private Dialog dialog1;
    private String mVerificationId;
    private PinView optCode;
    private FirebaseAuth mAuth;
    private TextView textNumber;
    private String mobileWhatToDo;
    private String phone_no;
    private String name;
    private String password;
    private Button verify;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

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
        setContentView(R.layout.activity_optcode_screen);
        mAuth = FirebaseAuth.getInstance();
        optCode = findViewById(R.id.optCode);
        verify = findViewById(R.id.verifyBtn);
        textNumber=findViewById(R.id.textNumber);
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");




        Intent intent = getIntent();
        phone_no = intent.getStringExtra("phone_no");
        mobileWhatToDo=intent.getStringExtra("whatToDo");
        assert mobileWhatToDo != null;
        if (mobileWhatToDo.equals("signUp")){
            name = intent.getStringExtra("name");
            password = intent.getStringExtra("address");
            password = intent.getStringExtra("password");
        }
        sendVerificationCode(phone_no);



    }

    public void Resend(View view) {
        sendVerificationCode(phone_no);
    }

    public void Verify(View view) {
        ShowLoadingDialog();
        verify.setVisibility(View.GONE);
        String code = optCode.getText().toString().trim();
        if (code.isEmpty() || code.length() < 6) {
            optCode.setError("invalid code");
            optCode.requestFocus();
            return;
        }
        verifyVerificationCode(code);
    }

    private void sendVerificationCode(String mobile) {
        if (mobile.charAt(0) == '0') {
            mobile = mobile.substring(1);
        }
        final String _phoneNo = "+" + 92 + mobile;
        textNumber.setText(_phoneNo);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                _phoneNo,
                60,
                TimeUnit.SECONDS,
                OPTCodeScreen.this,
                mCallbacks);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                optCode.setText(code);
                ShowLoadingDialog();
                verify.setVisibility(View.GONE);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OPTCodeScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OPTCodeScreen.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog1.dismiss();
                            verify.setVisibility(View.VISIBLE);
                            if (mobileWhatToDo.equals("forgetPass")){
                                Intent intent = new Intent(OPTCodeScreen.this, SetNewPassScreen.class);
                                intent.putExtra("mobile", phone_no);
                                startActivity(intent);
                                finish();
                            }else if (mobileWhatToDo.equals("signUp")){
                                ShowLoadingDialog();
                                RegisterUser();
                            }
                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.verifyBtn), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                            dialog1.dismiss();
                            verify.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void RegisterUser(){
        UserHelperClass userHelperClass=new UserHelperClass(name,phone_no,password);
        reference.child(phone_no).setValue(userHelperClass);
        SessionManager sessionManager=new SessionManager(OPTCodeScreen.this);
        dialog1.dismiss();
        sessionManager.CreateLoginSession(name,phone_no,password,"0");
        sessionManager.CreateLoginSessions("0");
        Intent intent = new Intent(OPTCodeScreen.this, DashboardScreen.class);
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