package com.example.example02.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.example02.Activity.MapActivity;
import com.example.example02.Activity.WritingActivity;
import com.example.example02.Info.MapInfo;
import com.example.example02.R;


public class MapFragment extends Fragment {
    private MapInfo map;
    private TextView area;
    private TextView explain;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        map = (MapInfo) ((MapActivity) getActivity()).getMapInfo(((MapActivity) getActivity()).getCheckPositionNum());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);

        area = (TextView) view.findViewById(R.id.area);
        explain = (TextView) view.findViewById(R.id.explainText);

        area.setText(map.getName());
        explain.setText(map.getExplain());
        view.findViewById(R.id.searchAreaPost).setOnClickListener(onClickListener);
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.searchAreaPost:
                    ((MapActivity)getActivity()).startsearchAreaPost();
                    break;
            }
        }
    };

}
