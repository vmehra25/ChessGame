package com.chess.board;

import com.chess.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.chess.board.BoardUtils.NUM_TILES;

public abstract class ChessTile {
    protected final int tileCoordinate;
    private static final Map<Integer, EmptyTile> EMPTY_TILE_MAP = createAllEmptyTiles();

    private static Map<Integer, EmptyTile> createAllEmptyTiles() {
        Map<Integer, EmptyTile> emptyTileMap = new HashMap<Integer, EmptyTile>();
        for(int i = 0 ; i < NUM_TILES ; i++){
            emptyTileMap.put(i, new EmptyTile(i));
        }
        return Collections.unmodifiableMap(emptyTileMap);
//        return ImmutableMap.copyOf(emptyTileMap);
    }

    public int getTileCoordinate(){
        return this.tileCoordinate;
    }

    public static ChessTile createChessTile(final int tileCoordinate, final Piece piece){
        if(piece == null){
            return EMPTY_TILE_MAP.get(tileCoordinate);
        }else{
            return new OccupiedTile(tileCoordinate, piece);
        }
    }

    private ChessTile(int tileCoordinate){
        this.tileCoordinate = tileCoordinate;
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public static final class EmptyTile extends ChessTile{
        EmptyTile(int coordinate){
            super(coordinate);
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString(){
            return "-";
        }
    }


    public static final class OccupiedTile extends ChessTile{

        private final Piece pieceOnTile;

        OccupiedTile(int tileCoordinate, Piece pieceOnTile){
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return pieceOnTile;
        }

        @Override
        public String toString(){
            return this.pieceOnTile.getPieceType().isWhite() ?
                    this.pieceOnTile.toString().toLowerCase() : this.pieceOnTile.toString();
        }
    }




}
