package com.example.example02;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface OnFeedItemClickListener {

    public void onItemClick(RecyclerView.ViewHolder holder, View view, int position);
}
