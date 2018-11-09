package com.taskpal.taskpal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class PreferencesActivity extends AppCompatActivity {

    private EditText nameText;
    private Button saveButton;
    private RadioButton morningButton;
    private RadioButton dayButton;
    private RadioButton nightButton;
    private RadioButton all_at_onceButton;
    private RadioButton breaksButton;
    private RadioButton closerButton;
    private RadioButton beforeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        nameText = findViewById(R.id.nameTextPreferences);
        saveButton = findViewById(R.id.saveButton);
        morningButton = findViewById(R.id.morningButton);
        dayButton = findViewById(R.id.dayButton);
        nightButton = findViewById(R.id.nightButton);
        all_at_onceButton = findViewById(R.id.all_at_onceButton);
        breaksButton = findViewById(R.id.breaksButton);
        closerButton = findViewById(R.id.closerButton);
        beforeButton = findViewById(R.id.beforeButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreferencesActivity.this, MainActivity.class));
            }
        });

    }
}
