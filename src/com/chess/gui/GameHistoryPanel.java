package com.chess.gui;

import com.chess.board.Board;
import com.chess.board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryPanel extends JPanel{
    private final DataModel dataModel;
    private final JScrollPane jScrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400);

    GameHistoryPanel(){
        this.setLayout(new BorderLayout());
        this.dataModel = new DataModel();
        final JTable jTable = new JTable(dataModel);
        jTable.setRowHeight(15);
        this.jScrollPane = new JScrollPane(jTable);
        this.jScrollPane.setColumnHeaderView(jTable.getTableHeader());
        jScrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board,
              final Table.MoveLog moveLog){
        int currentRow = 0;
        this.dataModel.clear();
        for(final Move move: moveLog.getMoves()){
            final String moveText = move.toString();
            if(move.getMovedPiece().getPieceType().isWhite()){
                this.dataModel.setValueAt(moveText, currentRow, 0);
            }else if(move.getMovedPiece().getPieceType().isBlack()){
                this.dataModel.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }
        if(moveLog.getMoves().size() > 0){
            final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
            String moveText = lastMove.toString();
            if(lastMove.getMovedPiece().getPieceType().isWhite()){
                this.dataModel.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
            }else if(lastMove.getMovedPiece().getPieceType().isBlack()){
                this.dataModel.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow-1, 1);
            }
        }

        final JScrollBar scrollBar = jScrollPane.getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());
    }

    private String calculateCheckAndCheckMateHash(final Board board) {
        if(board.currentPlayer().isInCheckMate()){
            return "#";
        }else if(board.currentPlayer().isInChecked()){
            return "+";
        }else{
            return "";
        }
    }

    private static class DataModel extends DefaultTableModel{
        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel(){
            this.values = new ArrayList<>();
        }

        public void clear(){
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if(this.values == null){
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            final Row currentRow = this.values.get(row);
            if(column == 0){
                return currentRow.getWhiteMove();
            }else if(column == 1){
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            final Row currentRow;
            if(this.values.size() <= row){
                currentRow = new Row();
                this.values.add(currentRow);
            }else{
                currentRow = this.values.get(row);
            }
            if(column == 0){
                currentRow.setWhiteMove((String) aValue);
                fireTableRowsInserted(row, row);
            }else if(column == 1){
                currentRow.setBlackMove((String) aValue);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Move.class;
        }

        @Override
        public String getColumnName(int column) {
            return NAMES[column];
        }
    }

    private static class Row{
        private String whiteMove;
        private String blackMove;

        Row(){

        }

        public String getWhiteMove(){
            return this.whiteMove;
        }

        public String getBlackMove() {
            return this.blackMove;
        }

        public void setBlackMove(String blackMove) {
            this.blackMove = blackMove;
        }

        public void setWhiteMove(String whiteMove) {
            this.whiteMove = whiteMove;
        }


    }



}
