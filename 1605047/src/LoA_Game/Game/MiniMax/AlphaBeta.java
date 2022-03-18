package LoA_Game.Game.MiniMax;

import LoA_Game.Game.Controllers.GameControl;
import LoA_Game.Game.Controllers.Logics;
import LoA_Game.Game.StartGame;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;


public class AlphaBeta {
    private static final Logics Logics = new Logics();
    private static int MAXI, MINI, EMPTY;


    public static void assignMiniMax(int maxi, int mini, int empty) {
        MAXI = maxi;
        MINI = mini;
        EMPTY = empty;
    }

    // Done : returns an appropriate move {sx, sy} to {tx, ty}
    /*function ALPHA-BETA-SEARCH (state) returns an action
        v ← MAX-VALUE (state, −∞, +∞)
        return the action in ACTIONS (state) with value v*/
    public static Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> AlphaBetaSearch(int [][] state, int maxDepth) {
        return MaxValue(state, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE).getValue();
    }

    // Done
    private static Pair< Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> > MaxValue(int[][] state, int depth, int alpha, int beta) {
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> bestMove = new Pair<>(new Pair<>(-1, -1), new Pair<>(-1, -1));

        if(TerminalTest(state, depth)) return new Pair<>(Utility(state), bestMove);
        Pair<Boolean, Integer> endTest = EndTest(state);
        if(endTest.getKey()) return new Pair<>(endTest.getValue(), bestMove);

        int v = Integer.MIN_VALUE;

        ArrayList< Pair<Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer>>> > actions = Actions(state, true);
        /* System.out.println("Hello I'm printing the state.......");
        for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
            for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
                System.out.print(state[x][y] + " ");
            }
            System.out.println();
        }
        System.out.println("Hello I'm printing my moves......");

        for(Pair<Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer>>> pieceMoves : actions) {
            for(Pair<Integer, Integer> target : pieceMoves.getValue()) {
                System.out.println("Source : (" + pieceMoves.getKey().getKey() + ", " + pieceMoves.getKey().getValue() + ")");
                System.out.println("Target : (" + target.getKey() + ", " + target.getValue() + ")");
            }
        }*/


        for(Pair<Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer>>> pieceMoves : actions) {
            Pair<Integer, Integer> source = pieceMoves.getKey();
            for(Pair<Integer, Integer> target : pieceMoves.getValue()) {
                // for each a in Actions(State) do
                Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> ret =
                        MinValue(Result(state, new Pair<>(source, target)), depth - 1, alpha, beta);
                if(v < ret.getKey()) {
                    v = ret.getKey();
                    bestMove = new Pair<>(source, target);
                }
                if(v >= beta) return new Pair<>(v, bestMove); // prune and return
                alpha = Math.max(alpha, v);
            }
        }
        return new Pair<>(v, bestMove);
    }

    // Done
    private static Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> MinValue(int[][] state, int depth, int alpha, int beta) {
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> bestMove = new Pair<>(new Pair<>(-1, -1), new Pair<>(-1, -1));

        if(TerminalTest(state, depth)) return new Pair<>(Utility(state), bestMove);
        Pair<Boolean, Integer> endTest = EndTest(state);
        if(endTest.getKey()) return new Pair<>(endTest.getValue(), bestMove);


        int v = Integer.MAX_VALUE;

        ArrayList< Pair<Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer>>> > actions = Actions(state, false);

        for(Pair<Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer>>> pieceMoves : actions) {
            Pair<Integer, Integer> source = pieceMoves.getKey();
            for(Pair<Integer, Integer> target : pieceMoves.getValue()) {
                // for each a in Actions(State) do
                Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> ret =
                        MaxValue(Result(state, new Pair<>(source, target)), depth - 1, alpha, beta);
                if(v > ret.getKey()) {
                    v = ret.getKey();
                    bestMove = new Pair<>(source, target);
                }
                if(v <= alpha) return new Pair<>(v, bestMove); // prune and return
                beta = Math.min(beta, v);
            }
        }
        return new Pair<>(v, bestMove);
    }

    private static Pair<Boolean, Integer> EndTest(int[][] state) {
        int endState = Logics.isEndGame(state);
        if(endState == 0) return new Pair<>(true, 10);
        if(endState == MAXI) return new Pair<>(true, 1000);
        if(endState == MINI) return new Pair<>(true, -1000);
        return new Pair<>(false, 0);
    }

    // may be elaborated
    private static boolean TerminalTest(int [][] state, int depth) {
        return depth <= 0;
    }

    // Done : returns a new state
    private static int[][] Result(int[][] state, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> action) {
        int[][] nextState = new int[StartGame.BOARD_SIZE][StartGame.BOARD_SIZE];
        for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
            for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
                nextState[x][y] = state[x][y];
            }
        }

        int sx, sy, tx, ty;
        sx = action.getKey().getKey();
        sy = action.getKey().getValue();
        tx = action.getValue().getKey();
        ty = action.getValue().getValue();

        int temp = nextState[sx][sy];
        nextState[sx][sy] = nextState[tx][ty];
        nextState[tx][ty] = temp;

        return nextState;
    }

    // Done     list of { {}, list of{}s }s
    private static ArrayList< Pair< Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer> >> > Actions(int [][] grid, boolean isMaxesTurn) {
        // Generate children of that state
        ArrayList< Pair<Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer>>> > actions = new ArrayList<>();
        // generate function to get all possible legal moves
        for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
            for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
               if((isMaxesTurn && grid[x][y]==MAXI) || (!isMaxesTurn && grid[x][y]==MINI)) {
                   Pair<Integer, Integer> oldPosition = new Pair<>(x, y);
                   ArrayList<Pair<Integer, Integer>> newPositions = GameControl.findMoves(oldPosition, grid);
                   Collections.shuffle(newPositions);
                   actions.add(new Pair<>(oldPosition, newPositions));
               }
            }
        }
        Collections.shuffle(actions);
        return actions;
    }

    // Done : returns +ve or -ve or 0
    private static int Utility(int [][] state) {
        // returns the utility value of this state for max
        return Heuristics.heuristicsValue(state, MAXI, MINI);
    }

}
