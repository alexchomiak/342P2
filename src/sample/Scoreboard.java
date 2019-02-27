package sample;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import static sample.PitchConstants.*;
import java.util.ArrayList;



public class Scoreboard {
    FlowPane displayPane;

    Label scorePrompt;
    Label trumpPrompt;
    ArrayList<Label> playerScores;
    ImageView trumpView;


    Scoreboard(int numPlayers) {
        displayPane = new FlowPane(Orientation.VERTICAL);
        displayPane.setVgap(10);
        displayPane.setHgap(20);
        displayPane.setPadding(new Insets(20,20,20,20));

        scorePrompt = new Label();
        scorePrompt.setText("Scoreboard");
        scorePrompt.setStyle("-fx-font: 18 arial");
        scorePrompt.setTextFill(textColor);
        displayPane.getChildren().add(scorePrompt);
        scorePrompt.setAlignment(Pos.CENTER);

        playerScores = new ArrayList<Label>();
        for(int i = 0; i < numPlayers; i++) {
            playerScores.add(new Label());
        }

        playerScores.forEach(label -> displayPane.getChildren().add(label));
        displayPane.setAlignment(Pos.CENTER);

        displayPane.setStyle(sideBarStyle);
        ArrayList<Integer> initial = new ArrayList<Integer>();
        initial.add(0);
        initial.add(0);
        initial.add(0);
        initial.add(0);
        setScores(initial);

        trumpPrompt = new Label("Current Trump Suit");
        trumpPrompt.setTextFill(textColor);
        trumpPrompt.setStyle("-fx-font: 18 arial");


        VBox Trump = new VBox(10);
        trumpView = new ImageView();
        trumpView.setFitWidth(50);
        trumpView.setFitHeight(50);

        Trump.setAlignment(Pos.CENTER);
        Trump.getChildren().addAll(trumpPrompt, trumpView);

        displayPane.getChildren().addAll(Trump);


    }

    void setTrumpSuit(char c) {
        if(c == 'E') {
            trumpView.setImage(null);
            return;
        }
        Image trump = new Image("/Assets/Suits/" + c + ".png");
        trumpView.setImage(trump);
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
