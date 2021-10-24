package com.chess.player;

import com.chess.PieceType;
import com.chess.board.Board;
import com.chess.board.ChessTile;
import com.chess.board.Move;
import com.chess.pieces.Piece;
import com.chess.pieces.Rook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlackPlayer extends Player{
    public BlackPlayer(Board board,
                       List<Move> blackMoves, List<Move> whiteMoves) {
        super(board, blackMoves, whiteMoves);

    }

    @Override
    public List<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.getWhitePlayer();
    }

    @Override
    protected List<Move> calculateKingCastles(final List<Move> playerLegalMoves, final List<Move> opponentLegalMoves) {
        final List<Move> kingCastles = new ArrayList<>();
        if(this.playerKing.isFirstMove() && !this.isInChecked()){
            if(!this.board.getTile(5).isTileOccupied()
                    && !this.board.getTile(6).isTileOccupied()){
                final ChessTile rookTile = this.board.getTile(7);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){
                    if(Player.calculateAttacksOnTile(5, opponentLegalMoves).isEmpty()
                            && Player.calculateAttacksOnTile(6, opponentLegalMoves).isEmpty() &&
                            rookTile.getPiece().getPieceConfig().isRook()){
                        kingCastles.add(new Move.KingSideCastleMove(
                                this.board,
                                this.playerKing,
                                6,
                                (Rook)rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                5
                        ));
                    }

                }
            }
            if(!this.board.getTile(1).isTileOccupied()
                    && !this.board.getTile(2).isTileOccupied()
                    && !this.board.getTile(3).isTileOccupied()){
                final ChessTile rookTile = this.board.getTile(0);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()
                 && Player.calculateAttacksOnTile(2, opponentLegalMoves).isEmpty()
                 && Player.calculateAttacksOnTile(3, opponentLegalMoves).isEmpty()
                 && rookTile.getPiece().getPieceConfig().isRook()){
                    kingCastles.add(new Move.QueenSideCastleMove(
                            this.board,
                            this.playerKing,
                            2,
                            (Rook)rookTile.getPiece(),
                            rookTile.getTileCoordinate(),
                            3
                    ));
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }
}
