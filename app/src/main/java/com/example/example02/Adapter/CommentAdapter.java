package com.example.example02.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.example02.GlideApp;
import com.example.example02.Info.CommentInfo;
import com.example.example02.OnFeedItemClickListener;
import com.example.example02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnFeedItemClickListener {
    OnFeedItemClickListener listener;
    private List<CommentInfo> result = new ArrayList<>();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();;

    public void addResult(CommentInfo comment){
        result.add(comment);
    }

    public CommentInfo getItem(int position) {
        return result.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_view, parent, false);
        return new CommentAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (user.getUid().equals(result.get(position).getPublisher()))
            ((CustomViewHolder) holder).removeImage.setImageResource(R.drawable.close_black);

        ((CustomViewHolder) holder).comment.setText(result.get(position).getComment());
        DocumentReference docRef = db.collection("users").document(result.get(position).getPublisher());
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
                                        into(((CustomViewHolder) holder).profileImage);
                            ((CustomViewHolder) holder).userName.setText(document.getData().get("name").toString());
                        } else {
                        }
                    }
                } else {
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return result.size();
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
        ImageView profileImage;
        ImageView removeImage;
        TextView userName;
        TextView comment;

        public CustomViewHolder(View view) {
            super(view);
            profileImage = (ImageView) view.findViewById(R.id.profileImage);
            removeImage = (ImageView) view.findViewById(R.id.removeButton);
            userName = (TextView) view.findViewById(R.id.userName);
            comment = (TextView) view.findViewById(R.id.comment);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(CustomViewHolder.this, v, position);
                    }
                }
            });
        }
    }
}

