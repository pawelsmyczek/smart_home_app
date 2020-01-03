package com.example.pablito.first_one;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.telephony.TelephonyManager;

import com.androidnetworking.AndroidNetworking;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.content.Intent.ACTION_GET_CONTENT;
import static android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;
import static com.example.pablito.first_one.R.id.editText;


public class MainActivity extends AppCompatActivity {
    //TODO change saving the switches state by using putExtra method
    //TODO and maybe using runnable in onCreate method
    public static final String  EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static String        URL;
    public static String        PHONE_NUMBER;
    public static String        alarmState;
    public static double        temperature;
    private final int           REQUEST_READ_PHONE_STATE = 1;
    private final int           REQUEST_CALL_PHONE = 1;
    private final Handler       handler = new Handler();
    private Runnable            get_temp;
    private Intent              makeCall;
    private Switch              switchAlarm;
    private Switch              switchFire;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    //public static final int MAX_THREADS = 3;

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidNetworking.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        alarmState = getSwitch();
        switchAlarm = findViewById( R.id.switch1 );
        switchFire = findViewById(R.id.switch2);
        Intent intent = getIntent();
        URL = intent.getStringExtra("URL");
        PHONE_NUMBER = intent.getStringExtra( "PHONE_NUMBER" );


        if(alarmState == "Is on")
            switchAlarm.setChecked(true);
        if(alarmState == "Is off"){
            switchAlarm.setChecked( false );
        }
        Toast.makeText( this, alarmState, Toast.LENGTH_SHORT ).show();
        switchAlarm.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(alarmState == "Is on") switchAlarm.setChecked( true );
                if(alarmState == "Is off") switchAlarm.setChecked( false );
                if(isChecked) postDataSwitch();
                else postDataSwitch();
            }
        } );

        switchFire.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) postFireSwitch();
                else postFireSwitch();
            }
        } );
        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
    }

    @Override
    public void onResume(){
        super.onResume();

        get_temp = new Runnable() {
            @Override
            public void run() {
                if(temperature > 21)
                {
                    try{
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL_PHONE);
                        makeCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + PHONE_NUMBER));
                        startActivity( makeCall );
                    } catch(NullPointerException e){
                        Toast.makeText(MainActivity.this, "No phone number has been provided, please enter Settings once again. ", Toast.LENGTH_LONG);
                    } catch(Exception e) {
                        Toast.makeText(MainActivity.this, "Something strange happened. Error Message: " + e.getMessage(), Toast.LENGTH_LONG);
                    }
                    handler.removeCallbacksAndMessages( this );
                } else{
                    handler.postDelayed( this, 1000 );
                    temperature = graphView.getTempData();
                }
            }

        };
        handler.postDelayed( get_temp, 1000 );
    }

    @Override
    public void onPause(){
        super.onPause();
        if(alarmState == "Is off") switchAlarm.setChecked( false );
        if(alarmState == "Is on") switchAlarm.setChecked( true );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.settings_menu, menu );
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO on request permissions handler
                    Toast.makeText( this, "Permission Granted" , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    finishActivity( FLAG_ACTIVITY_PREVIOUS_IS_TOP );
                    Toast.makeText( this, "Permission Denied" , Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                if (ActivityCompat.checkSelfPermission( this, Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission( this, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.READ_PHONE_STATE )
                            && ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.CALL_PHONE ) )
                    {
                        new AlertDialog.Builder(this)
                                .setTitle("Permission needed")
                                .setMessage("Permission is needed to call when Your house is on fire")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},REQUEST_READ_PHONE_STATE);
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL_PHONE);
                                    }
                                })
                                .setNegativeButton( "cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                } )
                                .create().show();
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},REQUEST_READ_PHONE_STATE);
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL_PHONE);
                    }
                }else {
                    final Intent intent = new Intent( this, Settings.class );
                    startActivity( intent );
                }
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState( savedInstanceState );
        String stateSaved = savedInstanceState.getString( "alarmState" );

        if(stateSaved == null){
            Toast.makeText( this,"onRestoreInstanceState: no State saved", Toast.LENGTH_LONG).show();
        }
        else {
            if(stateSaved == "Is off") switchAlarm.setChecked( false );
            if(stateSaved == "Is on") switchAlarm.setChecked( true );
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (alarmState == "Is on") switchAlarm.setChecked(true);
        if (alarmState == "Is off") switchAlarm.setChecked( false );

    }

    public void onClickGraph(View view){
        final Intent intent = new Intent(this, graphView.class);
        startActivity(intent);
    }

    public void getSensorData(View view){
        // sie zobaczy
        EditText tekst= findViewById( R.id.editText);
        String tekscik=tekst.getText().toString();
        final Intent intent = new Intent(this, DisplayMessageActivity.class);

        final int DEFAULT_TIMEOUT = 15 * 1000;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);

        final String TAG = "Gitara";
        final String TAGG = "Se nie dzia, bo:";
        RequestParams params = new RequestParams();
        params.put("q", "android");
        final RequestHandle requestHandle = client.get(URL, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                super.onSuccess(statusCode, headers, json);
                String result = null;
                try {
                    result = json.getString("status");
                    intent.putExtra(EXTRA_MESSAGE, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, result);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                startActivity(intent);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray json) {
                super.onSuccess(statusCode, headers, json);
                Log.d(TAG, json.toString());

                Toast.makeText(MainActivity.this, json.toString(), Toast.LENGTH_LONG).show();
                intent.putExtra(EXTRA_MESSAGE, json.toString());
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable e, JSONObject error) {
                super.onFailure(statusCode, headers, e, error);
                Log.i(TAGG, "Failed: " + error);

                if ( e.getCause() instanceof ConnectTimeoutException ) {
                    Log.d(TAGG,  "Connection timeout !" + error );
                }
            }
            /*@Override
            public void onFailure(Throwable error, String content) {
                if ( error.getCause() instanceof ConnectTimeoutException ) {
                    System.out.println("Connection timeout !" + content);
                }
            }*/

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public void postDataToSensor(View view) {
        EditText tekst = findViewById( editText);
        String tekscik = tekst.getText().toString();
        final Intent intent = new Intent(this, DisplayMessageActivity.class);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("id", tekscik);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity encja = null;
        try {
            encja = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        final String TAG = "Poszło";
        final String TAGZ = "Nie poszło";
        final RequestHandle requestHandle = client.post(MainActivity.this,URL+"/main/sensors", encja, "application/json", new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject json) {
                Log.d(TAG, json.toString());
                Toast.makeText(MainActivity.this, json.toString(), Toast.LENGTH_LONG).show();
                intent.putExtra(EXTRA_MESSAGE, json.toString());
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String result, Throwable e) {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAlarm(View view){
        // sie zobaczy
        EditText tekst= findViewById( editText);
        String tekscik=tekst.getText().toString();
        final Intent intent = new Intent(this, DisplayMessageActivity.class);

        final int DEFAULT_TIMEOUT = 15 * 1000;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);

        final String TAG = "Gitara";
        final String TAGG = "Se nie dzia, bo:";
        RequestParams params = new RequestParams();
        params.put("q", "android");
        final RequestHandle requestHandle = client.get(URL+"/main/sensors", new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                super.onSuccess(statusCode, headers, json);
                String result = null;
                try {
                    result = json.getString("status");
                    intent.putExtra(EXTRA_MESSAGE, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, result);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                startActivity(intent);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray json) {
                super.onSuccess(statusCode, headers, json);
                Log.d(TAG, json.toString());

                Toast.makeText(MainActivity.this, json.toString(), Toast.LENGTH_LONG).show();
                intent.putExtra(EXTRA_MESSAGE, json.toString());
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable e, JSONObject error) {
                super.onFailure(statusCode, headers, e, error);
                Log.i(TAGG, "Failed: " + error);

                if ( e.getCause() instanceof ConnectTimeoutException ) {
                    Log.d(TAGG,  "Connection timeout !" + error );
                }
            }
            /*@Override
            public void onFailure(Throwable error, String content) {
                if ( error.getCause() instanceof ConnectTimeoutException ) {
                    System.out.println("Connection timeout !" + content);
                }
            }*/
            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    //
    //
    //
    // GET POST METHODS TO GET POST INFO FROM ALARM SENSOR
    //
    //
    //

    public String postDataSwitch() {

        final Intent intent = new Intent(this, MainActivity.class);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("id", "ojciec");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity encja = null;
        try {
            encja = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        final String TAG = "Poszło";
        final String TAGZ = "Nie poszło";

        final RequestHandle requestHandle = client.post(MainActivity.this, URL+"/main/sensors", encja,"application/json", new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray json) {
                try {
                    JSONObject js = json.getJSONObject( 0 );
                    alarmState = js.toString( Integer.parseInt( "alarms" ) );
                    //switcher.isChecked();
                    //} else switcher.setChecked(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, alarmState);
                Toast.makeText(MainActivity.this, json.toString(), Toast.LENGTH_LONG).show();
                intent.putExtra(EXTRA_MESSAGE, json.toString());
                //startActivity(intent);

            }
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject json) {
                try {
                    JSONObject js = json.getJSONObject( "alarms" );
                    alarmState = js.toString();
                    //switcher.isChecked();
                    //} else switcher.setChecked(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, alarmState);
                Toast.makeText(MainActivity.this, json.toString(), Toast.LENGTH_LONG).show();
                intent.putExtra(EXTRA_MESSAGE, json.toString());
                //startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String result, Throwable e) {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
        requestHandle.setTag( this. alarmState);
        return alarmState;
    }

    public String getSwitch(){
        // sie zobaczy
        EditText tekst= findViewById( editText);
        String tekscik=tekst.getText().toString();
        final Intent intent = new Intent(this, DisplayMessageActivity.class);

        final int DEFAULT_TIMEOUT = 1000;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);

        final String TAG = "Gitara";
        final String TAGG = "Se nie dzia, bo:";
        RequestParams params = new RequestParams();
        params.put("q", "android");
        final RequestHandle requestHandle = client.get(URL+"/main/sensors", new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                super.onSuccess(statusCode, headers, json);

                try {
                    alarmState = json.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //intent.putExtra(EXTRA_MESSAGE, alarmState);
                Log.d(TAG, alarmState);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray json) {
                super.onSuccess(statusCode, headers, json);
                JSONObject js = null;
                try {
                    js = json.getJSONObject( 0 );

                    alarmState = js.getString( "status" );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, alarmState);
                //Toast.makeText(MainActivity.this, alarmState, Toast.LENGTH_LONG).show();
                //intent.putExtra(EXTRA_MESSAGE, alarmState);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable e, JSONObject error) {
                super.onFailure(statusCode, headers, e, error);
                Log.i(TAGG, "Failed: " + error);

                if ( e.getCause() instanceof ConnectTimeoutException ) {
                    Log.d(TAGG,  "Connection timeout !" + error );
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
        requestHandle.setTag(this.alarmState);
        return alarmState;
    }

    //
    //
    //
    // GET POST METHODS TO GET POST INFO FROM FIRE SENSOR
    //
    //
    //

    public String postFireSwitch() {

        final Intent intent = new Intent(this, MainActivity.class);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("id", "ojciec");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity encja = null;
        try {
            encja = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        final String TAG = "Poszło";
        final String TAGZ = "Nie poszło";

        final RequestHandle requestHandle = client.post(MainActivity.this, URL+"/main/fire", encja,"application/json", new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray json) {
                try {
                    JSONObject js = json.getJSONObject( 1 );
                    alarmState = js.toString( Integer.parseInt( "fire_sensor" ) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, alarmState);
                Toast.makeText(MainActivity.this, json.toString(), Toast.LENGTH_LONG).show();
                intent.putExtra(EXTRA_MESSAGE, json.toString());

            }
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject json) {
                try {
                    JSONObject js = json.getJSONObject( "fire_sensor" );
                    alarmState = js.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, alarmState);
                Toast.makeText(MainActivity.this, json.toString(), Toast.LENGTH_LONG).show();
                intent.putExtra(EXTRA_MESSAGE, json.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String result, Throwable e) {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
        requestHandle.setTag( this. alarmState);
        return alarmState;
    }
}
