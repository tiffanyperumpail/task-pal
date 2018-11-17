package com.taskpal.taskpal;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class completionTimeActivity extends AppCompatActivity {

    private RadioButton thirty_mins_completion;
    private RadioButton hour_completion;
    private RadioButton hour_half_completion;
    private RadioButton two_hour_completion;
    private RadioButton two_hour_half_completion;
    private RadioButton three_hour_completion;
    private RadioButton four_hour_completion;
    private RadioButton five_hour_completion;
    private RadioButton all_day_completion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion_time);
        thirty_mins_completion = findViewById(R.id.thirty_mins_completion);
        hour_completion = findViewById(R.id.hour_completion);
        hour_half_completion = findViewById(R.id.hour_half_completion);
        two_hour_completion = findViewById(R.id.two_hour_completion);
        two_hour_half_completion = findViewById(R.id.two_hour_half_completion2);
        three_hour_completion = findViewById(R.id.three_hour_completion3);
        four_hour_completion = findViewById(R.id.four_hour_completion);
        five_hour_completion = findViewById(R.id.five_hour_completion2);
        all_day_completion = findViewById(R.id.all_day_completion);

        Intent intent = getIntent();
        String curr = intent.getStringExtra("curr");
        if (curr != null) {
            switch (curr) {
                case "30":
                    thirty_mins_completion.setChecked(true);
                    break;
                case "60":
                    hour_completion.setChecked(true);
                    break;
                case "90":
                    hour_half_completion.setChecked(true);
                    break;
                case "120":
                    two_hour_completion.setChecked(true);
                    break;
                case "150":
                    two_hour_half_completion.setChecked(true);
                    break;
                case "180":
                    three_hour_completion.setChecked(true);
                    break;
                case "240":
                    four_hour_completion.setChecked(true);
                    break;
                case "300":
                    five_hour_completion.setChecked(true);
                    break;
                case "480":
                    all_day_completion.setChecked(true);
                    break;
            }
        }
    }

    public void onRadioButtonClicked(View v)
    {
        boolean checked = ((RadioButton) v).isChecked();
        Intent intent = new Intent();
        switch(v.getId()){
            case R.id.thirty_mins_completion:
                if (checked)
                    intent.putExtra("data", "30");
                    setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.hour_completion:
                if (checked)
                    intent.putExtra("data", "60");
                    setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.hour_half_completion:
                if (checked)
                    intent.putExtra("data", "90");
                    setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.two_hour_completion:
                if (checked)
                    intent.putExtra("data", "120");
                    setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.two_hour_half_completion2:
                if (checked)
                    intent.putExtra("data", "150");
                    setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.three_hour_completion3:
                if (checked)
                    intent.putExtra("data", "180");
                    setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.four_hour_completion:
                if (checked)
                    intent.putExtra("data", "240");
                    setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.five_hour_completion2:
                if (checked)
                    intent.putExtra("data", "300");
                    setResult(Activity.RESULT_OK, intent);
                break;
            case R.id.all_day_completion:
                if (checked)
                    intent.putExtra("data", "480");
                    setResult(Activity.RESULT_OK, intent);
                break;
        }
        finish();
    }
}
