package com.example.example02.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.example02.Info.PostInfo;
import com.example.example02.R;


public class ProfileActivity extends BasisActivity {
    private static final String TAG = "ProfileActivity";

    private PostInfo item;

    private PostDetailFragment PDF;
    private ProfileFragment PF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        startProfileFragment();
    }

    public void startProfileFragment(){
        PF = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, PF).commit();
    }

    public void startPostDetail(){
        PDF = new PostDetailFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container02, PDF).commit();
    }

    public void SetPostInfo(PostInfo item){
        this.item = item;
    }

    public PostInfo getPostInfo(){
        return this.item;
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void profileSetting() {
        Intent intent = new Intent(this, ProfileSettingActivity.class);
        startActivity(intent);
    }
}
