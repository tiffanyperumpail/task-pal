package com.taskpal.taskpal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

        final RadioGroup timeOfDay = (RadioGroup) findViewById(R.id.timeOfDayGroup);
        final RadioGroup attentionSpanGroup = (RadioGroup) findViewById(R.id.attentionSpanGroup);
        final RadioGroup procrastinationGroup = (RadioGroup) findViewById(R.id.procrastinationGroup);

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
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("TimePreference", timeOfDayChecked(timeOfDay).name());
                editor.putString("AttentionPreference", attentionSpanChecked(attentionSpanGroup).name());
                editor.putString("DeadlinePreference", procrastinationGroup(procrastinationGroup).name());
                editor.commit();
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

    private TimePreference timeOfDayChecked(RadioGroup timeOfDay) {
        TimePreference preference = TimePreference.DAY;
        int radioButtonId = timeOfDay.getCheckedRadioButtonId();
        switch (radioButtonId) {
            case R.id.morningButton:
                preference = TimePreference.MORNING;
                break;
            case R.id.dayButton:
                preference = TimePreference.DAY;
                break;
            case R.id.nightButton:
                preference = TimePreference.NIGHT;
                break;
        }
        return preference;
    }

    private AttentionPreference attentionSpanChecked(RadioGroup attention) {
        AttentionPreference preference = AttentionPreference.BREAKS;
        int radioButtonId = attention.getCheckedRadioButtonId();
        switch (radioButtonId) {
            case R.id.all_at_onceButton:
                preference = AttentionPreference.NO_BREAKS;
                break;
            case R.id.breaksButton:
                preference = AttentionPreference.BREAKS;
                break;
        }
        return preference;
    }

    private DeadlinePreference procrastinationGroup(RadioGroup procrastination) {
        DeadlinePreference preference = DeadlinePreference.CLOSER;
        int radioButtonId = procrastination.getCheckedRadioButtonId();
        switch (radioButtonId) {
            case R.id.closerButton:
                preference = DeadlinePreference.CLOSER;
                break;
            case R.id.beforeButton:
                preference = DeadlinePreference.LONGER;
                break;
        }
        return preference;
    }
}
