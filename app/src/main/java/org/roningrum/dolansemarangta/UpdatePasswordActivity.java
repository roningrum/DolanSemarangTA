package org.roningrum.dolansemarangta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity {
    TextInputEditText etNewPassWord;
    Button btnUpdatePassword;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        etNewPassWord = findViewById(R.id.et_new_pass);
        btnUpdatePassword = findViewById(R.id.btn_update_pass);

        firebaseAuth = FirebaseAuth.getInstance();
        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });
    }

    private void updatePassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newPassword = etNewPassWord.getText().toString();
        if(newPassword.isEmpty()){
            TextInputLayout etnewPaswordLayout = findViewById(R.id.et_new_pass_layout);
            etnewPaswordLayout.setError("Enter your new password");
        }
        else {
        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("pesan", "Password sukses update");
                        Toast.makeText(UpdatePasswordActivity.this,"Sukses update password", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("pesan", "Gagal update password");
                        Toast.makeText(UpdatePasswordActivity.this, " "+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
    }
}
