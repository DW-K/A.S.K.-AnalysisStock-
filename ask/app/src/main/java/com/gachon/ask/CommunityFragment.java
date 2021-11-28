package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.gachon.ask.community.ListActivity;
import com.gachon.ask.community.ListDetailActivity;

public class CommunityFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_community, container, false);


        root.findViewById(R.id.btn_community_car).setOnClickListener(onClickListener);
        root.findViewById(R.id.btn_community_entertainment).setOnClickListener(onClickListener);
        root.findViewById(R.id.btn_community_it).setOnClickListener(onClickListener);
        root.findViewById(R.id.btn_community_electronics).setOnClickListener(onClickListener);


        return root;
    }

    String category;
    View.OnClickListener onClickListener = v -> {

        Intent intent;
        switch (v.getId()){
            case R.id.btn_community_car:
                category = getString(R.string.category_car);
                intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
                break;

            case R.id.btn_community_entertainment:
                category = getString(R.string.category_entertainment);
                intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
                break;

            case R.id.btn_community_it:
                category = getString(R.string.category_it);
                intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
                break;

            case R.id.btn_community_electronics:
                category = getString(R.string.category_electronics);
                intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
                break;
        }
    };

    private void startListActivity(Class c){
        Intent intent = new Intent(getActivity(),c);
        startActivity(intent);
    }
}
