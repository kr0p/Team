package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece implements Serializable{

    private final static int[] CANDIDATE_MOVE_COORD = {8, 16, 7, 9};

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }

    public Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        System.out.println("INSIDE Pawn: calculateLegalMoves()");
        System.out.println("------------------------------>\n");

        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidate : CANDIDATE_MOVE_COORD){

            int candidateDestinationCoord = this.piecePosition + (this.getPieceAlliance().getDirection() * currentCandidate);

            if(!BoardUtils.isValidCoord(candidateDestinationCoord))
                continue;

            if(currentCandidate == 8 && !board.getTile(candidateDestinationCoord).isOccupied())
            {
                if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoord))
                    legalMoves.add(new Move.PawnPromotion(new Move.PawnMove(board, this, candidateDestinationCoord)));
                else
                    legalMoves.add(new Move.PawnMove(board, this, candidateDestinationCoord));
            }
            else if(currentCandidate == 16 && this.isFirstMove() &&
                    ((BoardUtils.SEVENTH_ROW[this.piecePosition] && this.getPieceAlliance().isBlack()) ||
                    (BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceAlliance().isWhite())))
            {
                final int behindCurrentCandidate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);

                if(!board.getTile(behindCurrentCandidate).isOccupied() &&
                        !board.getTile(candidateDestinationCoord).isOccupied())
                    legalMoves.add(new Move.PawnJump(board, this, candidateDestinationCoord));
            }
            else if(currentCandidate == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] &&
                            this.pieceAlliance.isWhite()) ||
                            (BoardUtils.FIRST_COLUMN[this.piecePosition] &&
                                    this.pieceAlliance.isBlack())))
            {
                if(board.getTile(candidateDestinationCoord).isOccupied())
                {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoord).getPiece();

                    if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                    {
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoord))
                            legalMoves.add(new Move.PawnPromotion(new Move.PawnAttackMove(board, this,
                                    candidateDestinationCoord, pieceOnCandidate)));
                        else
                            legalMoves.add(new Move.PawnAttackMove(board, this, candidateDestinationCoord,
                                    pieceOnCandidate));
                    }
                }
                else if(board.getEnPassant() != null)
                {
                    if(board.getEnPassant().getPiecePosition() == (this.piecePosition +
                            (this.pieceAlliance.getOppositeDirection())))
                    {
                        final Piece pieceOnCandidate = board.getEnPassant();

                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                            legalMoves.add(new Move.EnPassant(board, this, candidateDestinationCoord,
                                    pieceOnCandidate));
                    }
                }
            }
            else if(currentCandidate == 9 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] &&
                            this.pieceAlliance.isBlack()) ||
                            (BoardUtils.FIRST_COLUMN[this.piecePosition] &&
                                    this.pieceAlliance.isWhite())))
            {
                if(board.getTile(candidateDestinationCoord).isOccupied())
                {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoord).getPiece();

                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                    {
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoord))
                            legalMoves.add(new Move.PawnPromotion(new Move.PawnAttackMove(board, this,
                                    candidateDestinationCoord, pieceOnCandidate)));
                        else
                            legalMoves.add(new Move.PawnAttackMove(board, this, candidateDestinationCoord,
                                    pieceOnCandidate));
                    }
                }
                else if(board.getEnPassant() != null)
                {
                    if(board.getEnPassant().getPiecePosition() == (this.piecePosition -
                            (this.pieceAlliance.getOppositeDirection())))
                    {
                        final Piece pieceOnCandidate = board.getEnPassant();

                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                            legalMoves.add(new Move.EnPassant(board, this, candidateDestinationCoord,
                                    pieceOnCandidate));
                    }
                }
            }
        }

        return legalMoves;
    }

    public Piece getPromotionPiece(){
        return new Queen(this.piecePosition, this.pieceAlliance, false);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoord(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }
}
