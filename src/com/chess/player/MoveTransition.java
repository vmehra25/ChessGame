package com.chess.player;

import com.chess.board.Board;
import com.chess.board.Move;

import java.util.concurrent.Future;

public class MoveTransition {
    private final Board transitionBoard;
    private final Move move;
    private final MoveStatus moveStatus;


    public MoveTransition(Board transitionBoard, Move move, MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMovesStatus() {
        return this.moveStatus;
    }

    public Board getBoard() {
        return this.transitionBoard;
    }
}
