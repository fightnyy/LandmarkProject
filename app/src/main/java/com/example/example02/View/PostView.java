package com.example.example02.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.request.RequestOptions;
import com.example.example02.GlideApp;
import com.example.example02.PostInfo;
import com.example.example02.R;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PostView extends LinearLayout {
    ImageView imageView;

    public PostView(Context context) {
        super(context);

        init(context);
    }

    public PostView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_post_view,this,true);

        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void setItem(PostInfo postInfo){
        GlideApp.with(this).asBitmap().load(postInfo.getPhotoUrl()).into(imageView);
    }
}
