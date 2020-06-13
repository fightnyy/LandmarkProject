package com.example.example02.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.example02.Activity.FeedActivity;
import com.example.example02.Info.PostInfo;
import com.example.example02.OnFeedItemClickListener;
import com.example.example02.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<PostInfo> imageDTOs = new ArrayList<>();
    private List<PostInfo> Feeds = new ArrayList<>();
    private List<PostInfo> answer=new ArrayList<>();
    private List<String> photoanswer=new ArrayList<>();
    private int flag=0;
    private com.github.ybq.android.spinkit.SpinKitView spinkit;


    private FirebaseDatabase database;
    EditText editText;
    ViewGroup rootView;
    Button btn_toggle;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private List<String> uids = new ArrayList<>();
    int a=0;

    @Override
    public void onResume() {
        super.onResume();
        spinkit.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_feed, container, false);
        database = FirebaseDatabase.getInstance();


        final FeedFragment.BoardRecyclerViewAdapter boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();


        editText = rootView.findViewById(R.id.et_search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = editText.getText().toString();
                if (str.length() != 0) {
                    database.getReference().child("posts").orderByChild("area").equalTo(str).addValueEventListener(new ValueEventListener() {
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
                } else {
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








        btn_toggle = rootView.findViewById(R.id.btn_toggle);
        spinkit=rootView.findViewById(R.id.spin_kit);


        final String uid = user.getUid();


        btn_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_toggle.getText().equals("팔로워 보기")) {
                    btn_toggle.setText("모든 피드 보기");

                    Log.d("checkdebug", imageDTOs.size() + "");
                    Log.d("checkdebug", "---------------------------------1 버튼 눌렸을때 imgdto 사이즈");
                    if (flag == 0) {
                        for (int i = 0; i < imageDTOs.size(); i++) {
                            answer.add(imageDTOs.get(i));
                        }
                        flag=1;
                    }

                    database.getReference().child("follow").child(uid).child("following").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            uids.clear();
                            Feeds.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                uids.add(snapshot.getKey());
                                Log.d("checkdebug", snapshot.getKey());

                            }
                            Log.d("checkdebug", imageDTOs.size() + "answersize:     "+answer.size()+"uids:  "+uids.size());
                            Log.d("checkdebug", "---------------------------------2내가 팔로우하고 있는 모든 키들 가져오기 이때 imageDto의 사이즈도 asweruid");

                            for (int i = 0; i < answer.size(); i++) {
                                for (int j = 0; j < uids.size(); j++) {
                                    if (answer.get(i).getPublisher().equals(uids.get(j))) {
                                        Feeds.add(answer.get(i));
                                        Log.d("checkdebug", "---------------------------------3팔로우하고 있는 피드를 보기위해 imageDto 참고하는데 feed에 저장된 publisher");
                                        Log.d("checkdebug","image size  :"+imageDTOs.size()+"   uid size  :"+uids.size());
                                    }
                                }
                            }
                            Log.d("checkdebug","-----------------4순위 경쟁");
                            imageDTOs = Feeds;
                            boardRecyclerViewAdapter.notifyDataSetChanged();
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                else {
                    btn_toggle.setText("팔로워 보기");
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
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(boardRecyclerViewAdapter);
        boardRecyclerViewAdapter.setOnItemClickListener(new OnFeedItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                PostInfo item = boardRecyclerViewAdapter.getItem(position);
                ((FeedActivity) getActivity()).DetailFeed(item.getPublisher(), position);

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

        return rootView;
    }


    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnFeedItemClickListener {
        OnFeedItemClickListener listener;

        public PostInfo getItem(int position) {
            return imageDTOs.get(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feed_list, parent, false);
            spinkit.setVisibility(View.GONE);
            return new CustomViewHolder(view, listener);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext()).load(imageDTOs.get(position).getPhotoUrl()).into(((BoardRecyclerViewAdapter.CustomViewHolder) holder).imageView);
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

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;


            public CustomViewHolder(View view, final OnFeedItemClickListener listener) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.feed_image);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();

                        if (listener != null) {
                            listener.onItemClick(FeedFragment.BoardRecyclerViewAdapter.CustomViewHolder.this, v, position);
                        }
                    }
                });
            }
        }
    }
}
