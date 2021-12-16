package com.example.habitpanda.profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.habitpanda.R;
import com.example.habitpanda.reciever.AlarmReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

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

    Uri parseUri;
    int hour, min;
    boolean rem_on;

    ActivityResultLauncher<Intent> cropActivity;

    String prevTime, currTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        cropActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                CropImage.ActivityResult cropResult = CropImage.getActivityResult(result.getData());
                if (result.getResultCode() == RESULT_OK) {
                    parseUri = cropResult.getUri();
                    Toast.makeText(ProfileActivity.this, parseUri.toString(), Toast.LENGTH_SHORT).show();
                    try {
                        InputStream stream = ProfileActivity.this.getContentResolver().openInputStream(parseUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
                        profileImg.setImageBitmap(bitmap);
                        //Glide.with(getContext()).load(parseUri).into(profileImg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (result.getResultCode() == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception er = cropResult.getError();
                }
            }
        });

        emailTxt = findViewById(R.id.email);
        unameTxt = findViewById(R.id.name);
        timeTxt = findViewById(R.id.time);
        changePass = findViewById(R.id.changePass);
        remainderSwitch = findViewById(R.id.remainder_switch);
        timeBtn = findViewById(R.id.timeBtn);
        profileImg = findViewById(R.id.profile_img_btn);
        saveBtn = findViewById(R.id.saveBtn);

        prevTime = timeTxt.getText().toString();

        preferences = getSharedPreferences("panda_pref", Context.MODE_PRIVATE);

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
                                    Toast.makeText(ProfileActivity.this, "Mail Sent to your mail id. Change password from your mail", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parseUri!=null) {
                    uploadImage();
                } else {
                    updateData();
                }
            }
        });

        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (remainderSwitch.isChecked()) {
                    Calendar c = Calendar.getInstance();
                    int mHour = c.get(Calendar.HOUR_OF_DAY);
                    int mMin = c.get(Calendar.MINUTE);

                    TimePickerDialog dialog = new TimePickerDialog(ProfileActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            timeTxt.setText(hourOfDay + ":" + minute);
                            currTime = timeTxt.getText().toString();
                            hour = hourOfDay;
                            min = minute;
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
                if (pick == true) {
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


    }

    private void updateData() {
        String name = unameTxt.getText().toString();
        HashMap<String,Object> updateMap = new HashMap<>();
        if (!url.equals("")) {
            updateMap.put("url",url);
        }
        ProgressDialog progressDialog = ProgressDialog.show(this,"Please Wait","Saving your data to database");
        updateMap.put("Name",name);
        profileRef.updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    setPref();
                    Toast.makeText(ProfileActivity.this, "Values Updated Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error occurred while updating value", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void setPref() {
        if (remainderSwitch.isChecked())
            setRemainder(hour,min);
        else
            cancelRemainder();
        prefEdit = preferences.edit();
        prefEdit.putBoolean("rem_on", remainderSwitch.isChecked());
        if (!prevTime.equals(currTime))
            prefEdit.putString("time",hour+":"+min);
        prefEdit.putInt("hr",hour);
        prefEdit.putInt("min",min);
        prefEdit.commit();
    }

    private void cancelRemainder() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
    }

    private void setRemainder(int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
        c.set(Calendar.MINUTE,minute);
        c.set(Calendar.SECOND,0);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void uploadImage() {
        ProgressDialog dialog = ProgressDialog.show(this,"Please Wait","Uploading the profile");
        storageRef.child(uid + ".png").putFile(parseUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageRef.child(uid+".png").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isComplete()) {
                            url = task.getResult().toString();
                            dialog.dismiss();
                            updateData();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                dialog.setMessage((int) progress + "% Uploading...");
            }
        });
    }

    private void PickImage() {


        Intent intent = CropImage.activity().getIntent(ProfileActivity.this);
        cropActivity.launch(intent);
//        CropImage.activity().start(getActivity());
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    private boolean checkStoragePermission() {
        boolean res = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(ProfileActivity.this, "Please Wait", "Loading your profile");
        populateDataset();
    }

    private void populateDataset() {
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("Email").getValue().toString();
                if (snapshot.child("url").getValue() != null) {
                    url = snapshot.child("url").getValue().toString();
                    Glide.with(ProfileActivity.this).load(url).into(profileImg);
                }
                uname = snapshot.child("Name").getValue().toString();
                if (preferences.getBoolean("rem_on", true)) {
                    rem_on = preferences.getBoolean("rem_on", true);
                    time = preferences.getString("time", "");
                    hour = preferences.getInt("hr", -1);
                    min = preferences.getInt("min", -1);
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
}