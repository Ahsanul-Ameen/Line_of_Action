package LoA_Game.Game.Controllers;

import LoA_Game.Game.PieceClasses.*;
import LoA_Game.Game.StartGame;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Translate;
import javafx.util.Pair;

import java.util.ArrayList;

public class BoardImplementation extends Pane {

	public static final int WHITE_PLAYER = 1,
							BLACK_PLAYER = 2,
							EMPTY_PLAYER = 0;

    public static int current_player = BLACK_PLAYER; // black player moves first right?

	public static int getCurrent_player() {
		return current_player;
	}

	public static void setCurrent_player(int current_player) {
		BoardImplementation.current_player = current_player;
	}

	public static final int PlayerWhite = 1,
							PlayerBlack = 2,
							Empty = 0;
    private boolean pieceSelect = false;
    private boolean winner = false;
    private String clicklogic="false";//initially player wants to select/deselect a piece

	
	private Piece[][] pieces;
	private Rectangle[][] board;
	private Image[][] images;
	private ImageView[][] imageviews;
    private Translate position;


    //clicklogic is a String which can be set / get and so on...
	public String getClicklogic(){ return this.clicklogic; }

	public void changeclicktrue(){ this.clicklogic = "true"; }

	public void changeclickfalse(){ this.clicklogic = "false"; }

	public void changeclicknull(){ this.clicklogic = "null"; }

	//return currentplayer and otherplayer(1/2format)
	public int currentplayer(){ return this.current_player; }
	
	public int otherplayer(){
		return (current_player == PlayerWhite) ? PlayerBlack : PlayerWhite;
	}

	//return true if any  piece is selected by mouse
	public boolean pieceselect(){ return (boolean) this.pieceSelect; }

	public Rectangle[][] getBoard() {
		return this.board;
	}

	public void setBoard(Rectangle[][] board) {
		this.board = board;
	}

	public ImageView[][] getImageviews() {
		return this.imageviews;
	}

	public void setImageviews(ImageView[][] imageviews) {
		this.imageviews = imageviews;
	}

	// Reset imageViews array  MAYBE not used only one in GameControl Handler
	public void resetGame() {
		for(int x = 0; x < StartGame.BOARD_SIZE; x++) {
			for(int y = 0; y < StartGame.BOARD_SIZE; y++) {
				imageviews[x][y].setImage(images[x][y]);
			}
		}
	}

	//essential
    public void placeBoard(final int i, final int j) {
        getChildren().add(board[i][j]);
    }

    //essential
    public void placeImageViews(final int i, final int j) {
        getChildren().addAll(imageviews[i][j]);
    }

    // Returns stroke of board piece helps checking in checkMate
    public boolean getStroke(final int i, final int j, Paint color) { return (board[i][j].getStroke() == color); }

