// --module-path /usr/lib/jvm/javafx-sdk-11.0.2/lib --add-modules javafx.controls,javafx.fxml
// --module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml
package LoA_Game.Game;


import LoA_Game.Game.Controllers.GameControl;
import LoA_Game.Game.Controllers.PriningInTextAera;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.PrintStream;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;

/**
 *This is the launcher of client side game part, show GUI & calls Controller class
 * @author  Ahsanul Ameen Sabit
 */

/*
* TODO :
*  first button : set board size 8 or 6
*  second : H vs H  or   H vs AIW   or   H vs AIB
* */

public class StartGame extends Application {

    private static Stage huntStage;
    //private Stage stage;

    public static final int WHITE_PLAYER = 1,
                            BLACK_PLAYER = 2,
                            EMPTY_PLAYER = 0;
    public static int BOARD_SIZE = 8;
    public static boolean HumanVsHuman = true,
                            HumanVsAIB = false,
                            HumanVsAIW = false,
                            isAIBOT = false,
                            AIvsAI = false, isAIsTurn = false;
    public static String MyName = "MAX";
    public static String OpponentsName = "MIN";

    // private fields for this class
    private GameControl gameController;

    public static Label typeLabel = new Label("What is my TYPE?");

    private static final Label bLabel = new Label();
    private static final Label wLabel = new Label();
    private static final Label myName = new Label();
    private static final Label opponensName = new Label();

    private static Stage stage = null;

    @Override//no need to override.no follow the sequence of javaFx
    public void init() {}

    // overridden start method
    @Override
    public void start(Stage primaryStage) {
        huntStage = primaryStage;
        showBoard();
    }

    //show board
    public void showBoard() {
        stage = huntStage;
        //huntPage();
        showLogin();
    }

    //show login window
    public void showLogin() {
        stage = huntStage;
        stage.setTitle("Game Configuration");
        GridPane grid = new GridPane();

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 500, 370);
        stage.setScene(scene);
        stage.setMinHeight(350);
        stage.setMinWidth(500);

        Text scenetitle = new Text("Select appropriately");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        scenetitle.setFill(Color.DARKMAGENTA);
        scenetitle.setUnderline(true);

        grid.add(scenetitle, 0, 0, 4, 4);

        // create a tile pane
        TilePane r = new TilePane();

        CheckBox c1 = new CheckBox("8 by 8");
        r.getChildren().add(c1);
        CheckBox c2 = new CheckBox("6 by 6");
        r.getChildren().add(c2);
        CheckBox c3 = new CheckBox("H vs H");
        r.getChildren().add(c3);
        CheckBox c4 = new CheckBox("H vs AI(BLACK)");
        r.getChildren().add(c4);
        CheckBox c5 = new CheckBox("H vs AI(WHITE)");
        r.getChildren().add(c5);
        CheckBox c6 = new CheckBox("AI vs AI");
        r.getChildren().add(c6);
        Button btn = new Button("Log-In");
        r.getChildren().add(btn);

        grid.add(r, 1, 5);
        r.setAlignment(Pos.BOTTOM_CENTER);

        c1.setOnAction(actionEvent -> {
            if(c1.isSelected()) {
                BOARD_SIZE = 8;
            }
        });

        c2.setOnAction(actionEvent -> {
            if(c2.isSelected()) {
                BOARD_SIZE = 6;
            } else {
                BOARD_SIZE = 8;
            }
        });

        c3.setOnAction(actionEvent -> {
            if(c3.isSelected()) {
                HumanVsHuman = true;
                HumanVsAIB = HumanVsAIW = AIvsAI = false;
                isAIBOT = isAIsTurn = false;
            } else {
                HumanVsHuman = false;
            }
        });

        c4.setOnAction(actionEvent -> {
            if(c4.isSelected()) {
                HumanVsAIB = true;
                HumanVsHuman = HumanVsAIW = AIvsAI = false;
                isAIBOT = true;
                isAIsTurn = true;
            } else {
                HumanVsAIB = false;
                isAIBOT = isAIsTurn = false;
            }
        });

        c5.setOnAction(actionEvent -> {
            if(c5.isSelected()) {
                HumanVsAIW = true;
                HumanVsHuman = HumanVsAIB = AIvsAI = false;
                isAIBOT = true;
                isAIsTurn = false;
            } else {
                HumanVsAIW = false;
                isAIBOT = false;
                isAIsTurn = false;
            }
        });

