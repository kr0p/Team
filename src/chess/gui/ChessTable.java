package chess.gui;

import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import chess.engine.pieces.Piece;
import chess.engine.player.MoveTransition;
import chess.network.ChessClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class ChessTable {
    private final JFrame gameFrame;
    private GameHistoryPanel gameHistoryPanel;
    private TakenPiecesPanel takenPiecesPanel;
    private BoardPanel boardPanel;
    private MoveLog moveLog;
    private Board chessboard;
    private ChessClient client;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private FlipBlack orientation;
    private boolean highlightLegalMoves;
    private boolean isWhite;
    private boolean isBlack;
    private boolean yourTurn;
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private static String defaultPieceImagesPath = "simple/";

    public ChessTable(Board board, ChessClient client, boolean isWhite, boolean isBlack){
        System.out.println("INSIDE CHESSTABLE CONSTRUCTOR");
        System.out.println("------------------------------>\n");

        this.client = client;
        this.gameFrame = new JFrame();
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = populateMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessboard = board;
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.isWhite = isWhite;
        this.isBlack = isBlack;
        
        if(isBlack) {
        	yourTurn = false;
        	gameFrame.setTitle("Your Alliance is Black. It is your opponent's turn.");
        	orientation = FlipBlack.BLACKORIENT;
        }
        if(isWhite) {
        	yourTurn = true;
        	gameFrame.setTitle("Your Alliance is White. It is your turn");
        	orientation = FlipBlack.WHITEORIENT;
        }
        
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.highlightLegalMoves = true;
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);

        this.gameFrame.setVisible(true);
        boardPanel.drawBoard(chessboard);
    }
    
    public void updateBoard(Board board, int tileID, Tile sourceTile, Piece humanMovedPiece, 
    		MoveLog moveLog) {
    	
    	this.chessboard = board;
    	this.sourceTile = sourceTile;
    	this.humanMovedPiece = humanMovedPiece;
    	this.moveLog = moveLog;
    	
    	System.out.println("Second click: " + client.isConnected());
        destinationTile = chessboard.getTile(tileID);
        System.out.println(destinationTile);

        final Move move = Move.MoveFactory.createMove(chessboard, sourceTile.getTileCoord(),
                                                        destinationTile.getTileCoord());
    
        final MoveTransition transition = chessboard.currentPlayer().makeMove(move);
        System.out.println(chessboard);
        if(transition.getMoveStatus().isDone()){
            chessboard = transition.getTransitionBoard();
            this.moveLog.addMove(move);
        }

        this.sourceTile = null;
        this.destinationTile = null;
        this.humanMovedPiece = null;
        client.setMovedPiece(null);
        client.setSourceTile(null);
        
        gameHistoryPanel.redo(chessboard, this.moveLog);
        takenPiecesPanel.redo(this.moveLog);
        boardPanel.drawBoard(chessboard);
        
        if(yourTurn) {
        	yourTurn = false;
        	
        	if(isBlack) {
        		gameFrame.setTitle("Your Alliance is Black. It is your opponent's turn.");
        	}
        	else if(isWhite) {
        		gameFrame.setTitle("Your Alliance is White. It is your opponent's turn.");
        	}
        }
        else if(!yourTurn) {
        	yourTurn = true;
        	
        	if(isBlack) {
        		gameFrame.setTitle("Your Alliance is Black. It is your turn.");
        	}	
        	else if(isWhite) {
        		gameFrame.setTitle("Your Alliance is White. It is your turn.");
        	}
        		
        }
    }
    
    private JMenuBar populateMenuBar(){
        System.out.println("INSIDE ChessTable: populateMenuBar()");
        System.out.println("------------------------------>\n");

        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());

        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        System.out.println("INSIDE ChessTable: createFileMenu()");
        System.out.println("------------------------------>\n");

        final JMenu fileMenu = new JMenu("File");
        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    public enum FlipBlack{
    	WHITEORIENT{
    		@Override
    		List<TilePanel> iterate(final List<TilePanel> boardTiles){
    			return boardTiles;
    		}
    		
    		@Override
    		FlipBlack flip(){
    			return BLACKORIENT;
    		}
    	},
    	
    	BLACKORIENT{
    		@Override
    		List<TilePanel> iterate(final List<TilePanel> boardTiles){
    			List<TilePanel> blackBoard = new ArrayList<TilePanel>();
    			
    			for(int i = boardTiles.size() - 1; i >= 0; i--) {
    				blackBoard.add(boardTiles.get(i));
    			}
    			
    			return blackBoard;
    		}
    		
    		@Override
    		FlipBlack flip(){
    			return WHITEORIENT;
    		}
    	};
    	
    	abstract List<TilePanel> iterate(final List<TilePanel> boardTiles);
    	abstract FlipBlack flip();
    }
    
    public class BoardPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		final List<TilePanel> boardTiles;

        public BoardPanel(){
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();

            for(int i = 0; i < BoardUtils.NUM_TILES; i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
            System.out.println("INSIDE CHESSTABLE/BOARDPANEL CONSTRUCTOR");
            System.out.println("------------------------------>\n");


        }

        public void drawBoard(final Board board){
            System.out.println("INSIDE ChessTable/BoardPanel: drawBoard()");
            System.out.println("------------------------------>\n");

            removeAll();
            for(final TilePanel tilePanel : orientation.iterate(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog implements Serializable{
		private static final long serialVersionUID = 1L;
		private final List<Move> moves;

        MoveLog(){
            System.out.println("INSIDE CHESSTABLE/MOVELOG CONSTRUCTOR");
            System.out.println("------------------------------>\n");
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves(){
            System.out.println("INSIDE ChessTable/MoveLog: getMoves()");
            System.out.println("------------------------------>\n");
            return this.moves;
        }

        public void addMove(final Move move){
            System.out.println("INSIDE ChessTable/MoveLog: addMove()");
            System.out.println("------------------------------>\n");
            this.moves.add(move);
        }

        public int size(){
            System.out.println("INSIDE ChessTable/MoveLog: size()");
            System.out.println("------------------------------>\n");
            return this.moves.size();
        }

        public void clear(){
            System.out.println("INSIDE ChessTable/MoveLog: clear()");
            System.out.println("------------------------------>\n");
            this.moves.clear();
        }

        public Move removeMove(int index){
            System.out.println("INSIDE ChessTable/MoveLog: removeMove(int index)");
            System.out.println("------------------------------>\n");
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move){
            System.out.println("INSIDE ChessTable/MoveLog: removeMove(final Move move)");
            System.out.println("------------------------------>\n");
            return this.moves.remove(move);
        }
    }

    private class TilePanel extends JPanel{
		private static final long serialVersionUID = 1L;
		private final int tileID;

        TilePanel(final BoardPanel boardPanel, final int tileID){
            super(new GridBagLayout());
            System.out.println("INSIDE CHESSTABLE/TILEPANEL CONSTRUCTOR");
            System.out.println("------------------------------>\n");
            this.tileID = tileID;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessboard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                	if(yourTurn) {
                	    if (isRightMouseButton(e)) {
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        else if(isLeftMouseButton(e)){
                            if(sourceTile == null){
                            	
                                sourceTile = chessboard.getTile(tileID);
                                humanMovedPiece = sourceTile.getPiece();
                                
                                try {
    								client.sendToServer(sourceTile);
    								client.sendToServer(humanMovedPiece);
    							} catch (IOException e1) {
    								e1.printStackTrace();
    							}
                                if(highlightLegalMoves){
                                	boardPanel.drawBoard(chessboard);
                                }
                                
                                if(humanMovedPiece == null) {
                                	sourceTile = null;
                                	System.out.println("First click");
                                }    
                            }
                            else if(sourceTile != chessboard.getTile(tileID)){
                            	destinationTile = chessboard.getTile(tileID);
                            	final Move move = Move.MoveFactory.createMove(chessboard, sourceTile.getTileCoord(),
                                        destinationTile.getTileCoord());

                            	final MoveTransition transition = chessboard.currentPlayer().makeMove(move);
                            	System.out.println(chessboard);
                            	if(!transition.getMoveStatus().isDone()) {
                            		sourceTile = null;
                            		humanMovedPiece = null;
                            		return;
                            	}
                            	if(transition.getMoveStatus().isDone()){
                            		chessboard = transition.getTransitionBoard();
                            		moveLog.addMove(move);
                            	}
                            	System.out.println(client.isConnected());
                            	client.setTile(tileID);
                            	try {
                            		client.sendToServer(moveLog);
    								client.sendToServer(chessboard);
    							} catch (IOException e1) {
    								e1.printStackTrace();
    							}
                            }
                            else if(sourceTile == chessboard.getTile(tileID)) {
                            	sourceTile = null;
                            	humanMovedPiece = null;
                            }
                        }
                		
                	}
                
                }

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
            });

            validate();
        }

        private void highlightLegals(final Board board){
            System.out.println("INSIDE ChessTable/TilePanel: highlightLegals()");
            System.out.println("------------------------------>\n");

            if(highlightLegalMoves){
                for(final Move move : pieceLegalMoves(board)){
                    if(move.getDestinationCoord() == this.tileID){
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("misc/green_dot.png")))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board){
            System.out.println("INSIDE ChessTable/TilePanel: pieceLegalMoves()");
            System.out.println("------------------------------>\n");

            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                System.out.println("ChessTable/pieceLegalMoves: legal moves found.");
                return humanMovedPiece.calculateLegalMoves(board);
            }

            return Collections.emptyList();
        }

        public void drawTile(final Board board){
            System.out.println("INSIDE ChessTable/TilePanel: drawTile()");
            System.out.println("------------------------------>\n");

            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(chessboard);
            validate();
            repaint();
        }

        private void assignTileColor() {
            System.out.println("INSIDE ChessTable/TilePanel: assignTileColor()");
            System.out.println("------------------------------>\n");

            if(BoardUtils.FIRST_ROW[this.tileID] ||
                BoardUtils.THIRD_ROW[this.tileID] ||
                BoardUtils.FIFTH_ROW[this.tileID] ||
                BoardUtils.SEVENTH_ROW[this.tileID]){
                setBackground(this.tileID % 2 == 0 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
            }
            else if(BoardUtils.SECOND_ROW[this.tileID] ||
                    BoardUtils.FOURTH_ROW[this.tileID] ||
                    BoardUtils.SIXTH_ROW[this.tileID] ||
                    BoardUtils.EIGHTH_ROW[this.tileID]){
                setBackground(this.tileID % 2 != 0 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
            }
        }

        private void assignTilePieceIcon(final Board board){
            System.out.println("INSIDE ChessTable/TilePanel: assignTilePieceIcon()");
            System.out.println("------------------------------>\n");

            this.removeAll();
            if(board.getTile(this.tileID).isOccupied()){

                try {
                    final BufferedImage image =
                            ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.tileID).getPiece().getPieceAlliance().toString().substring(0, 1) +
                                    board.getTile(this.tileID).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
