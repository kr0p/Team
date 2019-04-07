package chess.network;

import chess.engine.board.Board;
import chess.engine.board.Tile;
import chess.engine.pieces.Piece;
import chess.gui.ChessTable;
import chess.gui.ChessTable.MoveLog;
import ocsf.client.AbstractClient;

public class ChessClient extends AbstractClient {
	private Board board;
	protected ChessTable chesstable;
	protected ChessTable chesstable2;
	private boolean allConnected;
	private ChessClient client;
	private int tileID;
	private Tile sourceTile;
	private Piece humanMovedPiece;
	private MoveLog moveLog;
	private boolean isWhite;
	private boolean isBlack;
	
	public ChessClient(String host, int port) {
		super(host, port);
	}

	public void setClient(ChessClient client) {
		this.client = client;
	}
	
	public void setSourceTile(Tile sourceTile) {
		this.sourceTile = sourceTile;
	}
	
	public void setMovedPiece(Piece humanMovedPiece) {
		this.humanMovedPiece = humanMovedPiece;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
	
	public boolean isWhite() {
		return isWhite;
	}
	
	public boolean isBlack() {
		return isBlack;
	}
	
	public void setMoveLog(MoveLog moveLog) {
		this.moveLog = moveLog;
	}
	
	public void setTile(int tileID) {
		this.tileID = tileID;
	}
	
	@Override
	protected void handleMessageFromServer(Object arg0) {
		
		if(arg0 instanceof Board && allConnected == true) {
			setBoard((Board) arg0);
			Thread update = new Thread(new Runnable() {

				@Override
				public void run() {
					chesstable.updateBoard(board, tileID, sourceTile, humanMovedPiece, moveLog);
				}
			});
			update.start();
			System.out.println(board);
		}
		if(arg0 instanceof Board && allConnected == false) {
			allConnected = true;
			setBoard((Board) arg0);
			Thread setup = new Thread(new Runnable() {

				@Override
				public void run() {
					chesstable = new ChessTable(board, client, isWhite, isBlack);
				}
			});
			setup.start();
		}
		if(arg0 instanceof Tile) {
			setSourceTile((Tile) arg0);
			System.out.println(sourceTile);
		}
		if(arg0 instanceof Piece) {
			setMovedPiece((Piece) arg0);
			System.out.println(humanMovedPiece);
		}
		if(arg0 instanceof MoveLog) {
			setMoveLog((MoveLog) arg0);
		}
		if(arg0 instanceof Long) {
			if((Long) arg0 == 21) {
				this.isWhite = true;
				this.isBlack = false;
			}
			if((Long) arg0 == 22) {
				this.isWhite = false;
				this.isBlack = true;
			}
		}
	}
}
