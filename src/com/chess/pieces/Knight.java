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

public class Knight extends Piece{
    private final static int[] POSSIBLE_MOVES = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final int piecePosition, final PieceType pieceType) {
        super(piecePosition, pieceType, PieceConfig.KNIGHT, true);
    }

    public Knight(final int piecePosition, final PieceType pieceType, boolean isFirstMove) {
        super(piecePosition, pieceType, PieceConfig.KNIGHT, isFirstMove);
    }


    @Override
    public List<Move> calcLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int move: POSSIBLE_MOVES){
            int destination = this.piecePosition + move;
            if(isValidTilePosition(destination)){
                if(isFirstColumnExclusion(this.piecePosition, move) || isLastColumnExclusion(this.piecePosition, move)
                 || isSecondColumnExclusion(this.piecePosition, move) || isSeventhColumnExclusion(this.piecePosition, move)){
                    continue;
                }
                final ChessTile destinationTile = board.getTile(destination);
                if(!destinationTile.isTileOccupied()) {
                    legalMoves.add(new Move.NormalMove(board, this, destination));
                }else{
                    final Piece piece = destinationTile.getPiece();
                    final PieceType pieceType = piece.getPieceType();

                    if(this.pieceType != pieceType){
                        legalMoves.add(new Move.NormalAttackMove(board, this, destination, piece));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getDestinationPosition(), move.getMovedPiece().getPieceType());
    }

    @Override
    public String toString(){
        return PieceConfig.KNIGHT.toString();
    }

    private static boolean isFirstColumnExclusion(final int position, final int offset){
        return BoardUtils.FIRST_COLUMN[position] &&
                ((offset == -17) || (offset == -10) || (offset == 6) || (offset == 15));
    }

    private static boolean isLastColumnExclusion(final int position, final int offset){
        return BoardUtils.EIGHTH_COLUMN[position] &&
                ((offset == -15) || (offset == -6) || (offset == 10) || (offset == 17));
    }

    private static boolean isSecondColumnExclusion(final int position, final int offset){
        return BoardUtils.SECOND_COLUMN[position] &&
                ((offset == -10) || (offset == 6));
    }

    private static boolean isSeventhColumnExclusion(final int position, final int offset){
        return BoardUtils.SEVENTH_COLUMN[position] &&
                ((offset == -6) || (offset == 10));
    }


}
