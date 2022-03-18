package LoA_Game.Game.Controllers;


/**
 * This is the sample controller class which handles the whole netWorking issues
 * @author  Ahsanul Ameen Sabit
 * */

import LoA_Game.Game.MiniMax.AlphaBeta;
import LoA_Game.Game.PieceClasses.Piece;
import LoA_Game.Game.StartGame;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.Control;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;
import javafx.util.Pair;
import java.util.ArrayList;



public class GameControl extends Control {
    public static final int WHITE_PLAYER = 1, BLACK_PLAYER = 2, EMPTY_PLAYER = 0;

    private int si,sj;
    private int ti,tj;
    private Piece selectedPiece;
    private boolean winner=false;
    private int aiPlayerType;

    private BoardImplementation checkerBoard;
    private final Translate pos;
    private final LoA_Game.Game.Controllers.Logics Logics;
    private int hash;

    public synchronized void makeTurnTrue() {
        String s = (checkerBoard.currentplayer() == WHITE_PLAYER) ? "WHITE" : "BLACK";
        StartGame.typeLabel.setText("Current Player : " + s);
        System.out.println("===========");
    }

    public GameControl() {
        pos = new Translate();
        setSkin(new GameControlSkin(this));
        checkerBoard = new BoardImplementation();
        Logics = new Logics();
        getChildren().addAll(checkerBoard);

        // Places background squares
        for(int x = 0; x < StartGame.BOARD_SIZE; x++) {
            for(int y = 0; y < StartGame.BOARD_SIZE; y++) {
                checkerBoard.placeBoard(x, y);
            }
        }

        // Places chess piece images
        for(int x = 0; x < StartGame.BOARD_SIZE; x++) {
            for(int y = 0; y < StartGame.BOARD_SIZE; y++) {
                checkerBoard.placeImageViews(x, y);
            }
        }

        //when pressing space bar .. optional
        setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.SPACE) System.out.println("|| Game Reset! ||");

            checkerBoard = new BoardImplementation();
            getChildren().addAll(checkerBoard);

            // Places background squares
            for(int x = 0; x < StartGame.BOARD_SIZE; x++){
                for(int y = 0; y < StartGame.BOARD_SIZE; y++){
                    checkerBoard.placeBoard(x, y);
                }
            }

            // Places chess piece images
            for(int x = 0; x < StartGame.BOARD_SIZE; x++){
                for(int y = 0; y < StartGame.BOARD_SIZE; y++){
                    checkerBoard.placeImageViews(x, y);
                }
            }

            winner = false;
            if(StartGame.isAIBOT && aiPlayerType == StartGame.BLACK_PLAYER)
                StartGame.isAIsTurn = true;

