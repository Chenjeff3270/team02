package com.example.googlemaptext5;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, LocationListener, LocationSource {
    private final int REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATIN = 100;

    private LocationManager mLocationMgr;
    private LocationSource.OnLocationChangedListener mLocationChangedListener;
    private GoogleMap mMap;
    float zoom;
    Spinner spnGeoPoint, spnMapType;
    String[] Names = new String[]{"桃園市中壢區新中北路", "桃園市楊梅區中山南路",
            "桃園市桃園區國豐一街", "桃園市桃園區中正三民路口", "桃園市龜山區小巨蛋", "桃園市八德區崁頂路",
            "新北市板橋區城林大橋", "新北市中和區環河西路三段", "新北市樹林區中正路",
            "台北市士林區社子堤防社子棒球場"
    };
    String[] MapTypes = new String[]{"一般地圖", "混合地圖", "地形圖"};
    LatLng[] aryLatLng = new LatLng[]{

            new LatLng(24.957081489255778, 121.24783660002166),
            new LatLng(24.907286438019334, 121.13811146776355),
            new LatLng(24.990787902998154, 121.28127739597824),
            new LatLng(24.999538700091666, 121.30795747775588),
            new LatLng(24.994719781614524, 121.32473202433228),
            new LatLng(24.94473956401899, 121.2651683396542),
            new LatLng(24.978228913609207, 121.43227653288311),
            new LatLng(25.01094, 121.48923),
            new LatLng(25.01104, 121.41378),
            new LatLng(25.093453, 121.506934)

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        spnGeoPoint = (Spinner) findViewById(R.id.spnGeoPoint);
        spnMapType = (Spinner) findViewById(R.id.spnManType);


        // 建立ArrayAdapter
        ArrayAdapter<String> adapterspnGeoPoint = new
                ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, Names);
        ArrayAdapter<String> adapterspnMapType = new
                ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, MapTypes);

        //設定Spinner顯示的格式
        adapterspnGeoPoint.setDropDownViewResource(
                android.R.layout.simple_dropdown_item_1line);
        adapterspnMapType.setDropDownViewResource(
                android.R.layout.simple_dropdown_item_1line);

        //設定Spinner 的資料來源
        spnGeoPoint.setAdapter(adapterspnGeoPoint);
        spnMapType.setAdapter(adapterspnMapType);

        //設定spnPoint 元件 ItemSelecTed 事件的listener
        spnGeoPoint.setOnItemSelectedListener(spnGeoPointListener);
        spnMapType.setOnItemSelectedListener(spnManTypeListener);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buildViews();

    }

    private void buildViews(){
        final Button btok;
        btok = (Button)findViewById(R.id.BT1);
        btok.setOnClickListener(btLintener);
    }

    private final View.OnClickListener btLintener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MapsActivity.this, LogSuccAct.class);
            startActivity(intent);

        }
    };



    protected void onStart() {
        super.onStart();
        if (mMap != null)
            checkLocationPermissonAndEnableIt(true);
    }

    protected void onStop() {
        super.onStop();

        checkLocationPermissonAndEnableIt(false);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATIN) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                checkLocationPermissonAndEnableIt(true);
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);          //一般地圖
        mMap.setLocationSource(this);
        mMap.getUiSettings().setCompassEnabled(true);       //顯示指南針
        mMap.getUiSettings().setZoomControlsEnabled(true);   //顯示縮放圖示
        zoom = 17;     //設定放大倍率1(地球)-21(街景)
        LatLng sydney = new LatLng(24.957081489255778, 121.24783660002166);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title(""));

        LatLng sydney2 = new LatLng(24.907286438019334, 121.13811146776355);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney2)
                .title(""));
        LatLng sydney3 = new LatLng(24.990787902998154, 121.28127739597824);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney3)
                .title(""));
        LatLng sydney4 = new LatLng(24.999538700091666, 121.30795747775588);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney4)
                .title(""));
        LatLng sydney5 = new LatLng(24.994719781614524, 121.32473202433228);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney5)
                .title(""));
        LatLng sydney6 = new LatLng(24.94473956401899, 121.2651683396542);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney6)
                .title(""));
        LatLng sydney7 = new LatLng(24.978228913609207, 121.43227653288311);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney7)
                .title(""));
        LatLng sydney8 = new LatLng(25.01094,121.48923);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney8)
                .title(""));
        LatLng sydney9 = new LatLng(25.01104,121.41378);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney9)
                .title(""));
        LatLng sydney10 = new LatLng(25.093453,121.506934);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney10)
                .title(""));
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Location location = mLocationMgr.getLastKnownLocation((
                    LocationManager.GPS_PROVIDER));
            mMap.setMyLocationEnabled(true);

            if (location == null)
                location = mLocationMgr.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                Toast.makeText(MapsActivity.this, "成功取得上一次定位",
                        Toast.LENGTH_LONG).show();
                onLocationChanged(location);
            } else
                Toast.makeText(MapsActivity.this, "沒有上一次的定位資料",
                        Toast.LENGTH_LONG).show();
        }
    }





    public void onLocationChanged(Location location){

        if(mLocationChangedListener != null);
        mLocationChangedListener.onLocationChanged(location);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(
                new LatLng(location.getLatitude(), location.getLongitude())));


    }

    public void onStatusChanged(String s, int i, Bundle bundle) {
        String str = s;
        switch (i) {
            case LocationProvider.OUT_OF_SERVICE:
                str += "定位功能無法使用";
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                str += "暫時無法定位";
                break;
        }
        Toast.makeText(MapsActivity.this, str, Toast.LENGTH_LONG)
                .show();
    }

    public void onProviderEnabled(String s) {
        Toast.makeText(MapsActivity.this, s + "定位功能開啟",
                Toast.LENGTH_LONG).show();
        checkLocationPermissonAndEnableIt(true);
    } public void onProviderDisabled(String s) {
        Toast.makeText(MapsActivity.this, s + "定位功能已經被關閉",
                Toast.LENGTH_LONG).show();
        checkLocationPermissonAndEnableIt(false);
    }
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener){
        mLocationChangedListener = onLocationChangedListener;
        checkLocationPermissonAndEnableIt(true);
        Toast.makeText(MapsActivity.this, "地圖的my_location layer已經啟用",
                Toast.LENGTH_LONG).show();
    }
    public void deactivate() {
        mLocationChangedListener = null;
        checkLocationPermissonAndEnableIt(false);
        Toast.makeText(MapsActivity.this, "地圖的my_location layer已經關閉",
                Toast.LENGTH_LONG).show();
    }

    private void checkLocationPermissonAndEnableIt (boolean on) {
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    MapsActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder altDlgBuilder =
                        new AlertDialog.Builder(MapsActivity.this);
                altDlgBuilder.setTitle("提示");
                altDlgBuilder.setMessage("App需要啟動定位功能。");
                altDlgBuilder.setIcon(android.R.drawable.ic_dialog_info);
                altDlgBuilder.setCancelable(false);
                altDlgBuilder.setPositiveButton("確定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface
                                                        dialogInterface, int i) {

                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{
                                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATIN);
                            }
                        });
                altDlgBuilder.show();

                return;
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATIN);

                return;

            }

        }

        if(on) {
            if(mLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mLocationMgr.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,5000,5,this);
                Toast.makeText(MapsActivity.this, "使用GPS定位",
                        Toast.LENGTH_LONG)
                        .show();
            } else
            if(mLocationMgr.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER));{
                mLocationMgr.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 5000,5, this);
                Toast.makeText(MapsActivity.this, "使用網路定位",
                        Toast.LENGTH_LONG)
                        .show();
            }

        }
    }

    private Spinner.OnItemSelectedListener spnGeoPointListener =
            new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id)
                {
                    //建立觀看地點的位置
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(aryLatLng[position])   //地圖的中心點
                            .zoom(zoom)
                            .tilt(0)                       //俯視的角度
                            .build();                      //建立 CamerPosition 物件
                    mMap.animateCamera(CameraUpdateFactory.
                            newCameraPosition(cameraPosition));
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //TODO AUTO-generated method stub
                }
            };
    private Spinner.OnItemSelectedListener spnManTypeListener=
            new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    String sel=parent.getSelectedItem().toString();
                    switch (sel){
                        case "一般地圖":
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                        case "混合地圖":
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            break;
                        case "地形圖":
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };

}