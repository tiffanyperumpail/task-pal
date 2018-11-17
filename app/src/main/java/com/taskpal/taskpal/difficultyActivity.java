package com.taskpal.taskpal;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

        Intent intent = getIntent();
        String curr = intent.getStringExtra("curr");
        if (curr != null) {
            switch (curr) {
                case "1":
                    very_low_Button.setChecked(true);
                    break;
                case "2":
                    low_Button.setChecked(true);
                    break;
                case "3":
                    medium_Button.setChecked(true);
                    break;
                case "4":
                    high_Button.setChecked(true);
                    break;
                case "5":
                    very_high_Button.setChecked(true);
                    break;
            }
        }
    }

    public void onRadioButtonClicked(View v)
    {
        boolean checked = ((RadioButton) v).isChecked();
        Intent intent = new Intent();
        switch(v.getId()){
            case R.id.very_low_Button:
                if (checked)
                    intent.putExtra("data", "1");
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.low_Button:
                if (checked)
                    intent.putExtra("data", "2");
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.medium_Button:
                if (checked)
                    intent.putExtra("data", "3");
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.high_Button:
                if (checked)
                    intent.putExtra("data", "4");
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.very_high_Button:
                if (checked)
                    intent.putExtra("data", "5");
                setResult(Activity.RESULT_OK, intent);
                break;
        }
        finish();
    }
}
