package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Rook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player implements Serializable{
	private static final long serialVersionUID = 1L;

	public WhitePlayer(final Board board, final Collection<Move> whiteLegalMoves,
                       final Collection<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
        System.out.println("INSIDE PLAYER/WHITEPLAYER CONSTRUCTOR");
        System.out.println("------------------------------>\n");

    }

    @Override
    public Collection<Piece> getActivePieces() {
        System.out.println("INSIDE Player/WhitePlayer: getActivePieces()");
        System.out.println("------------------------------>\n");

        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        System.out.println("INSIDE Player/WhitePlayer: getAlliance()");

        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        System.out.println("INSIDE Player/WhitePlayer: getOpponent()");
        System.out.println("------------------------------>\n");

        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentLegals) {
        System.out.println("INSIDE Player/WhitePlayer: calculateKingCastles()");
        System.out.println("------------------------------>\n");

        final List<Move> kingCastles = new ArrayList<>();

        if(this.playerKing.isFirstMove() && !this.isInCheck()){
            if(!this.board.getTile(61).isOccupied() && !this.board.getTile(62).isOccupied()){
                final Tile rookTile = this.board.getTile(63);

                if(rookTile.isOccupied() && rookTile.getPiece().isFirstMove()){
                    if(Player.calculateAttacksOnTile(61, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(62, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new Move.KingCastle(this.board, this.playerKing, 62,
                                                            (Rook) rookTile.getPiece(), rookTile.getTileCoord(),
                                                            61));
                    }
                }
            }
            if(!this.board.getTile(59).isOccupied() &&
                    !this.board.getTile(58).isOccupied() &&
                    !this.board.getTile(57).isOccupied()){
                final Tile rookTile = this.board.getTile(56);

                if(rookTile.isOccupied() && rookTile.getPiece().isFirstMove() &&
                    Player.calculateAttacksOnTile(58, opponentLegals).isEmpty() &&
                    Player.calculateAttacksOnTile(59, opponentLegals).isEmpty() &&
                    rookTile.getPiece().getPieceType().isRook()){
                    kingCastles.add(new Move.QueenCastle(this.board, this.playerKing, 58,
                                                        (Rook) rookTile.getPiece(), rookTile.getTileCoord(),
                                                        59));
                }
            }
        }
        return kingCastles;
    }
}
