package com.greenapex.mightyhomeplanz;

import com.greenapex.Request.models.LoginRequest;
import com.greenapex.Utils.Constants;
import com.greenapex.Utils.Utils;
import com.greenapex.webservice.LoginWebservice;
import com.greenapex.widgets.CustomEditText;
import com.greenapex.widgets.CustomTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greenapex.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Signin extends Activity implements OnClickListener, LoginWebservice.LoginWebserviceHandler {

    Activity activity;
    CustomTextView tvCreateAccount, tvForgotPassword, tvSignin, tvFacebook;
    private CustomEditText etUsername_Signin;
    private CustomEditText etPassword_Signin;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signin);

        activity = Signin.this;
        gson = new GsonBuilder().create();
        init();

    }

    public void init() {
        tvCreateAccount = (CustomTextView) findViewById(R.id.tvCreateAccount_Signin);
        tvForgotPassword = (CustomTextView) findViewById(R.id.tvForgotPassword_Signin);
        tvSignin = (CustomTextView) findViewById(R.id.tvsignin_Signin);
        etUsername_Signin = (CustomEditText) findViewById(R.id.etUsername_Signin);
        etPassword_Signin = (CustomEditText) findViewById(R.id.etPassword_Signin);

        tvCreateAccount.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvSignin.setOnClickListener(this);
    }


    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        // TODO onClick
        switch (v.getId()) {

            case R.id.tvCreateAccount_Signin:
                startActivity(new Intent(activity, Signup.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;

            case R.id.tvForgotPassword_Signin:
                startActivity(new Intent(activity, ForgotPassword.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;

            case R.id.tvsignin_Signin:
                if (validateData()) {
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setEmail(etUsername_Signin.getText().toString());
//                    loginRequest.setEmail("nilay.khandhar@green-apex.com");
                    loginRequest.setPassword(Utils.stringToMd5(etPassword_Signin.getText().toString()));
                    loginRequest.setRole("owner");

                    String strParams = getGson().toJson(loginRequest);
                    LoginWebservice loginWebservice = new LoginWebservice(this, this);
//                String url = Constants.loginWebservice+"email="+loginRequest.getEmail()
//                        + "&password="+loginRequest.getPassword()
//                        + "&role="+loginRequest.getRole();

                    try {
                        JSONObject params = new JSONObject(strParams);
                        loginWebservice.callService(params);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }


//                startActivity(new Intent(activity, Home.class));
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
                break;
        }
    }

    private boolean validateData() {
        boolean isvalid = true;
        if (etUsername_Signin.getText().length() <= 0) {
            Toast.makeText(this, "Looks like you forgot to enter your username !!!", Toast.LENGTH_SHORT).show();
            etUsername_Signin.requestFocus();
            isvalid = false;
            return isvalid;
        }
        if (etPassword_Signin.getText().length() <= 0) {
            Toast.makeText(this, "Looks like you forgot to enter your password !!!", Toast.LENGTH_SHORT).show();
            etPassword_Signin.requestFocus();
            isvalid = false;
            return isvalid;
        }


        return isvalid;
    }

    @Override
    public void loginWebserviceStart() {
//        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginWebserviceSucessful(String response, String message) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginWebserviceFailedWithMessage(String message) {
        Toast.makeText(this, "Fail:" + message, Toast.LENGTH_SHORT).show();
    }


    protected Gson getGson() {
        return gson;
    }
}
