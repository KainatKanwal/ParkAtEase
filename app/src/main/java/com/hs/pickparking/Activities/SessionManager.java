package com.hs.pickparking.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    private SharedPreferences user_session;
    private SharedPreferences.Editor editor;
    protected Context context;
    private static final String IS_LOGIN ="IsLoggedIn";
    public static final String KEY_NAME ="name";
    public static final String KEY_PHONE ="PhoneNo";
    public static final String KEY_PASSWORD ="Password";
    public static final String KEY_CHECK ="check";
    public static final String KEY_STAGE="stage";

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context){
        this.context=context;
        user_session=this.context.getSharedPreferences("UserLoginSession",Context.MODE_PRIVATE);
        editor = user_session.edit();
    }

    public void CreateLoginSession(String cnic,String phone,String password,String check){
//    public void CreateLoginSession(String name,String phone,String password,String address){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_NAME,cnic);
        editor.putString(KEY_PHONE,phone);
        editor.putString(KEY_PASSWORD,password);
        editor.putString(KEY_CHECK,check);
        editor.commit();
    }
    public void CreateLoginSession(String check){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_CHECK,check);
        editor.commit();
    }
    public void CreateLoginSessions(String stage){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_STAGE,stage);
        editor.commit();
    }
    public HashMap<String,String> GetUserDetailFromSession(){
        HashMap<String,String> userdata= new HashMap<String,String>();
        userdata.put(KEY_NAME,user_session.getString(KEY_NAME,null));
        userdata.put(KEY_PHONE,user_session.getString(KEY_PHONE,null));
        userdata.put(KEY_PASSWORD,user_session.getString(KEY_PASSWORD,null));
        userdata.put(KEY_CHECK,user_session.getString(KEY_CHECK,null));
        userdata.put(KEY_STAGE,user_session.getString(KEY_STAGE,null));
        return userdata;
    }
    public boolean CheckLogin(){
        return user_session.getBoolean(IS_LOGIN, false);

    }

    public void LogoutUserFromSession(){
        editor.clear();
        editor.commit();
    }




}
