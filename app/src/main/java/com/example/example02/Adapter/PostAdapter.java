package com.example.example02.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.example02.Info.PostInfo;
import com.example.example02.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private static final String TAG = "PostAdapter";
    ArrayList<PostInfo> items = new ArrayList<PostInfo>();

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
        String item = items.get(position).getPhotoUrl();
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(PostInfo postInfo) {
        items.add(postInfo);
    }

    public void setItem(ArrayList<PostInfo> items) {
        this.items = items;
    }

    public PostInfo getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, PostInfo item) {
        items.set(position, item);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.postView);
        }

        public void setItem(String str) {
            Glide.with(itemView).load(str).into(post_image);

        }
    }
}
