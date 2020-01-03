package com.example.pablito.first_one;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;
import cz.msebera.android.httpclient.entity.StringEntity;

public class graphView extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private double graph2LastXValue = 5d;
    public static final double[] getTempVal;

    static {
        getTempVal = new double[]{0.0};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_graph_view );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        putDataToChart();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
                        .setAction( "Action", null ).show();
            }
        } );*/
    }

    public void putDataToChart() {
        GraphView graph = (GraphView) findViewById( R.id.graph );


        mSeries1 = new LineGraphSeries<>( generateData() );
        graph.addSeries( mSeries1 );

        mSeries2 = new LineGraphSeries<>();
        graph.addSeries( mSeries2 );
        graph.getViewport().setXAxisBoundsManual( true );
        graph.getViewport().setMinX( 0 );
        graph.getViewport().setMaxX( 40 );

        mSeries1.setTitle( "Temperatura w przeciągu ostatnich 30 sekund" );


    }
    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(MainActivity.ARG_SECTION_NUMBER));
    }*/

    @Override
    public void onResume() {
        super.onResume();
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                mSeries1.resetData( generateData() );
                mHandler.postDelayed( this, 300 );
            }
        };
        mHandler.postDelayed( mTimer1, 300 );

        mTimer2 = new Runnable() {
            @Override
            public void run() {
                graph2LastXValue += 1d;
                mSeries2.appendData( new DataPoint( graph2LastXValue, getTemp() ), true, 40 );

                mHandler.postDelayed( this, 200 );
            }
        };
        mHandler.postDelayed( mTimer2, 1000 );
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks( mTimer1 );
        mHandler.removeCallbacks( mTimer2 );
        super.onPause();
    }

    private DataPoint[] generateData() {
        int count = 5;
        DataPoint[] values = new DataPoint[count];
        for (int i = 0; i < count; i++) {
            double x = i;
            double f = mRand.nextDouble() * 0.15 + 0.3;
            double y = Math.sin( i * f + 2 ) + mRand.nextDouble() * 0.3;
            DataPoint v = new DataPoint( x, y );
            values[i] = v;
        }
        return values;
    }

    //double mLastRandom = 2;
    Random mRand = new Random();

    public double getTemp() {
        return getTempData();
    }

    public static double getTempData() {
        String URL = MainActivity.URL + "/main/temp";
        final String TAGG = "taki o";
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("id", TAGG);
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

        final RequestHandle requestHandle = client.get( URL, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                super.onSuccess( statusCode, headers, json );
                Log.d("Temp value is: ", json.toString());
                try {
                    String result = json.getString( "temp" );
                    Log.d("Temp value is: ", result);
                    getTempVal[0] = Double.parseDouble( result );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray json) {
                super.onSuccess( statusCode, headers, json );
                Log.d("Temp value is: ", json.toString());
                try {
                    double[] getTempVal = {0.0};
                    JSONObject js = json.getJSONObject( 0 );
                    String nombre = js.getString( "temp" );
                    getTempVal[0] = Double.parseDouble( nombre );
                    Log.d( TAGG, nombre );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

                @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable e, JSONObject error) {
                super.onFailure( statusCode, headers, e, error );
                Log.i( TAGG, "Failed: " + error );

                if (e.getCause() instanceof ConnectTimeoutException) {
                    Log.d( TAGG, "Connection timeout !" + error );
                }
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String content, Throwable error) {
                Log.i( TAGG, "Failed: " + error );
                if ( error.getCause() instanceof ConnectTimeoutException ) {
                    System.out.println("Connection timeout !" + content);
                }
            }

        } );
        requestHandle.setTag( getTempVal );
        return getTempVal[0];
    }
}
