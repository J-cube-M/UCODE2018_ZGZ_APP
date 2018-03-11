package com.example.jorge.adidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by jorge on 10/03/2018.
 */

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        Button login = (Button) findViewById(R.id.login);
        final EditText user= (EditText)  findViewById(R.id.usuario);
        final EditText pass= (EditText)  findViewById(R.id.passs);

        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(user.getText().toString()=="root" && pass.getText().toString()=="toor"){
                    mostrarInicio();
                }
                else{
                    mostrarInicio();
                }
            }
        });



    }


    protected void mostrarInicio() {
        Intent i = new Intent(this, inicio.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }


}
