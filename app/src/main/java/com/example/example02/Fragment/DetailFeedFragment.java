package com.example.example02.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.example02.Activity.FeedActivity;
import com.example.example02.GlideApp;
import com.example.example02.Info.PostInfo;
import com.example.example02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class DetailFeedFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<PostInfo> imageDTOs = new ArrayList<>();
    private FirebaseDatabase database;
    private String userName;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ViewGroup rootView;
    String photoUrl;
    Button btn;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance();
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_detail_feed, container, false);


        if (getArguments() != null) {
            userName = getArguments().getString("userId");
            Log.d("Detailname", userName);
        }




        rootView.findViewById(R.id.btn_temp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedActivity feedActivity=(FeedActivity) getActivity();
                feedActivity.DetailFeed("kgn");
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView = rootView.findViewById(R.id.detail_recyclerView);
        final DetailFeedFragment.BoardRecyclerViewAdapter boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();
        recyclerView.setAdapter(boardRecyclerViewAdapter);

        recyclerView.setLayoutManager(layoutManager);


        database.getReference().child("posts").orderByChild("publisher").equalTo(userName).addValueEventListener(new ValueEventListener() {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return rootView;
    }


    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        public PostInfo getItem(int position) {
            return imageDTOs.get(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_detail_feed_list, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext()).load(imageDTOs.get(position).getPhotoUrl()).into(((DetailFeedFragment.BoardRecyclerViewAdapter.CustomViewHolder) holder).post_Image);
//            Glide.with(holder.itemView.getContext()).load(photoUrl).into(((DetailFeedFragment.BoardRecyclerViewAdapter.CustomViewHolder) holder).profile_photo);
            ((DetailFeedFragment.BoardRecyclerViewAdapter.CustomViewHolder) holder).userId.setText(userName);

        }

        @Override
        public int getItemCount() {
            return imageDTOs.size();
        }


        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public de.hdodenhof.circleimageview.CircleImageView profile_photo;
            public com.example.example02.View.SquareImageView post_Image;
            public TextView userId;
            public TextView image_time_posted;

            public CustomViewHolder(View view) {
                super(view);
                profile_photo = view.findViewById(R.id.profile_photo);
                post_Image = view.findViewById(R.id.post_image);
                userId = (TextView) view.findViewById(R.id.username);
                image_time_posted = view.findViewById(R.id.image_time_posted);


            }
        }
    }
}
