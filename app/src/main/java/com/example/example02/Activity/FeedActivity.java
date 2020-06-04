package com.example.example02.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.example02.Fragment.DetailFeedFragment;
import com.example.example02.Fragment.FeedFragment;
import com.example.example02.R;

public class FeedActivity extends AppCompatActivity {

    DetailFeedFragment detailFeedFragment;
    FeedFragment feedFragment;
    Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rootfeed);
        feedFragment = (FeedFragment) getSupportFragmentManager().findFragmentById(R.id.feedfrag);
        detailFeedFragment = new DetailFeedFragment();
        bundle = new Bundle();


    }

    public void DetailFeed(String username) {
        bundle.putString("username", username);
        Log.d("hello",username);
        getSupportFragmentManager().beginTransaction().replace(R.id.FeedContainer, detailFeedFragment).commit();
    }
}