    //FIXME : Constructor of CheckerBoard
    public BoardImplementation() {
        position = new Translate();

        // Declares new board
        int boardWidth = StartGame.BOARD_SIZE;
        int boardHeight = StartGame.BOARD_SIZE;
        board = new Rectangle[boardWidth][boardHeight];//StartGame.BOARD_SIZE*StartGame.BOARD_SIZE size 2D array

        // Initializes new Chess Board first time
        for(int x = 0; x < StartGame.BOARD_SIZE; x++){
            for(int j=0; j < StartGame.BOARD_SIZE; j++){
                board[x][j] = new Rectangle(50,50);
                board[x][j].setStroke(Color.TRANSPARENT);
                board[x][j].setStrokeType(StrokeType.INSIDE);
                board[x][j].setStrokeWidth(1);
                // Generates colours for the chessboard backgrounds
                if((x+j)%2==0)board[x][j].setFill(Color.SANDYBROWN);
                else board[x][j].setFill(Color.ROSYBROWN);
            }
        }
        
        // New image array
        images = new Image[StartGame.BOARD_SIZE][StartGame.BOARD_SIZE];

		/**intentionally
		 * made null* */
		// empty squares
		for(int x = 0; x < StartGame.BOARD_SIZE; x++) {
			for(int y = 0; y < StartGame.BOARD_SIZE; y++) {
				images[x][y] = null;
			}
		}

        // place top & bottom blacks
		for(int x = 1; x <= StartGame.BOARD_SIZE - 2; ++x) {
			images[x][0] = new Image("/LoA_Game/CursorPng/black_man.png");
			images[x][StartGame.BOARD_SIZE - 1] = new Image("/LoA_Game/CursorPng/black_man.png");
		}

		// place left & right whites
		for(int y = 1; y <= StartGame.BOARD_SIZE - 2; ++y) {
			images[0][y] = new Image("/LoA_Game/CursorPng/white_man.png");
			images[StartGame.BOARD_SIZE - 1][y] = new Image("/LoA_Game/CursorPng/white_man.png");
		}

		// Viewers for each image
		imageviews = new ImageView[StartGame.BOARD_SIZE ][StartGame.BOARD_SIZE ];

		// Initializes imageView and windows and init it
		for(int x = 0; x < StartGame.BOARD_SIZE ; x++) {
			for (int y = 0; y < StartGame.BOARD_SIZE ; y++) {
				imageviews[x][y] = new ImageView(images[x][y]);
				imageviews[x][y].setFitWidth(50);
				imageviews[x][y].setFitHeight(80);
				imageviews[x][y].setPreserveRatio(true);
				imageviews[x][y].setSmooth(true);
				imageviews[x][y].setCache(true);
				imageviews[x][y].setTranslateX(board[x][y].getWidth() / StartGame.BOARD_SIZE );
			}
		}

		//initialize the board: background, data structures, initial layout of pieces
		pieces = new Piece[boardWidth][boardHeight];

		// Black Pieces Left & Right
		for(int x = 1; x <= StartGame.BOARD_SIZE - 2; ++x) {
			pieces[x][0] = new PieceCircle(PlayerBlack, x, 0);
			pieces[x][StartGame.BOARD_SIZE - 1] = new PieceCircle(PlayerBlack, x, StartGame.BOARD_SIZE - 1);
		}

		// White Pieces Left & Right
		for(int y = 1; y <= StartGame.BOARD_SIZE - 2; ++y) {
			pieces[0][y] = new PieceCircle(PlayerWhite, 0, y);
			pieces[StartGame.BOARD_SIZE - 1][y] = new PieceCircle(PlayerWhite, StartGame.BOARD_SIZE - 1, y);
		}
		
		// Empty Pieces
		pieces[0][0] = new Empty(Empty, 0, 0);
		pieces[StartGame.BOARD_SIZE - 1][0] = new Empty(Empty, StartGame.BOARD_SIZE - 1, 0);
		for(int x = 1; x <= StartGame.BOARD_SIZE - 2; ++x) {
			for(int y = 1; y <= StartGame.BOARD_SIZE - 2; ++y) {
				pieces[x][y] = new Empty(Empty, x, y);
			}
		}
		pieces[0][StartGame.BOARD_SIZE - 1] = new Empty(Empty, 0, StartGame.BOARD_SIZE - 1);
		pieces[StartGame.BOARD_SIZE - 1][StartGame.BOARD_SIZE - 1] = new Empty(Empty, StartGame.BOARD_SIZE - 1, StartGame.BOARD_SIZE - 1);
		
		// set current player to black
		// console will show the current player
		current_player = PlayerBlack;
    }

	//resize method
	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
        double cell_width = width / StartGame.BOARD_SIZE ;
        double cell_height = height / StartGame.BOARD_SIZE ;

