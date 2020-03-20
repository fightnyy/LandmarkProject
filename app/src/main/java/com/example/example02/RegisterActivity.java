package com.example.example02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.registerButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.registerButton:
                    signUp();
                    break;
            }
        }
    };

    private void signUp() {
        String email = ((EditText)findViewById(R.id.emailText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordText)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.passwordCheck)).getText().toString();

        if(email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {

             if(password.equals(passwordCheck)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                      .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                          @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startToast("회원가입에 성공하였습니다.");
                                    finish();
                                } else {
                                 // If sign in fails, display a message to the user.
                                    if(task.getException() != null)
                                     startToast(task.getException().toString());
                                }
                           }
                       });
            } else{
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "이메일 또는 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }


    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
