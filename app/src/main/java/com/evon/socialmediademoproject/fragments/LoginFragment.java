package com.evon.socialmediademoproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evon.socialmediademoproject.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by sarabjjeet on 9/20/17.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
    private CallbackManager callbackManager;
    private LinearLayout ll_facebook;
    private TextView tv_facebook;
    private ImageView iv_facebook;
    private String str_facebookname, str_facebookemail, str_facebookid, str_birthday, str_location, str_gender;
    private boolean boolean_login;

    //CONSTRUCTOR
    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_fragment_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ll_facebook = (LinearLayout) view.findViewById(R.id.ll_facebook);
        tv_facebook = (TextView) view.findViewById(R.id.tv_facebook);
        iv_facebook = (ImageView) view.findViewById(R.id.iv_facebook);

        ll_facebook.setOnClickListener(this);
        tv_facebook.setOnClickListener(this);
        iv_facebook.setOnClickListener(this);
//        FacebookSdk.sdkInitialize(getActivity());
//        AppEventsLogger.activateApp(getActivity());

    }



    private void facebookLogin() {
        Log.e("in","facebookLogin");

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("ONSUCCESS", "User ID: " + loginResult.getAccessToken().getUserId()
                        + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken()
                );
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {

                                    boolean_login = true;
                                    tv_facebook.setText("Logout from Facebook");

                                    try {
                                        str_facebookname = object.getString("name");
                                    } catch (Exception e) {
                                        str_facebookname = "";
                                        e.printStackTrace();
                                    }

                                    try {
                                        str_facebookemail = object.getString("email");
                                    } catch (Exception e) {
                                        str_facebookemail = "";
                                        e.printStackTrace();
                                    }

                                    try {
                                        str_facebookid = object.getString("id");
                                    } catch (Exception e) {
                                        str_facebookid = "";
                                        e.printStackTrace();

                                    }

                                    try {
                                        str_birthday = object.getString("birthday");
                                    } catch (Exception e) {
                                        str_birthday = "";
                                        e.printStackTrace();
                                    }
                                    try {
                                        str_gender = object.getString("gender");
                                    } catch (Exception e) {
                                        str_birthday = "";
                                        e.printStackTrace();
                                    }

                                    try {
                                        JSONObject jsonobject_location = object.getJSONObject("location");
                                        str_location = jsonobject_location.getString("name");

                                    } catch (Exception e) {
                                        str_location = "";
                                        e.printStackTrace();
                                    }

                                    fn_profilepic();

                                } catch (Exception e) {

                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email,gender,birthday,location");

                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("in","cancel");
                if (AccessToken.getCurrentAccessToken() == null) {
                    return; // already logged out
                }
                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                        .Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        LoginManager.getInstance().logOut();
                        LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile,email,user_birthday,user_location"));
                        facebookLogin();
                    }
                }).executeAsync();
            }

            @Override
            public void onError(FacebookException e) {
                Log.e("in","error");
                Toast.makeText(getActivity(), "Internet connection error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void fn_profilepic() {

        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("type", "large");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "me/picture", params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        Log.e("Response 2", response + "");

                        try {
                            String str_facebookimage = (String) response.getJSONObject().getJSONObject("data").get("url");
                            Log.e("Picture", str_facebookimage);

                            //Glide.with(MainActivity.this).load(str_facebookimage).skipMemoryCache(true).into(iv_image);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("name",str_facebookname);
                        Log.e("email",str_facebookemail);
                        Log.e("id",str_facebookid);
                        Log.e("gender",str_gender);
                        Log.e("dob",str_birthday);
//                        tv_name.setText(str_facebookname);
//                        tv_email.setText(str_facebookemail);
//                        tv_id.setText(str_facebookid);
//                        tv_gender.setText((str_gender));
//                        tv_dob.setText(str_birthday);
//                        tv_location.setText(c);

                    }
                }
        ).executeAsync();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();

    }
    @Override
    public void onClick(View v) {

        if (boolean_login) {
            boolean_login = false;
            LoginManager.getInstance().logOut();
//            tv_location.setText("");
//            tv_dob.setText("");
//            tv_email.setText("");
//            tv_gender.setText("");
//            tv_id.setText("");
//            tv_name.setText("");
//            Glide.with(MainActivity.this).load(R.drawable.profile).into(iv_image);
            tv_facebook.setText("Login with Facebook");
        } else {
            LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile,email,user_birthday,user_location"));
            facebookLogin();
        }

    }
}
