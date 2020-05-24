package com.example.example02.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.example.example02.GlideApp;
import com.example.example02.Info.PostInfo;
import com.example.example02.R;
import com.example.example02.View.PostView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ProfileActivity extends BasisActivity {
    private static final String TAG = "ProfileActivity";
    GridView postList;
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        postList = (GridView)findViewById(R.id.gridView);
        postAdapter = new PostAdapter();
        String result;

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        /*db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(user.getUid() == document.getData().get("publisher")){
                                    PostInfo postInfo = new PostInfo(document.getData().get("name").toString(),
                                            document.getData().get("photoUrl").toString(), document.getData().get("publisher").toString(),
                                            (Date) document.getData().get("createdAt"));
                                    postAdapter.addItem(postInfo);
                                    Log.d(TAG,  postInfo.getpublisher());
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });*/

        db.collectionGroup("posts").whereEqualTo("publisher", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PostInfo postInfo = new PostInfo(document.getData().get("name").toString(),
                                        document.getData().get("photoUrl").toString(), document.getData().get("publisher").toString(),
                                        (Date) document.getData().get("createdAt"));
                                postAdapter.addItem(postInfo);
                                Log.d(TAG,  postInfo.getpublisher());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
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

    class PostAdapter extends BaseAdapter {
        ArrayList<PostInfo> items = new ArrayList<PostInfo>();
        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(PostInfo postInfo){
            items.add(postInfo);
        }

        @Override
        public PostInfo getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            PostView postView = new PostView(getApplicationContext());
            postView.setItem(items.get(i));
            return postView;
        }
    }


    private void setProflieImage(DocumentSnapshot document, ImageView ProfileImage){
        GlideApp.with(this).asBitmap().load(document.getData().get("photoUrl").toString()).apply(new RequestOptions().circleCrop()).into(ProfileImage);

    }
    private void profileSetting() {
        Intent intent = new Intent(this, ProfileSettingActivity.class);
        startActivity(intent);
    }

}
