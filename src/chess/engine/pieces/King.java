package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import chess.engine.board.Tile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece implements Serializable{

    private final static int[] CANDIDATE_MOVE_COORD = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.KING, piecePosition, pieceAlliance, true);
    }

    public King(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        System.out.println("INSIDE King: calculateLegalMoves()");
        System.out.println("------------------------------>\n");

        final List<Move> legalMoves = new ArrayList<>();
        int candidateDestinationCoord;


        for(final int currentCandidate : CANDIDATE_MOVE_COORD){
            candidateDestinationCoord = this.piecePosition + currentCandidate;

            if(isfirstColumnExclusion(this.piecePosition, currentCandidate) ||
                    isEighthColumnExclusion(this.piecePosition, currentCandidate)){
                continue;   //Continue the loop if any of these methods return true
            }

            if(BoardUtils.isValidCoord(candidateDestinationCoord)){
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoord);

                if(!candidateDestinationTile.isOccupied()){
                    //Add the move to the list of legal moves as a non-attack move.
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoord));
                }
                else{
                    //Otherwise, get the type and alliance of the piece at the destination.
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    //If the piece at the occupied tile's alliance differs...
                    if(this.pieceAlliance != pieceAlliance){
                        //Add the move to the list of legal moves as an attack move.
                        legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestinationCoord, pieceAtDestination));
                    }
                }
            }
        }

        return legalMoves;
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoord(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.KING.toString();
    }

    private static boolean isfirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9) || (candidateOffset == -1) ||
                (candidateOffset == 7));
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == -7) || (candidateOffset == 1) ||
                (candidateOffset == 9));
    }
}
