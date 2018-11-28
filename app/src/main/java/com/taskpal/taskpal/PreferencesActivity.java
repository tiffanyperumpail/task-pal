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

    public EditText nameText;
    private Button saveButton;
    private ImageButton exitButton;

    private RadioGroup timeOfDayGroup;
    private RadioButton timeOfDayButton;
    private RadioGroup attentionSpanGroup;
    private RadioButton attentionSpanButton;
    private RadioGroup procrastinationGroup;
    private RadioButton procrastinationButton;

    private RadioButton morningButton;
    private RadioButton dayButton;
    private RadioButton nightButton;
    private RadioButton all_at_onceButton;
    private RadioButton breaksButton;
    private RadioButton closerButton;
    private RadioButton beforeButton;

    SharedPreferences sharedpreferences;
    Editor sharedPreferenceEditor;

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

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreferencesActivity.this, MainActivity.class));
                Editor editor = sharedpreferences.edit();
                editor.putString("name", nameText.toString());
                timeOfDayButton = (RadioButton) findViewById(timeOfDayGroup.getCheckedRadioButtonId());
                editor.putString("timeOfDay", timeOfDayButton.getText().toString());
                attentionSpanButton = (RadioButton) findViewById(attentionSpanGroup.getCheckedRadioButtonId());
                editor.putString("attentionSpan", attentionSpanButton.getText().toString());
                procrastinationButton = (RadioButton) findViewById(procrastinationGroup.getCheckedRadioButtonId());
                editor.putString("procrastinationSpan", procrastinationButton.getText().toString());
                editor.commit();
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
