package com.taskpal.taskpal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class EditTaskActivity extends AppCompatActivity {

    private Button timeButton;
    private Button difficultyButton;
    private Button alertButton;
    private Button dateButton;
    private Button editButton;
    private Button cancelButton;
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
        cancelButton = findViewById(R.id.cancelButtonEdit);
        titleText = findViewById(R.id.titleTextEdit);
        locationText = findViewById(R.id.locationTextEdit);
    }
}
