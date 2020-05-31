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

import com.example.example02.Info.PostInfo;
import com.example.example02.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private static final String TAG = "PostAdapter";
    ArrayList<PostInfo> items = new ArrayList<PostInfo>();
    static int num;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemview = inflater.inflate(R.layout.activity_post_view, parent, false);
        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = "/storage/emulated/0/Download/PS16011800314.jpg";
        num = position;
       // String item = Uri.parse(items.get(position)).toString();
      //  holder.setItem(item);
        //Log.d(TAG, item);
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
        ImageView postView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setItem(String str) {
            postView = itemView.findViewById(R.id.postView);

            new DownloadFilesTask().execute(str);
        }

        private class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
            @Override
            protected Bitmap doInBackground(String[] strings) {
                Bitmap bmp = null;
                try {
                    String img_url = strings[num]; //url of the image
                    URL url = new URL(img_url);
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bmp;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                // doInBackground 에서 받아온 total 값 사용 장소
                postView.setImageBitmap(result);
            }
        }


    }
}
