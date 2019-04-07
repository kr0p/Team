package chess.engine.player;

import java.io.Serializable;

import chess.engine.board.Board;
import chess.engine.board.Move;

public class MoveTransition implements Serializable{

	private static final long serialVersionUID = 1L;
	private final Board transitionBoard;
    //private final Move move;
    private final MoveStatus moveStatus;

    public MoveTransition(final Board transitionBoard, final Move move, final MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        //this.move = move;
        this.moveStatus = moveStatus;
        System.out.println("INSIDE MOVETRANSITION CONSTRUCTOR");
        System.out.println("------------------------------>\n");

    }

    public MoveStatus getMoveStatus(){
        System.out.println("INSIDE MoveTransition: getMoveStatus()");
        System.out.println("------------------------------>\n");

        return this.moveStatus;
    }

    public Board getTransitionBoard(){
        System.out.println("INSIDE MoveTransition: getTransitionBoard()");
        System.out.println("------------------------------>\n");

        return this.transitionBoard; }
}
