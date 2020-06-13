package com.example.example02.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.example02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends BasisActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                        } else {
                            startProfileActivity();
                            finish();
                        }
                    }
                } else {
                }
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        textView = findViewById(R.id.textView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setupDrawerContent(navigationView);
        navigationView.setCheckedItem(R.id.nav_home);

        toolbar.setNavigationIcon(R.drawable.menu);


        if (user == null) {
            startLoginActivity();
        }

        //BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        //bottomNavigationView.setOnNavigationItemSelectedListener(onBottomNavigationListener);
        findViewById(R.id.mapButton).setOnClickListener(onClickListener);
        findViewById(R.id.feedButton).setOnClickListener(onClickListener);
        findViewById(R.id.requestButton).setOnClickListener(onClickListener);
        findViewById(R.id.writingButton).setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.writingButton:
                    startWritingActivity();
                    break;
                case R.id.mapButton:
                    startLocationActivity();
                    break;
                case R.id.feedButton:
                    Intent intent=new Intent(getApplicationContext(),FeedActivity.class);
                    startActivity(intent);
                    break;
                case R.id.requestButton:
                    Intent intent1=new Intent(getApplicationContext(),RequestActivity.class);
                    startActivity(intent1);
                    break;
            }
        }
    };


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_writing:
                                startWritingActivity();
                                break;
                            case R.id.nav_map:
                                startLocationActivity();
                                break;
                            case R.id.nav_request:
                                break;
                            case R.id.nav_feed:
                                Intent intent=new Intent(getApplicationContext(),FeedActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_profile:
                                startProfileActivity();
                                break;
                            case R.id.nav_logout:
                                FirebaseAuth.getInstance().signOut();
                                startLoginActivity();
                                break;
                        }
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


    /*BottomNavigationView.OnNavigationItemSelectedListener onBottomNavigationListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId())
            {
                case R.id.feed:
                    Intent feedintent=new Intent(getApplicationContext(),FeedActivity.class);
                    startActivity(feedintent);
                    return true;

                case R.id.Request:
                    Intent requestintent=new Intent(getApplicationContext(),RequestActivity.class);
                    startActivity(requestintent);
                    return true;

            }
            return true;
        }
    };*/

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startLocationActivity(){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private void startProfileActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user.getUid());
        startActivity(intent);
    }

    private void startWritingActivity() {
        Intent intent = new Intent(this, WritingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        if(isTaskRoot()){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("나가기");
            builder.setMessage("앱을 종료하시겠습니까?");
            builder.setIcon(R.drawable.air_plane);
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();


        }else{
            super.onBackPressed();
        }

    }

}


