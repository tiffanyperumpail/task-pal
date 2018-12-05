package com.taskpal.taskpal;

import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
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
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;

import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import org.joda.time.format.DateTimeFormat;

import static com.taskpal.taskpal.PreferencesActivity.MyPREFERENCES;

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
        checkDependencies();
        Bundle bundle = getIntent().getExtras();
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        calculateSchedule(bundle.getStringArray("curr"),
                TimePreference.toTimePreference(shared.getString("TimePreference", TimePreference.MORNING.name())),
                AttentionPreference.toAttentionPreference(shared.getString("AttentionPreference", AttentionPreference.NO_BREAKS.name())),
                DeadlinePreference.toDeadlinePreference(shared.getString("DeadlinePreference", DeadlinePreference.CLOSER.name())));
    }

    void calculateSchedule(String[] task, TimePreference time, AttentionPreference attention,
                           DeadlinePreference deadline) {
        //List<Event> events = new ArrayList<>();
        Event genericEvent = genericEvent(task);
        int remaining = Integer.parseInt(task[2]);
        Date start = startTime(task, deadline);
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        if (deadline == DeadlinePreference.LONGER) {
            startCal.add(Calendar.DATE, 1);
        }
        while (remaining > 0) {
            Date cur = startCal.getTime();
            Calendar curCal = Calendar.getInstance();
            curCal.setTime(cur);
            if (AttentionPreference.BREAKS == attention) {
                validEvent(genericEvent, time, Math.min(remaining, 120), curCal);
                remaining -= 120;
            } else {
                validEvent(genericEvent, time, Math.min(remaining, 180), curCal);
                remaining -= 180;
            }
            //events.add(currEvent);
            startCal.add(Calendar.DATE, 1);
            Log.d("DateIncrement", startCal.toString());
        }
    }

    @TargetApi(26)
    private Date startTime(String[] task, DeadlinePreference deadline) {
        Date start;
        int year = Integer.parseInt(task[7]);
        int month = Integer.parseInt(task[6]);
        int day = Integer.parseInt(task[5]);
        if (deadline == DeadlinePreference.LONGER) {
            start = new Date();
        } else {
            LocalDate dateMin = LocalDate.now();
            LocalDate dateMax = LocalDate.of(year, month, day);
            LocalDate median = dateMin.plusDays(ChronoUnit.DAYS.between(dateMin, dateMax) / 2);
            start = Date.from(median.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return start;
    }

    private void validEvent(Event task, TimePreference time, int remaining, Calendar cal) {
        Log.d("DateIncrementAsync", cal.toString());
        validEventAsync(task, time, remaining, cal);
    }
    /*
    private Event validEvent(Event task, TimePreference time, int remaining, Calendar cal) {
        FreeBusyRequest fbr = new FreeBusyRequest();
        FreeBusyRequestItem calendar = new FreeBusyRequestItem();
        calendar.setId("primary");
        fbr.setItems(new ArrayList<FreeBusyRequestItem>());
        fbr.getItems().add(calendar);
        List<Calendar> preferredTimes = preferredTime(cal, time);
        fbr.setTimeMin(new DateTime(preferredTimes.get(0).getTime()));
        fbr.setTimeMax(new DateTime(preferredTimes.get(1).getTime()));
        try {
            com.google.api.services.calendar.Calendar.Freebusy.Query query = service.freebusy().query(fbr);
            FreeBusyResponse response = query.execute();
            List<TimePeriod> busyIntervals = response.getCalendars().get("primary").getBusy();
            List<Calendar> freeTimes = freeTimes(busyIntervals, time, cal, remaining);
            task.setStart(new EventDateTime()
                    .setDateTime(new DateTime(freeTimes.get(0).getTime()))
                    .setTimeZone("America/Los_Angeles"));
            task.setEnd(new EventDateTime()
                    .setDateTime(new DateTime(freeTimes.get(1).getTime()))
                    .setTimeZone("America/Los_Angeles"));
        }
        catch (IOException e) {
            Log.d("FreeBusy", "Failed request.");
        }
        return task;
    }*/



    private List<DateTime> preferredTime(Calendar cal, TimePreference time) {
        Log.d("FinalTask2", cal.toString());
        List<DateTime> preferredTimes = new ArrayList<>();
        Date current = cal.getTime();
        DateTime start;
        DateTime end;
        if (time == TimePreference.DAY) {
            cal.set(Calendar.HOUR_OF_DAY, 11);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            start = new DateTime(cal.getTime());
            cal.add(Calendar.HOUR_OF_DAY, 6);
            end = new DateTime(cal.getTime());
        }
        else if (time == TimePreference.MORNING) {
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            start = new DateTime(cal.getTime());
            cal.add(Calendar.HOUR_OF_DAY, 4);
            end = new DateTime(cal.getTime());
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 17);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            start = new DateTime(cal.getTime());
            cal.add(Calendar.HOUR_OF_DAY, 5);
            cal.add(Calendar.DATE, 1);
            end = new DateTime(cal.getTime());
        }
        cal.setTime(current);
        preferredTimes.add(start);
        preferredTimes.add(end);
        Log.d("FinalTask", start.toString());
        Log.d("FinalTask", end.toString());
        return preferredTimes;
    }

    private List<DateTime> freeTimes(List<TimePeriod> intervals, TimePreference time, Calendar cal, int remaining) {
        List<DateTime> freeTimes = new ArrayList<>();
        Date current = cal.getTime();
        DateTime start;
        DateTime end;
        if (intervals.isEmpty()) {
            if (time == TimePreference.DAY) {
                start = freeEntry(cal, 11, 0);
                end = freeEntry(cal, 11 + (remaining / 60), remaining % 60);
            }
            else if (time == TimePreference.MORNING) {
                start = freeEntry(cal, 8, 0);
                end = freeEntry(cal, 8 + (remaining / 60), remaining % 60);
            }
            else {
                start = freeEntry(cal, 17, 0);
                end = freeEntry(cal, 17 + (remaining / 60), remaining % 60);
            }
        } else {
            Calendar endTime = Calendar.getInstance();
            endTime.setTimeInMillis(intervals.get(intervals.size() - 1).getEnd().getValue());
            start = intervals.get(intervals.size() - 1).getEnd();
            end = freeEntry(endTime, endTime.get(Calendar.HOUR_OF_DAY) + (remaining / 60),
                    endTime.get(Calendar.MINUTE) + remaining % 60);
        }
        cal.setTime(current);
        freeTimes.add(start);
        freeTimes.add(end);
        return freeTimes;
    }

    private DateTime freeEntry(Calendar cal, int hour, int minutes) {
        Date current = cal.getTime();
        DateTime value;
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minutes);
        value = new DateTime(cal.getTime());
        cal.setTime(current);
        return value;
    }

    /**
     * Returns a generic event with a input of NewTask variables.
     */
    public Event genericEvent(String[] task) {
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
        Event genericEvent = new Event().setSummary(task[0])
                .setLocation(task[1]);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone("America/Los_Angeles");
        genericEvent.setStart(start);
        EventDateTime end = new EventDateTime()
                .setDateTime(dueDate)
                .setTimeZone("America/Los_Angeles");
        genericEvent.setEnd(end);
        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=1"};
        genericEvent.setRecurrence(Arrays.asList(recurrence));
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(Integer.parseInt(task[4])),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        genericEvent.setReminders(reminders);
        return genericEvent;
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

    private void checkDependencies() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
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
                checkDependencies();
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
                    checkDependencies();
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
                        checkDependencies();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    checkDependencies();
                }
                break;
        }
    }


    public void validEventAsync(final Event task, final TimePreference time, final int remaining, final Calendar cal) {
        Log.d("DateIncrementval", cal.toString());

        new AsyncTask<Void, Void, FreeBusyResponse>() {

            @Override
            protected FreeBusyResponse doInBackground (Void...voids){
                try {

                    FreeBusyRequest fbr = new FreeBusyRequest();
                    FreeBusyRequestItem calendar = new FreeBusyRequestItem();
                    calendar.setId("primary");
                    fbr.setItems(new ArrayList<FreeBusyRequestItem>());
                    fbr.getItems().add(calendar);
                    List<DateTime> preferredTimes = preferredTime(cal, time);
                    fbr.setTimeMin(preferredTimes.get(0));
                    fbr.setTimeMax(preferredTimes.get(1));
                    Log.d("DateIncrementfbr", fbr.getTimeMin().toString());
                    Log.d("DateIncrementfbr", fbr.getTimeMax().toString());
                    com.google.api.services.calendar.Calendar.Freebusy.Query query = service.freebusy().query(fbr);
                    return query.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute (FreeBusyResponse response){
                List<TimePeriod> busyIntervals = response.getCalendars().get("primary").getBusy();
                List<DateTime> freeTimes = freeTimes(busyIntervals, time, cal, remaining);
                Log.d("DateIncrementPst", freeTimes.get(0).toString());
                Log.d("DateIncrementPst", freeTimes.get(1).toString());
                task.setStart(new EventDateTime()
                        .setDateTime(freeTimes.get(0))
                        .setTimeZone("America/Los_Angeles"));
                task.setEnd(new EventDateTime()
                        .setDateTime(freeTimes.get(1))
                        .setTimeZone("America/Los_Angeles"));
                createEventAsync(task.getSummary(), task.getLocation(), task.getDescription(),
                        task.getStart(), task.getEnd(), task.getAttendees());
            }
        }.execute();
    }

    public void createEventAsync(final String summary, final String location, final String des,
                                 final EventDateTime startDate, final EventDateTime endDate, final List<EventAttendee>
                                         eventAttendees) {

        new AsyncTask<Void, Void, String>() {

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
                checkDependencies();
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
                checkDependencies();
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
        scheduledEventsList.clear();
        for (Event event : items) {
            scheduledEventsList.add(event);
        }
    }

}