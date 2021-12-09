package com.gachon.ask.base;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.gachon.ask.util.Auth;


public abstract class BaseActivity<B extends ViewBinding> extends AppCompatActivity {
    protected B binding;
    protected abstract B getBinding();

    protected void initBinding() {
        binding = getBinding();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("BaseActivity", "onCreate");
        this.initBinding();
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        Log.d("BaseActivity", "onStart @ " + this.getClass().getSimpleName());
        super.onStart();
    }
}