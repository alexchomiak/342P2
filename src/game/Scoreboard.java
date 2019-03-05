package game;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import static game.PitchConstants.*;
import java.util.ArrayList;



public class Scoreboard {
    private FlowPane displayPane;
    private Label scorePrompt;
    private Label bidPrompt;
    private Label trumpPrompt;
    private Label turnSubjectPrompt;
    private Label turnPrompt;
    private Button exitGame;
    private ArrayList<Label> playerBids;
    private ArrayList<Label> playerScores;
    private ImageView trumpView;


    public Scoreboard(int numPlayers, Stage window) {
        //initialize displaypane for scoreboard
        displayPane = new FlowPane(Orientation.VERTICAL);
        displayPane.setVgap(10);
        displayPane.setHgap(20);
        displayPane.setPadding(new Insets(20,20,20,20));

        //intitialize exitGame button
        this.exitGame = new Button("Exit Application");
        exitGame.setOnAction(e -> {
            window.close();
        });

        exitGame.setScaleX(.75);
        exitGame.setScaleY(.75);

        //add exit game to scoreboard
        displayPane.getChildren().add(exitGame);

        //add turn prompt subject
        turnSubjectPrompt = new Label("Turn");
        turnSubjectPrompt.setStyle("-fx-font: 18 arial;");
        turnSubjectPrompt.setTextFill(textColor);
        turnSubjectPrompt.setAlignment(Pos.CENTER);
        displayPane.getChildren().add(turnSubjectPrompt);

        //add turn prompt label
        turnPrompt = new Label();
        turnPrompt.setStyle("-fx-font: 14 arial");
        turnPrompt.setTextFill(textColor);
        turnPrompt.setAlignment(Pos.CENTER);
        displayPane.getChildren().add(turnPrompt);


        //add round trump prompt
        trumpPrompt = new Label("Round Trump Suit");
        trumpPrompt.setTextFill(textColor);
        trumpPrompt.setStyle("-fx-font: 18 arial");


        //create Trump VBox with trump prompt and logo
        VBox Trump = new VBox(10);
        trumpView = new ImageView();
        trumpView.setFitWidth(50);
        trumpView.setFitHeight(50);

        //set Trump alignment to center
        Trump.setAlignment(Pos.CENTER);
        Trump.getChildren().addAll(trumpPrompt, trumpView);

        //add Trump to displayplane
        displayPane.getChildren().add(Trump);


        //create placeholder rectangle for spacing
        Rectangle placeholder = new Rectangle(155,50);
        placeholder.setOpacity(0);
        displayPane.getChildren().add(placeholder);


        //add score prompt
        scorePrompt = new Label();
        scorePrompt.setText("Scoreboard");
        scorePrompt.setStyle("-fx-font: 18 arial");
        scorePrompt.setTextFill(textColor);
        displayPane.getChildren().add(scorePrompt);
        scorePrompt.setAlignment(Pos.CENTER);

        //initialize player score and bid labels
        playerScores = new ArrayList<Label>();
        playerBids = new ArrayList<Label>();
        for(int i = 0; i < numPlayers; i++) {
            playerScores.add(new Label());
            playerBids.add(new Label());
        }

        //add each player score to the display pane
        playerScores.forEach(label -> displayPane.getChildren().add(label));


        //create bids vbox
        VBox bids = new VBox(10);

        //add bid prompt
        bidPrompt = new Label();
        bidPrompt.setText("Current Bids");
        bidPrompt.setStyle("-fx-font: 18 arial;");
        bidPrompt.setTextFill(textColor);

        //add prompt to bids object
        bids.getChildren().add(bidPrompt);

        //add each player bid label to bids objcet
        playerBids.forEach(bid -> bids.getChildren().add(bid));


        //add bids to displayPane
        displayPane.getChildren().add(bids);

        //set alignment to center
        displayPane.setAlignment(Pos.CENTER);

        //set displaypane style
        displayPane.setStyle(leftSideBarStyle);

        //initialize bids and scores
        ArrayList<Integer> initial = new ArrayList<Integer>();
        initial.add(0);
        initial.add(0);
        initial.add(0);
        initial.add(0);
        setScores(initial);
        setBids(initial);

    }


