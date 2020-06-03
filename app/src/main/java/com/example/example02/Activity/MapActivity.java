package com.example.example02.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.example02.Fragment.MapFragment;
import com.example.example02.Info.MapInfo;
import com.example.example02.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapActivity";
    private final ArrayList<MapInfo> mapInfo = new ArrayList<>();
    private int checkPositionNum;
    private GoogleMap mMap;
    private MapFragment MF;
    private MapFeedFragment MFF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.26222, 127.02889)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("locations").child("Suwon");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    MapInfo map = new MapInfo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    try {
                        map.setLocation1(objectMap.get("location1").toString());
                        map.setLocation2(objectMap.get("location2").toString());
                        map.setName(objectMap.get("name").toString());
                        map.setExplain(objectMap.get("explain").toString());

                        mapInfo.add(map);
                    } catch (NullPointerException e) {
                        Log.e(TAG, "onDataChange: NullPointerException: " + e.getMessage());
                    }
                }

                for (int idx = 0; idx < mapInfo.size(); idx++) {
                    MarkerOptions makerOptions = new MarkerOptions();
                    makerOptions.position(new LatLng(Double.valueOf(mapInfo.get(idx).getLocation1()), Double.valueOf(mapInfo.get(idx).getLocation2())))
                            .title(mapInfo.get(idx).getName());

                    mMap.addMarker(makerOptions);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_SHORT).show();
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        MF = new MapFragment();
        for(int idx = 0; idx < mapInfo.size(); idx++){
            if(marker.getTitle().equals(mapInfo.get(idx).getName())){
                checkPositionNum = idx;
                break;
            }
        }
        startMapFragment();
        return false;
    }

    public void startsearchAreaPost(){
        MFF = new MapFeedFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container02, MFF).commit();
    }


    public int getCheckPositionNum(){
        return checkPositionNum;
    }

    public Object getMapInfo(int num){
        return mapInfo.get(num);
    }

    private void startMapFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, MF).commit();
        Bundle arguments = new Bundle();
        MF.setArguments(arguments);
    }
}
