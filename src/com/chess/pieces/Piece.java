package com.chess.pieces;

import com.chess.PieceType;
import com.chess.board.Board;
import com.chess.board.Move;

import java.util.List;

public abstract class Piece {
    protected final int piecePosition;
    protected final PieceType pieceType;
    protected final boolean firstMove;
    protected final PieceConfig pieceConfig;
    private int cachedHashedCode;

    Piece(final int piecePosition, final PieceType pieceType, final PieceConfig pieceConfig, final boolean isFirstMove){
        this.piecePosition = piecePosition;
        this.pieceType = pieceType;
        this.firstMove = isFirstMove;
        this.pieceConfig = pieceConfig;
        this.cachedHashedCode = computeHashCode();
    }

    private int computeHashCode() {
        int result = pieceConfig.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove() ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }
        if(!(other instanceof Piece)){
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return this.pieceType == otherPiece.getPieceType()
                && this.isFirstMove() == otherPiece.isFirstMove()
                && this.piecePosition == otherPiece.getPiecePosition()
                && this.pieceConfig == otherPiece.getPieceConfig();
    }

    @Override
    public int hashCode(){
        return this.cachedHashedCode;
    }

    public PieceConfig getPieceConfig() {
        return pieceConfig;
    }

    public abstract List<Move> calcLegalMoves(final Board board);

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public boolean isFirstMove(){
        return this.firstMove;
    }

    public boolean isKing() { return false;}


    public Integer getPiecePosition(){
        return this.piecePosition;
    }

    public abstract Piece movePiece(Move move);

    public enum PieceConfig{
        PAWN("P", 100),
        KNIGHT("N", 300),
        BISHOP("B", 300),
        ROOK("R", 500){
            @Override
            public boolean isRook(){
                return true;
            }
        },
        QUEEN("Q", 900),
        KING("K", 10000);

        private final String pieceName;
        private int pieceValue;

        PieceConfig(final String pieceName, final int pieceValue){
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        public String getPieceName() {
            return this.pieceName;
        }

        public int getPieceValue() {
            return this.pieceValue;
        }

        @Override
        public String toString(){
            return this.pieceName;
        }

        public  boolean isRook(){
            return false;
        }
    }
}
