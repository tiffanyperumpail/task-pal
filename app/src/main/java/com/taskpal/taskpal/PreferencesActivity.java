package com.taskpal.taskpal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;

    private EditText nameText;
    private Button saveButton;
    private ImageButton exitButton;
    private RadioButton morningButton;
    private RadioButton dayButton;
    private RadioButton nightButton;
    private RadioButton all_at_onceButton;
    private RadioButton breaksButton;
    private RadioButton closerButton;
    private RadioButton beforeButton;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        // Name
        nameText = findViewById(R.id.nameTextPreferences);

        saveButton = findViewById(R.id.saveButton);
        exitButton = findViewById(R.id.exitButtonPreferences);
        // Time of day preference
        morningButton = findViewById(R.id.morningButton);
        dayButton = findViewById(R.id.dayButton);
        nightButton = findViewById(R.id.nightButton);
        // Attention span preference
        all_at_onceButton = findViewById(R.id.all_at_onceButton);
        breaksButton = findViewById(R.id.breaksButton);
        // Procrastination preference
        closerButton = findViewById(R.id.closerButton);
        beforeButton = findViewById(R.id.beforeButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreferencesActivity.this, MainActivity.class));
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreferencesActivity.this, MainActivity.class));
            }
        });

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }
}
