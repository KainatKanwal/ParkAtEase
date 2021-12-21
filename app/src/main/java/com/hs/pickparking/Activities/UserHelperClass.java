package com.hs.pickparking.Activities;

public class UserHelperClass {
    private String name;
    private String phone;
    private String password;
    private String check;
    private String date;

    public UserHelperClass() {
    }

    public UserHelperClass(String name, String phone, String password) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.check = "0";
        this.date = "0";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
