package house.thelittlemountaindev.islaamii;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sahaab.hijri.caldroid.CaldroidFragment;
import com.sahaab.hijricalendar.HijriCalendarDate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import house.thelittlemountaindev.islaamii.adapters.ExtDatabaseHandler;
import house.thelittlemountaindev.islaamii.adapters.PrayerAdapter;
import house.thelittlemountaindev.islaamii.utils.AlarmReceiver;
import house.thelittlemountaindev.islaamii.utils.Notifier;
import house.thelittlemountaindev.islaamii.utils.PrayTime;
import house.thelittlemountaindev.islaamii.utils.TrackGPS;

public class MainActivity extends FragmentActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private static final int LOCATION_REQUEST_CODE = 1;
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL = 1000;
    private static final long LOCATION_UPDATE_INTERVAL = 800;

    protected LocationManager locationManager;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private TextView tvNextPrayer, tvDate, tvAlarmMorning, tvAlarmEvening, tvLocationName;
    private LinearLayout calendarView;
    private ListView prayerListview;
    private Button btnShare;
    private ImageButton btnPrayerSettings;
    private CardView cardPrayer, cardHadith, cardDate, cardMorning, cardEvening, cardAlarm;
    private ViewGroup transtitionContainer;
    private ImageView ivDailyHadith;

    private PrayerAdapter adapter;

    private boolean prayerDisplayed, calendarDisplayed = false, morningDisplayed, eveningDisplayed;
    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference reference;

    // database related variables (as Constant).
    private SQLiteDatabase database;
    private static final String DB_NAME = "zikrdbV2";
    private static final String MOR_TABLE_NAME = "sabah_table";
    private static final String EVE_TABLE_NAME = "masa_table";
    private static final int NUM_PAGES = 20;

    //declare all database field as constants
    private static final String KEY_ID = "_id";
    private static final String KEY_ZIKR = "zikr_txt";
    private static final String KEY_COUNT = "count";


    //Pager related declarations
    public ViewPager mPager, mPager2;
    public PagerAdapter mPagerAdapter;
    public EveningPagerAdapter mEveningAdapter;

    ArrayList<String> countText = new ArrayList<String>();
    String[] countArray = new String[0];

    ArrayList<String> zikrText = new ArrayList<String>();
    String[] zikrTextArray = new String[0];

    ArrayList<String> countTextE = new ArrayList<String>();
    String[] countArrayE = new String[0];

    ArrayList<String> zikrTextE = new ArrayList<String>();
    String[] zikrTextArrayE = new String[0];

    private long prayerInMillis;
    static final Integer LOCATION = 0x1;
    private AlarmManager alarmMgr;
    private FirebaseStorage firebaseStorage;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, getString(R.string.addmod_app_id));

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-1324658631150169/3210215064");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        //Firebase initialization
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        reference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();

        //FindViewById...
        findViews();

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        }else{
            askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
        }

        fetchCalendar();

        getUrlOfTheDay();

        setPrayerAlarms();

        tvDate.setText(HijriCalendarDate.getSimpleDate(Calendar.getInstance(), 0));

        //Open the db in from 'assets'
        ExtDatabaseHandler dbHandler = new ExtDatabaseHandler(this, DB_NAME);
        database = dbHandler.openDataBase();

        //Instantiate ViewPager, with dimensional behaviors forMorning zikr
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager2 = findViewById(R.id.pager2);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setClipToPadding(false);
        mPager.setPageMargin(-80);
        mPager.setPadding(30, 0, 30, 30);
        mPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                final float normalPosition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalPosition / 2 + 0.5f);
                page.setScaleY(normalPosition / 2 + 0.5f);
            }
        });

