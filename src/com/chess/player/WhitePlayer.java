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

public class WhitePlayer extends Player{
    public WhitePlayer(Board board,
                       List<Move> whiteMoves, List<Move> blackMoves) {
        super(board, whiteMoves, blackMoves);

    }

    @Override
    public List<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.getBlackPlayer();
    }

    @Override
    protected List<Move> calculateKingCastles(final List<Move> playerLegalMoves, final List<Move> opponentLegalMoves) {
        final List<Move> kingCastles = new ArrayList<>();
        if(this.playerKing.isFirstMove() && !this.isInChecked()){
            if(!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()){
                final ChessTile rookTile = this.board.getTile(63);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){
                    if(Player.calculateAttacksOnTile(61, opponentLegalMoves).isEmpty()
                     && Player.calculateAttacksOnTile(62, opponentLegalMoves).isEmpty() &&
                     rookTile.getPiece().getPieceConfig().isRook()){
                        kingCastles.add(new Move.KingSideCastleMove(
                                this.board,
                                this.playerKing,
                                62,
                                (Rook)rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                61
                        ));
                    }

                }
            }
            if(!this.board.getTile(59).isTileOccupied()
                    && !this.board.getTile(58).isTileOccupied()
                    && !this.board.getTile(57).isTileOccupied()){
                final ChessTile rookTile = this.board.getTile(56);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()
                 && Player.calculateAttacksOnTile(59, opponentLegalMoves).isEmpty()
                 && Player.calculateAttacksOnTile(58, opponentLegalMoves).isEmpty()
                 && rookTile.getPiece().getPieceConfig().isRook()){
                    // TODO
                    kingCastles.add(new Move.QueenSideCastleMove(
                            this.board,
                            this.playerKing,
                            58,
                            (Rook)rookTile.getPiece(),
                            rookTile.getTileCoordinate(),
                            59
                    ));
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }
}