        c6.setOnAction(actionEvent -> {
            if(c6.isSelected()) {
                AIvsAI = true;
                HumanVsAIW = false;
                HumanVsHuman = HumanVsAIB = false;
                isAIBOT = true;
                isAIsTurn = true;
            } else {
                AIvsAI = false;
                HumanVsAIB = false;
                HumanVsAIW = false;
                isAIBOT = false;
                isAIsTurn = false;
            }
        });

        btn.setOnAction(e -> {
            int cnt = 0;
            if(HumanVsHuman) ++cnt; if(HumanVsAIB) ++cnt; if(HumanVsAIW) ++cnt; if(AIvsAI) ++cnt;
            if(cnt != 1 || (isAIBOT && HumanVsHuman)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Inputs");
                alert.setHeaderText("What vs What?");
                alert.setContentText("Please give valid choices....");
                alert.showAndWait();
            } else {
                huntPage();
            }
        });

        stage.show();
    }

    public void huntPage() {
        // initialize the layout, create a CustomControl and add it to the layout
        StackPane stackLayout = new StackPane();
        gameController = new GameControl();
        stackLayout.getChildren().add(gameController);

        typeLabel.setEffect(new InnerShadow());

        bLabel.setTextFill(Color.BLACK);
        wLabel.setTextFill(Color.PAPAYAWHIP);
        if(HumanVsHuman)  bLabel.setText("Human Vs Human");
        if(HumanVsAIB)  bLabel.setText("AI vs Human");
        if(HumanVsAIB)  bLabel.setText("Human Vs AI");
        if(AIvsAI) bLabel.setText("AI Vs AI");
        wLabel.setText("@Copyright : Ahsanul Ameen Sabit");
        bLabel.setStyle("-fx-font-size: 15;");
        wLabel.setStyle("-fx-font-size: 15;");
        myName.setStyle("-fx-font-size: 15;");
        opponensName.setStyle("-fx-font-size: 12;");
        typeLabel.setTextFill(Color.BLACK);
        typeLabel.setStyle("-fx-font-size: 12;");

        huntStage.setTitle("Line of Action [ 1605047 ]");
        huntStage.setScene(new Scene(stackLayout, 650, 550));

        // create text box
        TextArea status = new TextArea();
        status.setEditable(false);
        status.setEffect(new InnerShadow());
        status.setPromptText("Status Updates");
        status.setStyle("-fx-font-size: 13;");
        status.setWrapText(true);
        status.setPrefWidth(200);
        status.setPrefHeight(800);

        //making situation for printing in the TextArea
        PrintStream ps = System.out;
        System.setOut(new PriningInTextAera(status, ps));

        //add status in a VBox
        VBox vb1 = new VBox();
        vb1.getChildren().add(status);
        vb1.setPrefWidth(200);

        BorderPane bp = new BorderPane();
        HBox hb1 = new HBox();

        HBox hb2 = new HBox();

        //make it very much clear
        myName.setText(MyName);
        opponensName.setText(OpponentsName);


        hb1.setAlignment(Pos.CENTER);//make it resizable
        hb1.getChildren().addAll(bLabel,typeLabel);
        hb1.setSpacing(60);
        hb1.setStyle("-fx-background-color: #D3D3D3");
        hb1.setLayoutY(30);

        hb2.setAlignment(Pos.CENTER);//make it resizable
        hb2.getChildren().addAll(wLabel);
        hb2.setSpacing(60);
        hb2.setStyle("-fx-background-color: #2C2C2C");
        hb2.setLayoutY(30);

        huntStage.setScene(new Scene(bp, 670, 520 ));
        bp.setCenter(stackLayout);
        bp.setTop(hb1);
        bp.setBottom(hb2);
        bp.setRight(vb1);

        huntStage.setMaxHeight(700);
        huntStage.setMaxWidth(900);
        huntStage.setMinWidth(500);
        huntStage.setMinHeight(400);
        //huntStage.show();

        typeLabel.setOnMouseClicked(e-> gameController.makeTurnTrue());
    }

    // overridden stop method
    @Override
    public void stop(){}

    public static Stage getStage(){
        return stage;
    }

    //Launch Pp Pp
    public static void main(String[] args) {
        launch(args);
    }
}
