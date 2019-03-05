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


    private Stage window;
    private Scene mainMenu, gameWindow;

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

       //initialize menu scene
       initializeMenuScene();

       //set title and show window
       window.setTitle("Pitch Game");
       window.setScene(mainMenu);

       //show window
        window.show();

       //set default button focus (4 players)
        fourPlayers.requestFocus();

       //set min width and height to current width and height
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
    }


    public void setPlayerCount(int n){
        //update player count
        this.players = n;
        currentPlayerCountSelection.setText("Current Selection: " + Integer.toString(n) + " Players");
    }


    Label currentPlayerCountSelection;
    public void initializeMenuScene() {
        //Greeting label
        Label Greeting = new Label("Welcome to the Pitch Game!");
        //set greeting text color
        Greeting.setTextFill(textColor);

        //set greeting style
        Greeting.setStyle("-fx-font: 24 arial");

        //player prompt label
        Label Prompt = new Label("Choose a number of players to play against!");

        //set prompt styling
        Prompt.setStyle("-fx-font: 20 arial");
        Prompt.setTextFill(textColor);

        //current player selection label
        currentPlayerCountSelection = new Label("Current Selection: 4 Players");

        //set player selection styling
        currentPlayerCountSelection.setStyle("-fx-font: 20 arial");
        currentPlayerCountSelection.setTextFill(textColor);

        //button declarations
        Button twoPlayers = new Button("2 Players");
        Button threePlayers = new Button("3 Players");
        Button fourPlayers = new Button("4 Players");
        Button exitButton = new Button("Exit Application");
        Button startButton = new Button("Start Game");

        //initialize borderPane
        BorderPane layout = new BorderPane();

        //intialize vbox for menuitems
        VBox menuItems = new VBox(25);
        menuItems.setAlignment(Pos.CENTER);


        //animating
        Card displayCard = new Card(null,1,'S',false);

        //set scale of display card on menu screen
        displayCard.View().setWidth(1.5 * cardImageWidth);
        displayCard.View().setHeight(1.5 * cardImageHeight);

        //create animation timer for rotating card on menu screen
        AnimationTimer timer = new AnimationTimer() {
            int scalor = 0; //scale factor
            int offset = 1; //offset for texture of front and back
            @Override
            public void handle(long now) {
                scalor += 5; //add 5 to scalor


                if(scalor < 180) {
                    //if less than 180, the X scale will shrink to 0
                    displayCard.View().setScaleX( (180.0 - (double)scalor)/180.0);

                }
                else {
                    //otherwise it will grow back to 1.0
                    double j = scalor - 181;
                    displayCard.View().setScaleX(j/180.0);
                }

                if(scalor == 180 && offset % 2 == 0) {
                    //if offset is even, set texture to card
                   displayCard.View().setFill(new ImagePattern(new Image("/Assets/PlayingCards/AS.png")));
                }

                if(scalor == 180 && offset % 2 == 1){
                    //if offset is odd, set texture to back of card
                    displayCard.View().setFill(new ImagePattern(new Image("/Assets/PlayingCards/red_back.png")));
                }

                //if scalor == 360, reset and increment offset
                if(scalor >= 360) {
                    offset++;
                    scalor = 0;

                }

                //if offset == 10, reset offset
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

        //add start and exit buttons to vbox
        menuItems.getChildren().add(startAndExit);

        //set center object as menuItems
        layout.setCenter(menuItems);

        //event handlers for buttons
        //player count event handlers
        twoPlayers.setOnAction(e -> setPlayerCount(2));
        threePlayers.setOnAction(e -> setPlayerCount(3));
        fourPlayers.setOnAction(e -> setPlayerCount(4));

        //start event handler
        startButton.setOnAction(e -> {
            //instantiate an instance of Pitch
            Pitch game = new Pitch(window,players,mainMenu);

            //start pitch game
            game.start();
        });

        //exit event handler
        exitButton.setOnAction(e -> exitProgram());

        //set style of layout
        layout.setStyle(titleStyle);

        //set class data members
        this.fourPlayers = fourPlayers;
        this.threePlayers = threePlayers;
        this.twoPlayers = twoPlayers;

        //set mainMenu as a scene with the layout created
        mainMenu = new Scene(layout,windowWidth,windowHeight);

        //set stylesheet of mainMenu
        mainMenu.getStylesheets().add(getClass().getResource("/Assets/styles.css").toExternalForm());

    }



    public void exitProgram() {
        window.close();
    }



}
