package game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import static game.PitchConstants.*;

public class Main extends Application  {

    private final int windowWidth = 1000;
    private final int windowHeight = 800;


    Stage window;
    Scene mainMenu, gameWindow;
    Button button;


    private Button twoPlayers;
    private Button threePlayers;
    private Button fourPlayers;

    private int players = 4;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
       window = primaryStage;

        initializeMenuScene();




       //set title and show window
       window.setTitle("Pitch Game");
       window.setScene(mainMenu);

       window.show();

       fourPlayers.requestFocus();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
    }


    void setPlayerCount(int n){
        this.players = n;
        currentPlayerCountSelection.setText("Current Selection: " + Integer.toString(n) + " Players");
    }


    Label currentPlayerCountSelection;
    void initializeMenuScene() {
        //labels
        Label Greeting = new Label("Welcome to the Pitch Game!");
        Greeting.setTextFill(textColor);
        Label Prompt = new Label("Choose a number of players to play against!");
        Prompt.setTextFill(textColor);
        currentPlayerCountSelection = new Label("Current Selection: 4 Players");
        currentPlayerCountSelection.setTextFill(textColor);


        //set label styles
        Greeting.setStyle("-fx-font: 24 arial");

        //buttons
        Button twoPlayers = new Button("2 Players");
        Button threePlayers = new Button("3 Players");
        Button fourPlayers = new Button("4 Players");
        Button exitButton = new Button("Exit Application");
        Button startButton = new Button("Start Application");

        //initialize borderPane
        BorderPane layout = new BorderPane();



        //intialize vbox for menuitems
        VBox menuItems = new VBox(25);
        menuItems.setAlignment(Pos.CENTER);


        //animating
        Card displayCard = new Card(null,1,'S',false);



        //create animation timer for rotating card on menu screen
        AnimationTimer timer = new AnimationTimer() {
            int scalor = 0;
            int offset = 1;
            double rotator = 0;
            @Override
            public void handle(long now) {
                scalor += 5;

                if(scalor < 180) {
                    displayCard.View().setScaleX( (180.0 - (double)scalor)/180.0);

                }
                else {
                    double j = scalor - 181;
                    displayCard.View().setScaleX(j/180.0);
                }

                if(scalor == 180 && offset % 2 == 0) {
                   displayCard.View().setFill(new ImagePattern(new Image("/Assets/PlayingCards/AS.png")));
                }

                if(scalor == 180 && offset % 2 == 1){
                    displayCard.View().setFill(new ImagePattern(new Image("/Assets/PlayingCards/red_back.png")));
                }

                if(scalor >= 360) {
                    offset++;
                    scalor = 0;

                }


                if(offset == 10) {
                    offset = 0;
                }
            }
        };

        //start animator
        timer.start();


        //add elements to vbox
        menuItems.getChildren().addAll(Greeting,Prompt,currentPlayerCountSelection,displayCard.View(),twoPlayers,threePlayers,fourPlayers);

        //layout start and exit buttons next to eachother
        HBox startAndExit = new HBox(5);
        startAndExit.getChildren().addAll(startButton,exitButton);
        startAndExit.setAlignment(Pos.CENTER);

        menuItems.getChildren().add(startAndExit);





        //set center object as menuItems
        layout.setCenter(menuItems);





        //event handlers for buttons

        //player count event handlers
        twoPlayers.setOnAction(e -> setPlayerCount(2));
        threePlayers.setOnAction(e -> setPlayerCount(3));
        fourPlayers.setOnAction(e -> setPlayerCount(4));

        //start
        startButton.setOnAction(e -> {
            //instantiate an instance of Pitch
            Pitch game = new Pitch(window,players,windowWidth,windowHeight);

            //stop rotating card animation
            timer.stop();

            //start pitch game
            game.start();


        });
        //exit
        exitButton.setOnAction(e -> exitProgram());

        layout.setStyle(titleStyle);


        this.fourPlayers = fourPlayers;
        this.threePlayers = threePlayers;
        this.twoPlayers = twoPlayers;

        mainMenu = new Scene(layout,windowWidth,windowHeight);
        mainMenu.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

    }



    void exitProgram() {
        window.close();
    }



}
