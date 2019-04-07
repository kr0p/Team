package chess.engine.board;

import java.io.Serializable;

import chess.engine.pieces.Pawn;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Rook;

public abstract class Move implements Serializable{
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoord;
    protected final boolean isFirstMove;

    public static final Move INVALID_MOVE = new InvalidMove();

    private Move(final Board board, final Piece pieceMoved, final int destinationCoord){
        System.out.println("INSIDE MOVE CONSTRUCTOR (board, piece, destCoord)");
        System.out.println("------------------------------>\n");

        this.board = board;
        this.movedPiece = pieceMoved;
        this.destinationCoord = destinationCoord;
        this.isFirstMove = pieceMoved.isFirstMove();
    }


    private Move(final Board board, final int destinationCoord){
        System.out.println("INSIDE MOVE CONSTRUCTOR (board, destCoord)");
        System.out.println("------------------------------>\n");
        this.board = board;
        this.destinationCoord = destinationCoord;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode(){
        System.out.println("INSIDE Move: hashCode()");
        System.out.println("------------------------------>\n");

        int result = 1;

        result = 31 * result + this.getDestinationCoord();
        result = 31 * result + getMovedPiece().hashCode();
        result = 31 * result + this.getMovedPiece().getPiecePosition();
        //result += (isFirstMove ? 1 : 0);

        return result;
    }

    @Override
    public boolean equals(final Object other){
        System.out.println("INSIDE Move: equals()");
        System.out.println("------------------------------>\n");

        if(this == other)
            return true;

        if(!(other instanceof Move))
            return false;

        final Move otherMove = (Move) other;

        return getCurrentCoord() == otherMove.getCurrentCoord() &&
                getDestinationCoord() == otherMove.getDestinationCoord() &&
                getMovedPiece().equals(otherMove.getMovedPiece());
    }

    public Board getBoard(){
        return this.board;
    }

    public int getCurrentCoord(){
        System.out.println("INSIDE Move: getCurrentCoord()");
        System.out.println("------------------------------>\n");

        return this.movedPiece.getPiecePosition();
    }

    public int getDestinationCoord(){
        System.out.println("INSIDE Move: getDestinationCoord()");
        System.out.println("------------------------------>\n");

        return this.destinationCoord;
    }

    public Piece getMovedPiece(){
        System.out.println("INSIDE Move: getMovedPiece()");
        System.out.println("------------------------------>\n");

        return this.movedPiece;
    }

    public boolean isAttack(){
        System.out.println("INSIDE Move: isAttack()");
        System.out.println("------------------------------>\n");

        return false; }

    public boolean isCastle(){
        System.out.println("INSIDE Move: isCastle()");
        System.out.println("------------------------------>\n");

        return false; }

    public Piece getAttackedPiece(){
        System.out.println("INSIDE Move: getAttackedPiece()");
        System.out.println("------------------------------>\n");

        return null;
    }

    public Board execute() {
        System.out.println("INSIDE Move: execute()");
        System.out.println("------------------------------>\n");

        final Board.Builder builder = new Board.Builder();

        for(final Piece piece : this.board.currentPlayer().getActivePieces()){
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
                //System.out.println("Victim piece: " + piece.getPieceType());
            }
        }

        for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
            //System.out.println("Live unchanged opponent pieces: " + piece.getPieceType());
        }

        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setNextMove(this.board.currentPlayer().getOpponent().getAlliance());

