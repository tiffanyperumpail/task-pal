package com.taskpal.taskpal;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditTaskActivity extends AppCompatActivity {

    private Button timeButton;
    private Button difficultyButton;
    private Button alertButton;
    private Button dateButton;
    private Button editButton;
    private ImageButton exitButton;
    private EditText titleText;
    private EditText locationText;
    private String[] event;

    public static final int TIME_REQUEST_CODE = 1;
    public static final int PRIORITY_REQUEST_CODE = 2;
    public static final int ALERT_REQUEST_CODE = 3;
    public static final int DATE_REQUEST_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        timeButton = findViewById(R.id.timeButtonEdit);
        difficultyButton = findViewById(R.id.difficultyButtonEdit);
        alertButton = findViewById(R.id.alertButtonEdit);
        dateButton = findViewById(R.id.dateButtonEdit);
        editButton = findViewById(R.id.editButton);
        exitButton = findViewById(R.id.exitButtonEdit);
        titleText = findViewById(R.id.titleTextEdit);
        locationText = findViewById(R.id.locationTextEdit);
        event = new String[10];

        //setup(old_event);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event[0] = titleText.getText().toString();
                event[1] = locationText.getText().toString();

                for (int i = 0; i < 10; i++) {
                    if (event[i] == null || event[i].equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.new_task_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // 0-name, 1-location, 2-completion time (minutes), 3-priority (1 to 5), 4-alert (minutes), 5-due day, 6-due month, 7-due year, 8-due hour (24), 9-due min
                startActivity(new Intent(EditTaskActivity.this, CalendarActivity.class));
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTaskActivity.this, completionTimeActivity.class);
                intent.putExtra("curr", event[2]);
                startActivityForResult(intent, TIME_REQUEST_CODE);
            }
        });

        difficultyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTaskActivity.this, difficultyActivity.class);
                intent.putExtra("curr", event[3]);
                startActivityForResult(intent, PRIORITY_REQUEST_CODE);
            }
        });

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTaskActivity.this, alertActivity.class);
                intent.putExtra("curr", event[4]);
                startActivityForResult(intent, ALERT_REQUEST_CODE);
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTaskActivity.this, dateActivity.class);
                intent.putExtra("completion", event[2]);
                intent.putExtra("day", event[5]);
                intent.putExtra("month", event[6]);
                intent.putExtra("year", event[7]);
                intent.putExtra("hour", event[8]);
                intent.putExtra("min", event[9]);
                startActivityForResult(intent, DATE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TIME_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                event[2] = data.getStringExtra("data");
                timeButton.setText(event[2] + " MINS >");
            }
        } else if (requestCode == PRIORITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                event[3] = data.getStringExtra("data");
                switch (event[3]) {
                    case "1":
                        difficultyButton.setText("very low >");
                        break;
                    case "2":
                        difficultyButton.setText("low >");
                        break;
                    case "3":
                        difficultyButton.setText("medium >");
                        break;
                    case "4":
                        difficultyButton.setText("high >");
                        break;
                    case "5":
                        difficultyButton.setText("very high >");
                        break;
                }
            }
        } else if (requestCode == ALERT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                event[4] = data.getStringExtra("data");
                switch (event[4]) {
                    case "0":
                        alertButton.setText("NONE >");
                        break;
                    case "15":
                        alertButton.setText("15 MINS >");
                        break;
                    case "30":
                        alertButton.setText("30 MINS >");
                        break;
                    case "60":
                        alertButton.setText("1 HR >");
                        break;
                    case "120":
                        alertButton.setText("2 HRS >");
                        break;
                    case "1440":
                        alertButton.setText("1 DAY >");
                        break;
                }
            }
        } else if (requestCode == DATE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                event[5] = data.getStringExtra("day");
                event[6] = data.getStringExtra("month");
                event[7] = data.getStringExtra("year");
                event[8] = data.getStringExtra("hour");
                event[9] = data.getStringExtra("min");
                dateButton.setText(event[5] + "/" + event[6] + "/" + event[7].substring(2) + " " + event[8] + ":" + event[9]);
            }
        }
    }

    private void setup(String[] old_event) {
        for (int i = 0; i < 10; i++) {
            event[i] = old_event[i];
        }
        titleText.setText(event[0]);
        locationText.setText(event[1]);
        timeButton.setText(event[2] + " MINS >");
        switch (event[3]) {
            case "1":
                difficultyButton.setText("very low >");
                break;
            case "2":
                difficultyButton.setText("low >");
                break;
            case "3":
                difficultyButton.setText("medium >");
                break;
            case "4":
                difficultyButton.setText("high >");
                break;
            case "5":
                difficultyButton.setText("very high >");
                break;
        }
        switch (event[4]) {
            case "0":
                alertButton.setText("NONE >");
                break;
            case "15":
                alertButton.setText("15 MINS >");
                break;
            case "30":
                alertButton.setText("30 MINS >");
                break;
            case "60":
                alertButton.setText("1 HR >");
                break;
            case "120":
                alertButton.setText("2 HRS >");
                break;
            case "1440":
                alertButton.setText("1 DAY >");
                break;
        }
        dateButton.setText(event[5] + "/" + event[6] + "/" + event[7].substring(2) + " " + event[8] + ":" + event[9]);
    }
}
