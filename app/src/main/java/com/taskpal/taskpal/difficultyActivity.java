package com.taskpal.taskpal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;

public class difficultyActivity extends AppCompatActivity {

    private RadioButton very_high_Button;
    private RadioButton high_Button;
    private RadioButton medium_Button;
    private RadioButton low_Button;
    private RadioButton very_low_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        very_high_Button = findViewById(R.id.very_high_Button);
        high_Button = findViewById(R.id.high_Button);
        medium_Button = findViewById(R.id.medium_Button);
        low_Button = findViewById(R.id.low_Button);
        very_low_Button = findViewById(R.id.very_low_Button);
    }
}
