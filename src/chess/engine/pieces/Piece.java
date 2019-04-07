package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;

import java.io.Serializable;
import java.util.Collection;

/*
The Piece class defines a chess piece, comprised of two components: a position on the board (a coordinate),
and an alliance (black or white). It's constructor takes both of these as arguments.
 */
public abstract class Piece implements Serializable{
    //Class variables to store position and alliance.
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    protected final PieceType pieceType;
    private final int cachedHashCode;

    //Constructor
    Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        System.out.println("INSIDE PIECE CONSTRUCTOR");
        System.out.println("------------------------------>\n");
        this.pieceType = pieceType;
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove = isFirstMove;
        System.out.println("PIECE: pieceType: " + pieceType + " piecePosition: " + piecePosition +
                " pieceAlliance: " + pieceAlliance + " isFirstMove: " + isFirstMove);
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        System.out.println("INSIDE Piece: computeHashCode()");
        System.out.println("------------------------------>\n");

        int result = pieceType.hashCode();

        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);

        return result;
    }

    @Override
    public boolean equals(final Object other){
        System.out.println("INSIDE Piece: equals()");
        System.out.println("------------------------------>\n");

        if(this == other){
            return true;
        }

        if(!(other instanceof Piece)){
            return false;
        }

        final Piece otherPiece = (Piece)other;

        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
                pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode(){
        System.out.println("INSIDE Piece: hashCode()");
        System.out.println("------------------------------>\n");

        return this.cachedHashCode;
    }

    //This method returns a piece alliance.
    public Alliance getPieceAlliance(){
        System.out.println("INSIDE Piece: getPieceAlliance()");
        System.out.println("------------------------------>\n");

        return this.pieceAlliance; }

    public boolean isFirstMove(){
        System.out.println("INSIDE Piece: isFirstMove()");
        System.out.println("------------------------------>\n");

        //System.out.println("isFirstMove: " + this.isFirstMove);

        return this.isFirstMove;
    }

    public int getPiecePosition(){
        System.out.println("INSIDE Piece: getPiecePosition()");
        System.out.println("------------------------------>\n");

        return this.piecePosition;
    }

    public PieceType getPieceType(){
        System.out.println("INSIDE Piece: getPieceType()");
        System.out.println("------------------------------>\n");

        return this.pieceType;
    }

    public int getPieceValue(){
        System.out.println("INSIDE Piece: getPieceValue()");
        System.out.println("------------------------------>\n");
        return this.pieceType.getPieceValue();
    }

    /*
    This method can is used in a subclass to calculate the legal moves for a given piece. Takes
    the current state of the board as an argument.
     */
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public abstract Piece movePiece(Move move);

    public enum PieceType{

        PAWN("P", 100){
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() { return false; }
        },
        KNIGHT("N", 300){
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() { return false; }
        },
        BISHOP("B", 300){
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() { return false; }
        },
        ROOK("R", 500){
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() { return true; }
        },
        QUEEN("Q", 900){
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() { return false; }
        },
        KING("K", 10000){
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() { return false; }
        };

        private String pieceName;
        private int pieceValue;

        PieceType(final String pieceName, final int pieceValue){
            System.out.println("INSIDE PIECE/PIECETYPE (enum) CONSTRUCTOR");
            System.out.println("------------------------------>\n");
            this.pieceValue = pieceValue;
            this.pieceName = pieceName;
            System.out.println("pieceValue: " + pieceValue + "pieceName: " + pieceName);
        }

        @Override
        public String toString(){
            return this.pieceName;
        }

        public int getPieceValue(){
            System.out.println("INSIDE Piece/PieceType (enum): getPieceValue()");
            System.out.println("------------------------------>\n");
            return this.pieceValue;
        }

        public abstract boolean isKing();

        public abstract boolean isRook();
    }
}
