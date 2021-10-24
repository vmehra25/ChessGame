package com.chess;

import com.chess.board.Board;
import com.chess.gui.Table;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Chess {
    public static void main(String[] args) {
        Board board = Board.createStandardBoard();
        System.out.println(board);
        Table table = new Table();

    }


}
