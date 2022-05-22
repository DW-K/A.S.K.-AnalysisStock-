package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.gachon.ask.community.CommunityCategoryActivity;

public class CommunityFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_community, container, false);

        root.findViewById(R.id.btn_community_car).setOnClickListener(onClickListener);
        root.findViewById(R.id.btn_community_entertainment).setOnClickListener(onClickListener);
        root.findViewById(R.id.btn_community_it).setOnClickListener(onClickListener);
        root.findViewById(R.id.btn_community_electronics).setOnClickListener(onClickListener);
        root.findViewById(R.id.btn_community_univ).setOnClickListener(onClickListener);
        root.findViewById(R.id.btn_community_ask).setOnClickListener(onClickListener);

        return root;
    }

    String selected_category;
    View.OnClickListener onClickListener = v -> {

        Intent intent;
        switch (v.getId()){

            case R.id.btn_community_car:
                selected_category = getString(R.string.category_car);
                break;

            case R.id.btn_community_entertainment:
                selected_category = getString(R.string.category_entertainment);
                break;

            case R.id.btn_community_it:
                selected_category = getString(R.string.category_it);
                break;

            case R.id.btn_community_electronics:
                selected_category = getString(R.string.category_electronics);
                break;

            case R.id.btn_community_univ:
                selected_category = getString(R.string.gachon);
                break;

            case R.id.btn_community_ask:
                selected_category = getString(R.string.category_ask);
                break;
        }

        intent = new Intent(getActivity(), CommunityCategoryActivity.class);
        intent.putExtra("selected_category", selected_category);
        startActivity(intent);
    };

}
