 package com.alaaeltaweel.thikrallah;





import android.Manifest;

import android.app.AlarmManager;

import android.app.AlertDialog;

import android.content.ComponentName;

import android.content.Context;

import android.content.DialogInterface;

import android.content.Intent;

import android.content.ServiceConnection;

import android.content.SharedPreferences;

import android.content.pm.PackageManager;

import android.content.res.Configuration;

import android.location.Address;

import android.location.Criteria;

import android.location.Geocoder;

import android.location.Location;

import android.location.LocationManager;

import android.net.Uri;

import android.os.AsyncTask;

import android.os.Build;

import android.os.Bundle;

import android.os.Environment;

import android.os.Handler;

import android.os.IBinder;

import android.os.Looper;

import android.os.Message;

import android.os.Messenger;

import android.os.PowerManager;

import android.os.RemoteException;

import android.os.SystemClock;

import android.provider.Settings;

import android.text.TextUtils;

import android.util.Log;

import android.view.Menu;

import android.view.MenuItem;



import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;

import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;

import androidx.preference.PreferenceManager;



import com.alaaeltaweel.thikrallah.Fragments.AthanFragment;

import com.alaaeltaweel.thikrallah.Fragments.MainFragment;

import com.alaaeltaweel.thikrallah.Fragments.ThikrFragment;

import com.alaaeltaweel.thikrallah.Fragments.TutorialFragment;

import com.alaaeltaweel.thikrallah.Notification.AthanTimerService;

import com.alaaeltaweel.thikrallah.Utilities.AppRater;

import com.alaaeltaweel.thikrallah.Utilities.CitiesCoordinatesDbOpenHelper;

import com.alaaeltaweel.thikrallah.Utilities.CustomLocation;

import com.alaaeltaweel.thikrallah.Utilities.MainInterface;

import com.alaaeltaweel.thikrallah.Utilities.MyDBHelper;

import com.alaaeltaweel.thikrallah.Utilities.MyListPreference;

import com.alaaeltaweel.thikrallah.quran.labs.androidquran.QuranDataActivity;

import com.google.android.gms.location.LocationListener;

import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.Priority;

import com.google.android.gms.tasks.CancellationTokenSource;



import java.io.File;

import java.io.FileInputStream;

import java.io.FileOutputStream;

import java.io.IOException;

import java.io.InputStream;

import java.io.OutputStream;

import java.lang.ref.WeakReference;

import java.text.NumberFormat;

import java.util.ArrayList;

import java.util.List;

import java.util.Locale;

import java.util.concurrent.Executor;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;



import timber.log.Timber;



