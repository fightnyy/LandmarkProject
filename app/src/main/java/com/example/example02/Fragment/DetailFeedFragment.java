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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class DetailFeedFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<PostInfo> imageDTOs = new ArrayList<>();
    private List<String> uidlist = new ArrayList<>();
    private FirebaseDatabase database;
    private String userID;
    ViewGroup rootView;
    FirebaseAuth auth;
    int detailposition;

    final DetailFeedFragment.BoardRecyclerViewAdapter boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();
    LinearLayoutManager layoutManager;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString("username");
            detailposition=getArguments().getInt("position");
        }
        Log.d("onCreate작동","onCreate작동"+detailposition);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(detailposition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_detail_feed, container, false);

        database = FirebaseDatabase.getInstance();

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageDTOs.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    PostInfo imageDTO = snapshot.getValue(PostInfo.class);
                    imageDTOs.add(imageDTO);
                }
                boardRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uidlist.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uidkey = snapshot.getKey();
                    uidlist.add(uidkey);
                }
                boardRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        recyclerView = rootView.findViewById(R.id.detail_recyclerView);
        recyclerView.setAdapter(boardRecyclerViewAdapter);
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            userID = getArguments().getString("username");
            detailposition=getArguments().getInt("position");
        }
        Log.d("onResume작동","onResume작동"+detailposition);
        layoutManager.scrollToPosition(detailposition);

    }

    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public PostInfo getItem(int position) {
            return imageDTOs.get(position);
        }




        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_post_detail, parent, false);
            layoutManager.scrollToPosition(detailposition);
            return new CustomViewHolder(itemview);
        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            database = FirebaseDatabase.getInstance();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            Glide.with(holder.itemView.getContext()).load(imageDTOs.get(position).getPhotoUrl()).into(((CustomViewHolder) holder).postImage);
            ((CustomViewHolder) holder).description_text.setText(imageDTOs.get(position).getPostText());
            Log.d("startcomplete",position+"파이널전");
            ((CustomViewHolder) holder).Like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLikeClicked(database.getReference().child("posts").child(uidlist.get(position)));
                    Log.d("startcomplete",position+"");
                }
            });




            if (imageDTOs.get(position).stars.containsKey(auth.getCurrentUser().getUid())) {
                ((CustomViewHolder) holder).Like.setImageResource(R.drawable.favorite);
            } else {
                ((CustomViewHolder) holder).Like.setImageResource(R.drawable.favorite_border);
            }
            ((CustomViewHolder) holder).LikeNum.setText("좋아요"+imageDTOs.get(position).starCount+"개");




            DocumentReference docRef = db.collection("users").document(imageDTOs.get(position).getPublisher());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                ((CustomViewHolder) holder).userName.setText(document.getData().get("name").toString());
                                GlideApp.with(getContext()).asBitmap().load(document.getData().get("photoUrl").toString()).apply(new RequestOptions().circleCrop()).into(((CustomViewHolder) holder).profileImage);
                            } else {

                            }
                        }
                    } else {

                    }
                }
            });

        }
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };


        @Override
        public int getItemCount() {
            return imageDTOs.size();
        }


        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView profileImage;
            public TextView userName;
            public ImageView postImage;
            public TextView description_text;
            public ImageView Like;
            public TextView LikeNum;

            public CustomViewHolder(View itemview) {
                super(itemview);
                profileImage = itemview.findViewById(R.id.profileImage);
                userName = itemview.findViewById(R.id.userName);
                postImage = itemview.findViewById(R.id.postImage);
                description_text = itemview.findViewById(R.id.description_text);
                Like = itemview.findViewById(R.id.Like);
                LikeNum=itemview.findViewById(R.id.LikeNum);




            }
        }
        private void onLikeClicked(DatabaseReference postRef) {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    PostInfo p = mutableData.getValue(PostInfo.class);
                    if (p == null) {
                        return Transaction.success(mutableData);
                    }
                    if (p.stars.containsKey(auth.getCurrentUser().getUid())) {
                        p.starCount = p.starCount - 1;
                        p.stars.remove(auth.getCurrentUser().getUid());
                    } else {
                        p.starCount = p.starCount + 1;
                        p.stars.put(auth.getCurrentUser().getUid(), true);
                    }
                    mutableData.setValue(p);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                }
            });
        }
    }
}
