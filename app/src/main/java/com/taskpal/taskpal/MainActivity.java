package com.taskpal.taskpal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Button addTaskButton;
    private TextView nameText;
    private TextView monthText2;
    private TextView dayText;
    private TextView timeText1;
    private TextView timeText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addTaskButton = findViewById(R.id.addTaskButton);
        nameText = findViewById(R.id.nameText);
        monthText2 = findViewById(R.id.monthText2);
        dayText = findViewById(R.id.dayText);
        timeText1 = findViewById(R.id.timeText1);
        timeText2 = findViewById(R.id.timeText2);
    }
}
