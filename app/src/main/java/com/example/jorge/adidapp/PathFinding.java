package com.example.jorge.adidapp;

import java.util.ArrayDeque;

import GridNav.GridNav;
import GridNav.Vertex;
import GridNav.Options;

/**
 * Created by jorge on 11/03/2018.
 */

public class PathFinding {
    private GridNav gn;

    PathFinding(Mapa map) {
        gn = new GridNav();
        char[][] matrix = new char[map.x][map.y];
        for (int i=0; i<map.x; i++){
            for(int j=0; j<map.y; j++){
                if (map.mapa[i][j] >= 0) {
                    matrix[i][j] = '.';
                } else {
                    matrix[i][j] = 'x';
                }
                //matrix[i][j] = (char)map.mapa[i][j];
            }
        }

        gn.loadCharMatrix(matrix);
    }


    ArrayDeque<Vertex> search(int[] start, int[] goal) {
        return gn.route(start, goal, Options.ASTAR, Options.MANHATTAN_HEURISTIC, false);
    }
}
