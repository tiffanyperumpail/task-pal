package com.taskpal.taskpal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    }
}
