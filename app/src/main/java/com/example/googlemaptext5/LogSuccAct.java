package com.example.googlemaptext5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Criteria;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;

import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.googlemaptext5.R;
import com.example.googlemaptext5.TextString;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogSuccAct extends AppCompatActivity {
    private final int REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION = 100;
    private Button btnStart;
    private TextView textView;
    private Location mLocation;
    private LocationManager mLocationManager;

    Context context = this;
    Button b1,b2;
    TextView t1,t2;
    ListView LV;
    int last1=0;
    String select1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logsucc);
        buildViews();

        b1=(Button)findViewById(R.id.button);
        b2=(Button)findViewById(R.id.button1);
        t1=(TextView)findViewById(R.id.editText);
        t2=(TextView)findViewById(R.id.TextView);
        LV=(ListView)findViewById(R.id.LV);
        textView = (TextView) findViewById(R.id.TextView);
        btnStart = (Button) findViewById(R.id.button3);
        btnStart.setOnClickListener(btnClickListener); //開始定位\
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("data_text");

        final DatabaseReference myRef2 = myRef.child("data01");

        firebase_select(myRef2);

        //新增
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                last1 +=1;
                myRef2.child(String.valueOf(last1)).setValue(new TextString(t1.getText().toString(),
                        t2.getText().toString()));
                firebase_select(myRef2);
            }
        });
        //修改
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef2.child(select1).setValue(new TextString(t1.getText().toString(),
                        t2.getText().toString()));
                firebase_select(myRef2);

            }
        });


        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
                TextView t1_s =(TextView)view.findViewById(R.id.textview);
                select1 = t1_s.getText().toString();
                TextView t2_s =(TextView)view.findViewById(R.id.textview2);
                t1.setText(t2_s.getText().toString());
                TextView t3_s =(TextView)view.findViewById(R.id.textview3);
                t2.setText(t3_s.getText().toString());

            }
        });
    }
    public Button.OnClickListener btnClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            Button btn = (Button) v;
            if (btn.getId() == R.id.button3) {
                if (!gpsIsOpen())
                    return;
                mLocation = getLocation();

                if (mLocation != null)
                    textView.setText("經度" + mLocation.getLongitude() + "\n緯度" + mLocation.getLatitude());
                else
                    textView.setText("獲取不到資料");

            }
        }
    };
    private boolean gpsIsOpen() {
        boolean bRet = true;
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "未開啟GPS", Toast.LENGTH_SHORT).show();
            bRet = false;
        } else {
            Toast.makeText(this, "GPS已開啟", Toast.LENGTH_SHORT).show();
        }
        return bRet;
    }


    private Location getLocation() {
//獲取位置管理服務
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//查詢服務資訊
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //定位精度: 最高
        criteria.setAltitudeRequired(false); //海拔資訊：不需要
        criteria.setBearingRequired(false); //方位資訊: 不需要
        criteria.setCostAllowed(true);  //是否允許付費
        criteria.setPowerRequirement(Criteria.POWER_HIGH); //耗電量
        String provider = mLocationManager.getBestProvider(criteria,true); //獲取GPS資訊

        if (ContextCompat.checkSelfPermission(LogSuccAct.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LogSuccAct.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);


        }

        Location location = mLocationManager.getLastKnownLocation((
                LocationManager.GPS_PROVIDER));

        mLocationManager.requestLocationUpdates(provider, 2000, 5, locationListener);
        return location;
    }

    private final LocationListener locationListener = new LocationListener()
    {
        public void onLocationChanged(@NonNull Location location) {
            textView.setText("經度" + location.getLongitude() + "\n緯度" + location.getLatitude());
        };
        public void onProviderDisabled(String provider)
        {
// TODO Auto-generated method stub
        }
        public void onProviderEnabled(String provider)
        {
// TODO Auto-generated method stub
        }
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
// TODO Auto-generated method stub
        }
    };



    private void firebase_select(DatabaseReference db) {
        final List<Map<String,Object>> items = new ArrayList<Map<String, Object>>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    TextString user_data=ds.getValue(TextString.class);

                    Map<String,Object> item = new HashMap<String, Object>();
                    item.put("id",ds.getKey());
                    item.put("c1",user_data.getC1());
                    item.put("c2",user_data.getC2());
                    items.add(item);
                    last1=Integer.parseInt((ds.getKey()));


                }


                SimpleAdapter SA = new SimpleAdapter(context,items,R.layout.textstring,new String[]
                        {"id","c1","c2"},new int[]{R.id.textview,R.id.textview2,R.id.textview3});
                LV.setAdapter(SA);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void buildViews(){

        final Button btOK;
        btOK = (Button)findViewById(R.id.BT2);



        btOK.setOnClickListener(btListener);
    }
    private View.OnClickListener btListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            Bundle backbundle = new Bundle();
            intent.putExtras(backbundle);

            setResult(RESULT_OK,intent);
            finish();
        }
    };
}
