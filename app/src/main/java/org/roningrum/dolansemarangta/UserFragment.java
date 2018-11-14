package org.roningrum.dolansemarangta;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import org.roningrum.dolansemarangta.user.LoginActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
    Button btnLogoutApp;
    TextView tvUpdatePassword, tvNamaProfile, tvEmailProfile, tvUpdateProfile, tvDeleteUser;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth.AuthStateListener firebaseAuthListener;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        btnLogoutApp = view.findViewById(R.id.btn_signout_fb);
        tvUpdatePassword = view.findViewById(R.id.tv_pass_update_profile);
        tvNamaProfile = view.findViewById(R.id.tv_nama_profile);
        tvEmailProfile = view.findViewById(R.id.tv_email_profile);
        tvUpdateProfile = view.findViewById(R.id.tv_profile_update);
        tvDeleteUser = view.findViewById(R.id.tv_acc_delete_profile);
        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseUser != null) {
                    tvNamaProfile.setText(firebaseUser.getDisplayName());
                    tvEmailProfile.setText(firebaseUser.getEmail());
                    for(UserInfo profile: firebaseUser.getProviderData()){
                        String surel = profile.getEmail();
                        tvEmailProfile.setText(surel);
                    }
                }
            }
        };

        tvUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassPage();
            }
        });
        tvUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
        tvDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });

        btnLogoutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutApp();
            }
        });
        reloadView();
        return view;

    }

    private void deleteUser() {
        firebaseUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("pesan", "User account deleted.");
                           startActivity(new Intent(getActivity(),WelcomePageActivity.class));
                        }
                    }
                });

    }

    private void reloadView() {
        firebaseUser.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (firebaseUser != null) {
                    String nama = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    tvNamaProfile.setText(nama);
                    tvEmailProfile.setText(email);
                    for(UserInfo profile: firebaseUser.getProviderData()){
                        String surel = profile.getEmail();
                        tvEmailProfile.setText(surel);
                    }

                }
            }
        });
    }

    private void updateProfile() {
        startActivity(new Intent(getContext(), UpdateProfileActivity.class));
    }

    private void updatePassPage() {
        startActivity(new Intent(getContext(), UpdatePasswordActivity.class));
    }

    private void signOutApp() {
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            googleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //
                }
            });
            startActivity(new Intent(getContext(), WelcomePageActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

}
