package chess.gui;

import chess.engine.board.Board;
import chess.engine.board.Move;
import com.sun.rowset.internal.Row;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400);

    GameHistoryPanel(){
        System.out.println("INSIDE GAMEHISTORYPANEL CONSTRUCTOR");
        System.out.println("------------------------------>\n");
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board, final ChessTable.MoveLog moveLog){
        System.out.println("INSIDE GameHistoryPanel: redo()");
        System.out.println("------------------------------>\n");
        int currentRow = 0;
        this.model.clear();

        for(final Move move : moveLog.getMoves()){
            final String moveText = move.toString();

            if(move.getMovedPiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText, currentRow, 0);
            }
            else if(move.getMovedPiece().getPieceAlliance().isBlack()){
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }

        if(moveLog.getMoves().size() > 0){
            final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
            final String moveText = lastMove.toString();

            if(lastMove.getMovedPiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board),
                                    currentRow, 0);
            }
            else if(lastMove.getMovedPiece().getPieceAlliance().isBlack()){
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board),
                                    currentRow - 1, 1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private String calculateCheckAndCheckMateHash(final Board board) {
        System.out.println("INSIDE GameHistoryPanel: calculateCheckAndCheckMateHash()");
        System.out.println("------------------------------>\n");
        if(board.currentPlayer().isInCheckMate()){
            return "#";
        }
        else if(board.currentPlayer().isInCheck()){
            return "+";
        }

        return "";
    }

    private static class DataModel extends DefaultTableModel{
		private static final long serialVersionUID = 1L;
		private final List<GameHistoryPanel.Row> values;
        private static final String[] NAMES = { "White", "Black" };

        DataModel(){
            System.out.println("INSIDE GAMEHISTORYPANEL/DATAMODEL CONSTRUCTOR");
            System.out.println("------------------------------>\n");
            this.values = new ArrayList<>();
        }

        public void clear(){
            System.out.println("INSIDE GameHistoryPanel/DataModel: clear()");
            System.out.println("------------------------------>\n");
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount(){
            System.out.println("INSIDE GameHistoryPanel/DataModel: getRowCount()");
            System.out.println("------------------------------>\n");
            if(this.values == null)
                return 0;

            System.out.println("SIZE: " + this.values.size());
            return this.values.size();
        }

        @Override
        public int getColumnCount(){
            System.out.println("INSIDE GameHistoryPanel/DataModel: getColumnCount()");
            System.out.println("------------------------------>\n");
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column){
            System.out.println("INSIDE GameHistoryPanel/DataModel: getValueAt()");
            System.out.println("------------------------------>\n");
            final GameHistoryPanel.Row currentRow = this.values.get(row);

            if(column == 0){
                return currentRow.getWhiteMove();
            }
            else if(column == 1){
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object value, final int row, final int column){
            System.out.println("INSIDE GameHistoryPanel/DataModel: setValueAt()");
            System.out.println("------------------------------>\n");
            final GameHistoryPanel.Row currentRow;

            if(this.values.size() <= row){
                currentRow = new GameHistoryPanel.Row();
                this.values.add(currentRow);
            }
            else{
                currentRow = this.values.get(row);
            }

            if(column == 0){
                currentRow.setWhiteMove((String) value);
                fireTableRowsInserted(row, row);
            }
            else if(column == 1){
                currentRow.setBlackMove((String) value);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column){
            System.out.println("INSIDE GameHistoryPanel/DataModel: getColumnClass(): returns the Move class");
            System.out.println("------------------------------>\n");
            return Move.class;
        }

        @Override
        public String getColumnName(final int column){
            System.out.println("INSIDE GameHistoryPanel/DataModel: getColumnName()");
            System.out.println("------------------------------>\n");
            return NAMES[column];
        }
    }

    private static class Row{
        private String whiteMove;
        private String blackMove;

        Row(){
            System.out.println("INSIDE GAMEHISTORYPANEL/ROW CONSTRUCTOR");
            System.out.println("------------------------------>\n");
        }

        public String getWhiteMove(){
            System.out.println("INSIDE GameHistoryPanel/Row: getWhiteMove()");
            System.out.println("------------------------------>\n");
            return this.whiteMove;
        }

        public String getBlackMove(){
            System.out.println("INSIDE GameHistoryPanel/Row: getBlackMove()");
            System.out.println("------------------------------>\n");
            return this.blackMove;
        }

        public void setWhiteMove(final String move){
            System.out.println("INSIDE GameHistoryPanel/Row: setWhiteMove()");
            System.out.println("------------------------------>\n");
            this.whiteMove = move;
        }

        public void setBlackMove(final String move){
            System.out.println("INSIDE GameHistoryPanel/Row: setBlackMove()");
            System.out.println("------------------------------>\n");
            this.blackMove = move;
        }
    }
}