public class MainActivity extends AppCompatActivity implements MainInterface, LocationListener, android.location.LocationListener,

        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 2334;

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO = 43424;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION_FOR_LOCATION_UPDATES = 5678;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7051;

    String TAG = "MainActivity";

    public static final String DATA_TYPE_NIGHT_THIKR = "night";

    public static final String DATA_TYPE_DAY_THIKR = "morning";

    public static final String DATA_TYPE_GENERAL_THIKR = "general";

    public static final String DATA_TYPE_QURAN_KAHF = "quran/0";

    public static final String DATA_TYPE_QURAN_MULK = "quran/1";

    public static final String DATA_TYPE_QURAN = "quran";

    public static final String DATA_TYPE_ATHAN = "athan";

    public static final String DATA_TYPE_ATHAN1 = "athan1";

    public static final String DATA_TYPE_ATHAN2 = "athan2";

    public static final String DATA_TYPE_ATHAN3 = "athan3";

    public static final String DATA_TYPE_ATHAN4 = "athan4";

    public static final String DATA_TYPE_ATHAN5 = "athan5";





    private static final Intent[] POWERMANAGER_INTENTS = {

            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),

            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),

            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),

            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),

            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),

            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),

            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),

            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),

            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),

            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),

            new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),

            new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),

            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))};

    SharedPreferences mPrefs;

    static final int RC_ENABLE_LOCATION_SETTINGS = 786;

    private Context mcontext;



    Messenger mServiceThikrMediaPlayerMessenger = null;

    boolean mIsBoundMediaService;

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private LocationManager locationManager;

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;



    private long endnow;

    private long startnow = 0;





    @Override

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {



    }





    class IncomingHandler extends Handler {

        @Override

        public void handleMessage(Message msg) {

            Log.d(TAG, "message recieved what=" + msg.what + "arg1=" + msg.arg1);

            switch (msg.what) {

                case ThikrMediaPlayerService.MSG_CURRENT_PLAYING:

                    Log.d(TAG, "position" + msg.arg1);

                    sendPositionToThikrFragment(msg.arg1, msg.getData());

                    break;

                case ThikrMediaPlayerService.MSG_UNBIND:

                    unbindtoMediaService();

                    break;

                default:

                    super.handleMessage(msg);

            }

        }

    }



    private void sendPositionToThikrFragment(int position, Bundle data) {

        String datatype = data.getString("com.alaaeltaweel.thikrallah.datatype", null);

        Log.d(TAG, "datatype=" + datatype);

        if (datatype == null) {

            return;

        }

        if (datatype.contains(DATA_TYPE_QURAN)) {

            Log.d(TAG, "quran");



        } else {

            ThikrFragment fragment = (ThikrFragment) this.getSupportFragmentManager().findFragmentByTag("ThikrFragment");

            if (fragment != null && fragment.isVisible()) {

                fragment.setCurrentlyPlaying(position);

            }

        }



    }



    private ServiceConnection mConnectionMediaServer = new ServiceConnection() {

        @Override

        public void onServiceConnected(ComponentName className, IBinder service) {

            mServiceThikrMediaPlayerMessenger = new Messenger(service);

            mIsBoundMediaService = true;

            Log.d(TAG, "connected. binded? mIsBoundMediaService set to true");

            try {

                Message msg = Message.obtain(null, ThikrMediaPlayerService.MSG_CURRENT_PLAYING);

                msg.replyTo = mMessenger;

                mServiceThikrMediaPlayerMessenger.send(msg);

                requestMediaServiceStatus();

                Log.d(TAG, "requested status");

            } catch (RemoteException e) {



            }

        }



        @Override

        public void onServiceDisconnected(ComponentName className) {

            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.

            unbindtoMediaService();

            mServiceThikrMediaPlayerMessenger = null;

            mIsBoundMediaService = false;

            Log.d(TAG, "Disconnected. unbided? mIsBoundMediaService set to false");

        }

    };





    @Override

    public void requestMediaServiceStatus() {

        if (mIsBoundMediaService) {

            if (mServiceThikrMediaPlayerMessenger != null) {

                try {

                    Log.d(TAG, "requestMediaServiceStatus called to request message status");

                    Message msg = Message.obtain(null, ThikrMediaPlayerService.MSG_CURRENT_PLAYING, 0, 0);

                    msg.replyTo = mMessenger;

                    mServiceThikrMediaPlayerMessenger.send(msg);

                } catch (RemoteException e) {

                }

            }

        } else {

            Log.d(TAG, "mIsBoundMediaService is false to send message");

        }

    }



    private void unbindtoMediaService() {

        // unbind to the service

        Log.d(TAG, "unbind called. mIsBoundMediaService =" + mIsBoundMediaService);

        if (mIsBoundMediaService == true) {

            unbindService(mConnectionMediaServer);

        }





        mIsBoundMediaService = false;



    }



    private void bindtoMediaService() {

        // Bind to the service

        if (!mIsBoundMediaService) {

            try {

                mIsBoundMediaService = bindService(new Intent(this, ThikrMediaPlayerService.class), mConnectionMediaServer,

                        Context.BIND_ABOVE_CLIENT);



            } catch (Exception e) {

                mIsBoundMediaService = false;

            }



        }

        Log.d(TAG, "bind called. mIsBoundMediaService" + mIsBoundMediaService);

    }





    @Override

    protected void onStart() {

        bindtoMediaService();

        super.onStart();





    }



    public void sendActionToMediaService(Bundle data) {

        Log.d(TAG, "sendActionToMediaService called");

        if (data != null) {

            Log.d(TAG, "data is not null");

            if (data.getString("com.alaaeltaweel.thikrallah.datatype", "").equalsIgnoreCase("")) {

                Log.d(TAG, "datatype was empty");

                data.putString("com.alaaeltaweel.thikrallah.datatype", this.getThikrType());

                Log.d(TAG, "datatype assigned to " + this.getThikrType());

            }



            data.putBoolean("isUserAction", true);

            Log.d(TAG, "service to start");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                this.startForegroundService(new Intent(this, ThikrMediaPlayerService.class).putExtras(data));

            } else {

                this.startService(new Intent(this, ThikrMediaPlayerService.class).putExtras(data));

            }



            bindtoMediaService();

            this.requestMediaServiceStatus();



        }



    }



    @Override

    protected void onStop() {

        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.

        Log.d(TAG, "onstop finished");

    }

    @Override

    public void requestOverLayPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,

                        Uri.parse("package:" + this.getPackageName()));

                showMessageAndLaunchIntent(intent, R.string.need_overlay_permission_title, R.string.need_overlay_permission_message);



            }

        }

    }

    @Override

    public void requestBatteryExclusion() {

        Log.d(TAG,"requestBatteryExclusion");

        PowerManager powerManager = (PowerManager) this.getSystemService(POWER_SERVICE);

        String packageName = "com.alaaeltaweel.thikrallah.alaa";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {



            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(this.getResources().getString(R.string.power_Exclusion)).setMessage(this.getResources().getString(R.string.power_Exclusion_message))

                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

                            @Override

                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent();

                                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);

                                intent.setData(Uri.parse("package:" + mcontext.getPackageName()));

                                startActivity(intent);

                            }

                        })

                        .setCancelable(false)

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override

                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })

                        .create().show();

            }

        }

    }

    @Override

    public void requestExactAlarmPermission(){

        Log.d(TAG,"requestExactAlarmPermission");

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);

        String packageName = "com.alaaeltaweel.thikrallah";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {



            if (!alarmManager.canScheduleExactAlarms()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(this.getResources().getString(R.string.exact_alarm_title)).setMessage(this.getResources().getString(R.string.exact_alarm_message))

                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

                            @Override

                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent();

                                intent.setAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);

                                intent.setData(Uri.parse("package:" + mcontext.getPackageName()));

                                startActivity(intent);

                            }

                        })

                        .setCancelable(false)

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override

                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })

                        .create().show();

            }

        }

    }

    @Override

    public void requestPermission(String perm) {

            int isGranted = ContextCompat.checkSelfPermission(this,

                    perm);

            if (isGranted != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,new String[]{perm}, 9999);

            }



    }



    @Override

    public void requestLocationUpdate() {

        if (mPrefs.getBoolean("isCustomLocation", false)) {

            return;

        }



        // طلب إذن الموقع

        int fineCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        int coarseCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);



        if (fineCheck != PackageManager.PERMISSION_GRANTED && coarseCheck != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                showMessageAndLaunchLocationPermission(R.string.need_location_permission_title, R.string.need_location_permission_message);

            } else {

                ActivityCompat.requestPermissions(this,

                        new String[]{

                                Manifest.permission.ACCESS_FINE_LOCATION,

                                Manifest.permission.ACCESS_COARSE_LOCATION

                        },

                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION_FOR_LOCATION_UPDATES);

            }

            return;

        }



        Log.d(TAG, "location permission granted - using FusedLocationProvider");



        // استخدام FusedLocationProviderClient الحديث

        FusedLocationProviderClient fusedClient = LocationServices.getFusedLocationProviderClient(this);



        // أولاً: جرب الموقع الأخير المحفوظ

        fusedClient.getLastLocation().addOnSuccessListener(this, location -> {

            if (location != null) {

                Log.d(TAG, "Got last location: " + location.getLatitude() + ", " + location.getLongitude());

                saveLocation(location);

            } else {

                Log.d(TAG, "Last location null - requesting current location");

                // لو مفيش موقع محفوظ، اطلب موقع جديد

                CancellationTokenSource cts = new CancellationTokenSource();

                fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.getToken())

                        .addOnSuccessListener(this, currentLocation -> {

                            if (currentLocation != null) {

                                Log.d(TAG, "Got current location: " + currentLocation.getLatitude());

                                saveLocation(currentLocation);

                            } else {

                                Log.d(TAG, "Current location also null");

                                if (PreferenceManager.getDefaultSharedPreferences(this)

                                        .getString("latitude", "0.0").equalsIgnoreCase("0.0")) {

                                    buildAlertMessageNoGps();

                                }

                            }

                        })

                        .addOnFailureListener(e -> {

                            Log.e(TAG, "Failed to get current location: " + e.getMessage());

                            if (PreferenceManager.getDefaultSharedPreferences(this)

                                    .getString("latitude", "0.0").equalsIgnoreCase("0.0")) {

                                buildAlertMessageNoGps();

                            }

                        });

            }

        }).addOnFailureListener(e -> {

            Log.e(TAG, "getLastLocation failed: " + e.getMessage());

            if (PreferenceManager.getDefaultSharedPreferences(this)

                    .getString("latitude", "0.0").equalsIgnoreCase("0.0")) {

                buildAlertMessageNoGps();

            }

        });

    }



    private void saveLocation(Location location) {

        NumberFormat nf = NumberFormat.getInstance(new Locale("en_US"));

        nf.setMaximumFractionDigits(4);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        editor.putString("latitude", nf.format(location.getLatitude()));

        editor.putString("longitude", nf.format(location.getLongitude()));

        editor.apply();

        Log.d(TAG, "Location saved: " + location.getLatitude() + ", " + location.getLongitude());

    }

    @Override

    public void requestLocationPermission(){

        if (mPrefs.getBoolean("isCustomLocation",false)){

            if (MainActivity.getLatitude(this).equalsIgnoreCase("0.0")

                    || MainActivity.getLongitude(this).equalsIgnoreCase("0.0") ){

                //custom location not set. Manually set

                Log.d(TAG,"LOCATION:0,0 location even though custom location is chosen");

                this.showMessageAndLaunchManualLocation();

                return;

            }else{

                //custom location already set. Do nothing

                return;

            }

        }else{

            int permissionCheck = ContextCompat.checkSelfPermission(this,

                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED ) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){

                    showMessageAndLaunchLocationPermission(R.string.need_location_permission_title, R.string.need_location_permission_message);

                }else{

                    if (MainActivity.getLatitude(this).equalsIgnoreCase("0.0")

                            || MainActivity.getLongitude(this).equalsIgnoreCase("0.0")){

                        ActivityCompat.requestPermissions(this,

                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},

                                MY_PERMISSIONS_REQUEST_ACCESS_LOCATION_FOR_LOCATION_UPDATES);

                        //can no longer ask for location permission. If latest location is zero, ask for user to set manual location

                        //otherwise, use last known location and don't bother asking for location permission anymore

                        //showMessageAndLaunchManualLocation();

                    }

                }

            }

        }



    }

    public static boolean isLocationEnabled(Context context) {

        int locationMode = 0;

        String locationProviders;



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            try {

                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);



            } catch (Settings.SettingNotFoundException e) {

                e.printStackTrace();

                return false;

            }



            return locationMode != Settings.Secure.LOCATION_MODE_OFF;



        } else {

            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            return !TextUtils.isEmpty(locationProviders);

        }





    }



    private void requestNormalPermissions() {

        List<String> listPermissionsNeeded = new ArrayList<>();

        /*

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,

                        Uri.parse("package:" + getPackageName()));

                showMessageAndLaunchIntent(intent, R.string.need_overlay_permission_title, R.string.need_overlay_permission_message);



            }

        }



         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            int foregroundservice_permission = ContextCompat.checkSelfPermission(this,

                    Manifest.permission.FOREGROUND_SERVICE);

            if (foregroundservice_permission != PackageManager.PERMISSION_GRANTED) {

                listPermissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE);

            }

        }

        /*

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            int postnotification_permission = ContextCompat.checkSelfPermission(this,

                    Manifest.permission.POST_NOTIFICATIONS);

            if (postnotification_permission != PackageManager.PERMISSION_GRANTED) {

                listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);

                Log.d(TAG,"POST_NOTIFICATIONS permission requested");

            }

        }

        */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            int alarmsPermission = ContextCompat.checkSelfPermission(this,

                    Manifest.permission.SCHEDULE_EXACT_ALARM);

            if (alarmsPermission != PackageManager.PERMISSION_GRANTED) {

                listPermissionsNeeded.add(Manifest.permission.SCHEDULE_EXACT_ALARM);

            }

        }

        /*

        int locationPermission = ContextCompat.checkSelfPermission(this,

                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {

            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        }*/

        if (!listPermissionsNeeded.isEmpty()) {

            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,

                        Uri.parse("package:" + getPackageName()));

                startActivityForResult(intent, 1);

            }

        }





    }



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        MainActivity.setLocale(this);

        super.onCreate(savedInstanceState);

        requestNormalPermissions();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        PreferenceManager.setDefaultValues(this.getApplicationContext(), R.xml.preferences, true);

        PreferenceManager.setDefaultValues(this.getApplicationContext(), R.xml.preferences_athan, true);

        PreferenceManager.setDefaultValues(this.getApplicationContext(), R.xml.preferences_general, true);

        mcontext = this.getApplicationContext();

        populateBuiltinDatabase();

        if (!mPrefs.getBoolean("isFirstLaunch", true) &&

                !mPrefs.getBoolean("isMigrated", false) &&

                ContextCompat.checkSelfPermission(this,

                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            //if it is not first launch and we have storage permission and we have not backed up

            FilesMigration migration = new FilesMigration();

            migration.execute(this.getApplicationContext());

        } else {

            mPrefs.edit().putBoolean("isMigrated", true).apply();

        }





        Intent intent1 = new Intent("com.alaaeltaweel.thikrallah.Notification.ThikrBootReceiver.android.action.broadcast");





        //new WhatsNewScreen(this).show();

        //below is not nice or organized but will refactor and improve code next time

        MyListPreference isDownload = new MyListPreference(this);

        isDownload.downloadFilesIfNeeded();

        // تم إلغاء تفعيل التقييم على المتجر
        // new AppRater().app_launched(new WeakReference<>(this));

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();

        }

        if (mPrefs.getBoolean("isFirstLaunch", true)) {

            sendBroadcast(intent1);

            mPrefs.edit().putLong("time_at_last_ad", System.currentTimeMillis()).apply();

            launchFragment(new TutorialFragment(), null, "TutorialFragment");

        }

        if (!mPrefs.getBoolean("isCitiesDatabaseCopied",false)){

            ExecutorService executor = Executors.newSingleThreadExecutor();

            Handler handler = new Handler(Looper.getMainLooper());



            executor.execute(() -> {

                CitiesCoordinatesDbOpenHelper citiesDb = CitiesCoordinatesDbOpenHelper.getInstance(this);

                if (citiesDb.checkDataBase()){

                    mPrefs.edit().putBoolean("isCitiesDatabaseCopied", true).apply();

                }



                handler.post(() -> {

                    //UI Thread work here

                });

            });



        }

        if (!mPrefs.getBoolean("protected", false)) {

            for (final Intent intent : POWERMANAGER_INTENTS)

                if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {



                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle(this.getResources().getString(R.string.autostart)).setMessage(this.getResources().getString(R.string.autostart_message))

                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

                                @Override

                                public void onClick(DialogInterface dialogInterface, int i) {

                                    try {

                                        startActivity(intent);

                                    } catch (Exception e) {

                                        e.printStackTrace();

                                        Log.d(TAG, "" + e.getMessage());

                                    } finally {

                                        mPrefs.edit().putBoolean("protected", true).apply();

                                    }





                                }

                            })

                            .setCancelable(false)

                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                                @Override

                                public void onClick(DialogInterface dialog, int which) {

                                }

                            })

                            .create().show();

                    break;

                }

        }





        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);

        String packageName = "com.alaaeltaweel.thikrallah";

        startAthanTimer(this.getApplicationContext());

        Intent intent = this.getIntent();

        boolean isNotification = intent.getBooleanExtra("FromNotification", false);

        if (isNotification == true) {

            Log.d(TAG, "from notification");

            if (intent.getExtras().getString("DataType") != null) {

                if (intent.getExtras().getString("DataType").equalsIgnoreCase(MainActivity.DATA_TYPE_DAY_THIKR) ||

                        intent.getExtras().getString("DataType").equalsIgnoreCase(MainActivity.DATA_TYPE_NIGHT_THIKR)) {

                    Log.d(TAG, "general thikr notification");

                    launchFragment(new ThikrFragment(), intent.getExtras(), "ThikrFragment");

                }

                if (intent.getExtras().getString("DataType").contains(MainActivity.DATA_TYPE_QURAN)) {

                    Log.d(TAG, "quran thikr notification");

                    Intent intent2 = new Intent();

                    intent2.setClass(this, QuranDataActivity.class);

                    intent2.putExtras(intent.getExtras());

                    startActivityForResult(intent2, 0);

                }

                if (intent.getExtras().getString("DataType").contains(MainActivity.DATA_TYPE_ATHAN)) {

                    Log.d(TAG, "athan thikr notification");

                    launchFragment(new AthanFragment(), new Bundle(), "AthanFragment");

                }

            }



        }

        boolean isFromSettings = intent.getBooleanExtra("FromPreferenceActivity", false);

        if (isFromSettings == true) {

            intent = new Intent();

            intent.setClass(MainActivity.this, PreferenceActivity.class);

            startActivityForResult(intent, 0);

        }

    }



    public static void startAthanTimer(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean isTimer = prefs.getBoolean("foreground_athan_timer", true);

        double latitude = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(context).getString("latitude", "0.0"));



        boolean isLocationDefined = latitude != 0.0;

        if (isTimer && isLocationDefined) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                context.startForegroundService(new Intent(context, AthanTimerService.class));

            } else {

                context.startService(new Intent(context, AthanTimerService.class));

            }

        }

    }



    private void showMessageAndLaunchLocationPermission(int title_resource, int message_resource) {

        AppCompatActivity activity = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getResources().getString(title_resource)).setMessage(this.getResources().getString(message_resource))

                .setPositiveButton(this.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialogInterface, int i) {

                        ActivityCompat.requestPermissions(activity,

                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},

                                MY_PERMISSIONS_REQUEST_ACCESS_LOCATION_FOR_LOCATION_UPDATES);





                    }

                })

                .setCancelable(false)

                .create().show();

    }



    private void showMessageAndLaunchManualLocation() {

        AppCompatActivity activity = this;

        CustomLocation Customlocation=new CustomLocation(this);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getResources().getString(R.string.choose_manual_location)).setMessage(this.getResources().getString(R.string.choose_manual_location_message))

                .setPositiveButton(this.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialogInterface, int i) {

                        Customlocation.show();





                    }

                })

                .setCancelable(false)

                .create().show();

    }



    public void showMessageAndLaunchIntent(Intent intent, int title_resource, int message_resource) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getResources().getString(title_resource)).setMessage(this.getResources().getString(message_resource))

                .setPositiveButton(this.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {

                            if (intent != null) {

                                startActivityForResult(intent, 9999);

                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                            Log.d(TAG, "" + e.getMessage());

                        }





                    }

                })

                .setCancelable(false)

                .create().show();

    }



    @Override

    public void requestMediaOrStoragePermission() {

        int reminderType=Integer.parseInt(mPrefs.getString("RemindmeThroughTheDayType", "1"));

        if (reminderType==1 || reminderType==2){//audio notification

            AppCompatActivity activity = this;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

                int permissionCheck = ContextCompat.checkSelfPermission(this,

                        Manifest.permission.READ_MEDIA_AUDIO);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle(this.getResources().getString(R.string.need_audio_media_permission_title)).setMessage(this.getResources().getString(R.string.need_audio_media_permission_message))

                            .setPositiveButton(this.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {

                                @Override

                                public void onClick(DialogInterface dialogInterface, int i) {

                                    try {

                                        ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_MEDIA_AUDIO}, MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO);



                                    } catch (Exception e) {



                                    }





                                }

                            })

                            .setCancelable(false)

                            .create().show();

                }

            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                int permissionCheck = ContextCompat.checkSelfPermission(this,

                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle(this.getResources().getString(R.string.need_audio_media_permission_title)).setMessage(this.getResources().getString(R.string.need_audio_media_permission_message))

                            .setPositiveButton(this.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {

                                @Override

                                public void onClick(DialogInterface dialogInterface, int i) {

                                    try {

                                        ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_MEDIA_AUDIO}, MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO);

                                        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                                    } catch (Exception e) {



                                    }





                                }

                            })

                            .setCancelable(false)

                            .create().show();



                }

            }

        }



    }



    private void timeOperation(String mytag, String operation) {

        endnow = SystemClock.elapsedRealtime();

        Timber.d("Execution time: " + (endnow - startnow) + " ms for " + operation);

        startnow = SystemClock.elapsedRealtime();

    }





    private void populateBuiltinDatabase() {

        MyDBHelper db = new MyDBHelper(this);

        db.getReadableDatabase();

        db.close();

        if (db.getAllBuiltinThikrs().size() == 0) {

            db.populateInitialThikr();

        }



    }



    @Override

    public boolean onCreateOptionsMenu(Menu menu) {



        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);

        return true;

    }



    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will

        // automatically handle clicks on the Home/Up button, so long

        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Intent intent = new Intent();

            intent.setClass(MainActivity.this, PreferenceActivity.class);

            startActivityForResult(intent, 0);

            return true;

        }

        if (id == R.id.menu_share) {

            share();

            //mCallback.shareToFacebook(DBHelper.getInstance(this.getActivity()).getHadithTextforPageCurlView(v.getHadithsIdList(), v.getmIndex()));

            return true;

        }

        return super.onOptionsItemSelected(item);

    }





    @Override

    public void launchFragment(Fragment iFragment, Bundle args, String mytag) {

        iFragment.setArguments(args);

        FragmentTransaction fragmentTransaction1 = this.getSupportFragmentManager().beginTransaction();

        fragmentTransaction1.replace(R.id.container, iFragment, mytag);

        fragmentTransaction1.addToBackStack(null);

        fragmentTransaction1.commit();



    }



    @Override

    public void share() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_TEXT, this.getResources().getText(R.string.share_text));

        startActivity(Intent.createChooser(shareIntent, this.getResources().getString(R.string.share)));

    }





    @Override

    public void onPause() {

        Log.d(TAG, "on pause started");



        stopLocationUpdates();

        unbindtoMediaService();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        prefs.unregisterOnSharedPreferenceChangeListener(this);

        Log.d(TAG, "on pause finished");

        super.onPause();

        Log.d(TAG, "on pause finished on parent");



    }



    public static String getLatitude(Context context) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPrefs.getBoolean("isCustomLocation", false)) {

            return sharedPrefs.getString("c_latitude", "0.0");

        } else {

            return sharedPrefs.getString("latitude", "0.0");



        }

    }



    public static String getLongitude(Context context) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPrefs.getBoolean("isCustomLocation", false)) {

            return sharedPrefs.getString("c_longitude", "0.0");

        } else {

            return sharedPrefs.getString("longitude", "0.0");



        }

    }

    public static String getCityCountryLocation(Context context) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPrefs.getBoolean("isCustomLocation", false)) {

            if (PreferenceManager.getDefaultSharedPreferences(context).getString("city", "").equalsIgnoreCase("")){

                //This section deals with upgrading prior versions that do not have city adn country saved

                String[] approxLocation = CitiesCoordinatesDbOpenHelper.getInstance(context).getClosestLocation();

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

                editor.putString("city", approxLocation[1]);

                editor.putString("country", approxLocation[0]);

                editor.commit();

            }

            return PreferenceManager.getDefaultSharedPreferences(context).getString("city", "")+", "+

                    PreferenceManager.getDefaultSharedPreferences(context).getString("country", "");

        } else {

            String[] approxLocation= CitiesCoordinatesDbOpenHelper.getInstance(context).getClosestLocation();

            return approxLocation[1]+", "+approxLocation[0];



        }

    }



    public static void setLocale(Context context) {

        if (androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString("language", null) != null) {

            Locale locale = new Locale(androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString("language", null));

            Locale.setDefault(locale);

            Configuration config = new Configuration();

            config.locale = locale;

            context.getResources().updateConfiguration(config,

                    context.getResources().getDisplayMetrics());

        }

    }



    @Override

    protected void onResume() {

        bindtoMediaService();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);



        prefListener = this;

        prefs.registerOnSharedPreferenceChangeListener(prefListener);

        MainActivity.setLocale(this);

        super.onResume();



    }



    @Override

    protected void onDestroy() {



        unbindtoMediaService();



        super.onDestroy();



    }



    @Override

    public void onBackPressed() {

        super.onBackPressed();

    }



    @Override

    public void playAll(String AssetFolder) {

        Bundle data = new Bundle();

        data.putInt("ACTION", ThikrMediaPlayerService.MEDIA_PLAYER_PLAYALL);

        data.putString("com.alaaeltaweel.thikrallah.datatype", AssetFolder);

        sendActionToMediaService(data);

    }



    @Override

    public void incrementCurrentPlaying(String AssetFolder, int i) {

        Bundle data = new Bundle();

        data.putInt("ACTION", ThikrMediaPlayerService.MEDIA_PLAYER_INNCREMENT);

        data.putInt("INCREMENT", i);

        data.putString("com.alaaeltaweel.thikrallah.datatype", AssetFolder);

        sendActionToMediaService(data);

    }



    @Override

    public void pausePlayer(String thikrtype) {

        Log.d(TAG, "pauseplayer called");

        Bundle data = new Bundle();

        data.putInt("ACTION", ThikrMediaPlayerService.MEDIA_PLAYER_PAUSE);

        data.putString("com.alaaeltaweel.thikrallah.datatype", thikrtype);

        sendActionToMediaService(data);



    }



    @Override

    public void play(String AssetFolder, int fileNumber) {

        Bundle data = new Bundle();

        data.putInt("ACTION", ThikrMediaPlayerService.MEDIA_PLAYER_PLAY);

        data.putInt("FILE", fileNumber);

        data.putString("com.alaaeltaweel.thikrallah.datatype", AssetFolder);

        sendActionToMediaService(data);



    }



    @Override

    public void play(String path) {

        Bundle data = new Bundle();

        data.putInt("ACTION", ThikrMediaPlayerService.MEDIA_PLAYER_PLAY);

        data.putInt("FILE", -1);

        data.putString("com.alaaeltaweel.thikrallah.datatype", DATA_TYPE_GENERAL_THIKR);

        data.putString("FILE_PATH", path);

        data.putString("URI", "null");

        sendActionToMediaService(data);



    }



    @Override

    public void play(Uri uri) {

        Bundle data = new Bundle();

        data.putInt("ACTION", ThikrMediaPlayerService.MEDIA_PLAYER_PLAY);

        data.putInt("FILE", -1);

        data.putString("com.alaaeltaweel.thikrallah.datatype", DATA_TYPE_GENERAL_THIKR);

        data.putString("FILE_PATH", "");

        data.putString("URI", uri.toString());

        sendActionToMediaService(data);



    }





    @Override

    public boolean isPlaying() {

        //hanihani

        Bundle data = new Bundle();

        data.putInt("ACTION", ThikrMediaPlayerService.MEDIA_PLAYER_ISPLAYING);

        sendActionToMediaService(data);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return sharedPrefs.getBoolean("ISPLAYING", false);



    }



    @Override

    public void resetPlayer(String thikrtype) {

        Bundle data = new Bundle();

        data.putInt("ACTION", ThikrMediaPlayerService.MEDIA_PLAYER_RESET);

        data.putString("com.alaaeltaweel.thikrallah.datatype", thikrtype);

        sendActionToMediaService(data);





    }



    @Override

    public void setCurrentPlaying(String AssetFolder, int currentPlaying) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        sharedPrefs.edit().putInt("currentPlaying", currentPlaying).apply();



    }



    @Override

    public int getCurrentPlaying() {



        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        return sharedPrefs.getInt("currentPlaying", 1);

    }



    @Override

    public void setThikrType(String thikrType) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        sharedPrefs.edit().putString("thikrType", thikrType).apply();



    }



    public String getThikrType() {

        return this.mPrefs.getString("thikrType", MainActivity.DATA_TYPE_DAY_THIKR);

    }



    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_ENABLE_LOCATION_SETTINGS) {

            Log.d(TAG, "requestLocationUpdate. Settings enabled");

            this.requestLocation
