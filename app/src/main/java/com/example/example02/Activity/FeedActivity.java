package com.example.example02.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.example02.Info.PostInfo;
import com.example.example02.OnFeedItemClickListener;
import com.example.example02.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<PostInfo> imageDTOs = new ArrayList<>();
    private FirebaseDatabase database;
    private List<PostInfo> result = new ArrayList<>();
    EditText editText;
    String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        database = FirebaseDatabase.getInstance();
        final BoardRecyclerViewAdapter boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();
        editText = findViewById(R.id.et_search);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String str = editText.getText().toString();
                    if(str.length()!=0){
                    database.getReference().child("posts").orderByChild("location").equalTo(str).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            imageDTOs.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                PostInfo imageDTO = snapshot.getValue(PostInfo.class);
                                imageDTOs.add(imageDTO);
                            }
                            boardRecyclerViewAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                    else
                    {
                        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                imageDTOs.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    PostInfo imageDTO = snapshot.getValue(PostInfo.class);
                                    imageDTOs.add(imageDTO);
                                }
                                boardRecyclerViewAdapter.notifyDataSetChanged();


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });



        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(boardRecyclerViewAdapter);
        boardRecyclerViewAdapter.setOnItemClickListener(new OnFeedItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                PostInfo item=boardRecyclerViewAdapter.getItem(position);
                Toast.makeText(getApplicationContext(),"아이템선택됨"+item.getPhotoUrl(),Toast.LENGTH_LONG).show();
            }
        });

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageDTOs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PostInfo imageDTO = snapshot.getValue(PostInfo.class);
                    imageDTOs.add(imageDTO);
                }
                boardRecyclerViewAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnFeedItemClickListener {
        OnFeedItemClickListener listener;

        public PostInfo getItem(int position)
        {
            return imageDTOs.get(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_list, parent, false);

            return new CustomViewHolder(view,listener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext()).load(imageDTOs.get(position).getPhotoUrl()).into(((CustomViewHolder) holder).imageView);
        }

        @Override
        public int getItemCount() {
            return imageDTOs.size();
        }

        public void setOnItemClickListener(OnFeedItemClickListener listener)
        {
            this.listener=listener;
        }

        @Override
        public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                if(listener!=null)
                {
                    listener.onItemClick(holder,view,position);
                }
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;


            public CustomViewHolder(View view, final OnFeedItemClickListener listener) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.feed_image);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position=getAdapterPosition();

                        if(listener!=null)
                        {
                            listener.onItemClick(CustomViewHolder.this,v,position);
                        }
                    }
                });
            }
        }
    }
}