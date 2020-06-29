package com.example.maptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MarkerActivity extends AppCompatActivity {
    TextView markerInfo;

    private ImageButton qrscan;

    FirebaseDatabase database;
    DatabaseReference myRef;

    WifiManager wifiManager;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        markerInfo = findViewById(R.id.id_marker_info);
        String s = getIntent().getStringExtra("marker");
        markerInfo.setText(s);

        qrscan = findViewById (R.id.qrscan);

        qrscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarkerActivity.this, QrReadActivity.class);
                startActivityForResult(intent, 0);
            }
        });



        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 0){
            String s = data.getStringExtra("result");
            markerInfo.setText("Оплата пройдена");





        }



    }
}

