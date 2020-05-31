package com.example.example02.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.example02.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    static ArrayList<String> photoList = new ArrayList<>();



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemview = inflater.inflate(R.layout.feed_list, parent, false);
        DatabaseReference mDatabase;// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();

        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = photoList.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public void addItem(String str) {
        photoList.add(str);
    }

    public void setItem(ArrayList<String> photoList) {
        this.photoList = photoList;
    }

    public String getItem(int position) {
        return photoList.get(position);
    }

    public void setItem(int position, String item) {
        photoList.set(position, item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView feed_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            feed_image=itemView.findViewById(R.id.feed_image);
        }

        public void setItem(String str) {
           Glide.with(itemView).load(str).into(feed_image);

        }
    }
}

