package com.example.habitpanda.home.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.habitpanda.ForgetPasswordActivity;
import com.example.habitpanda.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.InputStream;
import java.util.Calendar;

public class ProfileFragment extends Fragment {

    EditText emailTxt, unameTxt, timeTxt;
    TextView changePass;
    SwitchCompat remainderSwitch;
    ImageButton timeBtn;
    ImageView profileImg;
    Button saveBtn;
    ProgressDialog progressDialog;

    SharedPreferences preferences;
    SharedPreferences.Editor prefEdit;

    FirebaseAuth auth;

    FirebaseDatabase database;
    DatabaseReference profileRef;

    StorageReference storageRef;

    String uid, time, email, uname, url = "";
    int hour,min;
    boolean rem_on;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        emailTxt = view.findViewById(R.id.email);
        unameTxt = view.findViewById(R.id.name);
        timeTxt = view.findViewById(R.id.time);
        changePass = view.findViewById(R.id.changePass);
        remainderSwitch = view.findViewById(R.id.remainder_switch);
        timeBtn = view.findViewById(R.id.timeBtn);
        profileImg = view.findViewById(R.id.profile_img_btn);
        saveBtn = view.findViewById(R.id.saveBtn);

        preferences = getContext().getSharedPreferences("panda_pref", Context.MODE_PRIVATE);

        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();

        database = FirebaseDatabase.getInstance();
        profileRef = database.getReference().child("Users").child(uid);

        storageRef = FirebaseStorage.getInstance().getReference().child("Profile").child(uid);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Mail Sent to your mail id. Change password from your mail", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (remainderSwitch.isChecked()) {
                    Calendar c = Calendar.getInstance();
                    int mHour = c.get(Calendar.HOUR_OF_DAY);
                    int mMin = c.get(Calendar.MINUTE);

                    TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            timeTxt.setText(hourOfDay + ":" + minute);
//                            spEditor.putBoolean("rem_on", true);
//                            spEditor.putString("time", hourOfDay + ":" + minute);
//                            spEditor.commit();
                        }
                    }, mHour, mMin, true);

                    dialog.show();
                }
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pick = true;
                if (pick==true) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else
                        PickImage();
                } else {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else
                        PickImage();
                }
            }
        });

        return view;
    }

    private void PickImage() {
        CropImage.activity().start(getActivity());
    }

    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA},100);
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }

    private boolean checkStoragePermission() {
        boolean res = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(getContext(), "Please Wait", "Loading your profile");
        populateDataset();
    }

    private void populateDataset() {
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("Email").getValue().toString();
                if (snapshot.child("url").getValue() != null)
                    url = snapshot.child("url").getValue().toString();
                uname = snapshot.child("Name").getValue().toString();
                if (preferences.getBoolean("rem_on", true)) {
                    rem_on = preferences.getBoolean("rem_on", true);
                    time = preferences.getString("time", "");
                }


                emailTxt.setText(email);
                unameTxt.setText(uname);
                if (rem_on)
                    timeTxt.setText(time);
                remainderSwitch.setChecked(rem_on);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
          CropImage.ActivityResult result = CropImage.getActivityResult(data);
          if (resultCode == RESULT_OK) {
              Uri parseUri = result.getUri();
              Toast.makeText(getContext(), parseUri.toString(), Toast.LENGTH_SHORT).show();
              try {
                  InputStream stream = getContext().getContentResolver().openInputStream(parseUri);
                  Bitmap bitmap = BitmapFactory.decodeStream(stream);
                  profileImg.setImageBitmap(bitmap);
                  //Glide.with(getContext()).load(parseUri).into(profileImg);
              } catch (Exception e) {
                  e.printStackTrace();
              }
          } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
              Exception er = result.getError();
          }
        }
    }
}