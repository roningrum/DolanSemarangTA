package org.roningrum.dolansemarangta.user;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.roningrum.dolansemarangta.HomeActivity;
import org.roningrum.dolansemarangta.R;
import org.roningrum.dolansemarangta.ResetPasswordActivity;
import org.roningrum.dolansemarangta.WelcomePageActivity;
import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    //komponen
    TextInputEditText etEmailLogin;
    TextInputEditText etPasswordLogin;
    TextView tvForgotPassword;
    TextView tvLinkRegisterPage;
    Button btnLogin;
    ProgressBar pbHorizontal;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmailLogin = findViewById(R.id.et_email_login);
        etPasswordLogin = findViewById(R.id.et_password_login);
        tvForgotPassword = findViewById(R.id.tv_reset_pass_pg);
        tvLinkRegisterPage = findViewById(R.id.tv_link_register_page);
        btnLogin = findViewById(R.id.btn_signin_email);
        pbHorizontal = findViewById(R.id.pb_horizontal);

        firebaseAuth = FirebaseAuth.getInstance();
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPasswordPg();
            }
        });
        tvLinkRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPage();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction();
            }
        });
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            }
        };
    }

    private void loginAction() {
        //login denga firebase
        String email = etEmailLogin.getText().toString();
        final String password = etPasswordLogin.getText().toString();
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, " Masukkan Email", Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Masukkan password dan pastikan sesuai", Toast.LENGTH_SHORT).show();
        } else {
            pbHorizontal.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                pbHorizontal.setVisibility(View.GONE);
                                connectifyInternet();
                                    firebaseAuth.getCurrentUser();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                            } else {
                                pbHorizontal.setVisibility(View.GONE);
                                connectifyInternet();
                                Toast.makeText(LoginActivity.this,  ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void connectifyInternet() {
        //gagal terdaftar jika koneksi gagal atau akun sudah terdaftar
        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true) {
            Log.d("pesan", "Login");
        } else {
            Log.d("pesan", "Silakan check koneksi kembali");
            Toast.makeText(this, "Silakan cek koneksi Anda Kembali", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerPage() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void resetPasswordPg() {
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        pbHorizontal.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,WelcomePageActivity.class));
        finish();
    }
}