        System.out.println("ENTERING BUILDER.BUILD()");
        return builder.build();
    }

    public static final class MajorMove extends Move{

        public MajorMove(final Board board, final Piece piece, final int destinationCoord) {
            super(board, piece, destinationCoord);
            System.out.println("INSIDE MOVE/MAJORMOVE CONSTRUCTOR");
            System.out.println("------------------------------>\n");

        }

        @Override
        public boolean equals(final Object other){
            System.out.println("INSIDE Move/MajorMove: equals()");
            System.out.println("------------------------------>\n");
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString(){
            System.out.println("INSIDE Move/MajorMove: toString()");
            System.out.println("------------------------------>\n");
            return movedPiece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoord);
        }
    }

    public static class MajorAttackMove extends AttackMove{
        public MajorAttackMove(final Board board, final Piece pieceMoved,
                               final int destinationCoord, final Piece pieceAttacked){
            super(board, pieceMoved, destinationCoord, pieceAttacked);
            System.out.println("INSIDE MOVE/ATTACKMOVE/MAJORATTACKMOVE CONSTRUCTOR");
            System.out.println("------------------------------>\n");
        }

        @Override
        public boolean equals(final Object other){
            System.out.println("INSIDE Move/AttackMove/MajorAttackMove: equals()");
            System.out.println("------------------------------>\n");
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            System.out.println("INSIDE Move/AttackMove/MajorAttackMove: toString()");
            System.out.println("------------------------------>\n");
            return movedPiece.getPieceType() + BoardUtils.getPositionAtCoordinate(this.destinationCoord);
        }
    }

    public static class AttackMove extends Move{

        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece piece,
                   final int destinationCoord, final Piece attackedPiece) {
            super(board, piece, destinationCoord);
            System.out.println("INSIDE MOVE/ATTACKMOVE CONSTRUCTOR");
            System.out.println("------------------------------>\n");
            this.attackedPiece = attackedPiece;

        }

        @Override
        public int hashCode(){
            System.out.println("INSIDE Move/AttackMove: hashCode()");
            System.out.println("------------------------------>\n");

            return this.attackedPiece.hashCode() + super.hashCode();}

        @Override
        public boolean equals(final Object other){
            System.out.println("INSIDE Move/AttackMove: equals()");
            System.out.println("------------------------------>\n");

            if(this == other)
                return true;

            if(!(other instanceof AttackMove))
                return false;

            final AttackMove otherAttack = (AttackMove) other;

            return super.equals(otherAttack) && getAttackedPiece().equals(otherAttack.getAttackedPiece());
        }

        @Override
        public boolean isAttack(){
            System.out.println("INSIDE Move/AttackMove: isAttack()");
            System.out.println("------------------------------>\n");

            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            System.out.println("INSIDE Move/AttackMove: getAttackedPiece()");
            System.out.println("------------------------------>\n");

            return this.attackedPiece;
        }


    }

    public static final class PawnMove extends Move{

        public PawnMove(final Board board, final Piece piece, final int destinationCoord) {
            super(board, piece, destinationCoord);
            System.out.println("INSIDE MOVE/PAWNMOVE CONSTRUCTOR");
            System.out.println("------------------------------>\n");

        }

        @Override
        public boolean equals(final Object other){
            System.out.println("INSIDE Move/PawnMove: equals()");
            System.out.println("------------------------------>\n");
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString(){
            System.out.println("INSIDE Move/PawnMove: toString()");
            System.out.println("------------------------------>\n");
            return BoardUtils.getPositionAtCoordinate(this.destinationCoord);
        }
    }

    public static class PawnAttackMove extends AttackMove{

        public PawnAttackMove(final Board board, final Piece piece, final int destinationCoord, final Piece attackedPiece) {
            super(board, piece, destinationCoord, attackedPiece);
            System.out.println("INSIDE MOVE/ATTACKMOVE/PAWNATTACKMOVE CONSTRUCTOR");
            System.out.println("------------------------------>\n");

        }

        @Override
        public boolean equals(final Object other){
            System.out.println("INSIDE Move/AttackMove/PawnAttackMove: equals()");
            System.out.println("------------------------------>\n");
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            System.out.println("INSIDE Move/AttackMove/PawnAttackMove: toString()");
            System.out.println("------------------------------>\n");
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1) + "x" +
                                                        BoardUtils.getPositionAtCoordinate(this.destinationCoord);
        }
    }

    public static final class EnPassant extends PawnAttackMove{

        public EnPassant(final Board board, final Piece piece, final int destinationCoord, final Piece attackedPiece) {
            super(board, piece, destinationCoord, attackedPiece);
            System.out.println("INSIDE MOVE/ATTACKMOVE/PAWNATTACKMOVE/ENPASSANT CONSTRUCTOR");
            System.out.println("------------------------------>\n");
        }

        @Override
        public boolean equals(final Object other){
            System.out.println("INSIDE Move/AttackMove/PawnAttackMove/EnPassant: equals()");
            System.out.println("------------------------------>\n");
            return this == other || other instanceof EnPassant && super.equals(other);
        }

        @Override
        public Board execute(){
            System.out.println("INSIDE Move/AttackMove/PawnAttackMove/EnPassant: execute()");
            System.out.println("------------------------------>\n");
            final Board.Builder builder = new Board.Builder();

            this.board.currentPlayer().getActivePieces().stream().filter(piece -> !this.movedPiece.equals(piece)).forEach(builder::setPiece);
            this.board.currentPlayer().getOpponent().getActivePieces().stream().filter(piece -> !piece.equals(this.getAttackedPiece())).forEach(builder::setPiece);

            /*
            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }

            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                if(!piece.equals(this.getAttackedPiece())){
                    builder.setPiece(piece);
                }
            }
            */
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setNextMove(this.board.currentPlayer().getOpponent().getAlliance());
            builder.setMoveTransition(this);

            return builder.build();
        }
    }

    public static final class PawnJump extends Move{

        public PawnJump(final Board board, final Piece piece, final int destinationCoord) {
            super(board, piece, destinationCoord);
            System.out.println("INSIDE MOVE/PAWNJUMP CONSTRUCTOR");
            System.out.println("------------------------------>\n");

        }

        @Override
        public Board execute(){
            System.out.println("INSIDE Move/PawnJump: execute()");
            System.out.println("------------------------------>\n");

            final Board.Builder builder = new Board.Builder();

            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece))
                    builder.setPiece(piece);
            }

            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces())
                builder.setPiece(piece);

            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassant(movedPawn);
            builder.setNextMove(this.board.currentPlayer().getOpponent().getAlliance());

            return builder.build();
        }

        @Override
        public String toString(){
            System.out.println("INSIDE Move/PawnJump: toString()");
            System.out.println("------------------------------>\n");
            return BoardUtils.getPositionAtCoordinate(this.destinationCoord);
        }
    }

    public static class PawnPromotion extends Move{

        final Move promotionMove;
        final Pawn promotedPawn;

        public PawnPromotion(final Move promotionMove){
            super(promotionMove.getBoard(), promotionMove.getMovedPiece(), promotionMove.getDestinationCoord());
            this.promotionMove = promotionMove;
            this.promotedPawn = (Pawn) promotionMove.getMovedPiece();
            System.out.println("INSIDE MOVE/PAWNPROMOTION CONSTRUCTOR");
            System.out.println("------------------------------------->\n");
        }

        @Override
        public Board execute(){
            System.out.println("INSIDE Move/PawnPromotion: execute()");
            System.out.println("------------------------------>\n");
            final Board pawnPromotionBoard = this.promotionMove.execute();
            final Board.Builder builder = new Board.Builder();

            for(final Piece piece : pawnPromotionBoard.currentPlayer().getActivePieces()){
                if(!this.promotedPawn.equals(piece)){
                    builder.setPiece(piece);
                }
            }

            for(final Piece piece : pawnPromotionBoard.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }

            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setNextMove(pawnPromotionBoard.currentPlayer().getAlliance());

            return builder.build();
        }

        @Override
        public boolean isAttack(){
            System.out.println("INSIDE Move/PawnPromotion: isAttack()");
            System.out.println("------------------------------>\n");
            return this.promotionMove.isFirstMove;
        }

        @Override
        public Piece getAttackedPiece(){
            System.out.println("INSIDE Move/PawnPromotion: getAttackedPiece()");
            System.out.println("------------------------------>\n");
            return this.promotionMove.getAttackedPiece();
        }

        @Override
        public String toString(){
            System.out.println("INSIDE Move/PawnPromotion: toString()");
            System.out.println("------------------------------>\n");
            return "";
        }

        @Override
        public int hashCode(){
            System.out.println("INSIDE Move/PawnPromotion: hashCode()");
            System.out.println("------------------------------>\n");
            return promotionMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other){
            System.out.println("INSIDE Move/PawnPromotion: equals()");
            System.out.println("------------------------------>\n");
            return this == other || other instanceof PawnPromotion && super.equals(other);
        }
    }

    static abstract class CastleMove extends Move{
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDest;

        public CastleMove(final Board board, final Piece piece, final int destinationCoord,
                            final Rook castleRook, final int castleRookStart,
                            final int castleRookDest) {
            super(board, piece, destinationCoord);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDest = castleRookDest;
            System.out.println("INSIDE MOVE/CASTLEMOVE CONSTRUCTOR");
            System.out.println("------------------------------>\n");

        }

        public Rook getCastleRook(){
            System.out.println("INSIDE Move/CastleMove: getCastleRook()");
            System.out.println("------------------------------>\n");

            return this.castleRook;
        }

        @Override
        public boolean isCastle(){
            System.out.println("INSIDE Move/CastleMove: isCastle()");
            System.out.println("------------------------------>\n");

            return true; }

        @Override
        public Board execute(){
            System.out.println("INSIDE Move/CastleMove: execute()");
            System.out.println("------------------------------>\n");

            final Board.Builder builder = new Board.Builder();

            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece))
                    builder.setPiece(piece);
            }

            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces())
                builder.setPiece(piece);

            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDest, this.board.currentPlayer().getAlliance()));
            builder.setNextMove(this.board.currentPlayer().getOpponent().getAlliance());

            return builder.build();
        }

        @Override
        public int hashCode(){
            System.out.println("INSIDE Move/CastleMove: hashCode()");
            System.out.println("------------------------------>\n");
            final int prime = 31;
            int result = super.hashCode();

            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDest;

            return result;
        }

        @Override
        public boolean equals(final Object other){
            System.out.println("INSIDE Move/CastleMove: equals()");
            System.out.println("------------------------------>\n");
            if(this == other){
                return true;
            }
            if(!(other instanceof CastleMove)){
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;

            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }

    public static final class KingCastle extends CastleMove{

        public KingCastle(final Board board, final Piece piece, final int destinationCoord,
                          final Rook castleRook, final int castleRookStart, final int castleRookDest) {
            super(board, piece, destinationCoord, castleRook, castleRookStart, castleRookDest);
            System.out.println("INSIDE MOVE/CASTLEMOVE/KINGCASTLE CONSTRUCTOR");
            System.out.println("------------------------------>\n");

        }

        @Override
        public boolean equals(final Object other){
            System.out.println("INSIDE Move/CastleMove/KingCastle: equals()");
            System.out.println("------------------------------>\n");
            return this == other || other instanceof KingCastle && super.equals(other);
        }

        @Override
        public String toString(){
            System.out.println("INSIDE Move/CastleMove/KingCastle: toString()");
            System.out.println("------------------------------>\n");
            return "0-0";
        }
    }

    public static final class QueenCastle extends CastleMove{

        public QueenCastle(final Board board, final Piece piece, final int destinationCoord,
                           final Rook castleRook, final int castleRookStart, final int castleRookDest) {
            super(board, piece, destinationCoord, castleRook, castleRookStart, castleRookDest);
            System.out.println("INSIDE MOVE/CASTLEMOVE/QUEENCASTLE CONSTRUCTOR");
            System.out.println("------------------------------>\n");

        }

        @Override
        public boolean equals(final Object other){
            System.out.println("INSIDE Move/CastleMove/QueenCastle: equals()");
            System.out.println("------------------------------>\n");
            return this == other || other instanceof QueenCastle && super.equals(other);
        }

        @Override
        public String toString(){
            System.out.println("INSIDE Move/CastleMove/QueenCastle: toString()");
            System.out.println("------------------------------>\n");
            return "0-0-0";
        }
    }

    public static final class InvalidMove extends Move{

        private InvalidMove(){
            super(null, -1);
            System.out.println("INSIDE MOVE/INVALIDMOVE CONSTRUCTOR");
            System.out.println("------------------------------>\n");
        }

        @Override
        public int getCurrentCoord(){
            System.out.println("INSIDE Move/InvalidMove: getCurrentCoord()");
            System.out.println("------------------------------>\n");
            return -1;
        }

        @Override
        public int getDestinationCoord(){
            System.out.println("INSIDE Move/InvalidMove: getDestinationCoord()");
            System.out.println("------------------------------>\n");
            return -1;
        }

        @Override
        public Board execute(){
            System.out.println("INSIDE Move/InvalidMove: execute()");
            System.out.println("------------------------------>\n");

            throw new RuntimeException("INVALID MOVE!!! CANNOT EXECUTE!!!!");
        }

        @Override
        public String toString(){
            System.out.println("INSIDE Move/InvalidMove: toString()");
            System.out.println("------------------------------>\n");
            return "Invalid Move";
        }
    }

    public static class MoveFactory{

        private MoveFactory(){
            throw new RuntimeException("No instantiation");
        }

        public static Move createMove(final Board board, final int currentCoord, final int destinationCoord){
            System.out.println("INSIDE Move/MoveFactory: createMove()");
            System.out.println("------------------------------>\n");

            //System.out.println("currentCoord: " + currentCoord);
            //System.out.println("destinationCoord: " + destinationCoord);
            for(final Move move: board.getAllLegalMoves()){
                //System.out.println("Move.getCurrentCoord: " + move.getCurrentCoord());
                //System.out.println("Move.getDestinationCoord: " + move.getDestinationCoord());
                if(move.getCurrentCoord() == currentCoord && move.getDestinationCoord() == destinationCoord) {
                    //System.out.println("Inside createMove: Found in list of legal moves.");
                    return move;
                }
            }
            //System.out.println("Invalid Move: Not inside list of legals");
            return INVALID_MOVE;
        }
    }
}
