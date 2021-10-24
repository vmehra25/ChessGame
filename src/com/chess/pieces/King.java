package com.chess.pieces;

import com.chess.PieceType;
import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.chess.board.ChessTile;
import com.chess.board.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class King extends Piece{

    private final static int[] POSSIBLE_MOVES = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(int piecePosition, PieceType pieceType) {
        super(piecePosition, pieceType, PieceConfig.KING, true);
    }

    public King(int piecePosition, PieceType pieceType, boolean isFirstMove) {
        super(piecePosition, pieceType, PieceConfig.KING, isFirstMove);
    }

    @Override
    public List<Move> calcLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for(final int move: POSSIBLE_MOVES){
            if(isFirstColumnExclusion(this.piecePosition, move)
                    || isLastColumnExclusion(this.piecePosition, move)){

            }
            final int destination = this.piecePosition + move;
            if(BoardUtils.isValidTilePosition(destination)){
                final ChessTile destinationTile = board.getTile(destination);
                if(destinationTile.isTileOccupied()){
                    final Piece pieceAtDest = destinationTile.getPiece();
                    final PieceType pieceType = pieceAtDest.getPieceType();
                    if(this.pieceType != pieceType){
                        legalMoves.add(new Move.NormalAttackMove(board, this, destination, pieceAtDest));
                    }
                }else{
                    legalMoves.add(new Move.NormalMove(board, this, destination));
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public boolean isKing(){
        return true;
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationPosition(), move.getMovedPiece().getPieceType());
    }

    @Override
    public String toString(){
        return PieceConfig.KING.toString();
    }

    private boolean isLastColumnExclusion(int position, int offset) {
        return BoardUtils.EIGHTH_COLUMN[position] &&
                ((offset == -7) || (offset == 1) || (offset == 9));
    }

    private boolean isFirstColumnExclusion(int position, int offset) {
        return BoardUtils.FIRST_COLUMN[position] &&
                ((offset == -1) || (offset == -9) || (offset == 7));
    }
}
