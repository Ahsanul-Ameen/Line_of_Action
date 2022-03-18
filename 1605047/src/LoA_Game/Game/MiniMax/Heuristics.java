package LoA_Game.Game.MiniMax;

import LoA_Game.Game.Controllers.GameControl;
import LoA_Game.Game.StartGame;
import javafx.util.Pair;

import java.util.ArrayList;

public class Heuristics {
    
    public static int heuristicsValue(int[][] state, int max, int min) {
        return pieceSquareTable(state, max, min)
                + area(state, max, min)
                + mobility(state, max, min) * 5
                + connectedness(state, max, min)
                + quad(state, max, min) * 4
                + density(state, max, min) * 7;
    }
    
    // Heuristics starts from here

    // maximizing
    private static int pieceSquareTable(int[][] state, int max, int min) {
        int[][] pieceSquareTable8 =  {
                                        {-80, -25, -20, -20, -20, -20, -25, -80},
                                        {-25,  10,  10,  10,  10,  10,  10, -25},
                                        {-20,  10,  25,  25,  25,  25,  10, -20},
                                        {-20,  10,  25,  50,  50,  25,  10, -20},
                                        {-20,  10,  25,  50,  50,  25,  10, -20},
                                        {-20,  10,  25,  25,  25,  25,  10, -20},
                                        {-25,  10,  10,  10,  10,  10,  10, -25},
                                        {-80, -25, -20, -20, -20, -20, -25, -80}
                                    };
        int[][] pieceSquareTable6 = {
                                        {-60, -25, -20, -20, -25, -60},
                                        {-25,  10,  10,  10,  10, -25},
                                        {-20,  10,  45,  45,  10, -20},
                                        {-20,  10,  45,  45,  10, -20},
                                        {-25,  10,  10,  10,  10, -25},
                                        {-60, -25, -20, -20, -25, -60}
                                    };
        int[][] values = (StartGame.BOARD_SIZE == 8) ? pieceSquareTable8 : pieceSquareTable6;
        int sumMax = 0;
        int sumMin = 0;
        for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
            for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
                if(state[x][y] == max) {
                    sumMax += values[x][y];
                }
                if(state[x][y] == min) {
                    sumMax += values[x][y];
                }
            }
        }
        return sumMax - sumMin;
    }

    // minimizing
    private static int area(int[][] state, int max, int min) {
        int lowX = StartGame.BOARD_SIZE - 1;
        int highX = 0;
        int lowY = StartGame.BOARD_SIZE - 1;
        int highY = 0;
        int lx = lowX, hx = highX, ly = lowY, hy = highY;
        for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
            for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
                if(state[x][y] == max) {
                    lowX = Math.min(lowX, x);
                    highX = Math.max(highX, x);
                    lowY = Math.min(lowY, y);
                    highY = Math.max(highY, y);
                }
                if(state[x][y] == min) {
                    lx = Math.min(lx, x);
                    hx = Math.max(hx, x);
                    ly = Math.min(ly, y);
                    hy = Math.max(hy, y);
                }
            }
        }
        int maxArea = Math.max(0, (highX - lowX) * (highY - lowY));
        int minArea = Math.max(0, (hx - lx) * (hy - ly));
        return minArea - maxArea;
    }

    // maximizing
    private static int mobility(int[][] state, int max, int min) {
        return mobilityUtil(state, max) - mobilityUtil(state, min);
    }
    private static int mobilityUtil(int[][] state, int type) {
        int tx, ty;
        int mobilityValue = 0;
        for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
            for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
                if(state[x][y] == type) {
                    ArrayList <Pair<Integer, Integer>> moves = GameControl.findMoves(new Pair<>(x, y), state);
                    for(Pair<Integer, Integer> target : moves) {
                        tx = target.getKey();
                        ty = target.getValue();

                        assert (isBounded(tx, ty));

                        mobilityValue++;
                        if(state[tx][ty] != StartGame.EMPTY_PLAYER && state[tx][ty] != type) {
                            // capture move
                            mobilityValue++;
                        }
                    }
                }
            }
        }
        return mobilityValue;
    }

    // maximizing
    private static int connectedness(int[][] state, int max, int min) {
        int maxConnectedNess = 0;
        int minConnectedNess = 0;
        int maxPieces = 0;
        int minPieces = 0;
        int tx, ty;
        final int[][] dir8 = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
            for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
                if(state[x][y] == max) {
                    ++maxPieces;
                    for(int[] dir : dir8) {
                        tx = x + dir[0];
                        ty = y + dir[1];
                        if(isBounded(tx, ty) && state[tx][ty] == max) {
                            ++maxConnectedNess;
                        }
                    }
                }
                if(state[x][y] == min) {
                    ++minPieces;
                    for(int[] dir : dir8) {
                        tx = x + dir[0];
                        ty = y + dir[1];
                        if(isBounded(tx, ty) && state[tx][ty] == min) {
                            ++minConnectedNess;
                        }
                    }
                }
            }
        }
        return (int) Math.round((1.0 * maxConnectedNess / maxPieces) - (1.0 * minConnectedNess / minPieces));
    }

    // maximizing
    private static int quad(int[][] state, int max, int min) {
        return quadUtil(state, max) - quadUtil(state, min);
    }

    private static int quadUtil(int[][] state, int type) {
        int cx, cy;
        Pair<Integer, Integer> com = CoM(state, type);
        cx = com.getKey();
        cy = com.getValue();

        assert (isBounded(cx, cy));

        int distance = 2;
        int ltx = Math.max(0, cx - distance);
        int lty = Math.max(0, cy - distance);
        int rbx = Math.min(StartGame.BOARD_SIZE - 1, cx + distance);
        int rby = Math.min(StartGame.BOARD_SIZE - 1, cy + distance);
        
        int quadValue = 0;
        for(int i = ltx; i < rbx; ++i) {
            for(int j = lty; j < rby; ++j) {
                // i, j is top left corner of the quad
                int pc = 0;
                assert (isBounded(i, j));
                if(state[i][j] == type) ++pc;
                assert (isBounded(i + 1, j));
                if(state[i + 1][j] == type) ++pc;
                assert (isBounded(i + 1, j + 1));
                if(state[i + 1][j + 1] == type) ++pc;
                assert (isBounded(i, j + 1));
                if(state[i][j + 1] == type) ++pc;
                if(pc >= 3) {
                    // found q3 or q4
                    ++quadValue;
                }
            }
        }
        return quadValue;
    }

    // using distance : minimizing
    private static int density(int[][] state, int max, int min) {
        return densityUtil(state, min) - densityUtil(state, max);
    }

    private static int densityUtil(int[][] state, int type) {
        int cx, cy;
        Pair<Integer, Integer> com = CoM(state, type);
        cx = com.getKey();
        cy = com.getValue();

        double distance = 0;
        int pieceCnt = 0;
        for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
            for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
                if(state[x][y] == type) {
                    /*distance += Math.abs(cx - x);
                    distance += Math.abs(cy - y);*/
                    distance += Math.sqrt(Math.pow(cx - x, 2) + Math.pow(cy - y, 2));
                    ++pieceCnt;
                }
            }
        }
        //return distance;
        return (int) Math.round(distance/pieceCnt);
    }

    // center of mass
    private static Pair<Integer, Integer> CoM(int[][] state, int type) {
        int cx = 0;
        int cy = 0;
        int pieceCnt = 0;
        for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
            for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
                if(state[x][y] == type) {
                    ++pieceCnt;
                    cx += x; cy += y;
                }
            }
        }
        cx /= pieceCnt;
        cy /= pieceCnt;

        assert (isBounded(cx, cy));

        return new Pair<>(cx, cy);
    }

    private static boolean isBounded(int cx, int cy) {
        return  (cx >= 0 && cx < StartGame.BOARD_SIZE && cy >= 0 && cy < StartGame.BOARD_SIZE);
    }

}
