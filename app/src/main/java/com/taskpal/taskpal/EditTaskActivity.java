package com.taskpal.taskpal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class EditTaskActivity extends AppCompatActivity {

    private Button timeButton;
    private Button difficultyButton;
    private Button alertButton;
    private Button dateButton;
    private Button editButton;
    private ImageButton exitButton;
    private EditText titleText;
    private EditText locationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        timeButton = findViewById(R.id.timeButtonEdit);
        difficultyButton = findViewById(R.id.difficultyButtonEdit);
        alertButton = findViewById(R.id.alertButtonEdit);
        dateButton = findViewById(R.id.dateButtonEdit);
        editButton = findViewById(R.id.editButton);
        exitButton = findViewById(R.id.exitButtonEdit);
        titleText = findViewById(R.id.titleTextEdit);
        locationText = findViewById(R.id.locationTextEdit);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditTaskActivity.this, CalendarActivity.class));
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditTaskActivity.this, CalendarActivity.class));
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditTaskActivity.this, completionTimeActivity.class));
            }
        });

        difficultyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditTaskActivity.this, difficultyActivity.class));
            }
        });

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditTaskActivity.this, alertActivity.class));
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditTaskActivity.this, dateActivity.class));
            }
        });
    }
}
