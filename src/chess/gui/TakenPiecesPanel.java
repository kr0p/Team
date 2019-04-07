package chess.gui;

import chess.engine.board.Move;
import chess.engine.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class TakenPiecesPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JPanel north;
    private final JPanel south;

    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);
    private static final Color PANEL_COLOR = Color.decode("0xFDFE6");
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    public TakenPiecesPanel(){
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.north = new JPanel(new GridLayout(8, 2));
        this.south = new JPanel(new GridLayout(8, 2));
        this.north.setBackground(PANEL_COLOR);
        this.south.setBackground(PANEL_COLOR);

        this.add(this.north, BorderLayout.NORTH);
        this.add(this.south, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
        System.out.println("INSIDE TAKENPIECESPANEL CONSTRUCTOR");
        System.out.println("------------------------------>\n");
    }

    public void redo(final ChessTable.MoveLog moveLog){
        System.out.println("INSIDE TakenPiecesPanel: redo()");
        System.out.println("------------------------------>\n");
        this.south.removeAll();
        this.north.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for(final Move move : moveLog.getMoves()){
            if(move.isAttack()){
                final Piece takenPiece = move.getAttackedPiece();

                if(takenPiece.getPieceAlliance().isWhite())
                    whiteTakenPieces.add(takenPiece);
                else if(takenPiece.getPieceAlliance().isBlack())
                    blackTakenPieces.add(takenPiece);
                else
                    throw new RuntimeException("You've reached the twilight zone");
            }
        }

        Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Integer.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        Collections.sort(blackTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Integer.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        for(final Piece takenPiece : whiteTakenPieces){
            try{
                final BufferedImage image = ImageIO.read(new File("simple/" +
                        takenPiece.getPieceAlliance().toString().substring(0, 1) +
                        "" + takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel iLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance
                        (icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.south.add(iLabel);
            }catch(final IOException e){
                e.printStackTrace();
            }
        }

        for(final Piece takenPiece : blackTakenPieces){
            try{
                final BufferedImage image = ImageIO.read(new File("simple/" +
                        takenPiece.getPieceAlliance().toString().substring(0, 1) +
                        "" + takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel iLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance
                        (icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.north.add(iLabel);
            }catch(final IOException e){
                e.printStackTrace();
            }
        }
        validate();

    }
}
