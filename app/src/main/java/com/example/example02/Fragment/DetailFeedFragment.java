package com.example.example02.Fragment;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.example02.GlideApp;
import com.example.example02.Info.PostInfo;
import com.example.example02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    final DetailFeedFragment.BoardRecyclerViewAdapter boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();
    Bundle bundle;
    String str;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_detail_feed, container, false);
        if (getArguments() != null) {
            userName = getArguments().getString("username");

        }
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("users").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            ImageView ProfileImage = (ImageView) rootView.findViewById(R.id.profile_photo);
                            TextView tv_name = (TextView) rootView.findViewById(R.id.username);
                            tv_name.setText(document.getData().get("name").toString());
                            String photoUrl = document.getData().get("photoUrl").toString();
                            GlideApp.with(getContext()).asBitmap().load(document.getData().get("photoUrl").toString()).apply(new RequestOptions().circleCrop()).into(ProfileImage);
                        } else {

                        }
                    }
                } else {

                }
            }
        });






        database = FirebaseDatabase.getInstance();
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView = rootView.findViewById(R.id.detail_recyclerView);
        recyclerView.setAdapter(boardRecyclerViewAdapter);
        recyclerView.setLayoutManager(layoutManager);

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
