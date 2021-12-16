package com.example.habitpanda;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitpanda.home.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInFragment extends Fragment {


    Button signInBtn;
    EditText emailTxt, passTxt;
    ImageButton googleBtn, fbBtn;
    TextView forgetPassTxt;

    FirebaseAuth mAuth;


    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        signInBtn = view.findViewById(R.id.signin_btn);
        emailTxt = view.findViewById(R.id.email_sign_in);
        passTxt = view.findViewById(R.id.pass_sign_in);
//        googleBtn = view.findViewById(R.id.google_btn);
//        fbBtn = view.findViewById(R.id.fb_btn);
        forgetPassTxt = view.findViewById(R.id.forget_pass);

        mAuth = FirebaseAuth.getInstance();

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
//                Intent homepage = new Intent(getContext(), HomeActivity.class);
//                startActivity(homepage);
            }
        });

//        googleBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Configure Google Sign In
////                GoogleSignInOpti
////                ons gso = new GoogleSignInOptions
////                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
////                        .requestIdToken("1027724892127-a55acc44ra2lk9jnamuog4s1m2vts9i6.apps.googleusercontent.com")
////                        .requestEmail()
////                        .build();
//            }
//        });
//
//        fbBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        return view;
    }

    private void loginUser() {
        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTxt.getText().toString();
        password = passTxt.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(),
                    "Please enter email!!",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(),
                    "Please enter password!!",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(
                    @NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Login successful!!",
                            Toast.LENGTH_LONG)
                            .show();

                    // if sign-in is successful
                    // intent to home activity
                    getActivity().finish();
                    Intent intent
                            = new Intent(getContext(),
                            MainActivity.class);
                    startActivity(intent);
                } else {

                    // sign-in failed
                    Toast.makeText(getContext(),
                            "Login failed!!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
}