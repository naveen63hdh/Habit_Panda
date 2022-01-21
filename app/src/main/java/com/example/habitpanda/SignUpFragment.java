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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment {
    Button signUpBtn;
    EditText unameTxt, emailTxt, passTxt, retypePassTxt;

    private FirebaseAuth mAuth;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        signUpBtn = view.findViewById(R.id.sign_up_btn);
        unameTxt = view.findViewById(R.id.name_sign_up);
        emailTxt = view.findViewById(R.id.email_sign_up);
        passTxt = view.findViewById(R.id.pass_sign_up);
        retypePassTxt = view.findViewById(R.id.retype_pass_sign_up);

        mAuth = FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
//                Intent homepage = new Intent(getContext(), HomeActivity.class);
//                startActivity(homepage);
            }
        });

        return view;
    }

    void registerUser() {
        // Take the value of two edit texts in Strings
        String email, password, name;
        email = emailTxt.getText().toString();
        password = passTxt.getText().toString();
        name = unameTxt.getText().toString();

        // Validations for input email and password
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

        // create new user or register new user

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Registration successful!",
                            Toast.LENGTH_LONG)
                            .show();

                    String uid = task.getResult().getUser().getUid();

                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Users").child(uid);

                    myRef.child(uid).child("Name").setValue(name);
                    myRef.child(uid).child("Email").setValue(email);

                    // if the user created intent to login activity
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    getActivity().finish();
                    startActivity(intent);


                } else {

                    // Registration failed
                    Toast.makeText(getContext(), "Registration failed!! Please try again later", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}