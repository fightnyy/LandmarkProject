package com.example.example02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileSettingActivity extends AppCompatActivity {
    private static final String TAG = "ProfileSettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        findViewById(R.id.done).setOnClickListener(onClickListener);
        findViewById(R.id.ProfileImageSetting).setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super .onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0 :{
                if(resultCode == Activity.RESULT_OK){
                    String resultValue = data.getStringExtra("someKey");
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.done:
                    profileUpdate();
                    break;
                case R.id.ProfileImageSetting:
                    startSettingProfileImage();
                    break;
            }
        }
    };

    private void profileUpdate() {
        String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();

        if (name.length() > 0 && address.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            ProfileInfo profileinfo = new ProfileInfo(name, address);

            if(user != null) {
                db.collection("users").document(user.getUid()).set(profileinfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startToast("회원정보 등록에 성공하였습니다.");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startToast("회원정보 등록에 실패하였습니다.");
                            }
                        });
            }
        } else {
            startToast("이름을 입력해주세요.");
        }
    }

    private void startToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }

    private void startSettingProfileImage() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, 0);
    }
}
