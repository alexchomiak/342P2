package game;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import static game.PitchConstants.*;
import java.util.ArrayList;



public class Scoreboard {
    FlowPane displayPane;

    Label scorePrompt;
    Label bidPrompt;
    Label trumpPrompt;
    Label turnSubjectPrompt;
    Label turnPrompt;

    ArrayList<Label> playerBids;
    ArrayList<Label> playerScores;
    ImageView trumpView;


    Scoreboard(int numPlayers) {
        displayPane = new FlowPane(Orientation.VERTICAL);
        displayPane.setVgap(10);
        displayPane.setHgap(20);
        displayPane.setPadding(new Insets(20,20,20,20));


        turnSubjectPrompt = new Label("Turn");
        turnSubjectPrompt.setStyle("-fx-font: 18 arial;");
        turnSubjectPrompt.setTextFill(textColor);
        turnSubjectPrompt.setAlignment(Pos.CENTER);
        displayPane.getChildren().add(turnSubjectPrompt);

        turnPrompt = new Label();
        turnPrompt.setStyle("-fx-font: 14 arial");
        turnPrompt.setTextFill(textColor);
        turnPrompt.setAlignment(Pos.CENTER);
        displayPane.getChildren().add(turnPrompt);


        trumpPrompt = new Label("Round Trump Suit");
        trumpPrompt.setTextFill(textColor);
        trumpPrompt.setStyle("-fx-font: 18 arial");


        VBox Trump = new VBox(10);
        trumpView = new ImageView();
        trumpView.setFitWidth(50);
        trumpView.setFitHeight(50);

        Trump.setAlignment(Pos.CENTER);
        Trump.getChildren().addAll(trumpPrompt, trumpView);

        displayPane.getChildren().addAll(Trump);


        Rectangle placeholder = new Rectangle(155,50);
        placeholder.setOpacity(0);
        displayPane.getChildren().add(placeholder);


        scorePrompt = new Label();
        scorePrompt.setText("Scoreboard");
        scorePrompt.setStyle("-fx-font: 18 arial");
        scorePrompt.setTextFill(textColor);
        displayPane.getChildren().add(scorePrompt);
        scorePrompt.setAlignment(Pos.CENTER);

        playerScores = new ArrayList<Label>();
        playerBids = new ArrayList<Label>();
        for(int i = 0; i < numPlayers; i++) {
            playerScores.add(new Label());
            playerBids.add(new Label());
        }

        playerScores.forEach(label -> displayPane.getChildren().add(label));


        VBox bids = new VBox(10);

        bidPrompt = new Label();
        bidPrompt.setText("Current Bids");
        bidPrompt.setStyle("-fx-font: 18 arial;");
        bidPrompt.setTextFill(textColor);

        bids.getChildren().add(bidPrompt);
        playerBids.forEach(bid -> bids.getChildren().add(bid));


        displayPane.getChildren().add(bids);

        displayPane.setAlignment(Pos.CENTER);

        displayPane.setStyle(leftSideBarStyle);
        ArrayList<Integer> initial = new ArrayList<Integer>();
        initial.add(0);
        initial.add(0);
        initial.add(0);
        initial.add(0);
        setScores(initial);
        setBids(initial);







    }


    void setTurnPrompt(int player) {
        if(player == 0) {
            turnSubjectPrompt.setOpacity(0.0);
            turnPrompt.setText("");
            return;
        }

        turnSubjectPrompt.setOpacity(1.0);

        if(player == 1) {
            turnPrompt.setText("Your turn.");
        }
        else {
            turnPrompt.setText("Player " + Integer.toString(player) + " making move...");
        }
    }

    void setTrumpSuit(char c) {
        if(c == 'E') {
            trumpView.setImage(null);
            trumpPrompt.setOpacity(0.0);
            return;
        }
        trumpPrompt.setOpacity(1.0);

        Image trump = new Image("/Assets/Suits/" + c + ".png");
        trumpView.setImage(trump);
    }


    void setBids(ArrayList<Integer> bids) {
        for(int i = 0; i < playerBids.size(); i++){
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


            playerBids.get(i).setText(prompt);
            switch(i){
                case 0: playerBids.get(i).setTextFill(player1); break;
                case 1: playerBids.get(i).setTextFill(player2); break;
                case 2: playerBids.get(i).setTextFill(player3); break;
                case 3: playerBids.get(i).setTextFill(player4); break;
            }



            playerBids.get(i).setStyle("-fx-font: 14 arial");
            playerBids.get(i).setAlignment(Pos.CENTER_LEFT);
        }
    }


    void setScores(ArrayList<Integer> scores) {
        for(int i = 0; i < playerScores.size(); i++) {
            if(i == 0) {
                playerScores.get(i).setText("You have " + scores.get(i) + " points");

            }
            else {
                playerScores.get(i).setText("Player " + Integer.toString(i + 1) + " has " + scores.get(i) + " points");
            }

            switch(i){
                case 0: playerScores.get(i).setTextFill(player1); break;
                case 1: playerScores.get(i).setTextFill(player2); break;
                case 2: playerScores.get(i).setTextFill(player3); break;
                case 3: playerScores.get(i).setTextFill(player4); break;
            }

            playerScores.get(i).setStyle("-fx-font: 14 arial");
            playerScores.get(i).setAlignment(Pos.CENTER_LEFT);
        }
    }


    public FlowPane View() { return this.displayPane;}

}
