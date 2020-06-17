package com.example.example02.Fragment;



import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.example02.Activity.ProfileActivity;
import com.example.example02.Adapter.CommentAdapter;
import com.example.example02.GlideApp;
import com.example.example02.Info.CommentInfo;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class DetailFeedFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<PostInfo> imageDTOs = new ArrayList<>();
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
            detailposition = getArguments().getInt("position");
        }
        Log.d("onCreate작동","onCreate작동"+detailposition);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

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
                Collections.reverse(imageDTOs);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutManager.scrollToPosition(detailposition);
            }
        }, 100);
    }

    private void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public PostInfo getItem(int position) {
            return imageDTOs.get(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_post_detail, parent, false);

            return new CustomViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            ((CustomViewHolder)holder).boardRecyclerViewAdapter = new CommentAdapter(imageDTOs.get(position).getKey());
            database = FirebaseDatabase.getInstance();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            Glide.with(holder.itemView.getContext()).load(imageDTOs.get(position).getPhotoUrl()).into(((CustomViewHolder) holder).postImage);
            ((CustomViewHolder) holder).description_text.setText(imageDTOs.get(position).getPostText());
            Log.d("startcomplete",position+"파이널전");
            ((CustomViewHolder) holder).Like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLikeClicked(database.getReference().child("posts").child(imageDTOs.get(position).getKey()));
                    Log.d("startcomplete",position+"");
                }
            });

            if (imageDTOs.get(position).stars.containsKey(auth.getCurrentUser().getUid())) {
                ((CustomViewHolder) holder).Like.setImageResource(R.drawable.favorite);
            } else {
                ((CustomViewHolder) holder).Like.setImageResource(R.drawable.favorite_border);
            }
            ((CustomViewHolder) holder).LikeNum.setText("좋아요"+imageDTOs.get(position).starCount+"개");

            database.getReference().child("comments").child(imageDTOs.get(position).getKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ((CustomViewHolder)holder).boardRecyclerViewAdapter.clearResult();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CommentInfo imageDTO = snapshot.getValue(CommentInfo.class);
                        ((CustomViewHolder)holder).boardRecyclerViewAdapter.addResult(imageDTO);
                    }
                    ((CustomViewHolder)holder).boardRecyclerViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            ((CustomViewHolder) holder).profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra("user", imageDTOs.get(position).getPublisher());
                    startActivity(intent);
                }
            });

            ((CustomViewHolder) holder).userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra("user", imageDTOs.get(position).getPublisher());
                    startActivity(intent);
                }
            });


            ((CustomViewHolder)holder).comments.setOnTouchListener(new View.OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            if(!((CustomViewHolder)holder).checkComment) {
                                ((CustomViewHolder) holder).recyclerView.setAdapter(((CustomViewHolder) holder).boardRecyclerViewAdapter);
                                ((CustomViewHolder)holder).checkComment = !((CustomViewHolder)holder).checkComment;
                            }
                            break;
                        }
                    }
                    return false;
                }
            });

            ((CustomViewHolder)holder).sendCommend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference databaseReferenceComment = FirebaseDatabase.getInstance().getReference("comments");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String key = databaseReferenceComment.child("comments").child(imageDTOs.get(position).getKey()).push().getKey();
                    CommentInfo commentAdd = new CommentInfo(((CustomViewHolder)holder).comments.getText().toString(), user.getUid(), key);
                    Map<String, Object> postValues = commentAdd.toMap();

                    databaseReferenceComment.child(imageDTOs.get(position).getKey()).child(key).setValue(postValues);
                    startToast("댓글을 작성하였습니다.");
                }
            });

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
            ImageView backButton;
            EditText comments;
            RecyclerView recyclerView;
            ImageView sendCommend;
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
            CommentAdapter boardRecyclerViewAdapter;
            boolean checkComment = false;

            public CustomViewHolder(View itemview) {
                super(itemview);
                profileImage = itemview.findViewById(R.id.profileImage);
                userName = itemview.findViewById(R.id.userName);
                postImage = itemview.findViewById(R.id.postImage);
                description_text = itemview.findViewById(R.id.description_text);
                Like = itemview.findViewById(R.id.Like);
                LikeNum=itemview.findViewById(R.id.LikeNum);
                backButton=itemview.findViewById(R.id.backButton);
                backButton.setVisibility(View.INVISIBLE);
                comments = (EditText) itemview.findViewById(R.id.commend);
                recyclerView = (RecyclerView) itemview.findViewById(R.id.recyclerView);
                sendCommend = (ImageView) itemview.findViewById(R.id.sendCommend);
                recyclerView.setLayoutManager(layoutManager);

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
