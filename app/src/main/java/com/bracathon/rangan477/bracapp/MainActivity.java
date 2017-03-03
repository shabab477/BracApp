package com.bracathon.rangan477.bracapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.RelativeLayout;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main)RelativeLayout relativeLayout;
    @BindView(R.id.login_button) LoginButton loginButton;
    AnimationDrawable animationDrawable;
    CallbackManager callbackManager;
    ProfileTracker mProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        animationDrawable =(AnimationDrawable)relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);

        //facebook code
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new MyFacebookCallbackListener());
        loginButton.setReadPermissions(Arrays.asList("public_profile"));
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.bracathon.rangan477.bracapp", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e("HASH", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("ALGOEROR", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("EROR", "printHashKey()", e);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        animationDrawable.setOneShot(false);
        animationDrawable.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    class MyFacebookCallbackListener implements FacebookCallback<LoginResult>
    {

        @Override
        public void onSuccess(LoginResult loginResult) {
            if(Profile.getCurrentProfile() == null) {
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new profile
                        Log.v("facebook - profile", profile2.getFirstName());
                        mProfileTracker.stopTracking();
                        Profile.setCurrentProfile(profile2);
                    }
                };
                // no need to call startTracking() on mProfileTracker
                // because it is called by its constructor, internally.
            }
            else {
                Profile profile = Profile.getCurrentProfile();
                Log.v("facebook - profile", profile.getFirstName());
            }
        }

        @Override
        public void onCancel() {
            Log.v("facebook - onCancel", "cancelled");

        }

        @Override
        public void onError(FacebookException error) {
            Log.v("facebook - onError", error.getMessage());
        }
    }
}