    public void setTurnPrompt(int player) {
        //function to set turn prompt

        //if 0, set opacity to 0.0 and text to empty
        if(player == 0) {
            turnSubjectPrompt.setOpacity(0.0);
            turnPrompt.setText("");
            return;
        }

        //otherise set opacity to full
        turnSubjectPrompt.setOpacity(1.0);

        //set player number as label
        if(player == 1) {
            turnPrompt.setText("Your turn.");
        }
        else {
            turnPrompt.setText("Player " + Integer.toString(player) + " making move...");
        }
    }

    public void setTrumpSuit(char c) {
        //function to set trump suit logo
        //if 'E', set opacity to 0 and set image to null
        if(c == 'E') {
            trumpView.setImage(null);
            trumpPrompt.setOpacity(0.0);
            return;
        }

        //otherwise set to full opacity
        trumpPrompt.setOpacity(1.0);

        //update image asset
        Image trump = new Image("/Assets/Suits/" + c + ".png");
        trumpView.setImage(trump);
    }


    public void setBids(ArrayList<Integer> bids) {
        //function to set bids based off of integer array
        for(int i = 0; i < playerBids.size(); i++){
            //loop through each players bids
            //and set prompt accordingly
            String prompt = new String("");

            if(i == 0) {
                prompt += "You bid ";
            }
            else {
                prompt +=  "Player " + Integer.toString(i + 1) + " bid ";
            }

            if(bids.get(i) == 0) {
                prompt = "Player " + Integer.toString(i + 1) + " passed.";
            }
            else if(bids.get(i) == -1) {
               prompt = "Player " + Integer.toString(i + 1) + " has not bid.";
               if(i == 0) {
                   prompt = "You have not bid.";
               }
            }
            else if(bids.get(i) == 5) {
                prompt += "smudge.";
            }
            else {
                prompt += Integer.toString(bids.get(i)) + ".";
            }

            //set text of prompt
            playerBids.get(i).setText(prompt);

            //set text fill according to index
            switch(i){
                case 0: playerBids.get(i).setTextFill(player1); break;
                case 1: playerBids.get(i).setTextFill(player2); break;
                case 2: playerBids.get(i).setTextFill(player3); break;
                case 3: playerBids.get(i).setTextFill(player4); break;
            }


            //set text styling and alignment
            playerBids.get(i).setStyle("-fx-font: 14 arial");
            playerBids.get(i).setAlignment(Pos.CENTER_LEFT);
        }
    }


    public void setScores(ArrayList<Integer> scores) {
        //function to update scoreboard on screen
        for(int i = 0; i < playerScores.size(); i++) {
            //set text of player score based off of index
            if(i == 0) {
                playerScores.get(i).setText("You have " + scores.get(i) + " points");

            }
            else {
                playerScores.get(i).setText("Player " + Integer.toString(i + 1) + " has " + scores.get(i) + " points");
            }

            //set text fill of player score based off index
            switch(i){
                case 0: playerScores.get(i).setTextFill(player1); break;
                case 1: playerScores.get(i).setTextFill(player2); break;
                case 2: playerScores.get(i).setTextFill(player3); break;
                case 3: playerScores.get(i).setTextFill(player4); break;
            }

            //set style and alignment
            playerScores.get(i).setStyle("-fx-font: 14 arial");
            playerScores.get(i).setAlignment(Pos.CENTER_LEFT);
        }
    }


    //returns flowpane object
    public FlowPane View() {
        return this.displayPane;
    }

    //hide and show functions for exit button
    public void hideExitButton(){
        this.exitGame.setDisable(true);
        this.exitGame.setOpacity(0.0);
    }
    public void showExitButton(){
        this.exitGame.setDisable(false);
        this.exitGame.setOpacity(1.0);
    }


}
