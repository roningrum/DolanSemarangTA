package org.roningrum.dolansemarangta.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

import org.roningrum.dolansemarangta.R;
import org.roningrum.dolansemarangta.WelcomePageActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity{
    TextInputEditText etNamaRegister,etEmailRegister,etPasswordRegister,etPasswordConfirmRegister;
    Button btnRegister;
    TextView tvLinkLoginPage;
    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProgressBar pbHorizontal;
    private View dialogView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNamaRegister = findViewById(R.id.et_nama_register);
        etEmailRegister = findViewById(R.id.et_email_register);
        etPasswordRegister = findViewById(R.id.et_password_register);
        etPasswordConfirmRegister = findViewById(R.id.et_password_confirm);
        tvLinkLoginPage = findViewById(R.id.tv_link_login_page);
        btnRegister = findViewById(R.id.btn_register_email);
        pbHorizontal = findViewById(R.id.pb_horizontal);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        tvLinkLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAction();
            }
        });
    }

    private void registerAction() {
        String nama = etNamaRegister.getText().toString().trim();
        String email = etEmailRegister.getText().toString();
        String password = etPasswordRegister.getText().toString();
        String konfirmasi = etPasswordConfirmRegister.getText().toString();
        //check nama
        if(TextUtils.isEmpty(nama)){
            Toast.makeText(this,"nama tidak boleh kosong",Toast.LENGTH_SHORT).show();
            return;
        }
        //check email
        if (TextUtils.isEmpty(email)&&!isValidEmail(email)){
            TextInputLayout etEmailRegisterLayout = findViewById(R.id.et_email_register_layout);
            etEmailRegisterLayout.setError("Enter Email Valid");
            return;
        }
        //check password
        if(TextUtils.isEmpty(password)){
            TextInputLayout etPasswordRegisterLayout = findViewById(R.id.et_password_register_layout);
            etPasswordRegisterLayout.setError("Enter Password");
            Toast.makeText(this,"password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        //validate password
        if(!password.equals(konfirmasi)){
            Toast.makeText(this, " password tidak sesuai, silakan coba lagi", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6){
            TextInputLayout etPasswordRegisterLayout = findViewById(R.id.et_password_register_layout);
            etPasswordRegisterLayout.setError("Minimum 6 characters in password");
        }
        pbHorizontal.setVisibility(View.VISIBLE);
        //create user
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                connectifyInternet();
                if(task.isSuccessful()) {
                    pbHorizontal.setVisibility(View.GONE);
                    setDialogAlert(dialogView);
                    nama(task.getResult().getUser(),etNamaRegister.getText().toString().trim());
                    sendEmailVerification();
                }
                else {
                    Toast.makeText(RegisterActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    pbHorizontal.setVisibility(View.GONE);
                }
            }
        });

    }

    private void sendEmailVerification() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Silahkan check email untuk verifikasi akun", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                        finish();
                    }
                }
            });
        }
    }

    //check internet conection before register
    private void connectifyInternet() {
        //gagal terdaftar jika koneksi gagal atau akun sudah terdaftar
        ConnectivityManager ConnectionManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true ) {
            Log.w("message","Email sudah terdaftar. silakan cek kembali");}
        else {
            Log.e("message","Silakan check koneksi kembali");
            Toast.makeText(RegisterActivity.this, "Silakan cek koneksi Anda Kembali", Toast.LENGTH_SHORT).show();
        }
    }

    private void nama(FirebaseUser user, String nama) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(nama)
                .build();
        user.updateProfile(profileChangeRequest);
    }
    private boolean isValidEmail(String email) {
        String PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //AlertDialog
    private void setDialogAlert(View view) {
        dialogView = view;
        final AlertDialog.Builder alertSukses = new AlertDialog.Builder(this);
        alertSukses.setTitle("Sukses Dibuat");
        alertSukses.setMessage("Apakah ingin lanjut untuk masuk?");
        alertSukses.setCancelable(false);

        alertSukses.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
        alertSukses.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //keluar
            }
        });
        AlertDialog alertDialog = alertSukses.create();
        alertDialog.show();
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
