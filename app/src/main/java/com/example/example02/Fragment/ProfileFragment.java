package com.example.example02.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.example02.Activity.ProfileActivity;
import com.example.example02.GlideApp;
import com.example.example02.Info.PostInfo;
import com.example.example02.OnFeedItemClickListener;
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

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private RecyclerView recyclerView;

    private List<PostInfo> imageDTOs = new ArrayList<>();
    private List<PostInfo> result = new ArrayList<>();

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
        final View view = inflater.inflate(R.layout.fragment_profile, null);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(user.getUid());
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
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        final ProfileFragment.BoardRecyclerViewAdapter boardRecyclerViewAdapter = new ProfileFragment.BoardRecyclerViewAdapter();
        recyclerView.setAdapter(boardRecyclerViewAdapter);

        boardRecyclerViewAdapter.setOnItemClickListener(new OnFeedItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                PostInfo item = boardRecyclerViewAdapter.getItem(position);
                ((ProfileActivity) getActivity()).SetPostInfo(boardRecyclerViewAdapter.getItem(position));
                ((ProfileActivity) getActivity()).startPostDetail();
                startToast("아이템선택됨"+item.getPhotoUrl());
            }
        });

        database.getReference().child("posts").orderByChild("publisher").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
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

                boardRecyclerViewAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        view.findViewById(R.id.setting).setOnClickListener(onClickListener);
        view.findViewById(R.id.Profileimage).setOnClickListener(onClickListener);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.setting:
                    profileSetting();
                    break;
                case R.id.Profileimage:
                    break;
            }
        }
    };

    private void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void setProflieImage(DocumentSnapshot document, ImageView ProfileImage) {
        String photoUrl = document.getData().get("photoUrl").toString();
        if(photoUrl != null)
            GlideApp.with(this).asBitmap().load(document.getData().get("photoUrl").toString()).apply(new RequestOptions().circleCrop()).into(ProfileImage);
    }

    private void profileSetting() {
        ((ProfileActivity) getActivity()).profileSetting();
    }

    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnFeedItemClickListener {
        OnFeedItemClickListener listener;

        public PostInfo getItem(int position)
        {
            return result.get(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post_view, parent, false);
            return new BoardRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext()).load(result.get(position).getPhotoUrl()).into(((ProfileFragment.BoardRecyclerViewAdapter.CustomViewHolder)holder).imageView);
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
            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.postView);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if(listener!=null)
                        {
                            listener.onItemClick(ProfileFragment.BoardRecyclerViewAdapter.CustomViewHolder.this,v,position);
                        }
                    }
                });
            }
        }
    }

}
