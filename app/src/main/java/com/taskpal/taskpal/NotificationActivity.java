package com.taskpal.taskpal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {

    private RadioButton yesButton;
    private RadioButton noButton;
    private TextView taskText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);
        taskText = findViewById(R.id.taskText);
    }
}
