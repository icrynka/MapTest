package com.example.maptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrReadActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_read);
    }


    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent();
        intent.putExtra("result", result.getText());
        setResult(RESULT_OK, intent);
        finish();

    }


    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();

    }
    @Override
    protected void onResume() {
        super.onResume();
        scan();
    }

    public void scan() {
        scannerView = new ZXingScannerView(getApplicationContext());
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
