package com.chess.gui;

import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.chess.board.ChessTile;
import com.chess.board.Move;
import com.chess.pieces.Piece;
import com.chess.player.MoveTransition;
import com.google.common.collect.Lists;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {

    private ChessTile sourceTile;
    private ChessTile destinationTile;
    private Piece userMovedPiece;
    private BoardDirection boardDirection;

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final MoveLog moveLog;
    private final BoardPanel boardPanel;
    private Board chessBoard;
    private static Dimension OUTER_DIMENSION = new Dimension(600, 600);
    private static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private Color lightTileColor = Color.decode("#FFFACD");
    private Color darkTileColor = Color.decode("#593E1A");
    private boolean highlightLegalMove;

    private String simplePieceIconPath = "display/simple/";


    public Table(){
        chessBoard = Board.createStandardBoard();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.gameHistoryPanel = new GameHistoryPanel();
        boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMove = true;
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar( tableMenuBar);
        this.gameFrame.setSize(OUTER_DIMENSION);
        this.boardPanel = new BoardPanel();
        gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.moveLog = new MoveLog();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
    }

    private boolean getHighlightLegalMove(){
        return this.highlightLegalMove;
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    private static JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open PGN file");
            }
        });
        fileMenu.add(openPGN);
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

    private class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;
        BoardPanel(){
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for(int i = 0 ; i < BoardUtils.NUM_TILES ; i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoardPanel(Board board) {
            removeAll();
            for(final TilePanel tilePanel: boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    private class TilePanel extends  JPanel{
        private final int tileId;

        private void setAllNull(){
            sourceTile = null;
            destinationTile = null;
            userMovedPiece = null;
        }

        TilePanel(final BoardPanel boardPanel,
                  final int tileId){
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);


            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(isRightMouseButton(e)){
                        setAllNull();
                    }else if(isLeftMouseButton(e)){
                        if(sourceTile == null){
                            sourceTile = chessBoard.getTile(tileId);
                            userMovedPiece = sourceTile.getPiece();
                            if(userMovedPiece == null){
                                setAllNull();
                            }
                        }else{
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if(transition.getMovesStatus().isDone()){
                                chessBoard = transition.getBoard();
                                moveLog.addMove(move);
                            }
                            setAllNull();
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                boardPanel.drawBoardPanel(chessBoard);
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });

            validate();
        }

        private void assignTileColor() {
            boolean isLight = ((this.tileId + this.tileId / 8) % 2 == 0);
            setBackground(isLight ? lightTileColor : darkTileColor);
        }

        private void assignTilePieceIcon(final Board board){
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()){

                try {
                    final BufferedImage image = ImageIO.read(new File(simplePieceIconPath +
                            board.getTile(this.tileId).getPiece()
                                    .getPieceType()
                                    .toString()
                                    .substring(0, 1)
                            + board.getTile(this.tileId).getPiece().toString() + ".gif")
                    );
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void drawTile(Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegalMoves(board);
            validate();
            repaint();
        }

        private void highlightLegalMoves(final Board board){
            if(highlightLegalMove){
                for(final Move move: pieceLegalMoves(board)){
                    if(move.getDestinationPosition() == this.tileId){
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("display/misc/green_dot.png")))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private List<Move> pieceLegalMoves(Board board){
            if(userMovedPiece != null && userMovedPiece.getPieceType() == board.currentPlayer().getPieceType()){
                return userMovedPiece.calcLegalMoves(board);
            }else{
                return Collections.emptyList();
            }
        }
    }

    private JMenu createPreferencesMenu(){
        final JMenu prefMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoardPanel(chessBoard) ;
            }
        });
        prefMenu.add(flipBoardMenuItem);

        prefMenu.addSeparator();
        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Moves", false);
        legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMove = legalMoveHighlighterCheckbox.isSelected();
            }
        });
        prefMenu.add(legalMoveHighlighterCheckbox);

        return prefMenu;
    }

    public enum BoardDirection{
        NORMAL{
            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }

            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }
        },
        FLIPPED{
            @Override
            BoardDirection opposite() {
                return NORMAL;
            }

            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }
        };
        abstract BoardDirection opposite();
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
    }

    public static class MoveLog{
        private final List<Move> moves;
        MoveLog(){
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return moves;
        }

        public void addMove(final Move move){
            this.moves.add(move);
        }

        public int size(){
            return this.moves.size();
        }

        public void clear(){
            this.moves.clear();
        }

        public Move removeMove(final int index){
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }
    }


}
