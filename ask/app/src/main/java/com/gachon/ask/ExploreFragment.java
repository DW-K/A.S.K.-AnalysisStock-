package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.gachon.ask.community.CommunityCategoryActivity;
import com.gachon.ask.xingapi.MainView;

public class ExploreFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_explore, container, false);
        
        Button btn_senti = root.findViewById(R.id.button_sentiment);


        btn_senti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ExploreFragment", "Invest Button pressed.");

                Intent intent = new Intent(getActivity(), SentimentReportActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}
