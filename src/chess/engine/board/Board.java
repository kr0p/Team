package chess.engine.board;

import chess.engine.Alliance;
import chess.engine.pieces.*;
import chess.engine.player.BlackPlayer;
import chess.engine.player.Player;
import chess.engine.player.WhitePlayer;

import java.io.Serializable;
import java.util.*;

public final class Board implements Serializable{

    //Class variable initializations.
    private final List<Tile> boardTiles;
    private final Collection<Piece> white;
    private final Collection<Piece> black;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassant;

    private Board(final Builder builder){
        System.out.println("INSIDE BOARD CONSTRUCTOR");
        System.out.println("------------------------------>\n");
        this.boardTiles = createBoardTiles(builder);
        this.white = calculateActivePieces(this.boardTiles, Alliance.WHITE);
        this.black = calculateActivePieces(this.boardTiles, Alliance.BLACK);
        this.enPassant = builder.enPassant;

        final Collection<Move> whiteLegalMoves = calculateLegalMoves(this.white);
        final Collection<Move> blackLegalMoves = calculateLegalMoves(this.black);

        this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteLegalMoves, blackLegalMoves);
        this.currentPlayer = builder.nextMove.choosePlayer(this.whitePlayer, this.blackPlayer);

    }

    @Override
    public String toString(){
        final StringBuilder builder = new StringBuilder();

        for(int i = 0; i < BoardUtils.NUM_TILES; i++){
            final String tileString = this.boardTiles.get(i).toString();
            builder.append(String.format("%3s", tileString));

            if((i + 1) % BoardUtils.PER_ROW == 0){
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    public Player whitePlayer(){
        System.out.println("INSIDE Board: whitePlayer()");
        System.out.println("------------------------------>\n");

        return this.whitePlayer;
    }

    public Player blackPlayer(){
        System.out.println("INSIDE Board: blackPlayer()");
        System.out.println("------------------------------>\n");

        return this.blackPlayer;
    }

    public Player currentPlayer(){
        System.out.println("INSIDE Board: currentPlayer()");
        System.out.println("------------------------------>\n");

        return this.currentPlayer;
    }
/*
    public Piece getPiece(final int coordinate){
        return this.boardTiles.get(coordinate);
    }
    */
    public Collection<Piece> getBlackPieces(){
        System.out.println("INSIDE Board: getBlackPieces()");
        System.out.println("------------------------------>\n");

        return this.black;
    }

    public Collection<Piece> getWhitePieces(){
        System.out.println("INSIDE Board: getWhitePieces()");
        System.out.println("------------------------------>\n");

        return this.white;
    }

    private Collection<Move> calculateLegalMoves(Collection<Piece> alliance) {
        System.out.println("INSIDE Board: calculateLegalMoves()");
        System.out.println("------------------------------>\n");

        final List<Move> legalMoves = new ArrayList<>();

        for(final Piece piece : alliance){
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }

        return legalMoves;
    }

    private static Collection<Piece> calculateActivePieces(final List<Tile> boardTiles, final Alliance alliance) {
        System.out.println("INSIDE Board: calculateActivePieces()");
        System.out.println("------------------------------>\n");

        final List<Piece> activePieces = new ArrayList<>();

        for(final Tile tile : boardTiles){
            if(tile.isOccupied()){
                final Piece piece = tile.getPiece();

                if(piece.getPieceAlliance() == alliance){
                    activePieces.add(piece);
                }
            }
        }
        return activePieces;
    }

    public Tile getTile(final int tileCoordinate){
        System.out.println("INSIDE Board: getTile()");
        System.out.println("------------------------------>\n");

        return boardTiles.get(tileCoordinate);
    }

    private static List<Tile> createBoardTiles(final Builder builder){
        System.out.println("INSIDE Board: createBoardTiles()");
        System.out.println("------------------------------>\n");

        final List<Tile> tiles = new ArrayList<Tile>();

        for(int i = 0; i < BoardUtils.NUM_TILES; i++){
            tiles.add(Tile.createTile(i, builder.currentBoard.get(i)));
        }

        return tiles;
    }

    //This is the method, called from Main(), that initially creates the board. It's the first method called.
    public static Board createInitialBoard(){
        System.out.println("INSIDE Board: createInitialBoard()");
        System.out.println("------------------------------>\n");

        //Create a new Builder object, which will create a new HashMap for the board.
        final Builder builder = new Builder();

        /*
        Using the setPiece method, a piece of each color at each initial position is created. New Piece objects are
        passed as arguments, resulting in the constructor for each type being called.
         */
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        builder.setPiece(new Queen(3, Alliance.BLACK));
        builder.setPiece(new King(4, Alliance.BLACK));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        builder.setPiece(new Pawn(8, Alliance.BLACK));
        builder.setPiece(new Pawn(9, Alliance.BLACK));
        builder.setPiece(new Pawn(10, Alliance.BLACK));
        builder.setPiece(new Pawn(11, Alliance.BLACK));
        builder.setPiece(new Pawn(12, Alliance.BLACK));
        builder.setPiece(new Pawn(13, Alliance.BLACK));
        builder.setPiece(new Pawn(14, Alliance.BLACK));
        builder.setPiece(new Pawn(15, Alliance.BLACK));

        builder.setPiece(new Pawn(48, Alliance.WHITE));
        builder.setPiece(new Pawn(49, Alliance.WHITE));
        builder.setPiece(new Pawn(50, Alliance.WHITE));
        builder.setPiece(new Pawn(51, Alliance.WHITE));
        builder.setPiece(new Pawn(52, Alliance.WHITE));
        builder.setPiece(new Pawn(53, Alliance.WHITE));
        builder.setPiece(new Pawn(54, Alliance.WHITE));
        builder.setPiece(new Pawn(55, Alliance.WHITE));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Queen(59, Alliance.WHITE));
        builder.setPiece(new King(60, Alliance.WHITE));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Rook(63, Alliance.WHITE));

        //Set the first move to white (customary in chess) by calling the WHITE object in the Alliance enum.
        builder.setNextMove(Alliance.WHITE);

        //Return the new board via the Builder method build().
        return builder.build();

    }

    /*
    NOTE: THIS MAY GIVE PROBLEMS!!!!! HE ORIGINALLY MADE IT RETURN ITERABLE<MOVE>!!!! WATCH!!!!
     */
    public Collection<Move> getAllLegalMoves(){
        System.out.println("INSIDE Board: getAllLegalMoves()");
        System.out.println("------------------------------>\n");

        List<Move> temp = new ArrayList<Move>();
        temp.addAll(this.whitePlayer.getLegalMoves());
        temp.addAll(this.blackPlayer.getLegalMoves());
/*
        for(final Move move : temp){
            System.out.println("Piece Type: " + move.getMovedPiece() + "Piece Position: " + move.getCurrentCoord() + "Legal Move: " + move.getDestinationCoord());
        }
*/
        return temp;
    }

    public Pawn getEnPassant(){
        return this.enPassant;
    }

    public static class Builder{

        Map<Integer, Piece> currentBoard;
        Alliance nextMove;
        Pawn enPassant;
        Move transition;

        //When the Builder class is called, create a new instance of the current board as a HashMap.
        public Builder(){
            System.out.println("INSIDE BOARD/BUILDER CONSTRUCTOR");
            System.out.println("------------------------------>\n");

            this.currentBoard = new HashMap<>();
        }

        //Setter for the board pieces. Takes a piece as an argument.
        public Builder setPiece(final Piece piece){
            System.out.println("INSIDE Board/Builder: setPiece()");
            System.out.println("------------------------------>\n");

            //In the currentBoard HashMap, store the piece's position as the key, and the piece as the value.
            this.currentBoard.put(piece.getPiecePosition(), piece);

            //Return the Builder object.
            return this;
        }

        //Setter for which alliance gets the next move. Takes an Alliance argument (Alliance.WHITE or Alliance.BLACK)
        public Builder setNextMove(final Alliance nextMove){
            System.out.println("INSIDE Board/Builder: setNextMove()");
            System.out.println("------------------------------>\n");

            this.nextMove = nextMove;
            return this;
        }

        //Returns a new Board object.
        public Board build(){
            System.out.println("INSIDE Board/Builder: build()");
            System.out.println("------------------------------>\n");

            return new Board(this);
        }

        public void setEnPassant(Pawn enPassant) {
            System.out.println("INSIDE Board/Builder: setEnPassant()");
            System.out.println("------------------------------>\n");

            this.enPassant = enPassant;
        }

        public Builder setMoveTransition(final Move transition) {
            this.transition = transition;

            return this;
        }
    }
}
