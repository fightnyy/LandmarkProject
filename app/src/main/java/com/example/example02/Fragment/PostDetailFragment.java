package com.example.example02.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Image;
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
import com.example.example02.Adapter.CommentAdapter;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

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
    private ImageView changeButton;
    private ImageView removeButton;
<<<<<<< HEAD
    FirebaseAuth auth;
    ImageView Like;
    TextView LikeNum;
=======
    private ImageView Like;
    private TextView LikeNum;
>>>>>>> 77fdabbc36ed9d675754fa01a89a6598726c63f9

    private List<CommentInfo> imageDTOs = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post_detail, null);




        view.findViewById(R.id.backButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.sendCommend).setOnClickListener(onClickListener);
        userName = (TextView) view.findViewById(R.id.userName);
        userText = (TextView) view.findViewById(R.id.description_text);
        userImage = (ImageView) view.findViewById(R.id.profileImage);
        Image = (ImageView) view.findViewById(R.id.postImage);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        Like = (ImageView) view.findViewById(R.id.Like);
        LikeNum = (TextView) view.findViewById(R.id.LikeNum);

        item = ((ProfileActivity) getActivity()).getPostInfo();

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        databaseReferenceComment = FirebaseDatabase.getInstance().getReference("comments");
        database = FirebaseDatabase.getInstance();
        GlideApp.with(getActivity()).asBitmap().load(item.getPhotoUrl()).into(Image);
        userText.setText(item.getPostText());


        LikeNum=view.findViewById(R.id.LikeNum);
        LikeNum.setText("좋아요"+item.starCount+"개");
        Like=view.findViewById(R.id.Like);

        Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onLikeClicked(database.getReference().child("posts"));
                Log.d("thisis","클릭됨");
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        final CommentAdapter boardRecyclerViewAdapter = new CommentAdapter(item.getKey());
        recyclerView.setAdapter(boardRecyclerViewAdapter);

        Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLikeClicked(database.getReference().child("posts").child(item.getKey()));
            }
        });

        boardRecyclerViewAdapter.setOnItemClickListener(new OnFeedItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                CommentInfo comment = boardRecyclerViewAdapter.getItem(position);
                startToast("아이템선택됨" + comment.getComment());
            }
        });

<<<<<<< HEAD
        if (item.stars.containsKey(auth.getCurrentUser().getUid())) {
=======
        if (item.stars.containsKey(user.getUid())) {
>>>>>>> 77fdabbc36ed9d675754fa01a89a6598726c63f9
            Like.setImageResource(R.drawable.favorite);
        } else {
            Like.setImageResource(R.drawable.favorite_border);
        }
<<<<<<< HEAD
=======
        LikeNum.setText("좋아요"+item.starCount+"개");
>>>>>>> 77fdabbc36ed9d675754fa01a89a6598726c63f9

        if(user.getUid().equals(item.getPublisher())){
            changeButton = (ImageView) view.findViewById(R.id.changeButton);
            changeButton.setImageResource(R.drawable.baseline_create_black);
            view.findViewById(R.id.changeButton).setOnClickListener(onClickListener);

            removeButton = (ImageView) view.findViewById(R.id.removeButton);
            removeButton.setImageResource(R.drawable.close_black);
            view.findViewById(R.id.removeButton).setOnClickListener(onClickListener);
        }

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
        database.getReference().child("posts").orderByChild("key").equalTo(item.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PostInfo p = snapshot.getValue(PostInfo.class);
                    LikeNum.setText("좋아요"+p.starCount+"개");
                }
                boardRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        database.getReference().child("comments").child(item.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageDTOs.clear();
                boardRecyclerViewAdapter.clearResult();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CommentInfo imageDTO = snapshot.getValue(CommentInfo.class);
                    boardRecyclerViewAdapter.addResult(imageDTO);
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
                    usertext = (EditText) view.findViewById(R.id.edit_text);
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
                            String commend = ((EditText) view.findViewById(R.id.edit_text)).getText().toString();
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

    private void onLikeClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                PostInfo p = mutableData.getValue(PostInfo.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                if (p.stars.containsKey(user.getUid())) {
                    p.starCount = p.starCount - 1;
                    Like.setImageResource(R.drawable.favorite_border);
                    p.stars.remove(user.getUid());
                } else {
                    p.starCount = p.starCount + 1;
                    Like.setImageResource(R.drawable.favorite);
                    p.stars.put(user.getUid(), true);
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

    private void deleteCotent() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("posts").child(item.getKey()).removeValue();
    }

    public void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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
