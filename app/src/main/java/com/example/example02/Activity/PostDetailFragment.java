package com.example.example02.Activity;

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

import com.bumptech.glide.request.RequestOptions;
import com.example.example02.Fragment.MapFeedFragment;
import com.example.example02.GlideApp;
import com.example.example02.Info.PostInfo;
import com.example.example02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostDetailFragment extends Fragment {
    private static final String TAG = "ProfileFragmentDetail";
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
        userName = (TextView) view.findViewById(R.id.userName);
        userText = (TextView) view.findViewById(R.id.description_text);
        userImage = (ImageView) view.findViewById(R.id.profileImage);
        Image = (ImageView) view.findViewById(R.id.postImage);

        item = ((ProfileActivity) getActivity()).getPostInfo();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                            if(photoUrl != null)
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
            }
        }
    };
}
