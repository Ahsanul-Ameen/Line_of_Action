package LoA_Game.Game.PieceClasses;

import LoA_Game.Game.StartGame;
import javafx.scene.image.Image;

public class PieceCircle extends Piece {
    public static final int WHITE_PLAYER = 1,BLACK_PLAYER = 2,EMPTY_PLAYER = 0;
    public String name = "Circle";
    public String imgName = "_man.png";
    private Piece[][] boardState;
    private int i;
    private int j;
    private int te;

    public PieceCircle(int type, int ii, int jj) {
        super(type);
        te=type;
        i = ii;
        j = jj;
    }

    @Override
    public Image image(){
        if(te==1) return new Image("/LoA_Game/CursorPng/white_man_cursor.png");
        else return new Image("/LoA_Game/CursorPng/black_man_cursor.png");
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public String imageFileName() { return imgName; }

    @Override
    public int icoord(){
        // Bounds correction
        if(i > StartGame.BOARD_SIZE)
            return StartGame.BOARD_SIZE - 1;
        if(i < 0)
            return 0;
        else
            return i;
    }

    @Override
    public int jcoord() {
        // Bounds correction
        if(j > StartGame.BOARD_SIZE)
            return StartGame.BOARD_SIZE - 1;
        if(j < 0)
            return 0;
        else
            return j;
    }

    public Piece[][] moveCircle(Piece c, Piece t, Piece[][] bs) {
        boardState = bs;
        // Move circle
        boardState[t.icoord()][t.jcoord()] = new PieceCircle(c.type(), t.icoord(), t.jcoord()); // move to new cell
        boardState[c.icoord()][c.jcoord()] = new Empty(Piece.EMPTY_PLAYER, c.icoord(), c.jcoord()); // leave previous cell
        // Return the new board
        return boardState;
    }
}
