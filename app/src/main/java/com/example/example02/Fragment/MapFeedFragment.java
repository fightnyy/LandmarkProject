
package com.example.example02.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.example02.Activity.MapActivity;
import com.example.example02.Activity.ProfileActivity;
import com.example.example02.Adapter.CommentAdapter;
import com.example.example02.GlideApp;
import com.example.example02.Info.CommentInfo;
import com.example.example02.Info.MapInfo;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MapFeedFragment extends Fragment {
    private static final String TAG = "MapFeedFragment";

    private RecyclerView recyclerView;

    private List<PostInfo> imageDTOs = new ArrayList<>();
    private List<String> uidlist = new ArrayList<>();

    private MapInfo map;
    private TextView areaName;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    ;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        map = (MapInfo) ((MapActivity) getActivity()).getMapInfo(((MapActivity) getActivity()).getCheckPositionNum());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_feed, null);

        areaName = (TextView) view.findViewById(R.id.areaName);
        view.findViewById(R.id.backButton).setOnClickListener(onClickListener);

        final MapPostAdapter mapPostAdapter = new MapPostAdapter();
        final Comparator<PostInfo> salesComparator = new Comparator<PostInfo>() {
            @Override
            public int compare(PostInfo o1, PostInfo o2) {
                return o2.getStarCount() - o1.getStarCount();
            }
        };

        database.getReference().child("posts").orderByChild("area").equalTo(map.getName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageDTOs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PostInfo imageDTO = snapshot.getValue(PostInfo.class);
                    imageDTOs.add(imageDTO);
                    Collections.sort(imageDTOs,salesComparator);
                }
                mapPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mapPostAdapter);

        areaName.setText(map.getName());
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.backButton:
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(MapFeedFragment.this).commit();
                    fragmentManager.popBackStack();
                    break;
            }
        }
    };

    private void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    class MapPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_post_detail, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            ((CustomViewHolder) holder).boardRecyclerViewAdapter = new CommentAdapter(imageDTOs.get(position).getKey());
            Glide.with(holder.itemView.getContext()).load(imageDTOs.get(position).getPhotoUrl()).
                    into(((CustomViewHolder) holder).imageView);

            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            ((CustomViewHolder) holder).Like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLikeClicked(database.getReference().child("posts").child(imageDTOs.get(position).getKey()));
                    Log.d("onclickhere", "클릭은 되었다."+position);
                }
            });
            ((CustomViewHolder) holder).userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra("user", imageDTOs.get(position).getPublisher());
                    startActivity(intent);
                }
            });
            ((CustomViewHolder) holder).LikeNum.setText("좋아요"+imageDTOs.get(position).starCount+"개");

            ((CustomViewHolder) holder).userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra("user", imageDTOs.get(position).getPublisher());
                    startActivity(intent);
                }
            });
            if (imageDTOs.get(position).stars.containsKey(auth.getCurrentUser().getUid())) {
                ((CustomViewHolder) holder).Like.setImageResource(R.drawable.favorite);
            } else {
                ((CustomViewHolder) holder).Like.setImageResource(R.drawable.favorite_border);
            }

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

            DocumentReference docRef = db.collection("users").document(imageDTOs.get(position).getPublisher());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                String photoUrl = document.getData().get("photoUrl").toString();
                                if (photoUrl != null)
                                    GlideApp.with(holder.itemView.getContext()).asBitmap().load(photoUrl).apply(new RequestOptions().circleCrop()).
                                            into(((CustomViewHolder) holder).userImage);
                                ((CustomViewHolder) holder).userName.setText(document.getData().get("name").toString());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            if (imageDTOs.get(position).getPostText() != null)
                ((CustomViewHolder) holder).description_text.setText(imageDTOs.get(position).getPostText());


        }

        @Override
        public int getItemCount() {
            return imageDTOs.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageView userImage;
            ImageView backButton;
            TextView userName;
            TextView description_text;
            ImageView Like;
            TextView LikeNum;
            EditText comments;
            RecyclerView recyclerView;
            ImageView sendCommend;
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
            CommentAdapter boardRecyclerViewAdapter;
            boolean checkComment = false;

            public CustomViewHolder(View view) {
                super(view);
                userImage = (ImageView) view.findViewById(R.id.profileImage);
                userName = (TextView) view.findViewById(R.id.userName);
                imageView = (ImageView) view.findViewById(R.id.postImage);
                description_text = (TextView) view.findViewById(R.id.description_text);
                Like = view.findViewById(R.id.Like);
                backButton = view.findViewById(R.id.backButton);
                backButton.setVisibility(View.INVISIBLE);
                LikeNum = view.findViewById(R.id.LikeNum);
                comments = (EditText) view.findViewById(R.id.commend);
                recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                sendCommend = (ImageView) view.findViewById(R.id.sendCommend);
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