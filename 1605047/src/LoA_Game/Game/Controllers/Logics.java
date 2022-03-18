package LoA_Game.Game.Controllers;
import LoA_Game.Game.PieceClasses.Piece;
import LoA_Game.Game.StartGame;


/**
 *This is the logic class for check resolve it & check for checkmates stalemate etc
 * @author Ahsanul Ameen Sabit
 */



public class Logics implements Cloneable {//having fun? Object(clone) experimental..
    //can you make  a new clone of your obj with a  same name???isn't it amazing???
    /**
     * they are filled inside #check4check() function
     */
    public static final int WHITE_PLAYER = 1,
                            BLACK_PLAYER = 2,
                            EMPTY_PLAYER = 0;

    private static final int [][] dir8 = {{0, +1}, {0, -1}, {+1, 0}, {-1, 0}, {+1, +1}, {+1, -1}, {-1, +1}, {-1, -1}};



    // TODO : check if game is ended
    // -1 : NO WIN
    // 0 : BOTH WIN / DRAW
    // 1 : WHITE WIN
    // 2 : BLACK WIN
    public int isEndGame(int[][] currentBoardState) {
        int callCount1 = 0;
        int callCount2 = 0;
        boolean [][] flagged = new boolean[StartGame.BOARD_SIZE][StartGame.BOARD_SIZE];
        for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
            for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
                if(currentBoardState[x][y] == WHITE_PLAYER && !flagged[x][y]) {
                    dfs(x, y, WHITE_PLAYER, flagged, currentBoardState);
                    ++callCount1;
                }
                if(currentBoardState[x][y] == BLACK_PLAYER && !flagged[x][y]) {
                    dfs(x, y, BLACK_PLAYER, flagged, currentBoardState);
                    ++callCount2;
                }
            }
        }

        /*for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
            for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {

            }
        }*/
        if(callCount1 == 1 && callCount2 == 1) return 0; // draw
        else if(callCount1 == 1) return 1; // win (WHITE)
        else if(callCount2 == 1) return 2; // win (BLACK)
        else return -1; // no win
    }

    private static boolean isSafe(int x, int y) { return x >= 0 && x < StartGame.BOARD_SIZE && y >= 0 && y < StartGame.BOARD_SIZE; }

    private void dfs(int x, int y, int playerType, boolean[][] flagged, int[][] grid) {
        flagged[x][y] = true;
        for(int[] a : dir8) {
            int xx = x + a[0], yy = y + a[1];
            if(isSafe(xx, yy) && grid[xx][yy] == playerType && !flagged[xx][yy]) {
                dfs(xx, yy, playerType, flagged, grid);
            }
        }
    }

    public static int[][] piecesToGrid(Piece[][] boardState) {
        int n = StartGame.BOARD_SIZE;
        int[][] grid = new int[StartGame.BOARD_SIZE][StartGame.BOARD_SIZE];
        for(int i = 0; i < n; ++i) {
            for(int j = 0; j < n; ++j) {
                grid[i][j] = boardState[i][j].type();
            }
        }
        return grid;
    }
}