package com.chess.pieces;

import com.chess.PieceType;
import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.chess.board.ChessTile;
import com.chess.board.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bishop extends Piece{
    private final static int[] POSSIBLE_MOVES = {-9, -7, 7, 9};

    public Bishop(int piecePosition, PieceType pieceType) {
        super(piecePosition, pieceType, PieceConfig.BISHOP, true);
    }

    public Bishop(int piecePosition, PieceType pieceType, boolean isFirstMove) {
        super(piecePosition, pieceType, PieceConfig.BISHOP, isFirstMove);
    }

    @Override
    public List<Move> calcLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for(final int move: POSSIBLE_MOVES){
            int destination = this.piecePosition;
            while(BoardUtils.isValidTilePosition(destination + move)){
                if(isFirstColumnExclusion(destination, move) || isLastColumnExclusion(destination, move)){
                    break;
                }
                destination += move;
                if(BoardUtils.isValidTilePosition(destination)){
                    final ChessTile destinationTile = board.getTile(destination);
                    if(!destinationTile.isTileOccupied()) {
                        legalMoves.add(new Move.NormalMove(board, this, destination));
                    }else{
                        final Piece piece = destinationTile.getPiece();
                        final PieceType pieceType = piece.getPieceType();

                        if(this.pieceType != pieceType){
                            legalMoves.add(new Move.NormalAttackMove(board, this, destination, piece));
                        }
                        break;
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getDestinationPosition(), move.getMovedPiece().getPieceType());
    }

    @Override
    public String toString(){
        return PieceConfig.BISHOP.toString();
    }

    private static boolean isFirstColumnExclusion(final int position, final int offset){
        return BoardUtils.FIRST_COLUMN[position] &&
                ((offset == -9) || (offset == 7));
    }

    private  static boolean isLastColumnExclusion(final int position, final int offset){
        return BoardUtils.EIGHTH_COLUMN[position] &&
                ((offset == -7) || (offset == 9));
    }

}






