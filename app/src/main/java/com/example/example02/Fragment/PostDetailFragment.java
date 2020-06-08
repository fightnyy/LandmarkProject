package com.example.example02.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.bumptech.glide.request.RequestOptions;
import com.example.example02.Activity.ProfileActivity;
import com.example.example02.GlideApp;
import com.example.example02.Info.CommentInfo;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostDetailFragment extends Fragment {
    private static final String TAG = "ProfileFragmentDetail";
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db;
    private DatabaseReference databaseReferenceComment;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private PostInfo item;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private RecyclerView recyclerView;

    private TextView userName;
    private TextView userText;
    private EditText usertext;
    private ImageView userImage;
    private ImageView Image;
    private ImageView finishButton;
    private View view;

    private List<CommentInfo> imageDTOs = new ArrayList<>();
    private List<CommentInfo> result = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post_detail, null);

        view.findViewById(R.id.backButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.removeButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.changeButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.sendCommend).setOnClickListener(onClickListener);
        userName = (TextView) view.findViewById(R.id.userName);
        userText = (TextView) view.findViewById(R.id.description_text);
        userImage = (ImageView) view.findViewById(R.id.profileImage);
        Image = (ImageView) view.findViewById(R.id.postImage);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        try {
            item = ((ProfileActivity) getActivity()).getPostInfo();
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.d("hello123","oaidgjoadgij");
        }
        Log.d("qwerty","abcde"+getActivity().toString());

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        databaseReferenceComment = FirebaseDatabase.getInstance().getReference("comments");
        database  = FirebaseDatabase.getInstance();
        GlideApp.with(getActivity()).asBitmap().load(item.getPhotoUrl()).into(Image);
        userText.setText(item.getPostText());

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(layoutManager);
        final BoardRecyclerViewAdapter boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();
        recyclerView.setAdapter(boardRecyclerViewAdapter);

        boardRecyclerViewAdapter.setOnItemClickListener(new OnFeedItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                CommentInfo comment = boardRecyclerViewAdapter.getItem(position);
                startToast("아이템선택됨"+comment.getComment());
            }
        });

        DocumentReference docRef = db.collection("users").document(item.getPublisher());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            String photoUrl = document.getData().get("photoUrl").toString();
                            if (photoUrl != null)
                                GlideApp.with(getActivity()).asBitmap().load(photoUrl).apply(new RequestOptions().circleCrop()).into(userImage);
                            userName.setText(document.getData().get("name").toString());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        database.getReference().child("comments").child(item.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageDTOs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CommentInfo imageDTO = snapshot.getValue(CommentInfo.class);
                    imageDTOs.add(imageDTO);
                }
                for (int i = 0; i < imageDTOs.size(); i++) {
                    result.add(imageDTOs.get(imageDTOs.size() - i - 1));
                }
                boardRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.backButton:
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(PostDetailFragment.this).commit();
                    fragmentManager.popBackStack();
                    break;

                case R.id.removeButton:
                    builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("삭제");
                    builder.setMessage("정말 게시글을 삭제하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteCotent();
                            startToast("게시글을 삭제하였습니다.");
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().remove(PostDetailFragment.this).commit();
                            fragmentManager.popBackStack();
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                    break;

                case R.id.changeButton:
                    userText.setVisibility(view.GONE);
                    usertext = (EditText) view.findViewById(R.id.edit_description_text);
                    usertext.setVisibility(view.VISIBLE);
                    finishButton = (ImageView) view.findViewById(R.id.finishButton);
                    finishButton.setImageResource(R.drawable.ic_check_black_24dp);
                    view.findViewById(R.id.finishButton).setOnClickListener(onClickListener);
                    break;

                case R.id.finishButton:
                    builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("수정");
                    builder.setMessage("정말 게시글을 수정하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String commend = ((EditText) view.findViewById(R.id.edit_description_text)).getText().toString();
                            databaseReference.child(item.getKey()).child("postText").setValue(commend);
                            startToast("게시글을 수정하였습니다.");
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().remove(PostDetailFragment.this).commit();
                            fragmentManager.popBackStack();
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                    break;

                case R.id.sendCommend:
                    String commend = ((EditText) view.findViewById(R.id.commend)).getText().toString();
                    ((EditText) view.findViewById(R.id.commend)).setText("");
                    String key = databaseReferenceComment.child("comments").child(item.getKey()).push().getKey();
                    CommentInfo commentInfo = new CommentInfo(commend, user.getUid(), key);
                    Map<String, Object> postValues = commentInfo.toMap();

                    databaseReferenceComment.child(item.getKey()).child(key).setValue(postValues);
                    startToast("댓글을 작성하였습니다.");
                    break;
            }
        }
    };

    private void deleteCotent() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("posts").child(item.getKey()).removeValue();
    }

    public void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnFeedItemClickListener {
        OnFeedItemClickListener listener;

        public CommentInfo getItem(int position)
        {
            return result.get(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_view, parent, false);
            return new BoardRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if(user.getUid().equals(result.get(position).getPublisher()))
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
                                Log.d(TAG, "No such document");
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
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
                            listener.onItemClick(PostDetailFragment.BoardRecyclerViewAdapter.CustomViewHolder.this, v, position);
                        }
                    }
                });
            }
        }
    }
}
