package LoA_Game.Game.PieceClasses;

import LoA_Game.Game.StartGame;

public class Empty extends Piece {
	public static final int EMPTY_PLAYER = 0, WHITE_PLAYER = 1, BLACK_PLAYER = 2;
	private int i;
	private int j;
	private String name = "Empty";

	public Empty(int type, int ii, int jj) {
		super(type);
		i = ii;
		j = jj;
	}

	@Override
	public String toString(){
		return name;
	}

	@Override
	public int icoord(){
		// Bounds correction
		if(i > StartGame.BOARD_SIZE )
			return StartGame.BOARD_SIZE - 1;
		if(i < 0)
			return 0;
		else
			return i;
	}

	@Override
	public int jcoord() {
		// Bounds correction
		if(j > StartGame.BOARD_SIZE )
			return StartGame.BOARD_SIZE - 1;
		if(j < 0)
			return 0;
		else
			return j;
	}

}
