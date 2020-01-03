package com.example.pablito.first_one;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;

public class Settings extends AppCompatActivity {

    public static String PHONE_NUMBER;
    public static String URL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // int REQUEST_READ_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );


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

    public void onClickConfirm(View view){
        final Intent intent = new Intent(this, MainActivity.class);

        try {
            TelephonyManager tm = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE );
            EditText numberEdit = findViewById( R.id.editText6 );
            Toast.makeText( this, tm.getLine1Number(), Toast.LENGTH_LONG ).show();
            if(numberEdit.getText().toString().matches("")) {
                numberEdit.setText( tm.getLine1Number() );
                PHONE_NUMBER = tm.getLine1Number();
            } else {
                PHONE_NUMBER = numberEdit.getText().toString();
            }
        } catch (SecurityException e) {
            Toast.makeText( this, "An exception security related has occured", Toast.LENGTH_LONG );
        }
        EditText ipEdit = findViewById(R.id.editText5);
        URL = "https://" + String.valueOf( ipEdit.getText() ) + ":5000";
        intent.putExtra("URL", URL);
        intent.putExtra("PHONE_NUMBER", PHONE_NUMBER);
        startActivity( intent );
        this.finish();
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
