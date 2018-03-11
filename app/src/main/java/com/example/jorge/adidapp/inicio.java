package com.example.jorge.adidapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import GridNav.Vertex;

/**
 * Created by jorge on 10/03/2018.
 */

public class inicio extends AppCompatActivity {

    private IndoorAplication odometry;
    private Canvas canvas;
    private Bitmap bitmap;
    Tag myTag;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;

    private Mapa map;
    private PathFinding pathFinding;

    boolean writeMode;

    private static final int ACTIVITY_CREATE=0;

    static final String[] numbers = new String[] {
            "aaa", "sss", "ddd"};

    HashMap<String,Integer> numbers2=new HashMap<String, Integer>();
    HashMap<String,Integer> numbers3=new HashMap<String, Integer>();


    private Activity mine;
    GridView gridView;

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;

    IntentFilter writeTagFilters[];

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        //requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mine = this;
        RequestQueue queue = Volley.newRequestQueue(mine);
        String url ="http://j3m-backend.herokuapp.com/getMap?id=1";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject mainObject = null;

                        try {
                            mainObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        map=new Mapa(mainObject);
                        pathFinding = new PathFinding(map);


                        //CANVAS
                        ImageView imageView=(ImageView) findViewById(R.id.imageView);
                        bitmap = Bitmap.createBitmap(2300, 2000, Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(bitmap);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.GRAY);
                        //canvas.drawCircl(50, 50, 10, paint);
                        canvas.drawColor(Color.WHITE);

                        map.dibujar(canvas);
                        imageView.setImageBitmap(bitmap);

                        odometry = new IndoorAplication();
                        odometry.start("shop-test-3", getApplicationContext(), new MyCallback(){
                            public void execute(double x, double y) {
                                updatePos(x, y);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mine, "Error recuperando mapa", 5).show();
                        Log.d("VOLLEY", error.toString());
                    }
        });
        queue.add(stringRequest);


        //GRID VIEW
        gridView = (GridView) findViewById(R.id.gridview);
        //rellenarGrid();

        registerForContextMenu(gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {


                String item = ((TextView)view).getText().toString();

                Log.d("ppp", item);



                RequestQueue queue = Volley.newRequestQueue(mine);
                String url ="http://j3m-backend.herokuapp.com/getProductInfo?id="+numbers2.get(item).toString();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                JSONObject mainObject = null;

                                try {
                                    mainObject = new JSONObject(response);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String titulo= null;
                                try {
                                    titulo = mainObject.getString("name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String url= null;
                                try {
                                    url = mainObject.getString("image");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String desc= null;
                                try {
                                    desc = mainObject.getString("description");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.d("Prueba", titulo);
                                mostrarProducto2(titulo,url,desc);


                            }
                        }, new Response.ErrorListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(mine, "Error recuperando mapa", 5).show();
                        Log.d("VOLLEY", error.toString());
                    }
                });
                queue.add(stringRequest);




            }
        });


        //NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }
        readFromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };

        //Recomendaciones
        //final Activity mine = this;
        queue = Volley.newRequestQueue(mine);
        url ="http://j3m-backend.herokuapp.com/getRecomendations?name=Turtle";
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONArray mainObject = null;

                        try {
                            mainObject = new JSONArray(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        int o=0;
                        String titulo=null;
                        int id=-1;
                        int zona = -1;

                        while(o<3){
                            JSONObject este=null;
                            try {
                                este = (JSONObject) mainObject.get(o);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            try {
                                titulo = este.getString("name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            try {
                                id = este.getInt("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                zona = este.getInt("zone");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d("Prueba", titulo);
                            numbers[o]=titulo;
                            o++;
                            numbers2.put(titulo,id);
                            numbers3.put(titulo,zona);

                        }

                       rellenarGrid2();


                    }
                }, new Response.ErrorListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mine, "Error recuperando mapa", 5).show();
                Log.d("VOLLEY", error.toString());
            }
        });
        queue.add(stringRequest2);


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

            menu.add(Menu.NONE, 0, Menu.NONE, "Llévame!");



    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case 0:
                //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();



                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String key = ((TextView) info.targetView).getText().toString();

                Log.d("manteniendo", key);

                int idZone = this.numbers3.get(key);
                int[] goal = map.getZone(idZone);
                this.searchPath(goal[1], goal[0], idZone);

                return true;

        }
        return super.onContextItemSelected(item);
    }



    private void readFromIntent(Intent intent) {
        Log.d("NFC", "Detectado");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);

        }

    }
    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0){
            mostrarProducto();
        }
        else{
            String text = "";
            //String tagId = new String(msgs[0].getRecords()[0].getType());
            byte[] payload = msgs[0].getRecords()[0].getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
            int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

            try {
                // Get the Text
                text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.toString());
            }
            //llamada api
            final Activity mine = this;
            RequestQueue queue = Volley.newRequestQueue(mine);
            String url ="http://j3m-backend.herokuapp.com/getProductInfo?id="+text;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject mainObject = null;

                            try {
                                mainObject = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String titulo= null;
                            try {
                                titulo = mainObject.getString("name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String url= null;
                            try {
                                url = mainObject.getString("image");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String desc= null;
                            try {
                                desc = mainObject.getString("description");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d("Prueba", titulo);
                            mostrarProducto2(titulo,url,desc);


                        }
                    }, new Response.ErrorListener() {
                @SuppressLint("WrongConstant")
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mine, "Error recuperando mapa", 5).show();
                    Log.d("VOLLEY", error.toString());
                }
            });
            queue.add(stringRequest);


        }


    }

    public void rellenarGrid2(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, numbers);

        gridView.setAdapter(adapter);
    }

    public void rellenarGrid(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, numbers);

        gridView.setAdapter(adapter);
    }

    public void updatePos(double x, double y) {
        //Logger.getLogger("MAP").log(Level.INFO, "X :" + x + " Y : " + y);
        Log.d("POSITION", "X :" + x + " Y : " + y);
        map.updatePos((int)Math.round(x), (int)Math.round(y), canvas);
        findViewById(R.id.imageView).invalidate();
    }

    public void searchPath(int newX, int newY, int zone) {
        int[] start = {this.map.yPos, this.map.xPos};
        int[] goal = {newX, newY};

        ArrayDeque<Vertex> vertexs = pathFinding.search(start, goal);
        ArrayList<Integer> xPath = new ArrayList<>();
        ArrayList<Integer> yPath = new ArrayList<>();

        while(!vertexs.isEmpty()) {
            Vertex v = vertexs.pop();
            xPath.add(v.getX());
            yPath.add(v.getY());
        }

        map.setPath(xPath, yPath, zone);
        map.dibujar(canvas);
    }

    public void onDestroy() {
        super.onDestroy();
        odometry.end();
    }






    protected void mostrarProducto() {
        Intent i = new Intent(this, product.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }





    protected void mostrarProducto2(String titulo,String foto,String desc ) {
        Intent i = new Intent(this, product.class);
        i.putExtra("foto",foto);
        i.putExtra("titulo",titulo);
        i.putExtra("desc",desc);
        startActivityForResult(i, ACTIVITY_CREATE);
    }



    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        readFromIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();

    }
    @Override
    protected void onPause() {
        WriteModeOff();
        super.onPause();
        saveState();
    }
    @Override
    protected void onResume() {
        WriteModeOn();

        super.onResume();
    }

    private void saveState() {

    }




    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
    
    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

}
