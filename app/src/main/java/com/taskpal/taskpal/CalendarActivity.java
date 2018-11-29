package com.taskpal.taskpal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import org.joda.time.format.DateTimeFormat;

public class CalendarActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private com.google.api.services.calendar.Calendar service = null;
    GoogleAccountCredential mCredential;
    private List<Event> scheduledEventsList = new ArrayList<>();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final String CALENDAR_EVENTS = "https://www.googleapis.com/auth/calendar.events";
    private TextView mOutputText;
    ProgressDialog mProgress;


    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CALENDAR_EVENTS, CalendarScopes.CALENDAR};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        getResultsFromApi();
        Bundle bundle = getIntent().getExtras();
        calculateSchedule(bundle.getStringArray("curr"));
    }

    /**
     * Will calculate a list of subevents to best fit the current users schedule.
     */
    void calculateSchedule(String[] task) {
        List<Event> events = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        Log.d("input", task.toString());
        cal.set(Integer.parseInt(task[7]), Integer.parseInt(task[6]), Integer.parseInt(task[5]),
                Integer.parseInt(task[8]), Integer.parseInt(task[9]));
        cal.add(Calendar.MONTH, -1);
        DateTime dueDate = new DateTime(cal.getTime());
        Log.d("insertEventHour", task[8]);
        Log.d("insertEventEnd", dueDate.toString());
        cal.add(Calendar.HOUR, -2);
        DateTime startDate = new DateTime(cal.getTime());
        Log.d("insertEventStart", startDate.toString());
        //DateTime startTime = calculateStartTime(task[4], dueDate);
        Event new1 = new Event().setSummary(task[0])
                                .setLocation(task[1]);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone("America/Los_Angeles");
        new1.setStart(start);
        EventDateTime end = new EventDateTime()
                .setDateTime(dueDate)
                .setTimeZone("America/Los_Angeles");
        new1.setEnd(end);
        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=1"};
        new1.setRecurrence(Arrays.asList(recurrence));
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(Integer.parseInt(task[4])),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        new1.setReminders(reminders);
        Log.d("event", new1.getStart().toString());
        Log.d("event", new1.getEnd().toString());
        events.add(new1);
        for (Event event : events) {
            createEventAsync(event.getSummary(), event.getLocation(), event.getDescription(),
                    event.getStart(), event.getEnd(), event.getAttendees());
        }
    }

    private DateTime calculateStartTime(String priority, DateTime dueDate) {
        return new DateTime("asd");
    }


    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private Exception mLastError = null;
        private boolean FLAG = false;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            service = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("TaskPal")
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                getDataFromApi();
            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    Log.d("Auth", "AuthIO");
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            CalendarActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            Log.d("Insert", "Sets up service");
            new MakeRequestTask(mCredential).execute();
        }
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                CalendarActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            Log.d("Insert", "HasPermissions");
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                Log.d("Insert", "HasName");
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                Log.d("Insert", "NoName");
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            Log.d("Insert", "RequestPermissions");
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }



    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    public void createEventAsync(final String summary, final String location, final String des,
                                 final EventDateTime startDate, final EventDateTime endDate, final List<EventAttendee>
            eventAttendees) {

        new AsyncTask<Void, Void, String>() {
            private com.google.api.services.calendar.Calendar mService = null;
            private Exception mLastError = null;
            private boolean FLAG = false;


            @Override
            protected String doInBackground (Void...voids){
                try {
                    insertEvent(summary, location, des, startDate, endDate, eventAttendees);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute (String s){
                super.onPostExecute(s);
                getResultsFromApi();
                long startMillis2 = System.currentTimeMillis();
                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                ContentUris.appendId(builder, startMillis2);
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(builder.build());
                startActivity(intent);
            }
        }.execute();
    }

    public void createEventAsync(final String summary, final String location, final String des,
                                 final DateTime startDate, final DateTime endDate, final EventAttendee[]
            eventAttendees) {

        new AsyncTask<Void, Void, String>() {
            private com.google.api.services.calendar.Calendar mService = null;
            private Exception mLastError = null;
            private boolean FLAG = false;


            @Override
            protected String doInBackground (Void...voids){
                try {
                    insertEvent(summary, location, des, startDate, endDate, eventAttendees);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute (String s){
                super.onPostExecute(s);
                getResultsFromApi();
            }
        }.execute();
    }

    /**
     * Inserts a event into Google Calendar with associated params.
     */
    void insertEvent(String summary, String location, String des, EventDateTime startDate,
                     EventDateTime endDate, List<EventAttendee> eventAttendees) throws IOException {
        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(des);
        event.setStart(startDate);
        Log.d("insertStart", startDate.toString());
        event.setEnd(endDate);
        Log.d("insertEnd", endDate.toString());
        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=1"};
        event.setRecurrence(Arrays.asList(recurrence));
        event.setAttendees(eventAttendees);
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);
        String calendarId = "primary";
        Log.d("Insert", "Before execution");
        //event.send
        if (service != null) {
            try {
                Log.d("Insert", "After Execution");
                service.events().insert(calendarId, event).setSendNotifications(true).execute();
                System.out.printf("Event created: %s\n", event.getHtmlLink());
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), CalendarActivity.REQUEST_AUTHORIZATION);
            }
        }
    }

    /**
     * Inserts a event into Google Calendar with associated params.
     */
    void insertEvent(String summary, String location, String des, DateTime startDate,
                     DateTime endDate, EventAttendee[] eventAttendees) throws IOException {
        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(des);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDate)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);
        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=1"};
        event.setRecurrence(Arrays.asList(recurrence));
        event.setAttendees(Arrays.asList(eventAttendees));
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);
        String calendarId = "primary";
        Log.d("Insert", "Before execution");
        //event.send
        if (service != null) {
            try {
                Log.d("Insert", "After Execution");
                service.events().insert(calendarId, event).setSendNotifications(true).execute();
                System.out.printf("Event created: %s\n", event.getHtmlLink());
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), CalendarActivity.REQUEST_AUTHORIZATION);
            }
        }
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private void getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        Event scheduledEvent;
        scheduledEventsList.clear();
        for (Event event : items) {
            scheduledEventsList.add(event);
        }
    }

}