//Viewpager for Evening zikr
        mEveningAdapter = new EveningPagerAdapter(getSupportFragmentManager());
        mPager2.setAdapter(mEveningAdapter);
        mPager2.setClipToPadding(false);
        mPager2.setPageMargin(-80);
        mPager2.setPadding(30, 0, 30, 30);
        mPager2.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                final float normalPosition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalPosition / 2 + 0.5f);
                page.setScaleY(normalPosition / 2 + 0.5f);
            }
        });
    }

    private void findViews() {
        transtitionContainer = (ViewGroup) findViewById(R.id.main_transition_container);
        calendarView = (LinearLayout) findViewById(R.id.calendarView);
        prayerListview = (ListView) findViewById(R.id.listView_prayer);
        btnShare = (Button) findViewById(R.id.btn_share);
        btnPrayerSettings = (ImageButton) findViewById(R.id.btn_prayer_options);

        ivDailyHadith = (ImageView) findViewById(R.id.iv_daily_hadith);

        tvNextPrayer = (TextView) findViewById(R.id.tv_next_prayer_time);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvLocationName = (TextView) findViewById(R.id.tv_location_name);

        cardPrayer = (CardView) findViewById(R.id.cv_prayer);
        cardHadith = (CardView) findViewById(R.id.cv_hadith);
        cardDate = (CardView) findViewById(R.id.cv_calendar);
        cardMorning = (CardView) findViewById(R.id.cv_morning_zikr);
        cardEvening = (CardView) findViewById(R.id.cv_evening_zikr);
        cardAlarm = findViewById(R.id.cv_alarms);

        cardPrayer.setOnClickListener(this);
        cardHadith.setOnClickListener(this);
        cardDate.setOnClickListener(this);
        cardMorning.setOnClickListener(this);
        cardEvening.setOnClickListener(this);
        cardAlarm.setOnClickListener(this);

        btnShare.setOnClickListener(this);
        btnPrayerSettings.setOnClickListener(this);
    }

    private void fetchPrayers(double latitude, double longitude) {
        // Create the object of prayer time and call all parameters
        PrayTime prayers = new PrayTime();

        // time format check from device
        if (DateFormat.is24HourFormat(this)) {
            prayers.setTimeFormat(prayers.Time24);
        } else {
            prayers.setTimeFormat(prayers.Time12);
        }

        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setCalcMethod(prayers.Egypt);

        Calendar cal = Calendar.getInstance();
        double timezone = getTimeZone();

        Date alaan = new Date();
        cal.setTime(alaan);

        // Shorten and round Lat and Lon to 2 digits and to nearest
        double shortLat = (float) Math.round(latitude);
        double shortLon = (float) Math.round(longitude);

        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal, shortLat, shortLon,
                timezone);

        String[] x = getResources().getStringArray(R.array.prayer_names);
        ArrayList<String> prayerNames = new ArrayList<String>(Arrays.asList(x));

        adapter = new PrayerAdapter(getApplicationContext(),
                prayerNames, prayerTimes);

        prayerListview.setAdapter(adapter);

    }

    private double getTimeZone() {
        TimeZone timez = TimeZone.getDefault();
        double hoursDiff = (timez.getRawOffset() / 1000.0) / 3600;
        return hoursDiff;
    }

    private void showOrHidePrayers() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_hidden_prayertimes);
        TransitionManager.beginDelayedTransition(transtitionContainer);
        prayerDisplayed = !prayerDisplayed;
        linearLayout.setVisibility(prayerDisplayed ? View.VISIBLE : View.GONE);
        btnPrayerSettings.setVisibility(prayerDisplayed ? View.VISIBLE : View.GONE);
        tvLocationName.setVisibility(prayerDisplayed ? View.VISIBLE : View.GONE);
        if (!prayerDisplayed && interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    private void showOrHideMorning() {
        TransitionManager.beginDelayedTransition(transtitionContainer);
        morningDisplayed = !morningDisplayed;
        mPager.setVisibility(morningDisplayed ? View.VISIBLE : View.GONE);
    }

    private void showOrHideEvening() {
        TransitionManager.beginDelayedTransition(transtitionContainer);
        eveningDisplayed = !eveningDisplayed;
        mPager2.setVisibility(eveningDisplayed ? View.VISIBLE : View.GONE);
    }

    private void showOrHideCalendar() {
        TransitionManager.beginDelayedTransition(transtitionContainer);
        if (!calendarDisplayed) {
            calendarView.setVisibility(View.VISIBLE);
        }else {
            calendarView.setVisibility(View.GONE);
        }

        /*calendarDisplayed = !calendarDisplayed;
        calendarView.setVisibility(calendarDisplayed ? View.VISIBLE: View.GONE);*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_prayer:
                showOrHidePrayers();


                logAnalytics("card_prayer_id", "Prayers shown");
                break;

            case R.id.cv_calendar:
                showOrHideCalendar();
                logAnalytics("card_calendar", "Calendar shown");
                break;

            case R.id.btn_share:
                shareImage();
                logAnalytics("btn_share", "Hadith shared");
                break;

            case R.id.cv_morning_zikr:
                showOrHideMorning();
                loadMorningZikr(MOR_TABLE_NAME);
                logAnalytics("card_morning", "Morning zikr shown");
                break;

            case R.id.cv_evening_zikr:
                loadEveningZikr(EVE_TABLE_NAME);
                showOrHideEvening();
                logAnalytics("card_evening", "Evening shown");
                break;

            case R.id.btn_prayer_options:
                logAnalytics("prefs", "Prefs. opened");
                startActivity(new Intent(MainActivity.this, PreferencesActivity.class));
                break;

            case R.id.cv_alarms:
                setAlarm();
                logAnalytics("card_alarms", "Alarms shown");
                break;

            case R.id.cv_hadith:
                final TextView title = findViewById(R.id.tv_hadith_title);
                cardHadith.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                title.setVisibility(View.VISIBLE);
                                break;
                            case MotionEvent.ACTION_UP:
                                title.setVisibility(View.GONE);
                                break;
                            default:
                                break;
                        }

                        return false;
                    }


                });

            default:
                break;
        }
    }

    private void logAnalytics(String id, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void fetchLocation() {
        SharedPreferences sharedCache = getPreferences(Context.MODE_PRIVATE);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildGoogleApiClient();
        }else {
            if (sharedCache.contains("latitude")) {
                double savedLat = Double.parseDouble(sharedCache.getString("latitude", "0.00"));
                double savedLon = Double.parseDouble(sharedCache.getString("longitude", "0.00"));
                fetchPrayers(savedLat, savedLon);
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(getResources().getString(R.string.location_off_dialog_title));
                dialog.setMessage(getResources().getString(R.string.location_off_dialog_text));
                dialog.setPositiveButton(getResources().getString(R.string.location_dialog_pstv), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        //get gps
                    }
                });
                dialog.setNegativeButton(getResources().getString(R.string.location_dialog_ngtv), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                    }
                });
                dialog.show();
            }
        }
    }

    private void getUrlOfTheDay() {
        String deviceLanguage = Locale.getDefault().getLanguage();
        if (deviceLanguage.equals("fr")) {
            //get url_key of the day
            reference.child("french/url_of_the_day").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key_daily_of_url = dataSnapshot.getValue(String.class);
                    fetchHadith(key_daily_of_url);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (deviceLanguage.equals("en")) {
            //get url_key of the day
            reference.child("english/url_of_the_day").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key_daily_of_url = dataSnapshot.getValue(String.class);
                    fetchHadith(key_daily_of_url);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            //get url_key of the day
            reference.child("arabic/url_of_the_day").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key_daily_of_url = dataSnapshot.getValue(String.class);
                    fetchHadith(key_daily_of_url);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void fetchHadith(String fileUrl) {
        StorageReference storageReference;

        storageReference = firebaseStorage.getReferenceFromUrl(fileUrl);

        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivDailyHadith.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MainActivity.this, "" + exception, Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
            }
        });

    }

    private void fetchCalendar() {
        final CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);


        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendarView, caldroidFragment);
        t.commit();
    }

    //Pull data from table and insert into array
    private void loadMorningZikr(String table) {
        Cursor cursor = database.query(table,
                new String[]
                        {KEY_ID, KEY_ZIKR, KEY_COUNT},
                null, null, null, null
                , KEY_ID);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String item = cursor.getString(1);
                String count = cursor.getString(2);

                zikrText.add(item);
                countText.add(count);

            } while (cursor.moveToNext());
        }
        cursor.close();
        zikrTextArray = zikrText.toArray(new String[0]);
        countArray = countText.toArray(new String[0]);
    }

    private void loadEveningZikr(String table) {
        Cursor cursor = database.query(table,
                new String[]
                        {KEY_ID, KEY_ZIKR, KEY_COUNT},
                null, null, null, null
                , KEY_ID);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String item = cursor.getString(1);
                String count = cursor.getString(2);

                zikrTextE.add(item);
                countTextE.add(count);

            } while (cursor.moveToNext());
        }
        cursor.close();
        zikrTextArrayE = zikrTextE.toArray(new String[0]);
        countArrayE = countTextE.toArray(new String[0]);
    }

    private void setupNotifications() {
        //Retrieve user preferences to check if notification is ON
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notifEnabled = preferences.getBoolean("notif_status", true);

        //Also retrieving prayer times from Prefs --- used to run Alarm
        SharedPreferences sharedCache = getPreferences(Context.MODE_PRIVATE);

        //Checking the availability of both notif and prayer times
        if (sharedCache.contains("p_") && notifEnabled) {

            Set<String> getRaw = sharedCache.getStringSet("p_", null);
            ArrayList<String> get = new ArrayList<String>(getRaw); // Convert Set<String> to ArrayList

            // running alarm from data in the list 'get'
            for (int i = 0; i < get.size(); i++) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                String fDate = f.format(c.getTime());
                String fTime = get.get(i);

                String toParse = fDate + " " + fTime;
                try {
                    SimpleDateFormat formater = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");
                    Date date = formater.parse(toParse);
                    prayerInMillis = date.getTime();

                } catch (Exception e) {
                    // TODO: handle exception
                }

                Intent intent1 = new Intent(this, Notifier.class);
                final int _id = (int) System.currentTimeMillis();
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, _id,
                        intent1, 0);

                if (System.currentTimeMillis() > prayerInMillis) {
                    prayerInMillis += AlarmManager.INTERVAL_DAY;
                } else {
                    AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, prayerInMillis,
                            pendingIntent);
                }
            }
        } else {
            //Do not try to retrieve anything
        }

    }

    /***************************LOCATION**************************************
    |
    |
    |
    |
    |
    ***************************************************************************/
    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        fetchPrayers(latitude, longitude);
        //mGoogleApiClient.disconnect();

        //Save lat & lon for future
        SharedPreferences sharedCache = getPreferences(
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedCache.edit();
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.apply();

        if (mLastLocation != null){
            new ReversGeocodingTask().execute();
        }
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MorningFragment fragment = new MorningFragment();
            Bundle args = new Bundle();
            args.putString("zikrText", zikrTextArray[position]);
            args.putString("zikrCount", countArray[position]);
            fragment.setArguments(args);

            return  fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

    public class EveningPagerAdapter extends FragmentStatePagerAdapter {

        public EveningPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            EveningFragment fragment = new EveningFragment();
            Bundle args = new Bundle();
            args.putString("zikrTextE", zikrTextArrayE[position]);
            args.putString("zikrCountE", countArrayE[position]);
            fragment.setArguments(args);

            return  fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }

    /**************************Alarm***********************************************
    *
    *
    *
    *
    *
    *******************************************************************************/
    private TimePicker mTimePicker;
    public int tp_hour;
    public int tp_min;
    private TextClock tv_chosen_time;
    private AlarmManager mAlarmManger;
    private PendingIntent pIntent;
    private boolean alarmIsOff = false;
    private Switch swToggleAlarm; private ImageButton btnToggleAlarm;


    private void setAlarm(){
        mTimePicker = (TimePicker) findViewById(R.id.alarm_time_picker);
        tv_chosen_time = (TextClock) findViewById(R.id.tc_alarm_time);
        swToggleAlarm = findViewById(R.id.sw_toggle_alarm);
        btnToggleAlarm = findViewById(R.id.btn_toggle_alarm);

        mAlarmManger = (AlarmManager) getSystemService(ALARM_SERVICE);
        mTimePicker.setVisibility(View.VISIBLE);
        mTimePicker.setIs24HourView(true);

        if (!alarmIsOff) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
            calendar.set(Calendar.MINUTE, mTimePicker.getMinute());

            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            pIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
            mAlarmManger.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);
            swToggleAlarm.setChecked(true);

        }else {
            mAlarmManger.cancel(pIntent);
            swToggleAlarm.setChecked(false);
        }

        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                alarmIsOff = false;
                tp_hour = mTimePicker.getCurrentHour();
                tp_min = mTimePicker.getCurrentMinute();
                tv_chosen_time.setText(new StringBuilder().append(tp_hour).append(" : ").append(tp_min));
            }
        });
    }

    private void shareImage() {
        Uri bmpUri = getLocalBitmapUri(ivDailyHadith);

        Intent sharing = new Intent();
        sharing.setAction(Intent.ACTION_SEND);
        sharing.putExtra(Intent.EXTRA_STREAM, bmpUri);
        sharing.setType("image/*");
        startActivity(Intent.createChooser(sharing, " share "));
        logAnalytics("share", "Reminder shared");
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "zikr_shared_image_" + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    // The Background job of finding locations and other stuff
    private class ReversGeocodingTask extends AsyncTask<Location, Void, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Location... arg0) {
            Geocoder geocoder;
            String city, country;
            List<Address> addresses = null;

            geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(
                        mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                         1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {

                if (addresses.get(0).getAddressLine(2) == null) {
                    city = addresses.get(0).getAddressLine(3);
                    if (addresses.get(0).getAddressLine(3) == null) {
                        city = addresses.get(0).getAddressLine(1);
                    }

                } else {
                    city = addresses.get(0).getAddressLine(1);
                }

                country = addresses.get(0).getCountryCode();

                return city + ", " + country;
            }
            return "" + getResources().getString(R.string.no_connexion);
        }

        @Override
        protected void onPostExecute(String result) {

            tvLocationName.setText("------- " + result + " -------");

            // Saved data about last location
            SharedPreferences sharedCache = getPreferences(
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedCache.edit();
            editor.putString("lastLocationName", result);
            editor.apply();
        }

    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION) {
            fetchLocation();
        }
    }

    private void setPrayerAlarms() {
        //Retrieve user preferences to check if notification is ON
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notifEnabled = preferences.getBoolean("notif_status", true);

        //Also retrieving prayer times from Prefs --- used to run Alarm
        SharedPreferences sharedCache = getPreferences(Context.MODE_PRIVATE);

        //Checking the availability of both notif and prayer times
        if (sharedCache.contains("p_") &&  notifEnabled) {

            Set<String> getRaw = sharedCache.getStringSet("p_", null);
            ArrayList<String> get = new ArrayList<String>(getRaw); // Convert Set<String> to ArrayList

            // running alarm from data in the list 'get'
            for (int i = 0; i < get.size(); i++) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                String fDate = f.format(c.getTime());
                String fTime = get.get(i);

                String toParse = fDate + " " + fTime;
                try {
                    SimpleDateFormat formater = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");
                    Date date = formater.parse(toParse);
                    prayerInMillis = date.getTime();

                } catch (Exception e) {
                    // TODO: handle exception
                }

                Intent intent1 = new Intent(this, Notifier.class);
                final int _id = (int) System.currentTimeMillis();
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, _id,
                        intent1, 0);

                if (System.currentTimeMillis() > prayerInMillis) {
                    prayerInMillis += AlarmManager.INTERVAL_DAY;
                } else {
                    alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, prayerInMillis,
                            pendingIntent);
                }
            }
        }else{
            //Do not try to retrieve anything
        }
    }

    @Override
    protected void onStart() {
        //loadMorningZikr(MOR_TABLE_NAME);
        //loadEveningZikr(EVE_TABLE_NAME);
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
