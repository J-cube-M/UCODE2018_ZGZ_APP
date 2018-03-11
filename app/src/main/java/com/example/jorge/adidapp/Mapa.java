package com.example.jorge.adidapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jorge on 11/03/2018.
 */

public class Mapa {


    public int [][] mapa;
    public int x;
    public int y;
    public int xPos;
    public int yPos;

    private Map<Integer, Integer> zonesX;
    private Map<Integer, Integer> zonesY;

    private ArrayList<Integer> xPath;
    private ArrayList<Integer> yPath;

    public Mapa(JSONObject mapData) {
        try {
            JSONObject mapObject = mapData.getJSONObject("map");

            mapa = new int[mapObject.getInt("maxX")][mapObject.getInt("maxY")];
            x=mapObject.getInt("maxX");
            y=mapObject.getInt("maxY");

            xPos = 3;
            yPos = 1;
            xPath = null;
            yPath = null;

            zonesX = new HashMap<Integer, Integer>();
            zonesY = new HashMap<Integer, Integer>();

            JSONArray zones = mapData.getJSONArray("zones");
            for (int i=0; i<zones.length(); i++) {
                JSONObject zone = zones.getJSONObject(i);
                int id = zone.getInt("id");
                if (!zone.has("posX")) {
                    continue;
                }
                int[] coords = {zone.getInt("posX"), zone.getInt("posY")};
                try {
                    zonesX.put(id, coords[0]);
                    zonesY.put(id, coords[1]);
                } catch (Exception ex) {
                    Log.d("a", "pepe");
                }
            }


            JSONArray cellsArray = mapData.getJSONArray("cells");

            for (int i=0;i<cellsArray.length(); i++ ) {
                mapa[cellsArray.getJSONObject(i).getInt("posX")][cellsArray.getJSONObject(i).getInt("posY")]=cellsArray.getJSONObject(i).getInt("value");
            }


        } catch (Exception e) {}


    }

    public void dibujar(Canvas canvito){
        int tamX=2300/x;
        int tamY=2000/y;
        Paint verde = new Paint(Paint.ANTI_ALIAS_FLAG);
        verde.setColor(Color.GREEN);
        Paint gris = new Paint(Paint.ANTI_ALIAS_FLAG);
        gris.setColor(Color.GRAY);
        Paint rojo = new Paint(Paint.ANTI_ALIAS_FLAG);
        rojo.setColor(Color.RED);

        for(int i=0;i<x;i++){
            for (int u=0; u<y;u++){
                if(mapa[i][u]==-1){
                    canvito.drawRect(i*tamX,u*tamY,(i*tamX)+tamX,(u*tamY)+tamY,rojo);
                }
                else if(mapa[i][u]==0){
                    canvito.drawRect(i*tamX,u*tamY,(i*tamX)+tamX,(u*tamY)+tamY,verde);
                }

                else {
                    canvito.drawRect(i * tamX, u * tamY, (i * tamX) + tamX, (u * tamY) + tamY, gris);
                }
            }

            if (xPath != null) {
                for(int k=0; k<xPath.size(); k++) {
                    int xk = xPath.get(k);
                    int yk = yPath.get(k);

                    canvito.drawRect(xk * tamX, yk*tamY, (xk * tamX) + tamX, (yk * tamY) + tamY, new Paint(Color.BLUE));
                }
            }

            canvito.drawCircle(xPos*tamX+tamX/2,yPos*tamY+tamY/2, tamX/2, new Paint(Color.BLACK));
        }



    }

    public void updatePos(int x, int y, Canvas canvas) {
        this.xPos = x;
        this.yPos = y;

        if (xPath != null) {
            int xk = xPath.get(xPath.size()-1);
            int yk = yPath.get(yPath.size()-1);

            double distance = Math.sqrt(Math.pow(xk-xPos, 2) + Math.pow(yk-yPos, 2));
            if (distance < 0.5) {
                xPath = null;
                yPath = null;
            }
        }

        this.dibujar(canvas);
    }

    public void setPath(ArrayList<Integer> xPath, ArrayList<Integer> yPath, int idZone) {
        this.xPath = xPath;
        this.yPath = yPath;
    }

    public int[] getZone(int id) {
        int[] res = {zonesX.get(id), zonesY.get(id)};
        return res;
    }



}
