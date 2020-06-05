package com.example.example02.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.example02.Activity.ProfileActivity;
import com.example.example02.GlideApp;
import com.example.example02.Info.PostInfo;
import com.example.example02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostDetailFragment extends Fragment {
    private static final String TAG = "ProfileFragmentDetail";
    private FirebaseFirestore db;
    private PostInfo item;

    private TextView userName;
    private TextView userText;
    private ImageView userImage;
    private ImageView Image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_post_detail, null);

        view.findViewById(R.id.backButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.removeButton).setOnClickListener(onClickListener);
        userName = (TextView) view.findViewById(R.id.userName);
        userText = (TextView) view.findViewById(R.id.description_text);
        userImage = (ImageView) view.findViewById(R.id.profileImage);
        Image = (ImageView) view.findViewById(R.id.postImage);

        item = ((ProfileActivity) getActivity()).getPostInfo();

        db = FirebaseFirestore.getInstance();
        GlideApp.with(getActivity()).asBitmap().load(item.getPhotoUrl()).into(Image);
        userText.setText(item.getPostText());

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
            }
        }
    };

    private void deleteCotent() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("posts").child(item.getKey()).removeValue();
    }

    public void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
