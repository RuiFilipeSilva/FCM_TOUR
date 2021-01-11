package com.example.fcm_tour.Views;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fcm_tour.Controllers.Users;
import com.example.fcm_tour.Homepage;
import com.example.fcm_tour.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SocialMediaAuth extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Intent homePage;
    LoginButton loginButton;
    String loginType; // "Normal" / "Google" / "Facebook"
    //Google Variables
    private static final int RC_GET_TOKEN = 9002;
    CallbackManager callbackManager;
    GoogleSignInAccount account;
    GoogleSignInClient mGoogleSignInClient;

    private String mParam1;
    private String mParam2;

    public SocialMediaAuth() {
        // Required empty public constructor
    }

    public static SocialMediaAuth newInstance(String param1, String param2) {
        SocialMediaAuth fragment = new SocialMediaAuth();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("993641904076-cca6vq2tt4e72b19pf3jqn1n8qpu8j19.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
        account = GoogleSignIn.getLastSignedInAccount(getContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_social_media_auth, container, false);
        v.findViewById(R.id.sign_in_button).setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_GET_TOKEN);
            }
        }));

        loginButton = (LoginButton) v.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Insert your code here
                                try {
                                    String token = loginResult.getAccessToken().getToken();
                                    String name = object.getString("name");
                                    String email = object.getString("email");
                                    Users.facebookLogin(token, name, email, getApplicationContext());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("INFO", "CANCEL CANCEL CANCEL CANCEL CANCEL CANCEL");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("INFO", "ERRO ERRO ERRO ERRO ERRO ERRO");
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            //Facebook Login
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String username = account.getDisplayName();
            String email = account.getEmail();
            String picture = account.getPhotoUrl().toString();
            Users.googleLogin(username, email, picture, getApplicationContext());
        } catch (ApiException e) {
            Log.w("SIGA", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    public void home() {
        Intent homePage = new Intent(getContext(), Homepage.class);
        startActivity(homePage);
    }
}