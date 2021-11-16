package com.example.habitpanda.add_task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.habitpanda.R;
import com.example.habitpanda.add_habit.AddHabitActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class AddTaskActivity extends AppCompatActivity {

    ImageButton dateBtn;
    EditText dateTxt,taskTxt,descTxt;
    private int mYear, mMonth, mDay;

    String taskName,taskDesc,taskDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskTxt = findViewById(R.id.task_name);
        descTxt = findViewById(R.id.task_desc);
        dateTxt = findViewById(R.id.date);
        dateBtn = findViewById(R.id.start_date_btn);

        ActionBar actionBar;

        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color = '#000000'>Add Task</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green)));

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dateTxt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    public void createTask(View view) {
        ProgressDialog progressDialog = ProgressDialog.show(this,"Please Wait","Creating your task");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference habitRef = database.getReference().child("Users").child(auth.getUid()).child("Task").push();
        taskName = taskTxt.getText().toString();
        taskDesc = descTxt.getText().toString();
        taskDate = dateTxt.getText().toString();

        HashMap<String,Object> task = new HashMap<>();
        task.put("name",taskName);
        task.put("desc",taskDesc);
        task.put("date",taskDate);

        habitRef.updateChildren(task).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(AddTaskActivity.this, "Task Created Successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddTaskActivity.this, "Please check your network connectivity and try again later", Toast.LENGTH_SHORT).show();
            }
        });

    }
}