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


        bundle = new Bundle();
        StartFeed();

    }

    public void StartFeed()
    {
        FeedFragment feedFragment=new FeedFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container12, feedFragment).commit();
    }

    public void DetailFeed(String username,int position) {
        detailFeedFragment = new DetailFeedFragment();
        bundle.putString("username",username);
        bundle.putInt("position",position);
        detailFeedFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container023,detailFeedFragment).commit();
        Log.d("DetailFeed",username+"abc");

    }
}