package com.example.example02.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.example02.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BasisActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startLoginActivity();
        }

        findViewById(R.id.LocationButtion).setOnClickListener(onClickListener);
        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.writingButton).setOnClickListener(onClickListener);
        findViewById(R.id.CameraButton).setOnClickListener(onClickListener);
        findViewById(R.id.ProfileButton).setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logoutButton:
                    FirebaseAuth.getInstance().signOut();
                    startLoginActivity();
                    break;

                case R.id.CameraButton:
                    startCameraActivity();
                    break;

                case R.id.ProfileButton:
                    startProfileActivity();
                    break;
                case R.id.writingButton:
                    startWritingActivity();
                    break;
                case R.id.LocationButtion:
                    startLocationActivity();
                    break;
            }
        }
    };

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
        Intent intent = new Intent(this, ProfileActivity.class);
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


