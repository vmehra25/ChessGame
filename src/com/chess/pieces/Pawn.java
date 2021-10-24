package com.chess.pieces;

import com.chess.PieceType;
import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.chess.board.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece{

    private final static int[] POSSIBLE_MOVES = {8, 16, 7, 9};

    public Pawn(int piecePosition, PieceType pieceType) {
        super(piecePosition, pieceType, PieceConfig.PAWN, true);
    }

    public Pawn(int piecePosition, PieceType pieceType, boolean isFirstMove) {
        super(piecePosition, pieceType, PieceConfig.PAWN, isFirstMove);
    }

    @Override
    public List<Move> calcLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for(final int move: POSSIBLE_MOVES){
            int destination = this.pieceType.getDirection() * move + this.piecePosition;
            if(!BoardUtils.isValidTilePosition(destination)){
                continue;
            }
            if(move == 8 && !board.getTile(destination).isTileOccupied()){
                if(this.pieceType.isPawnPromotionSquare(destination)){
                    legalMoves.add(new Move.PawnPromotion(new Move.PawnMove(board, this, destination)));
                }else{
                    legalMoves.add(new Move.PawnMove(board, this, destination));
                }
            } else if(move == 16 && this.isFirstMove() &&
                    ( (BoardUtils.SEVENTH_RANK[this.piecePosition] && this.getPieceType().isBlack()) ||
                    (BoardUtils.SECOND_RANK[this.piecePosition] && this.getPieceType().isWhite()) )
            ){
                int positionBehind = this.pieceType.getDirection() * 8 + this.piecePosition;
                if(!board.getTile(positionBehind).isTileOccupied() && !board.getTile(destination).isTileOccupied()){
                    legalMoves.add(new Move.PawnJump(board, this, destination));
                }
            }else if(move == 7 &&
                !( (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceType.isWhite())
                    || (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceType.isBlack()) )
            ){
                if(board.getTile(destination).isTileOccupied()){
                    final Piece destinationPiece = board.getTile(destination).getPiece();
                    if(this.pieceType != destinationPiece.pieceType){
                        if(this.pieceType.isPawnPromotionSquare(destination)){
                            legalMoves.add(new Move.PawnPromotion(new Move.PawnAttackMove(board, this, destination, destinationPiece)));
                        }else {
                            legalMoves.add(new Move.PawnAttackMove(board, this, destination, destinationPiece));
                        }
                    }
                }else if(board.getEnPassantPawn() != null){
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceType.getOppositeDirection()))){
                        final Piece pieceOnCand = board.getEnPassantPawn();
                        if(this.pieceType != pieceOnCand.getPieceType()){
                            legalMoves.add(new Move.PawnEnPassantAttackMove(board, this, destination, pieceOnCand));
                        }
                    }
                }
            }else if(move == 9 &&
                    !( (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceType.isWhite()) ||
                    (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceType.isBlack()) )
            ){
                if(board.getTile(destination).isTileOccupied()){
                    final Piece destinationPiece = board.getTile(destination).getPiece();
                    if(this.pieceType != destinationPiece.pieceType){
                        if(this.pieceType.isPawnPromotionSquare(destination)){
                            legalMoves.add(new Move.PawnPromotion(new Move.PawnAttackMove(board, this, destination, destinationPiece)));
                        }else {
                            legalMoves.add(new Move.PawnAttackMove(board, this, destination, destinationPiece));
                        }
                    }
                } else if(board.getEnPassantPawn() != null){
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceType.getOppositeDirection()))){
                        final Piece pieceOnCand = board.getEnPassantPawn();
                        if(this.pieceType != pieceOnCand.getPieceType()){
                            legalMoves.add(new Move.PawnEnPassantAttackMove(board, this, destination, pieceOnCand));
                        }
                    }
                }

            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationPosition(), move.getMovedPiece().getPieceType());
    }

    @Override
    public String toString(){
        return PieceConfig.PAWN.toString();
    }

    public Piece getPromotionPiece() {
        return new Queen(this.getPiecePosition(), this.getPieceType(), false);
    }
}
