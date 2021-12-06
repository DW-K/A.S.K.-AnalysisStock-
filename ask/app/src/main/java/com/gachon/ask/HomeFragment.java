package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.gachon.ask.xingapi.MainView;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        Button btnInvest = (Button) view.findViewById(R.id.button_investment);
        // 모의투자 화면으로 이동!
        btnInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("HomeFragment", "Invest Button pressed.");
                Intent intent = new Intent(getActivity(), MainView.class);
                startActivity(intent);
            }
        });
        return view;
    }


}
