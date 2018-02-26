package com.startup.driveschoolclient;

import android.*;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.startup.driveschoolclient.util.Config;
import com.startup.driveschoolclient.util.ServerConnection;

import java.io.IOException;

public class QRScannerActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;

    SurfaceView surfaceView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            if(checkPermission()){
                Toast.makeText(QRScannerActivity.this,"permission granted",Toast.LENGTH_SHORT).show();

            }
            else{
                requestPermission();
            }
        }

        surfaceView = findViewById(R.id.cameraView);
        surfaceView.setZOrderMediaOverlay(true);
        holder = surfaceView.getHolder();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        if(!barcodeDetector.isOperational()){
            Log.e("Error","reader not functional");
        }

        cameraSource = new CameraSource.Builder(this,barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920,1024)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if(checkPermission()){
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        Log.e("Error",e.getMessage());
                    }
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size()>0){
                    String piKey = barcodes.valueAt(0).displayValue;
                    String url = Config.baseUrl+"sessions/start-session";
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.GET,
                            url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Error",error.getMessage());
                                }
                            }
                    );

                    ServerConnection.sendMessage(stringRequest);


                }
            }
        });

    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(QRScannerActivity.this, android.Manifest.permission.CAMERA))== PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(QRScannerActivity.this,new String[]{android.Manifest.permission.CAMERA},REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CAMERA:
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        Toast.makeText(QRScannerActivity.this,"Permission granted",Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(QRScannerActivity.this,"Permission denied",Toast.LENGTH_SHORT).show();
                        Log.e("Permission","Denied 111");
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                            if(shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
                                displayAlert("Give permission",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{android.Manifest.permission.CAMERA},REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;

        }
    }

    void displayAlert(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(QRScannerActivity.this).setMessage(message)
                .setPositiveButton("Ok",listener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }
}
