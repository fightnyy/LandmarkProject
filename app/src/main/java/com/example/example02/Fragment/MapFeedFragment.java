package com.example.example02.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapFeedFragment extends Fragment {
    private static final String TAG = "MapFeedFragment";

    private RecyclerView recyclerView;

    private List<PostInfo> imageDTOs = new ArrayList<>();
    private List<PostInfo> result = new ArrayList<>();

    private MapInfo map;
    private TextView areaName;

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

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        areaName = (TextView) view.findViewById(R.id.areaName);
        view.findViewById(R.id.backButton).setOnClickListener(onClickListener);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(layoutManager);

        final MapPostAdapter mapPostAdapter = new MapPostAdapter();
        recyclerView.setAdapter(mapPostAdapter);

        database.getReference().child("posts").orderByChild("area").equalTo(map.getName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageDTOs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PostInfo imageDTO = snapshot.getValue(PostInfo.class);
                    imageDTOs.add(imageDTO);
                }
                for(int i = 0; i < imageDTOs.size(); i++) {
                    result.add(imageDTOs.get(imageDTOs.size() - i - 1));
                    Log.d(TAG, imageDTOs.get(i).getPhotoUrl());
                }
                mapPostAdapter.notifyDataSetChanged();
        }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

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
        private FirebaseDatabase database = FirebaseDatabase.getInstance();
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_post_detail, parent, false);
            return new MapPostAdapter.CustomViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            Glide.with(holder.itemView.getContext()).load(result.get(position).getPhotoUrl()).
                    into(((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).imageView);

            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            ((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra("user", result.get(position).getPublisher());
                    startActivity(intent);
                }
            });

            ((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra("user", result.get(position).getPublisher());
                    startActivity(intent);
                }
            });

            ((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).comments.setOnTouchListener(new View.OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).recyclerView.setAdapter(((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).boardRecyclerViewAdapter);
                            break;
                        }
                    }
                    return false;
                }
            });

            database.getReference().child("comments").child(result.get(position).getKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ((CustomViewHolder)holder).commentInfo.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CommentInfo imageDTO = snapshot.getValue(CommentInfo.class);
                        ((CustomViewHolder)holder).commentInfo.add(imageDTO);
                    }
                    for (int i = 0; i < imageDTOs.size(); i++) {
                        ((CustomViewHolder)holder).boardRecyclerViewAdapter.addResult(((CustomViewHolder)holder).commentInfo.get(((CustomViewHolder)holder).commentInfo.size() - i - 1));
                    }
                    ((CustomViewHolder)holder).boardRecyclerViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            ((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).sendCommend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference databaseReferenceComment = FirebaseDatabase.getInstance().getReference("comments");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String key = databaseReferenceComment.child("comments").child(result.get(position).getKey()).push().getKey();
                    CommentInfo commentAdd = new CommentInfo(((CustomViewHolder)holder).comments.getText().toString(), user.getUid(), key);
                    Map<String, Object> postValues = commentAdd.toMap();

                    databaseReferenceComment.child(result.get(position).getKey()).child(key).setValue(postValues);
                    startToast("댓글을 작성하였습니다.");
                }
            });


            DocumentReference docRef = db.collection("users").document(result.get(position).getPublisher());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    if (document.exists()) {
                                        String photoUrl = document.getData().get("photoUrl").toString();
                                        if(photoUrl != null)
                                            GlideApp.with(holder.itemView.getContext()).asBitmap().load(photoUrl).apply(new RequestOptions().circleCrop()).
                                                    into(((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).userImage);
                                        ((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).userName.setText(document.getData().get("name").toString());
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                }
            });

            if(result.get(position).getPostText() != null)
                ((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).description_text.setText(result.get(position).getPostText());
        }

        @Override
        public int getItemCount() {
            return imageDTOs.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageView userImage;
            TextView userName;
            TextView description_text;
            EditText comments;
            RecyclerView recyclerView;
            ImageView sendCommend;
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
            final CommentAdapter boardRecyclerViewAdapter = new CommentAdapter();
            private List<CommentInfo> commentInfo = new ArrayList<>();

            public CustomViewHolder(final View view) {
                super(view);
                userImage = (ImageView) view.findViewById(R.id.profileImage);
                userName = (TextView) view.findViewById(R.id.userName);
                imageView = (ImageView) view.findViewById(R.id.postImage);
                description_text = (TextView) view.findViewById(R.id.description_text);
                comments = (EditText) view.findViewById(R.id.commend);
                recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                sendCommend = (ImageView) view.findViewById(R.id.sendCommend);
                recyclerView.setLayoutManager(layoutManager);

            }
        }

    }
}

