package com.example.pablito.first_one;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.example.pablito.first_one.R.id.editText;





public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public final String URL = "http://192.168.0.185:5000/";
    //public static final int MAX_THREADS = 3;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidNetworking.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();

    public void onClickGraph(View view){
        final Intent intent = new Intent(this, graphView.class);
        startActivity(intent);
    }

    public void getSensorData(View view){
        // sie zobaczy
        EditText tekst=(EditText)findViewById( R.id.editText);
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
        EditText tekst = (EditText) findViewById( editText);
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
        EditText tekst=(EditText)findViewById( editText);
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

}
