package com.example.example02;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.example02.Adapter.FeedAdapter;

public interface OnFeedItemClickListener {

    public void onItemClick(RecyclerView.ViewHolder holder, View view, int position);
}
