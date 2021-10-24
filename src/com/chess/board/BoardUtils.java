package com.chess.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BoardUtils {
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN  = initColumn(1);
    public static final boolean[] THIRD_COLUMN = initColumn(2);
    public static final boolean[] FOURTH_COLUMN = initColumn(3);
    public static final boolean[] FIFTH_COLUMN = initColumn(4);
    public static final boolean[] SIXTH_COLUMN = initColumn(5);
    public static final boolean[] SEVENTH_COLUMN  = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    public static final boolean[] EIGHTH_RANK = initRow(0);
    public static final boolean[] SEVENTH_RANK = initRow(8);
    public static final boolean[] SIXTH_RANK = initRow(16);
    public static final boolean[] FIFTH_RANK = initRow(24);
    public static final boolean[] FOURTH_RANK = initRow(32);
    public static final boolean[] THIRD_RANK = initRow(40);
    public static final boolean[] SECOND_RANK = initRow(48);
    public static final boolean[] FIRST_RANK = initRow(56);

    public static final String[] ALGEBRIC_NOTATION = initAlgebricNotation();
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initPositionToCoordinateMap();

    private static Map<String, Integer> initPositionToCoordinateMap() {
        final Map<String, Integer> positionCoordinate = new HashMap<>();
        for(int i = 0 ; i < BoardUtils.NUM_TILES ; i++){
            positionCoordinate.put(ALGEBRIC_NOTATION[i], i);
        }
        return Collections.unmodifiableMap(positionCoordinate);
    }

    private static String[] initAlgebricNotation() {
        String[] finalArray = new String[BoardUtils.NUM_TILES];
        int[] num = {8, 7, 6, 5, 4, 3, 2, 1};
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        int index = 0;
        for(int n: num){
            for(String letter: letters){
                finalArray[index] = letter + n;
                index++;
            }
        }
        return finalArray;
    }

    private static boolean[] initRow(int rowNum){
        final boolean[] rows = new boolean[NUM_TILES];
        do{
            rows[rowNum] = true;
            rowNum++;
        }while(rowNum % NUM_TILES_PER_ROW != 0);
        return rows;
    }


    private static boolean[] initColumn(int col) {
        final boolean[] column = new boolean[64];
        do{
            column[col] = true;
            col += 8;
        }while (col < 64);
        return column;
    }

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    public static boolean isValidTilePosition(int position) {
        return position >= 0 && position < NUM_TILES;
    }

    private BoardUtils(){
        throw new RuntimeException("Cannot instantiate this");
    }

    public static int getCoordinateAtPosition(final String position){
        return POSITION_TO_COORDINATE.get(position);
    }

    public static String getPositionAtCoordinate(final int coordinate) {
        return ALGEBRIC_NOTATION[coordinate];
    }
}
