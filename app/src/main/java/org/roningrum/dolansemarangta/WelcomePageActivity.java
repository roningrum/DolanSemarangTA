package org.roningrum.dolansemarangta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.roningrum.dolansemarangta.user.LoginActivity;
import org.roningrum.dolansemarangta.user.RegisterActivity;

import java.util.Arrays;

public class WelcomePageActivity extends AppCompatActivity {
    SignInButton btnSignInGoogle;
    Button btnLoginSession, btnRegisteSession, btnLoginFacebookCustom;
    LoginButton btnSignInFacebook;
    CallbackManager callbackManager;
    FirebaseUser firebaseUser;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;
    private GoogleSignInClient googleSignInClient;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener firebaseListener;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        btnSignInGoogle = findViewById(R.id.btn_signinGoogle);
        btnLoginSession = findViewById(R.id.btn_LoginHome);
        btnRegisteSession = findViewById(R.id.btn_RegisterHome);
        btnSignInFacebook = findViewById(R.id.btn_signin_facebook);
        btnLoginFacebookCustom = findViewById(R.id.btn_login_facebook);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        //facebook
        callbackManager = CallbackManager.Factory.create();
        btnLoginFacebookCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(WelcomePageActivity.this, Arrays.asList("public_profile", "email"));
            }
        });
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("pesan", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                if(AccessToken.getCurrentAccessToken()!=null) {

                    System.out.println(AccessToken.getCurrentAccessToken().getToken());

                    GraphRequest request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    // Application code
                                    try {
                                        String email = object.getString("email");
                                        String name = object.getString("name");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "name,email");
                    request.setParameters(parameters);
                    request.executeAsync();

                }
                else
                {
                    System.out.println("Access Token NULL");
                }
            }

            @Override
            public void onCancel() {
                Log.d("pesan", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("pesan", "facebook:onError", exception);
            }
        });

        //lacak token
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });
        btnRegisteSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSession();
            }
        });
        btnLoginSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSession();
            }
        });
        btnSignInFacebook.setReadPermissions("email", "public_profile");
        firebaseListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseUser != null) {
                    startActivity(new Intent(WelcomePageActivity.this, HomeActivity.class));
                    finish();
                }
            }
        };
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d("pesan", "handleFacebookAccessToken:" + accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("pesan", "signInWithCredential:success");

                            startActivity(new Intent(WelcomePageActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("pesan", "signInWithCredential:failure", task.getException());
                            Toast.makeText(WelcomePageActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    private void loginSession() {

        startActivity(new Intent(this, LoginActivity.class));
        firebaseAuth.addAuthStateListener(firebaseListener);
    }

    private void registerSession() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    private void signInGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("pesan", "Google Sign In Failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("pesan", "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("pesan", "signInWithCredential:success");
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("pesan", "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseListener);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//        onQuitPressed();
    }

//    private void onQuitPressed() {
//        int pid = android.os.Process.myPid();
//        android.os.Process.killProcess(pid);
//    }

}
