package com.example.jorge.adidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.like.LikeButton;
import com.like.OnLikeListener;

/**
 * Created by jorge on 10/03/2018.
 */

public class product extends AppCompatActivity {


    TextView titulo;
    TextView desc;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producto);

        titulo=(TextView) findViewById(R.id.textView);
        desc=(TextView) findViewById(R.id.textView2);

        final LikeButton toggle2 = (LikeButton) findViewById(R.id.tog2);

        toggle2.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });


        Bundle extras = getIntent().getExtras();
        if(extras != null) {

            String tit = extras.getString("titulo");

            String dc= extras.getString("desc");

            titulo.setText(tit);
            desc.setText(dc);

        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();

    }
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void saveState() {

    }




}
