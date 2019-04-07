package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.pieces.King;
import chess.engine.pieces.Piece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player implements Serializable{
	private static final long serialVersionUID = 1L;
	protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board, final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves){
        System.out.println("INSIDE PLAYER CONSTRUCTOR");
        System.out.println("------------------------------>\n");

        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
        /*
        NOTE: THIS MAY GIVE PROBLEMS!!!!! HE ORIGINALLY MADE IT RETURN ITERABLE<MOVE>!!!! WATCH!!!!
        */
        List<Move> temp = new ArrayList<>();
        temp.addAll(legalMoves);
        temp.addAll(calculateKingCastles(legalMoves, opponentMoves));

        this.legalMoves = temp;
        //System.out.println("legalMoves: " + legalMoves);

    }

    public King getPlayerKing(){
        System.out.println("INSIDE Player: getPlayerKing()");
        System.out.println("------------------------------>\n");

        return this.playerKing;
    }

    public Collection<Move> getLegalMoves(){
        System.out.println("INSIDE Player: getLegalMoves()");
        System.out.println("------------------------------>\n");

        return this.legalMoves;
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> opponentMoves){
        System.out.println("INSIDE Player: calculateAttacksOnTile()");
        System.out.println("------------------------------>\n");

        final List<Move> attackMoves = new ArrayList<>();

        for(final Move move: opponentMoves){
            if(piecePosition == move.getDestinationCoord())
                attackMoves.add(move);
        }

        return attackMoves;
    }

    private King establishKing() {
        System.out.println("INSIDE Player: establishKing()");
        System.out.println("------------------------------>\n");

        for(final Piece piece : getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King)piece;
            }
        }
        throw new RuntimeException("Invalid state");
    }

    public boolean isMoveLegal(final Move move){
        System.out.println("INSIDE Player: isMoveLegal()");
        System.out.println("------------------------------>\n");

        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        System.out.println("INSIDE Player: isInCheck()");
        System.out.println("------------------------------>\n");

        //System.out.println("isInCheck: " + this.isInCheck);
        return this.isInCheck;
    }

    public boolean isInCheckMate(){
        System.out.println("INSIDE Player: isInCheckMate()");
        System.out.println("------------------------------>\n");

        return this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {
        System.out.println("INSIDE Player: hasEscapeMoves()");
        System.out.println("------------------------------>\n");

        for(final Move move : this.legalMoves){
            final MoveTransition transition = makeMove(move);

            if(transition.getMoveStatus().isDone()){
                return true;
            }
        }

        return false;
    }

    public boolean isInStaleMate(){
        System.out.println("INSIDE Player: isInStaleMate()");
        System.out.println("------------------------------>\n");

        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled(){
        System.out.println("INSIDE Player: isCastled()");
        System.out.println("------------------------------>\n");

        return false;
    }

    public MoveTransition makeMove(final Move move){
        System.out.println("INSIDE Player: makeMove()");
        System.out.println("------------------------------>\n");

        if(!isMoveLegal(move)){
            System.out.println("ILLEGAL MOVE!!!!!!!!!!!!!!!!!!!!!!!!11");
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        final Board transitionBoard = move.execute();
        /*
        for(final Move moves : legalMoves){
            System.out.println("Current Legal Move (Source, Destination): " + move.getCurrentCoord() + ", " + move.getDestinationCoord());
        }
        */
        System.out.println(transitionBoard);
        System.out.println(transitionBoard.currentPlayer().getLegalMoves());
        System.out.println(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition());


        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionBoard.currentPlayer().getLegalMoves());

        if(!kingAttacks.isEmpty()){
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);
}
