package com.example.pablito.first_one;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;

public class Settings extends AppCompatActivity {

    public static String PHONE_NUMBER;
    public static String URL;
    SharedPreferences prefs;
    TelephonyManager tm;
    EditText numberEdit;
    EditText ipEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // int REQUEST_READ_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );
        prefs = getApplicationContext().getSharedPreferences( "Prefs", Context.MODE_PRIVATE);
        numberEdit = findViewById( R.id.editText6 );
        ipEdit = findViewById( R.id.editText5);
        if(prefs.contains( "URL" ) && prefs.contains( "PHONE_NUMBER" ))
        {
            ipEdit.setText( prefs.getString("URL", null) );
            numberEdit.setText( prefs.getString("PHONE_NUMBER", null) );
        }
        //
//  FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
//        fab.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
//                        .setAction( "Action", null ).show();
//            }
//        } );
    }

    @Override
    protected void onResume() {
        super.onResume();
        numberEdit = findViewById( R.id.editText6 );
        ipEdit = findViewById( R.id.editText5);
        if(prefs.contains( "URL" ) && prefs.contains( "PHONE_NUMBER" ))
        {
            ipEdit.setText( prefs.getString("URL", null) );
            numberEdit.setText( prefs.getString("PHONE_NUMBER", null) );
        }
    }

    public void onClickConfirm(View view){
        final Intent intent = new Intent(this, MainActivity.class);
        try {
            tm = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE );
            numberEdit = findViewById( R.id.editText6 );
            ipEdit = findViewById( R.id.editText5);
            SharedPreferences.Editor editPrefs = prefs.edit();
            Toast.makeText( this, tm.getLine1Number(), Toast.LENGTH_LONG ).show();
            if(numberEdit.getText().toString().matches("\\+ 00 000 000 000")) {
                PHONE_NUMBER = tm.getLine1Number();
            } else {
                PHONE_NUMBER = numberEdit.getText().toString();
            }

            URL = String.valueOf( ipEdit.getText() );
            editPrefs.putString( "URL",  URL);
            editPrefs.putString( "PHONE_NUMBER",  PHONE_NUMBER);
            editPrefs.commit();
            intent.putExtra("URL", URL);
            intent.putExtra("PHONE_NUMBER", PHONE_NUMBER);
        } catch (SecurityException e) {
            Toast.makeText( this, "An exception security-related has occured", Toast.LENGTH_LONG ).show();
            Log.d("SecurityException", "Failed: " + e.getMessage());
        }
        this.finish();
        startActivity( intent );
    }

    public void showInfoConnection(View view){
        AlertDialog.Builder infoBuilder = new AlertDialog.Builder( this );
        infoBuilder.setMessage(R.string.info_connection)
                .setTitle("Info connection")
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                } ).create().show();

    }

    public void showInfoDevice(View view){
        AlertDialog.Builder infoBuilder = new AlertDialog.Builder( this );
        infoBuilder.setMessage(R.string.info_device)
                .setTitle("Info device")
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                } ).create().show();

    }

}