            checkerBoard.changeclickfalse();
        });

        // when mouse is clicked on the board
        setOnMouseClicked(event -> {
            try {
                hash = event.getTarget().hashCode(); //clean totally
                ImageView [][]selectView = checkerBoard.getImageviews();
                Rectangle[][] targetSelect = checkerBoard.getBoard();

                boolean founds = false; //clean
                for(int x=0; x < StartGame.BOARD_SIZE && !founds; x++) {
                    for(int y=0; y<StartGame.BOARD_SIZE && !founds; y++) {
                        if(selectView[x][y].hashCode() == hash && selectView[x][y] != null) {
                            si = x; sj = y;
                            founds = true;
                        }
                    }
                }

                boolean foundt = false; //clean
                for(int x=0; x < StartGame.BOARD_SIZE && !foundt; x++) {
                    for(int y=0; y < StartGame.BOARD_SIZE && !foundt; y++) {
                        assert selectView[x][y] != null;
                        if(selectView[x][y].hashCode() == hash || targetSelect[x][y].hashCode() == hash) {
                            ti = x; tj = y;
                            foundt = true;
                        }
                    }
                }
                if(!StartGame.isAIBOT || !StartGame.isAIsTurn) {
                    performMoves(si, sj, ti, tj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if(StartGame.isAIBOT) {
            aiPlayerType = StartGame.HumanVsAIB ? BLACK_PLAYER : WHITE_PLAYER;
            runAI();
        }
    }

    // TODO : maxType is the player type of AI
    public synchronized void runAI() {
        // Lambda Expression
        // this turn is not for the AI so wait until turn comes back
        //------------------------------------------------------------------------------------------------------
        // Initial State of the Game Tree
        // we've got the start state of the Game Tree
        // for the sake of MINIMAX algo
        // TODO : Perform Alpha Beta Search within 1 or 2 second(s)
        // perform the move
        //System.out.println("selecting");
        //first click
        //System.out.print("targeting");
        //second click
        //-----------------------------------------------------------------------------
        // flip the turn
        Thread aiThread = new Thread(() -> { // Lambda Expression
            while (true) {
                while (winner || !StartGame.isAIsTurn) { // this turn is not for the AI so wait until turn comes back
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //------------------------------------------------------------------------------------------------------

                System.out.println("AI thinking....");

                // Initial State of the Game Tree
                // we've got the start state of the Game Tree
                int[][] initialState = generateState();

                // for the sake of MINIMAX algo
                AlphaBeta.assignMiniMax(checkerBoard.currentplayer(), checkerBoard.otherplayer(), EMPTY_PLAYER);

                // TODO : Perform Alpha Beta Search within 1 or 2 second(s)
                int maxDepth = (StartGame.BOARD_SIZE == 8) ? 4 : 5;
                Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move = AlphaBeta.AlphaBetaSearch(initialState, maxDepth);

                int selectX = move.getKey().getKey();
                int selectY = move.getKey().getValue();
                int targetX = move.getValue().getKey();
                int targetY = move.getValue().getValue();

                if (selectX == -1 && selectY == -1 && targetX == -1 && targetY == -1) {
                    System.out.println("ALPHA BETA CRASHED : no best move found");
                    assert (false);
                }

                // perform the move
                Platform.runLater(() -> {
                    //System.out.println("selecting");
                    performMoves(selectX, selectY, selectX, selectY);//first click
                    //System.out.print("targeting");
                    performMoves(selectX, selectY, targetX, targetY);//second click
                });

                System.out.println("AI moved....");


                //-----------------------------------------------------------------------------
                // flip the turn if not AI vs AI
                if(!StartGame.AIvsAI)
                    StartGame.isAIsTurn = !StartGame.isAIsTurn;
            }
        });
        aiThread.start();
    }

    public synchronized void performMoves(int si , int sj , int ti , int tj) {
        if(winner) {
            System.out.println("Game Ended");
            return;
        }

        /*if(isAIvsHUMAN && checkerBoard.currentplayer() == aiPlayerType) {
            System.out.println("AI_BOT's turn");
            return;
        }*/
        /*System.out.println(si + " " + sj);
        System.out.println("   TO   ");;
        System.out.println(ti + " " + tj);*/
        // Prints current player..
        if(checkerBoard.getClicklogic().equals("false") && !winner) {
            if(checkerBoard.currentplayer() == WHITE_PLAYER)
                System.out.print("WHITE's turn\n");
            else
                System.out.print("BLACK's turn\n");
        }

        /*Explanation:
          if (clickLogic == null) {
               that means a move is given completely
               now set clickLogic = false;
          }

          if(clickLogic == false) {
              means I can just select now
              and if I select a valid cell(not junk cell) then clickLogic should be set true
              else I can repeat that process again
          }

          if(clickLogic == true) {
              now I can give a valid move and make clickLogic = null again
              but if my move is not valid then repeat
          }

          */

        // Second click : give a move
        if(checkerBoard.getClicklogic().equalsIgnoreCase("true")) {
            Piece[][] boardState = checkerBoard.getState(); //copied
            Piece targetedPiece = checkerBoard.selectTarget(ti, tj); // allow selection of target cell

            // If Circle selected ....
            if(selectedPiece.toString().equals("Circle") && !selectedPiece.equals(targetedPiece)) {
                if(checkerBoard.getStroke(targetedPiece.icoord(), targetedPiece.jcoord(), Color.CORNFLOWERBLUE)) {
                    Piece[][] prevState = new Piece[StartGame.BOARD_SIZE][StartGame.BOARD_SIZE];
                    // Transfer pieces to backup variable
                    for(int x = 0; x < StartGame.BOARD_SIZE; x++){
                        System.arraycopy(boardState[x], 0, prevState[x], 0, StartGame.BOARD_SIZE);
                    }

                    // Do a move from selectedPiece to targetedPiece within current board state
                    boardState = selectedPiece.moveCircle(selectedPiece, targetedPiece, boardState);
                    //accept the move
                    checkerBoard.setBoard(boardState);
                    checkerBoard.drawMove(selectedPiece.icoord(), selectedPiece.jcoord(), targetedPiece.icoord(), targetedPiece.jcoord());

                    checkerBoard.changePlayer(); // alter current player
                    checkerBoard.changeclicknull(); // set state as move completed


                    //fixme probably we can check stalemate through this condition
                    if(checkerBoard.isBlocked(boardState, checkerBoard.currentplayer())) {
                        System.out.println("NO VALID MOVES");
                        System.out.println(checkerBoard.currentplayer() == BLACK_PLAYER ? "BLACK" : "WHITE" + " loses");
                        System.out.println("GAME ENDED ?");
                        winner = true;
                    }

                    //fixme -1 -> NO WIN; 0 -> BOTH WIN; 1 -> WHITE WIN; 2 -> BLACK WIN
                    int gameStatus = Logics.isEndGame(LoA_Game.Game.Controllers.Logics.piecesToGrid(boardState));
                    if(gameStatus != -1) {
                        System.out.println("LMV by : " + checkerBoard.otherplayer());
                        System.out.println("GAME ENDED ?");
                        if(gameStatus == 0) {
                            if(checkerBoard.otherplayer() == BLACK_PLAYER) {
                                System.out.println("BLACK Wins");
                            }
                            if(checkerBoard.otherplayer() == WHITE_PLAYER) {
                                System.out.println("WHITE Wins");
                            }
                        } else if(gameStatus == 1) {
                            System.out.println("WHITE Wins");
                        } else if(gameStatus == 2) {
                            System.out.println("BLACK Wins");
                        } else {
                            System.out.println("bla blah");
                        }
                        winner = true; // win or draw or no valid moves
                    }


                }
                else {
                    System.out.println("::Invalid Move::");
                }
            }
            checkerBoard.clearHighLights(); // clear the highlights
            checkerBoard.changeclicknull(); // set status as move completed (or discarded)

            getScene().setCursor(Cursor.DEFAULT);
        }
        // First click
        if(checkerBoard.getClicklogic().equals("false") && !winner) {
            // grab the selected piece by the mouse click action
            selectedPiece = checkerBoard.selectPiece(si,sj);

            boolean isJunkSelected = selectedPiece.toString().equals("Empty") || !checkerBoard.pieceselect();

            // piece selected : show all valid moves for this piece (in case it is a valid choice)
            if(!selectedPiece.equals("Empty") && !isJunkSelected) {
                //System.out.println(selectedPiece.toString() + " selected.");
                getScene().setCursor(new ImageCursor(selectedPiece.image()));
                checkerBoard.changeclicktrue();
                // Highlights valid moves..
                checkerBoard.validMoves(selectedPiece);
            }
        }
        // If completed move(or discarded due to invalidity), return to first click...
        if(checkerBoard.getClicklogic().equals("null")) {
            checkerBoard.changeclickfalse();
           /* System.out.println("Move Completed");
            System.out.println("|" + checkerBoard.currentplayer() + "|");*/

            if(StartGame.isAIBOT && !StartGame.isAIsTurn) {
                if(checkerBoard.currentplayer() == aiPlayerType) {
                    StartGame.isAIsTurn = true;
                }
            }
        }
        makeTurnTrue();

    }

    // Piece counting -- could expand on this but only need total number
    public int whitepieces(Piece[][] boardState) {
        int whitepieceCnt = 0;
        // Count white pieces
        for(int x=0; x < StartGame.BOARD_SIZE; x++) {
            for(int y=0; y < StartGame.BOARD_SIZE; y++) {
                if(boardState[x][y].type()==WHITE_PLAYER) {
                    whitepieceCnt++;
                }
            }
        }
        // Return int
        return whitepieceCnt;
    }

    public int blackpieces(Piece[][] boardState) {
        int blackpieceCnt = 0;
        // Count white pieces
        for(int x=0; x < StartGame.BOARD_SIZE; x++) {
            for(int y=0; y < StartGame.BOARD_SIZE; y++) {
                if(boardState[x][y].type()==BLACK_PLAYER) {
                    blackpieceCnt++;
                }
            }
        }
        // Return int
        return blackpieceCnt;
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        checkerBoard.resize(width, height);
    }

    @Override
    public void relocate(double x, double y) {
        super.relocate(x, y);
        pos.setX(x);
        pos.setY(x);
    }

    public int[][] generateState() {
        int [][] grid = new int[StartGame.BOARD_SIZE][StartGame.BOARD_SIZE];
        for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
            for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
                grid[x][y] = checkerBoard.getState()[x][y].type(); // type will be either MAXI, MINI, or EMPTY
            }
        }
        return grid;
    }

    public static ArrayList<Pair<Integer, Integer>> findMoves(Pair<Integer, Integer> piece, int[][] grid) {
        ArrayList<Pair<Integer, Integer>> nextMoves = new ArrayList<>();

        int my_type = grid[piece.getKey()][piece.getValue()];
        int opponent_type = (my_type == BLACK_PLAYER) ? WHITE_PLAYER : BLACK_PLAYER;
        int rowPieceCnt = 0;
        int filePieceCnt = 0;
        int firstDiagonalPieceCnt = 0;
        int secondDiagonalPieceCnt = 0;

        // count row pieces
        for (int x = 0, y = piece.getValue(); x < StartGame.BOARD_SIZE; ++x) {
            if (grid[x][y] != EMPTY_PLAYER) {
                ++rowPieceCnt;
            }
        }
        // count column pieces
        for (int x = piece.getKey(), y = 0; y < StartGame.BOARD_SIZE; ++y) {
            if (grid[x][y] != EMPTY_PLAYER) {
                ++filePieceCnt;
            }
        }
        // count first diagonal pieces [itself to TOP-LEFT]
        for (int x = piece.getKey(), y = piece.getValue(); x >= 0 && y >= 0; x--, y--) {
            if (grid[x][y] != EMPTY_PLAYER) {
                ++firstDiagonalPieceCnt;
            }
        }
        // count first diagonal pieces [itself to BOTTOM-RIGHT]
        for (int x = piece.getKey(), y = piece.getValue(); x < StartGame.BOARD_SIZE && y < StartGame.BOARD_SIZE; x++, y++) {
            if (grid[x][y] != EMPTY_PLAYER) {
                ++firstDiagonalPieceCnt;
            }
        }
        --firstDiagonalPieceCnt; // duplicate removal
        // count second diagonal pieces [itself to TOP-RIGHT]
        for (int x = piece.getKey(), y = piece.getValue(); x < StartGame.BOARD_SIZE && y >= 0; x++, y--) {
            if (grid[x][y] != EMPTY_PLAYER) {
                ++secondDiagonalPieceCnt;
            }
        }
        // count second diagonal pieces [itself to BOTTOM-LEFT]
        for (int x = piece.getKey(), y = piece.getValue(); y < StartGame.BOARD_SIZE && x >= 0; x--, y++) {
            if (grid[x][y] != EMPTY_PLAYER) {
                ++secondDiagonalPieceCnt;
            }
        }
        --secondDiagonalPieceCnt; // duplicate removal

        /*
        Now we've counted number of pieces in each direction for that piece
        so we know exactly how many steps this piece can move in a particular direction
        we'll apply the steps_count to show valid moves
        some edge_case might be performing move on same player but not on opponent
        again the piece can't move to an occupied cell of same type
        * */

        {    // FIXME : UP
            // is there a valid move on up
            boolean up = true;
            if (!BoardImplementation.isValidIndx(piece.getKey(), piece.getValue() - filePieceCnt)) up = false;
            if (up && grid[piece.getKey()][piece.getValue() - filePieceCnt] == my_type) up = false;
            if (up) {
                for (int x = piece.getKey(), y = piece.getValue(), i = 1; i < filePieceCnt; ++i) {
                    if (grid[x][y - i] == opponent_type) {
                        up = false;
                        break;
                    }
                }
            }

            // now select target cell
            if (up) {
                nextMoves.add(new Pair<>(piece.getKey(), piece.getValue() - filePieceCnt));
            }
        }


        {    // FIXME : DOWN
            // is there a valid move on down
            boolean down = true;
            if (!BoardImplementation.isValidIndx(piece.getKey(), piece.getValue() + filePieceCnt)) down = false;
            if (down && grid[piece.getKey()][piece.getValue() + filePieceCnt] == my_type) down = false;
            if (down) {
                for (int x = piece.getKey(), y = piece.getValue(), i = 1; i < filePieceCnt; ++i) {
                    if (grid[x][y + i] == opponent_type) {
                        down = false;
                        break;
                    }
                }
            }

            // now select target cell
            if (down) {
                nextMoves.add(new Pair<>(piece.getKey(), piece.getValue() + filePieceCnt));
            }
        }

        {    // FIXME : RIGHT
            // is there a valid move on right
            boolean right = true;
            if (!BoardImplementation.isValidIndx(piece.getKey() + rowPieceCnt, piece.getValue())) right = false;
            if (right && grid[piece.getKey() + rowPieceCnt][piece.getValue()] == my_type) right = false;
            if (right) {
                for (int x = piece.getKey(), y = piece.getValue(), i = 1; i < rowPieceCnt; ++i) {
                    if (grid[x + i][y] == opponent_type) {
                        right = false;
                        break;
                    }
                }
            }

            // now select target cell
            if (right) {
                nextMoves.add(new Pair<>(piece.getKey() + rowPieceCnt, piece.getValue()));
            }
        }

        {    // FIXME : LEFT
            // is there a valid move on left
            boolean left = true;
            if (!BoardImplementation.isValidIndx(piece.getKey() - rowPieceCnt, piece.getValue())) left = false;
            if (left && grid[piece.getKey() - rowPieceCnt][piece.getValue()] == my_type) left = false;
            if (left) {
                for (int x = piece.getKey(), y = piece.getValue(), i = 1; i < rowPieceCnt; ++i) {
                    if (grid[x - i][y] == opponent_type) {
                        left = false;
                        break;
                    }
                }
            }

            // now select target cell
            if (left) {
                nextMoves.add(new Pair<>(piece.getKey() - rowPieceCnt, piece.getValue()));
            }
        }


        {    // FIXME : TOP-RIGHT
            // is there a valid move on top-right
            boolean topright = true;
            if (!BoardImplementation.isValidIndx(piece.getKey() + secondDiagonalPieceCnt, piece.getValue() - secondDiagonalPieceCnt))
                topright = false;
            if (topright && grid[piece.getKey() + secondDiagonalPieceCnt][piece.getValue() - secondDiagonalPieceCnt] == my_type)
                topright = false;
            if (topright) {
                for (int x = piece.getKey(), y = piece.getValue(), i = 1; i < secondDiagonalPieceCnt; ++i) {
                    if (grid[x + i][y - i] == opponent_type) {
                        topright = false;
                        break;
                    }
                }
            }

            // now select target cell
            if (topright) {
                nextMoves.add(new Pair<>(piece.getKey() + secondDiagonalPieceCnt, piece.getValue() - secondDiagonalPieceCnt));
            }
        }

        {    // FIXME : BOTTOM_LEFT
            // is there a valid move on bottom-left
            boolean bottomleft = true;
            if (!BoardImplementation.isValidIndx(piece.getKey() - secondDiagonalPieceCnt, piece.getValue() + secondDiagonalPieceCnt))
                bottomleft = false;
            if (bottomleft && grid[piece.getKey() - secondDiagonalPieceCnt][piece.getValue() + secondDiagonalPieceCnt] == my_type)
                bottomleft = false;
            if (bottomleft) {
                for (int x = piece.getKey(), y = piece.getValue(), i = 1; i < secondDiagonalPieceCnt; ++i) {
                    if (grid[x - i][y + i] == opponent_type) {
                        bottomleft = false;
                        break;
                    }
                }
            }

            // now select target cell
            if (bottomleft) {
                nextMoves.add(new Pair<>(piece.getKey() - secondDiagonalPieceCnt, piece.getValue() + secondDiagonalPieceCnt));
            }
        }

        {    // FIXME : TOP-LEFT
            // is there a valid move on top-left
            boolean topleft = true;
            if (!BoardImplementation.isValidIndx(piece.getKey() - firstDiagonalPieceCnt, piece.getValue() - firstDiagonalPieceCnt))
                topleft = false;
            if (topleft && grid[piece.getKey() - firstDiagonalPieceCnt][piece.getValue() - firstDiagonalPieceCnt] == my_type)
                topleft = false;
            if (topleft) {
                for (int x = piece.getKey(), y = piece.getValue(), i = 1; i < firstDiagonalPieceCnt; ++i) {
                    if (grid[x - i][y - i] == opponent_type) {
                        topleft = false;
                        break;
                    }
                }
            }

            // now select target cell
            if (topleft) {
                nextMoves.add(new Pair<>(piece.getKey() - firstDiagonalPieceCnt, piece.getValue() - firstDiagonalPieceCnt));
            }
        }

        {    // FIXME : BOTTOM-RIGHT
            // is there a valid move on bottom-right
            boolean bottomright = true;
            if (!BoardImplementation.isValidIndx(piece.getKey() + firstDiagonalPieceCnt, piece.getValue() + firstDiagonalPieceCnt))
                bottomright = false;
            if (bottomright && grid[piece.getKey() + firstDiagonalPieceCnt][piece.getValue() + firstDiagonalPieceCnt] == my_type)
                bottomright = false;
            if (bottomright) {
                for (int x = piece.getKey(), y = piece.getValue(), i = 1; i < firstDiagonalPieceCnt; ++i) {
                    if (grid[x + i][y + i] == opponent_type) {
                        bottomright = false;
                        break;
                    }
                }
            }

            // now select target cell
            if (bottomright) {
                nextMoves.add(new Pair<>(piece.getKey() + firstDiagonalPieceCnt, piece.getValue() + firstDiagonalPieceCnt));
            }
        }
        return nextMoves;
    }
}