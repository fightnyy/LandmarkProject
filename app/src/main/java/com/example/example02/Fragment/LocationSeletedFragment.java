package com.example.example02.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.example02.Activity.WritingActivity;
import com.example.example02.R;


public class LocationSeletedFragment extends Fragment {
    private static final String TAG = "LocationSeletedFragment";

    WritingActivity activity;
    private LocationSeletedChildFragment LSFC;

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (WritingActivity) getActivity();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_location_seleted , container, false);

        return rootView;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LSFC = new LocationSeletedChildFragment();

        ImageView Image = getView().findViewById(R.id.Location_selection02);
        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onActivityCreated");
                insertNestedFragment();
            }
        });
    }

    private void insertNestedFragment(){
        getChildFragmentManager().beginTransaction().replace(R.id.container02, LSFC).commit();

        Bundle arguments = getArguments();
        LSFC.setArguments(arguments);
    }
}
