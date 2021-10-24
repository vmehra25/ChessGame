package com.chess.board;

import com.chess.PieceType;
import com.chess.pieces.Pawn;
import com.chess.pieces.Piece;
import com.chess.pieces.Rook;

public abstract class Move {
    final Board board;
    final Piece movedPiece;
    final int destinationPosition;
    public static final Move NULL_MOVE = new NullMove();
    protected final boolean isFirstMove;

    private Move(Board board, Piece movedPiece, int destinationPosition) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationPosition = destinationPosition;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(Board board, int destinationPosition){
        this.board = board;
        this.destinationPosition = destinationPosition;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public int getDestinationPosition(){
        return this.destinationPosition;
    }

    public int getCurrentPosition(){
        return this.getMovedPiece().getPiecePosition();
    }

    public Board getBoard(){
        return this.board;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = result * prime + this.getDestinationPosition();
        result = result * prime + this.movedPiece.hashCode();
        result = result * prime + this.movedPiece.getPiecePosition();
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }
        if(other instanceof Move){
            final Move otherMove = (Move) other;
            return  this.getDestinationPosition() == otherMove.getDestinationPosition()
                    && getMovedPiece().equals(otherMove.getMovedPiece())
                    && getCurrentPosition() == otherMove.getCurrentPosition();
        }
        return false;
    }

    public Board execute() {
        final Board.Builder builder = new Board.Builder();
        for(final Piece piece: this.board.currentPlayer().getActivePieces()){
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
            }
        }
        for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setPieceType(this.board.currentPlayer().getOpponent().getPieceType());
        return builder.build();
    }

    public boolean isAttack(){
        return false;
    }

    public boolean isCastlingMove(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    public static final class NormalMove extends Move{

        public NormalMove(Board board,
                          Piece movedPiece,
                          int destinationPosition) {
            super(board, movedPiece, destinationPosition);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || (other instanceof NormalMove && super.equals(other));
        }

        @Override
        public String toString(){
            return movedPiece.getPieceConfig().toString() + BoardUtils.getPositionAtCoordinate(this.destinationPosition);
        }
    }

    public static class AttackMove extends Move{
        final Piece attackedPiece;
        public AttackMove(Board board,
                          Piece movedPiece,
                          int destinationPosition,
                          Piece attackedPiece) {
            super(board, movedPiece, destinationPosition);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other){
            if(other == this){
                return true;
            }
            if(!(other instanceof AttackMove)){
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public boolean isAttack(){
            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }
    }

    public static class NormalAttackMove extends AttackMove{

        public NormalAttackMove(Board board, Piece movedPiece, int destinationPosition, Piece attackedPiece) {
            super(board, movedPiece, destinationPosition, attackedPiece);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof NormalAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceConfig() + BoardUtils.getPositionAtCoordinate(this.destinationPosition);
        }
    }

    public static final class PawnMove extends Move{

        public PawnMove(Board board, Piece movedPiece, int destinationPosition) {
            super(board, movedPiece, destinationPosition);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || (other instanceof PawnMove && super.equals(other));
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationPosition);
        }
    }

    public static class PawnAttackMove extends AttackMove{

        public PawnAttackMove(Board board, Piece movedPiece, int destinationPosition, Piece attackedPiece) {
            super(board, movedPiece, destinationPosition, attackedPiece);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1) + "x"
                    + BoardUtils.getPositionAtCoordinate(this.destinationPosition);
        }
    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove{

        public PawnEnPassantAttackMove(Board board, Piece movedPiece, int destinationPosition, Piece attackedPiece) {
            super(board, movedPiece, destinationPosition, attackedPiece);
        }

        @Override
        public boolean equals(Object other) {
            return other == this || other instanceof  PawnEnPassantAttackMove && super.equals(other);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece: this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
                if(!this.attackedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPieceType(this.board.currentPlayer().getOpponent().getPieceType());
            return builder.build();
        }
    }

    public static final class PawnJump extends Move{

        public PawnJump(Board board, Piece movedPiece, int destinationPosition) {
            super(board, movedPiece, destinationPosition);
        }

        @Override
        public Board execute(){
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece: this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setPieceType(this.board.currentPlayer().getOpponent().getPieceType());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationPosition);
        }
    }

    public static abstract class CastleMove extends Move{
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(final Board board,
                          final Piece movedPiece,
                          final int destinationPosition,
                          final Rook castleRook,
                          final int castleRookStart,
                          final int castleRookDestination
        ) {
            super(board, movedPiece, destinationPosition);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook(){
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove(){
            return true;
        }

        @Override
        public Board execute(){
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece: this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            // TODO
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceType()));
            builder.setPieceType(this.board.currentPlayer().getOpponent().getPieceType());
            return builder.build();
        }

        @Override
        public int hashCode(){
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other){
            if(this == other){
                return true;
            }
            if(!(other instanceof CastleMove)){
                return false;
            }
            final CastleMove castleMove = (CastleMove) other;
            return super.equals(castleMove) && this.castleRook.equals(castleMove.getCastleRook());
        }
    }

    public static final class KingSideCastleMove extends  CastleMove{

        public KingSideCastleMove(Board board,
                                  Piece movedPiece,
                                  int destinationPosition,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, movedPiece, destinationPosition, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString(){
            return "O-O";
        }

        @Override
        public boolean equals(Object other){
            return this == other || (other instanceof KingSideCastleMove && super.equals(other));
        }
    }

    public static final class QueenSideCastleMove extends CastleMove{

        public QueenSideCastleMove(Board board,
                                   Piece movedPiece,
                                   int destinationPosition,
                                   final Rook castleRook,
                                   final int castleRookStart,
                                   final int castleRookDestination) {
            super(board, movedPiece, destinationPosition, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString(){
            return "O-O-O";
        }

        @Override
        public boolean equals(Object other){
            return this == other || (other instanceof QueenSideCastleMove && super.equals(other));
        }
    }

    public static class PawnPromotion extends Move{

        final Move currentMove;
        final Pawn promotedPawn;

        public PawnPromotion(final Move currentMove){
            super(currentMove.getBoard(), currentMove.getMovedPiece(), currentMove.getDestinationPosition());
            this.currentMove = currentMove;
            this.promotedPawn = (Pawn) currentMove.getMovedPiece();
        }

        @Override
        public Board execute() {
            final Board pawnMovedBoard = this.currentMove.execute();
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece: pawnMovedBoard.currentPlayer().getActivePieces()){
                if(!this.promotedPawn.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece: pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            PieceType nextChance = this.promotedPawn.getPieceType().isWhite() ? PieceType.BLACK : PieceType.WHITE;
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setPieceType(nextChance);
            return builder.build();
        }

        @Override
        public int hashCode(){
            return currentMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other){
            return this == other || (other instanceof PawnPromotion && super.equals(other));
        }

        @Override
        public boolean isAttack() {
            return this.currentMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return this.currentMove.getAttackedPiece();
        }

        @Override
        public String toString(){
            return "";
        }
    }

    public static final class  NullMove extends Move{

        public NullMove() {
            super(null, 65);
        }

        @Override
        public Board execute(){
            throw new RuntimeException("Cannot execute null move");
        }

        @Override
        public int getCurrentPosition() {
            return -1;
        }
    }


    public static class MoveFactory{
        private MoveFactory(){
            throw new RuntimeException("Cannot instantiate");
        }

        public static Move createMove(final Board board, final int currentPosition, final int destinationPosition){
            for(final Move move: board.getAllLegalMoves()){
                if(move.getCurrentPosition() == currentPosition
                && move.getDestinationPosition() == destinationPosition){
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }



}
