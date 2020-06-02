package com.example.example02.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import com.example.example02.Adapter.PostAdapter;
import com.example.example02.Info.MapInfo;
import com.example.example02.Info.PostInfo;
import com.example.example02.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFeedFragment extends Fragment {
    private static final String TAG = "MapFeedFragment";

    private RecyclerView recyclerView;

    private List<PostInfo> imageDTOs = new ArrayList<>();
    private List<PostInfo> result = new ArrayList<>();

    private MapInfo map;

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

        return view;
    }
    class MapPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_post_item, parent, false);
            return new MapPostAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext()).load(result.get(position).getPhotoUrl()).
                    into(((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).imageView);
            ((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).username.setText(result.get(position).getArea());
            ((MapFeedFragment.MapPostAdapter.CustomViewHolder)holder).description_text.setText(result.get(position).getPostText());
        }

        @Override
        public int getItemCount() {
            return imageDTOs.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView username;
            TextView description_text;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.postImage);
                username = (TextView) view.findViewById(R.id.username);
                description_text = (TextView) view.findViewById(R.id.description_text);
            }
        }
    }
}

