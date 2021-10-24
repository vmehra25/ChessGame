package com.chess.player;

import com.chess.PieceType;
import com.chess.board.Board;
import com.chess.board.Move;
import com.chess.pieces.King;
import com.chess.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final List<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board, List<Move> legalMoves, List<Move> opponentMoves){
        this.board = board;
        this.playerKing = setKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    protected static List<Move> calculateAttacksOnTile(int piecePosition, List<Move> opponentMoves) {
        final List<Move> attackMoves = new ArrayList<>();
        for(Move move: opponentMoves){
            if(piecePosition == move.getDestinationPosition()){
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    public King setKing(){
        for(final Piece piece: getActivePieces()){
            if(piece.isKing()){
                return (King)piece;
            }
        }
        throw new RuntimeException("Invalid board!");
    }

    public abstract List<Piece> getActivePieces();

    public abstract PieceType getPieceType();

    public abstract Player getOpponent();

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    private boolean hasEscapeMoves(){
        for(final Move move: legalMoves){
            final MoveTransition transition = makeMove(move);
            if(transition.getMovesStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    public List<Move> getLegalMoves() {
        return this.legalMoves;
    }

    // TODO
    public boolean isInChecked(){
        return this.isInCheck;
    }

    public boolean isInCheckMate(){
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate(){
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled(){
        return false;
    }

    public MoveTransition makeMove(final Move move){
        if(!isMoveLegal(move)){
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionBoard = move.execute();
        final List<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().
                        getOpponent().getPlayerKing().getPiecePosition(),
                    transitionBoard.currentPlayer().getLegalMoves());

        if(kingAttacks != null && !kingAttacks.isEmpty()){
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    protected abstract List<Move> calculateKingCastles(List<Move> playerLegalMoves, List<Move> opponentLegalMoves);

}
