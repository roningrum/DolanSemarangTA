package org.roningrum.dolansemarangta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.roningrum.dolansemarangta.user.LoginActivity;
import org.roningrum.dolansemarangta.user.RegisterActivity;

public class ResetPasswordActivity extends AppCompatActivity {
    Button btnResetPassword;
    TextInputEditText etResetPassword;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        etResetPassword = findViewById(R.id.et_email_reset);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        firebaseAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }
    private void resetPassword() {
        String email = etResetPassword.getText().toString();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ResetPasswordActivity.this,"Silaka cek email untuk melanjutkan proses",Toast.LENGTH_SHORT);
                startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
}
