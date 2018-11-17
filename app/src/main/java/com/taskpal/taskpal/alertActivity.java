package com.taskpal.taskpal;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class alertActivity extends AppCompatActivity {

    private RadioButton none_Button;
    private RadioButton fifteen_mins_Button;
    private RadioButton thirty_mins_Button;
    private RadioButton hour_Button;
    private RadioButton two_hours_Button;
    private RadioButton day_before_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        none_Button = findViewById(R.id.none_button);
        fifteen_mins_Button = findViewById(R.id.fifteen_mins_Button);
        thirty_mins_Button = findViewById(R.id.thirty_mins_Button);
        hour_Button = findViewById(R.id.hour_Button);
        two_hours_Button = findViewById(R.id.two_hours_button);
        day_before_Button = findViewById(R.id.day_before_Button);

        Intent intent = getIntent();
        String curr = intent.getStringExtra("curr");
        if (curr != null) {
            switch (curr) {
                case "0":
                    none_Button.setChecked(true);
                    break;
                case "15":
                    fifteen_mins_Button.setChecked(true);
                    break;
                case "30":
                    thirty_mins_Button.setChecked(true);
                    break;
                case "60":
                    hour_Button.setChecked(true);
                    break;
                case "120":
                    two_hours_Button.setChecked(true);
                    break;
                case "1440":
                    day_before_Button.setChecked(true);
                    break;
            }
        }
    }

    public void onRadioButtonClicked(View v)
    {
        boolean checked = ((RadioButton) v).isChecked();
        Intent intent = new Intent();
        switch(v.getId()){
            case R.id.none_button:
                if (checked)
                    intent.putExtra("data", "0");
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.fifteen_mins_Button:
                if (checked)
                    intent.putExtra("data", "15");
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.thirty_mins_Button:
                if (checked)
                    intent.putExtra("data", "30");
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.hour_Button:
                if (checked)
                    intent.putExtra("data", "60");
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.two_hours_button:
                if (checked)
                    intent.putExtra("data", "120");
                setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.day_before_Button:
                if (checked)
                    intent.putExtra("data", "1440");
                setResult(Activity.RESULT_OK, intent);
                break;
        }
        finish();
    }
}
