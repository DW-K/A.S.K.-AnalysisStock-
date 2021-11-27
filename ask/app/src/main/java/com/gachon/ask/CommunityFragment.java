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


        return root;
    }

    View.OnClickListener onClickListener = v -> {

        switch (v.getId()){
            case R.id.btn_community_car:
                startListActivity(ListActivity.class);
                break;
        }
    };

    private void startListActivity(Class c){
        Intent intent = new Intent(getActivity(),c);
        startActivity(intent);
    }
}
