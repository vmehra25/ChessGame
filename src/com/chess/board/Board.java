package com.chess.board;

import com.chess.PieceType;
import com.chess.pieces.*;
import com.chess.player.BlackPlayer;
import com.chess.player.Player;
import com.chess.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

public class Board {

    private final List<ChessTile> board;
    private final List<Piece> whitePieces;
    private final List<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;

    private Board(Builder builder){
        this.board = createBoard(builder);
        this.whitePieces = calculateActivePieces(this.board, PieceType.WHITE);
        this.blackPieces = calculateActivePieces(this.board, PieceType.BLACK);

        final List<Move> whiteMoves = calculateLegalMoves(this.whitePieces);
        final List<Move> blackMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteMoves, blackMoves);
        this.blackPlayer = new BlackPlayer(this, blackMoves, whiteMoves);

        this.currentPlayer = builder.nextPieceType.choosePlayer(this.whitePlayer, this.blackPlayer);
        this.enPassantPawn = builder.enPassantPawn;

    }

    private List<Move> calculateLegalMoves(List<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
        for(final Piece piece: pieces){
            legalMoves.addAll(piece.calcLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString(){
        final StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0 ; i < BoardUtils.NUM_TILES ; i++){
            final String tileText = prettyText(this.board.get(i));
            stringBuilder.append(String.format("%3s", tileText));
            if((i+1) % BoardUtils.NUM_TILES_PER_ROW == 0){
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private static String prettyText(ChessTile chessTile) {
        return chessTile.toString();
    }

    private List<Piece> calculateActivePieces(List<ChessTile> board, PieceType pieceType) {
        final List<Piece> activePieces = new ArrayList<>();
        for(final ChessTile tile: board){
            if(tile.isTileOccupied()){
                final Piece piece = tile.getPiece();
                if(piece.getPieceType() == pieceType){
                    activePieces.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }

    private static List<ChessTile> createBoard(final Builder builder){
        final ChessTile[] chessTiles = new ChessTile[BoardUtils.NUM_TILES];
        for(int i = 0 ; i < BoardUtils.NUM_TILES ; i++){
            chessTiles[i] = ChessTile.createChessTile(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(chessTiles);
    }

    public static Board createStandardBoard(){
        final Builder builder = new Builder();
        builder.setPiece(new Rook(0, PieceType.BLACK));
        builder.setPiece(new Knight(1, PieceType.BLACK));
        builder.setPiece(new Bishop(2, PieceType.BLACK));
        builder.setPiece(new Queen(3, PieceType.BLACK));
        builder.setPiece(new King(4, PieceType.BLACK));
        builder.setPiece(new Bishop(5, PieceType.BLACK));
        builder.setPiece(new Knight(6, PieceType.BLACK));
        builder.setPiece(new Rook(7, PieceType.BLACK));

        builder.setPiece(new Pawn(8, PieceType.BLACK));
        builder.setPiece(new Pawn(9, PieceType.BLACK));
        builder.setPiece(new Pawn(10, PieceType.BLACK));
        builder.setPiece(new Pawn(11, PieceType.BLACK));

        builder.setPiece(new Pawn(12, PieceType.BLACK));
        builder.setPiece(new Pawn(13, PieceType.BLACK));
        builder.setPiece(new Pawn(14, PieceType.BLACK));
        builder.setPiece(new Pawn(15, PieceType.BLACK));


        builder.setPiece(new Rook(56, PieceType.WHITE));
        builder.setPiece(new Knight(57, PieceType.WHITE));
        builder.setPiece(new Bishop(58, PieceType.WHITE));
        builder.setPiece(new Queen(59, PieceType.WHITE));
        builder.setPiece(new King(60, PieceType.WHITE));
        builder.setPiece(new Bishop(61, PieceType.WHITE));
        builder.setPiece(new Knight(62, PieceType.WHITE));
        builder.setPiece(new Rook(63, PieceType.WHITE));

        builder.setPiece(new Pawn(48, PieceType.WHITE));
        builder.setPiece(new Pawn(49, PieceType.WHITE));
        builder.setPiece(new Pawn(50, PieceType.WHITE));
        builder.setPiece(new Pawn(51, PieceType.WHITE));

        builder.setPiece(new Pawn(52, PieceType.WHITE));
        builder.setPiece(new Pawn(53, PieceType.WHITE));
        builder.setPiece(new Pawn(54, PieceType.WHITE));
        builder.setPiece(new Pawn(55, PieceType.WHITE));

        builder.setPieceType(PieceType.WHITE);

        return builder.build();

    }

    public ChessTile getTile(int position) {
        return board.get(position);
    }

    public List<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    public List<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Player getWhitePlayer() {
        return this.whitePlayer;
    }

    public Player getBlackPlayer() {
        return this.blackPlayer;
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    public Iterable<Move> getAllLegalMoves() {
       return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    public static class Builder{
        Map<Integer, Piece> boardConfig;
        PieceType nextPieceType;
        Pawn enPassantPawn;

        public Builder(){
            this.boardConfig = new HashMap<>();
        }

        public Builder setPiece(final Piece piece){
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setPieceType(final PieceType pieceType){
            this.nextPieceType = pieceType;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn pawn) {
            this.enPassantPawn = pawn;
        }
    }
}
