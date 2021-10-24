package com.chess.pieces;

import com.chess.PieceType;
import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.chess.board.ChessTile;
import com.chess.board.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.chess.board.BoardUtils.isValidTilePosition;

public class Rook extends Piece{
    private final static int[] POSSIBLE_MOVES = {-8, 8, 1, -1};

    public Rook(int piecePosition, PieceType pieceType) {
        super(piecePosition, pieceType, PieceConfig.ROOK, true);
    }

    public Rook(int piecePosition, PieceType pieceType, boolean isFirstMove) {
        super(piecePosition, pieceType, PieceConfig.ROOK, isFirstMove);
    }


    @Override
    public List<Move> calcLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int move: POSSIBLE_MOVES){
            int destination = this.piecePosition;
            while(isValidTilePosition(destination + move)){
                if(isFirstColumnExclusion(destination, move) || isLastColumnExclusion(destination, move)){
                    break;
                }
                destination += move;
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
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Rook movePiece(Move move) {
        return new Rook(move.getDestinationPosition(), move.getMovedPiece().getPieceType());
    }


    private static boolean isFirstColumnExclusion(final int position, final int offset){
        return BoardUtils.FIRST_COLUMN[position] && (offset == -1);
    }

    private static boolean isLastColumnExclusion(final int position, final int offset){
        return BoardUtils.EIGHTH_COLUMN[position] && (offset == 1);
    }


    @Override
    public String toString(){
        return PieceConfig.ROOK.toString();
    }

}
