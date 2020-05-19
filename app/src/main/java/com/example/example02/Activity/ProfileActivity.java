package com.example.example02.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.example.example02.Activity.BasisActivity;
import com.example.example02.Activity.ProfileSettingActivity;
import com.example.example02.GlideApp;
import com.example.example02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends BasisActivity {
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                     if(document != null) {
                        if (document.exists()) {
                            ImageView ProfileImage = (ImageView)findViewById(R.id.Profileimage);
                            TextView tv_name = (TextView)findViewById(R.id.tv_name);
                            TextView tv_address = (TextView)findViewById(R.id.tv_address);
                            TextView tv_email = (TextView)findViewById(R.id.tv_email);
                            tv_name.setText(document.getData().get("name").toString());
                            tv_address.setText(document.getData().get("address").toString());
                            setProflieImage(document, ProfileImage);
                            tv_email.setText(user.getEmail());
                        } else {
                            Log.d(TAG, "No such document");
                            profileSetting();
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        findViewById(R.id.setting).setOnClickListener(onClickListener);
        findViewById(R.id.Profileimage).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.setting:
                    profileSetting();
                    break;
                case R.id.Profileimage:
                    break;
            }
        }
    };

    private void setProflieImage(DocumentSnapshot document, ImageView ProfileImage){
        GlideApp.with(this).asBitmap().load(document.getData().get("photoUrl").toString()).apply(new RequestOptions().circleCrop()).into(ProfileImage);

    }
    private void profileSetting() {
        Intent intent = new Intent(this, ProfileSettingActivity.class);
        startActivity(intent);
    }

}
