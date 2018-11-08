package com.taskpal.taskpal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class NewTaskActivity extends AppCompatActivity {

    private Button timeButton;
    private Button difficultyButton;
    private Button alertButton;
    private Button dateButton;
    private Button createButton;
    private Button cancelButton;
    private EditText titleText;
    private EditText locationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtask);
        timeButton = findViewById(R.id.timeButton);
        difficultyButton = findViewById(R.id.difficultyButton);
        alertButton = findViewById(R.id.alertButton);
        dateButton = findViewById(R.id.dateButton);
        createButton = findViewById(R.id.createButton);
        cancelButton = findViewById(R.id.cancelButton);
        titleText = findViewById(R.id.titleText);
        locationText = findViewById(R.id.locationText);
    }
}
