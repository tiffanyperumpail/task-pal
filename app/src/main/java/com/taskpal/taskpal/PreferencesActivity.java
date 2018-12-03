package com.taskpal.taskpal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.TextView;

public class PreferencesActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;

    private EditText nameText;
    private Button saveButton;
    private ImageButton exitButton;

    public static String NAMETEXT = null;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        // Name
        nameText = findViewById(R.id.nameTextPreferences);

        saveButton = findViewById(R.id.saveButton);
        exitButton = findViewById(R.id.exitButtonPreferences);

        if (NAMETEXT != null) {
            nameText.setText(NAMETEXT);
        }

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NAMETEXT = nameText.getText().toString();
                startActivity(new Intent(PreferencesActivity.this, MainActivity.class));
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreferencesActivity.this, MainActivity.class));
            }
        });

    }
}
