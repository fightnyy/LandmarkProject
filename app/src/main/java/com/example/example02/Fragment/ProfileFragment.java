package com.example.example02.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.example02.Activity.ProfileActivity;
import com.example.example02.GlideApp;
import com.example.example02.Info.FollowInfo;
import com.example.example02.Info.PostInfo;
import com.example.example02.OnFeedItemClickListener;
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

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private String userUid;

    private List<PostInfo> imageDTOs = new ArrayList<>();
    private List<PostInfo> result = new ArrayList<>();

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private TextView followButton;
    private TextView followingButton;
    private View view;

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, null);

        user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        databaseReference = databaseReference = FirebaseDatabase.getInstance().getReference("follow");

        Bundle bundle = getArguments();
        userUid = bundle.getString("user");

        followButton = (TextView) view.findViewById(R.id.followButton);
        followingButton = (TextView) view.findViewById(R.id.followingButton);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        final ProfileFragment.BoardRecyclerViewAdapter boardRecyclerViewAdapter = new ProfileFragment.BoardRecyclerViewAdapter();
        recyclerView.setAdapter(boardRecyclerViewAdapter);

        DocumentReference docRef = db.collection("users").document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            ImageView ProfileImage = (ImageView) view.findViewById(R.id.Profileimage);
                            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
                            TextView tv_address = (TextView) view.findViewById(R.id.tv_address);
                            TextView tv_email = (TextView) view.findViewById(R.id.tv_email);
                            tv_name.setText(document.getData().get("name").toString());
                            tv_address.setText(document.getData().get("address").toString());
                            setProflieImage(document, ProfileImage);
                            tv_email.setText(user.getEmail());
                        } else {
                            Log.d(TAG, "No such document");
                            profileSetting();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().remove(ProfileFragment.this).commit();
                            fragmentManager.popBackStack();
                            ((ProfileActivity) getActivity()).FinishActiviy();
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        database.getReference().child("posts").orderByChild("publisher").equalTo(userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageDTOs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PostInfo imageDTO = snapshot.getValue(PostInfo.class);
                    imageDTOs.add(imageDTO);
                }
                for (int i = 0; i < imageDTOs.size(); i++) {
                    result.add(imageDTOs.get(imageDTOs.size() - i - 1));
                    Log.d(TAG, imageDTOs.get(i).getPhotoUrl());
                }

                boardRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        database.getReference().child("follow").child(userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FollowInfo followInfo = dataSnapshot.getValue(FollowInfo.class);
                if (followInfo != null) {
                    followButton.setText(followInfo.followerCount + "");
                    followingButton.setText(followInfo.followingCount + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        boardRecyclerViewAdapter.setOnItemClickListener(new OnFeedItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                PostInfo item = boardRecyclerViewAdapter.getItem(position);
                ((ProfileActivity) getActivity()).SetPostInfo(boardRecyclerViewAdapter.getItem(position));
                ((ProfileActivity) getActivity()).startPostDetail();
            }
        });

        view.findViewById(R.id.followButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.followingButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.setting).setOnClickListener(onClickListener);
        view.findViewById(R.id.Profileimage).setOnClickListener(onClickListener);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.setting:
                    if (userUid.equals(user.getUid()))
                        profileSetting();
                    break;
                case R.id.Profileimage:
                    break;
                case R.id.followButton:
                    if (userUid.equals(user.getUid())) {

                    } else {

                        builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("팔로우");
                        builder.setMessage("팔로우 하시겠습니까?" + "\n" + "이미 팔로우 되어있다면 팔로우가 취소됩니다.");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onFollower(databaseReference.child(userUid));
                                onFollowing(databaseReference.child(user.getUid()));
                            }
                        });
                        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog = builder.create();
                        dialog.show();
                    }
                    break;
                case R.id.followingButton:
                    break;
            }
        }
    };

    private void onFollower(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                FollowInfo followInfo = mutableData.getValue(FollowInfo.class);
                if (followInfo == null) {
                    return Transaction.success(mutableData);
                }
                if (followInfo.follower.containsKey(user.getUid())) {
                    followInfo.followerCount = followInfo.followerCount - 1;
                    followInfo.follower.remove(user.getUid());
                } else {
                    followInfo.followerCount = followInfo.followerCount + 1;
                    followInfo.follower.put(user.getUid(), true);
                }
                mutableData.setValue(followInfo);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    private void onFollowing(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                FollowInfo followInfo = mutableData.getValue(FollowInfo.class);
                if (followInfo == null) {
                    return Transaction.success(mutableData);
                }
                if (followInfo.following.containsKey(userUid)) {
                    followInfo.followingCount = followInfo.followingCount - 1;
                    followInfo.following.remove(userUid);
                } else {
                    followInfo.followingCount = followInfo.followingCount + 1;
                    followInfo.following.put(userUid, true);
                }
                mutableData.setValue(followInfo);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    private void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void setProflieImage(DocumentSnapshot document, ImageView ProfileImage) {
        String photoUrl = document.getData().get("photoUrl").toString();
        if (photoUrl != null)
            GlideApp.with(this).asBitmap().load(document.getData().get("photoUrl").toString()).apply(new RequestOptions().circleCrop()).into(ProfileImage);
    }

    private void profileSetting() {
        ((ProfileActivity) getActivity()).profileSetting();
    }

    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnFeedItemClickListener {
        OnFeedItemClickListener listener;

        public PostInfo getItem(int position) {
            return result.get(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post_view, parent, false);
            return new BoardRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext()).load(result.get(position).getPhotoUrl()).into(((ProfileFragment.BoardRecyclerViewAdapter.CustomViewHolder) holder).imageView);
        }

        @Override
        public int getItemCount() {
            return imageDTOs.size();
        }

        public void setOnItemClickListener(OnFeedItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
            if (listener != null) {
                listener.onItemClick(holder, view, position);
            }
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.postView);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (listener != null) {
                            listener.onItemClick(ProfileFragment.BoardRecyclerViewAdapter.CustomViewHolder.this, v, position);
                        }
                    }
                });
            }
        }
    }

}
