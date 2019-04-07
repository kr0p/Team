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

/*
This class defines the behavior of a bishop. Each piece type maintains an array of offsets which are used to determine
the legal moves for a given piece from any given position on the board (or in the Tile HashMap, more accurately). Each
piece type inherits from the abstract Piece class, overriding the calculateLegalMoves method.
 */
public class Bishop extends Piece implements Serializable{
    //Array containing the bishop's offsets.
    private final static int[] CANDIDATE_VECTOR_COORDS = {-9, -7, 7, 9};

    //Constructor. Takes position and alliance as arguments, passing them to the Piece constructor.
    public Bishop(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.BISHOP, piecePosition, pieceAlliance, true);
    }

    public Bishop(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    /*
    Overridden method from the Piece class, which calculates all possible legal moves from a piece's current position
    on the board. The method begins by creating a List of legal moves which will be used to store legal moves. It then
    iterates through a piece's list of offsets, adding the current piece's position coordinate to each offset: each
    destination value is then stored in the list of legal moves.

    Additionally, the class checks to see whether or not a move is within the board's bounds, or doesn't move off the
    board onto the other side of the board (moving left and ending up on the right side, for example, since Tile
    coordinate values are a HashMap). It does this by passing each destination coordinate to the column exclusion
    methods in the BoardUtils class, checking to see if any of them return true.

    The method then checks the Tile of each destination coordinate to see if that Tile is occupied by another piece.
    If the Tile is unoccupied, the move is added to the list of legal non-attack moves. If the Tile is occupied, the
    method gets the type and alliance of the piece occupying the destination tile. If the alliance of the occupying
    piece differs from the alliance of the current moving piece, the move is added to the list of legal moves as an
    attack move.
     */
    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        System.out.println("INSIDE Bishop: calculateLegalMoves()");
        System.out.println("------------------------------>\n");

        //ArrayList of moves used to store all possible legal moves.
        final List<Move> legalMoves = new ArrayList<>();

        //Iterate the constant class array of piece offsets.
        for(final int currentCandidate : CANDIDATE_VECTOR_COORDS) {
            //Initially define the destination coordinate as the bishop's current position.
            int candidateDestinationCoord = this.piecePosition;

            //While the destination coordinate is within legal board bounds (between 0 and 64)...
            while (BoardUtils.isValidCoord(candidateDestinationCoord)) {

                if(isfirstColumnExclusion(candidateDestinationCoord, currentCandidate) ||
                        isEighthColumnExclusion(candidateDestinationCoord, currentCandidate)){
                    break;   //Break the loop if any of these methods return true
                }
                //Add the current offset to the bishop's current position.
                candidateDestinationCoord += currentCandidate;
                System.out.println("BISHOP legals: " + candidateDestinationCoord);

                //If the destination coordinate is within legal board bounds...
                if (BoardUtils.isValidCoord(candidateDestinationCoord)) {
                    //Store the tile of the destination coordinate.
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoord);
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    //final Piece pieceAtDestination = board.getP
                    //If the tile is not marked as occupied by the Tile class...
                    if (!candidateDestinationTile.isOccupied()) {
                        //Add the move to the list of legal moves as a non-attack move.
                        legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoord));
                    }
                    else {
                        //Otherwise, get the type and alliance of the piece at the destination.
                        //final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                        //If the piece at the occupied tile's alliance differs...
                        if (this.pieceAlliance != pieceAlliance) {
                            //Add the move to the list of legal moves as an attack move.
                            legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestinationCoord, pieceAtDestination));
                        }
                        break;
                    }
                    //break;
                }
            }
        }
        return legalMoves;
    }

    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getDestinationCoord(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.BISHOP.toString();
    }

    private static boolean isfirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9) || (candidateOffset == 7));
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == 9) || (candidateOffset == -7));
    }
}