		for(int i = 0; i < StartGame.BOARD_SIZE ; i++){
			for(int j = 0; j < StartGame.BOARD_SIZE ; j++){
				board[i][j].resize(i * cell_width, j * cell_height);
				board[i][j].relocate(i * cell_width, j * cell_height);
				board[i][j].setStrokeWidth(cell_width / 14);//size of movable box boundaries
				board[i][j].setWidth(cell_width);
				board[i][j].setHeight(cell_height);

				//Image/Shapes of Black Royalty, White Royalty and Norm Royalty
				imageviews[i][j].resize(cell_width / StartGame.BOARD_SIZE , cell_height / StartGame.BOARD_SIZE );
				imageviews[i][j].relocate(i * cell_width, j * cell_height);
				imageviews[i][j].setFitWidth(cell_width / 1.25);
				imageviews[i][j].setFitHeight(cell_height / 1.25);
				imageviews[i][j].setTranslateX(board[i][j].getWidth() / StartGame.BOARD_SIZE );
			}
		}
	}

	@Override
	public void relocate(double x, double y) {
		super.relocate(x, y);
		position.setX(x);
		position.setY(x);
	}

	public Piece selectTarget(int ti , int tj) {
		int i  = ti, j = tj;
		//System.out.println("You Targeted: a "+pieces[i][j].type()+'\n'+pieces[i][j].toString() +"("+i+" , "+j+")");
		int enemyPlayer = (current_player == PlayerWhite) ? PlayerBlack : PlayerWhite;
		if(!winner) {
			if(pieces[i][j].type() == Piece.EMPTY_PLAYER || pieces[i][j].type() == enemyPlayer) 
				return pieces[i][j];
		}
		return pieces[i][j];
	}

	//select piece method
	public Piece selectPiece(int si, int sj) {
		// Determine what piece was selected and if it can be selected
		int i = si, j = sj;
		//System.out.println("You Selected: a " + pieces[i][j].type() + '\n' + pieces[i][j].toString()  + "("+i+" , "+j+")");
		if(current_player == PlayerWhite && !winner) {
			if(pieces[i][j].type() == PlayerWhite) {
				// If player has already selected the piece, deselect it
				if(board[i][j].getStroke() == Color.CORAL && pieceSelect) {
				    if((i+j)%2==0) {
					    board[i][j].setFill(Color.SANDYBROWN);
					    pieceSelect = false;
                    }
                    else {
					    board[i][j].setFill(Color.ROSYBROWN);
					    pieceSelect = false;
                    }
				}
				// Otherwise select it and work out moves
				else {
					board[i][j].setStroke(Color.CORAL);
					pieceSelect = true;
					return pieces[i][j];
				}
			}
		}
		else { // if current player is BLACK
			if(pieces[i][j].type() == PlayerBlack) {
				//if already selected it then just deselect
				// TODO: if condition should be true sometimes
				if(board[i][j].getStroke() == Color.CORAL && pieceSelect) {
					System.out.println("RESET COLOR");
					// Resets color
                    if((i+j)%2==0) {
                        board[i][j].setFill(Color.SANDYBROWN);
                        pieceSelect = false;
                    }
                    else {
                        board[i][j].setFill(Color.ROSYBROWN);
                        pieceSelect = false;
                    }
				}
				//else select and return make stroke also
				else {
					board[i][j].setStroke(Color.CORAL);
					pieceSelect = true;
					return pieces[i][j];
				}
			}
		}

		// return something ..actually you've selected an Empty piece
		return new Empty(Piece.EMPTY_PLAYER,i,j);
	}


	// Draw the move and remove highlights
	public void drawMove(final int si, int sj, int ti, int tj) {
		Image empty = null;
		System.out.println("S("+si+" , "+sj+")\n"+pieces[si][sj].type()+'\n'+pieces[ti][tj].type()+"\nT("+ti+" , "+tj+")");
		/**
		 * Explanation:
		 * ______________________________
		 * pieces[si][sj] has already made Empty
		 * pieces[ti][tj] has already made (which piece you selected to move)
		 * */
		String piece = "black";//default
		if(pieces[ti][tj].type() == PlayerWhite) { piece = "white"; }
		piece = "LoA_Game/CursorPng/"+ piece + pieces[ti][tj].imageFileName();
		//System.out.println("The new piece image filename: " + piece);

		imageviews[ti][tj].setImage(new Image(piece));
		imageviews[si][sj].setImage(empty);
		// Remove highlight
		if(board[si][sj].getStroke() == Color.CORAL && pieceSelect) {
            if((si + sj)%2==0) board[si][sj].setFill(Color.SANDYBROWN);
            else board[si][sj].setFill(Color.ROSYBROWN);
		}
		else if(!pieceSelect) {
			board[si][sj].setStroke(Color.CORAL);
			pieceSelect = true;
		}
	}
	
	//after every move it will update the board
	public void setBoard(Piece[][] newBoard) {
		for(int x = 0; x < StartGame.BOARD_SIZE ; x++) {
			for(int y = 0; y < StartGame.BOARD_SIZE ; y++) {
				pieces[x][y] = newBoard[x][y];
				//System.out.println(pieces[y][x].toString() + " " + pieces[y][x].icoord() + "," + pieces[y][x].jcoord());
			}
		}
	}

	//we also use it in custom_control
	public void hoverhighlight(int x, int y) {
		// Set highlight color
		board[x][y].setStroke(Color.BURLYWOOD);
	}

	public void clearHighLights(){
		for(int x = 0; x < StartGame.BOARD_SIZE ; x++) {
			for(int y = 0; y < StartGame.BOARD_SIZE ; y++) {
				board[x][y].setStroke(Color.TRANSPARENT);
				if((x+y)%2==0) board[x][y].setFill(Color.SANDYBROWN);
				else board[x][y].setFill(Color.ROSYBROWN);
			}
		}
	}

	//we have to call it through networking
	public void changePlayer() {
		current_player = (current_player == PlayerBlack) ? PlayerWhite : PlayerBlack;
	}

    // Returns state of the chess board ..
    public Piece[][] getState() { return this.pieces; }

    public static boolean isValidIndx(int x, int y) {
		return x >= 0 && x < StartGame.BOARD_SIZE && y >= 0 && y < StartGame.BOARD_SIZE;
	}

	// check and return if there is a move available for the current player
	public boolean isBlocked(Piece[][] boardState, int playerToMove) {
		for(int x = 0; x < StartGame.BOARD_SIZE; ++x) {
			for(int y = 0; y < StartGame.BOARD_SIZE; ++y) {
				if(boardState[x][y].type() == playerToMove) {
					if(!validMoves(boardState[x][y]).isEmpty()) {
						return false;
					}
				}
			}
		}
		return true;
	}

    public ArrayList<Pair<Integer, Integer>> validMoves(Piece piece) {
		ArrayList<Pair<Integer, Integer>> finalMoves = new ArrayList<>();
		if(piece.toString().equalsIgnoreCase("Circle")) {
			int my_type = piece.type();
			int opponent_type = (my_type == BLACK_PLAYER) ? WHITE_PLAYER : BLACK_PLAYER;
			int rowPieceCnt = 0;
			int filePieceCnt = 0;
			int firstDiagonalPieceCnt = 0;
			int secondDiagonalPieceCnt = 0;

			// count row pieces
			for(int x = 0, y = piece.jcoord(); x < StartGame.BOARD_SIZE; ++x) {
				if(pieces[x][y].toString().equalsIgnoreCase("Circle")) {
					++rowPieceCnt;
				}
			}
			// count column pieces
			for(int x = piece.icoord(), y = 0; y < StartGame.BOARD_SIZE; ++y) {
				if(pieces[x][y].toString().equalsIgnoreCase("Circle")) {
					++filePieceCnt;
				}
			}
			// count first diagonal pieces [itself to TOP-LEFT]
			for(int x = piece.icoord(), y = piece.jcoord(); x >= 0 && y >= 0; x--, y--) {
				if(pieces[x][y].toString().equalsIgnoreCase("Circle")) {
					++firstDiagonalPieceCnt;
				}
			}
			// count first diagonal pieces [itself to BOTTOM-RIGHT]
			for(int x = piece.icoord(),  y = piece.jcoord(); x < StartGame.BOARD_SIZE && y < StartGame.BOARD_SIZE; x++, y++) {
				if(pieces[x][y].toString().equalsIgnoreCase("Circle")) {
					++firstDiagonalPieceCnt;
				}
			}
			--firstDiagonalPieceCnt; // duplicate removal
			// count second diagonal pieces [itself to TOP-RIGHT]
			for(int x = piece.icoord(), y = piece.jcoord(); x < StartGame.BOARD_SIZE && y >= 0; x++, y--) {
				if(pieces[x][y].toString().equalsIgnoreCase("Circle")) {
					++secondDiagonalPieceCnt;
				}
			}
			// count second diagonal pieces [itself to BOTTOM-LEFT]
			for(int x = piece.icoord(),  y = piece.jcoord(); y < StartGame.BOARD_SIZE && x >= 0; x--, y++) {
				if(pieces[x][y].toString().equalsIgnoreCase("Circle")) {
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

			{	// FIXME : UP
				// is there a valid move on up
				boolean up = true;
				if(!isValidIndx(piece.icoord(), piece.jcoord() - filePieceCnt)) up = false;
				if(up && pieces[piece.icoord()][piece.jcoord() - filePieceCnt].type() == my_type) up = false;
				if(up) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 1; i < filePieceCnt; ++i) {
						if(pieces[x][y - i].type() == opponent_type) {
							up = false;
							break;
						}
					}
				}

				// now highlight
				if(up) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 0; i <= filePieceCnt; ++i) {
						board[x][y - i].setStroke(Color.AQUAMARINE);
						if(i == filePieceCnt) {
							board[x][y - i].setStroke(Color.CORNFLOWERBLUE); // target cell
							finalMoves.add(new Pair<Integer, Integer>(x, y - i));
						} else {
							board[x][y - i].setStroke(Color.AQUAMARINE); // cell on path
						}
					}
				}
			}


			{	// FIXME : DOWN
				// is there a valid move on down
				boolean down = true;
				if(!isValidIndx(piece.icoord(), piece.jcoord() + filePieceCnt)) down = false;
				if(down && pieces[piece.icoord()][piece.jcoord() + filePieceCnt].type() == my_type) down = false;
				if(down) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 1; i < filePieceCnt; ++i) {
						if(pieces[x][y + i].type() == opponent_type) {
							down = false;
							break;
						}
					}
				}

				// now highlight
				if(down) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 0; i <= filePieceCnt; ++i) {
						board[x][y + i].setStroke(Color.AQUAMARINE);
						if(i == filePieceCnt) {
							board[x][y + i].setStroke(Color.CORNFLOWERBLUE); // target cell
							finalMoves.add(new Pair<Integer, Integer>(x, y + i));
						} else {
							board[x][y + i].setStroke(Color.AQUAMARINE); // cell on path
						}
					}
				}
			}

			{	// FIXME : RIGHT
				// is there a valid move on right
				boolean right = true;
				if(!isValidIndx(piece.icoord() + rowPieceCnt, piece.jcoord())) right = false;
				if(right && pieces[piece.icoord() + rowPieceCnt][piece.jcoord()].type() == my_type) right = false;
				if(right) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 1; i < rowPieceCnt; ++i) {
						if(pieces[x + i][y].type() == opponent_type) {
							right = false;
							break;
						}
					}
				}

				// now highlight
				if(right) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 0; i <= rowPieceCnt; ++i) {
						board[x + i][y].setStroke(Color.AQUAMARINE);
						if(i == rowPieceCnt) {
							board[x + i][y].setStroke(Color.CORNFLOWERBLUE); // target cell
							finalMoves.add(new Pair<Integer, Integer>(x + i, y));
						} else {
							board[x + i][y].setStroke(Color.AQUAMARINE); // cell on path
						}
					}
				}
			}

			{	// FIXME : LEFT
				// is there a valid move on left
				boolean left = true;
				if(!isValidIndx(piece.icoord() - rowPieceCnt, piece.jcoord())) left = false;
				if(left && pieces[piece.icoord() - rowPieceCnt][piece.jcoord()].type() == my_type) left = false;
				if(left) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 1; i < rowPieceCnt; ++i) {
						if(pieces[x - i][y].type() == opponent_type) {
							left = false;
							break;
						}
					}
				}

				// now highlight
				if(left) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 0; i <= rowPieceCnt; ++i) {
						board[x - i][y].setStroke(Color.AQUAMARINE);
						if(i == rowPieceCnt) {
							board[x - i][y].setStroke(Color.CORNFLOWERBLUE); // target cell
							finalMoves.add(new Pair<Integer, Integer>(x - i, y));
						} else {
							board[x - i][y].setStroke(Color.AQUAMARINE); // cell on path
						}
					}
				}
			}


			{	// FIXME : TOP-RIGHT
				// is there a valid move on top-right
				boolean topright = true;
				if(!isValidIndx(piece.icoord() + secondDiagonalPieceCnt, piece.jcoord() - secondDiagonalPieceCnt)) topright = false;
				if(topright && pieces[piece.icoord() + secondDiagonalPieceCnt][piece.jcoord() - secondDiagonalPieceCnt].type() == my_type) topright = false;
				if(topright) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 1; i < secondDiagonalPieceCnt; ++i) {
						if(pieces[x + i][y - i].type() == opponent_type) {
							topright = false;
							break;
						}
					}
				}

				// now highlight
				if(topright) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 0; i <= secondDiagonalPieceCnt; ++i) {
						board[x + i][y - i].setStroke(Color.AQUAMARINE);
						if(i == secondDiagonalPieceCnt) {
							board[x + i][y - i].setStroke(Color.CORNFLOWERBLUE); // target cell
							finalMoves.add(new Pair<Integer, Integer>(x + i, y - i));
						} else {
							board[x + i][y - i].setStroke(Color.AQUAMARINE); // cell on path
						}
					}
				}
			}

			{	// FIXME : BOTTOM_LEFT
				// is there a valid move on bottom-left
				boolean bottomleft = true;
				if(!isValidIndx(piece.icoord() - secondDiagonalPieceCnt, piece.jcoord() + secondDiagonalPieceCnt)) bottomleft = false;
				if(bottomleft && pieces[piece.icoord() - secondDiagonalPieceCnt][piece.jcoord() + secondDiagonalPieceCnt].type() == my_type) bottomleft = false;
				if(bottomleft) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 1; i < secondDiagonalPieceCnt; ++i) {
						if(pieces[x - i][y + i].type() == opponent_type) {
							bottomleft = false;
							break;
						}
					}
				}

				// now highlight
				if(bottomleft) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 0; i <= secondDiagonalPieceCnt; ++i) {
						board[x - i][y + i].setStroke(Color.AQUAMARINE);
						if(i == secondDiagonalPieceCnt) {
							board[x - i][y + i].setStroke(Color.CORNFLOWERBLUE); // target cell
							finalMoves.add(new Pair<Integer, Integer>(x - i, y + i));
						} else {
							board[x - i][y + i].setStroke(Color.AQUAMARINE); // cell on path
						}
					}
				}
			}

			{	// FIXME : TOP-LEFT
				// is there a valid move on top-left
				boolean topleft = true;
				if(!isValidIndx(piece.icoord() - firstDiagonalPieceCnt, piece.jcoord() - firstDiagonalPieceCnt)) topleft = false;
				if(topleft && pieces[piece.icoord() - firstDiagonalPieceCnt][piece.jcoord() - firstDiagonalPieceCnt].type() == my_type) topleft = false;
				if(topleft) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 1; i < firstDiagonalPieceCnt; ++i) {
						if(pieces[x - i][y - i].type() == opponent_type) {
							topleft = false;
							break;
						}
					}
				}

				// now highlight
				if(topleft) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 0; i <= firstDiagonalPieceCnt; ++i) {
						board[x - i][y - i].setStroke(Color.AQUAMARINE);
						if(i == firstDiagonalPieceCnt) {
							board[x - i][y - i].setStroke(Color.CORNFLOWERBLUE); // target cell
							finalMoves.add(new Pair<Integer, Integer>(x - i, y - i));
						} else {
							board[x - i][y - i].setStroke(Color.AQUAMARINE); // cell on path
						}
					}
				}
			}

			{	// FIXME : BOTTOM-RIGHT
				// is there a valid move on bottom-right
				boolean bottomright = true;
				if(!isValidIndx(piece.icoord() + firstDiagonalPieceCnt, piece.jcoord() + firstDiagonalPieceCnt)) bottomright = false;
				if(bottomright && pieces[piece.icoord() + firstDiagonalPieceCnt][piece.jcoord() + firstDiagonalPieceCnt].type() == my_type) bottomright = false;
				if(bottomright) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 1; i < firstDiagonalPieceCnt; ++i) {
						if(pieces[x + i][y + i].type() == opponent_type) {
							bottomright = false;
							break;
						}
					}
				}

				// now highlight
				if(bottomright) {
					for(int x = piece.icoord(), y = piece.jcoord(), i = 0; i <= firstDiagonalPieceCnt; ++i) {
						board[x + i][y + i].setStroke(Color.AQUAMARINE);
						if(i == firstDiagonalPieceCnt) {
							board[x + i][y + i].setStroke(Color.CORNFLOWERBLUE); // target cell
							finalMoves.add(new Pair<Integer, Integer>(x + i, y + i));
						} else {
							board[x + i][y + i].setStroke(Color.AQUAMARINE); // cell on path
						}
					}
				}
			}

		}

		return finalMoves;
	}

}
