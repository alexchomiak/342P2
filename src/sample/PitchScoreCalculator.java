package sample;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import static sample.PitchConstants.*;
import java.util.ArrayList;
import java.util.Random;

public class PitchScoreCalculator {


    PitchScoreCalculator(){}

    Player calculateTrickWinner(Pitch game){
        Deck currentTrick = game.getCurrentTrick();
        Player startPlayer = game.getStartPlayer();

        ArrayList<Card> Cards = currentTrick.getCards();

        int winningIndex = 0;

        ArrayList<Integer> possibleWinningIndexes = new ArrayList<Integer>();


        boolean containsTrump = false;
        //check for trump suit
        for(int i = 0; i < Cards.size(); i++) {
            if(Cards.get(i).getFace() == game.getCurrentTrumpSuit()) {
                possibleWinningIndexes.add(i);
                containsTrump = true;
            }
        }

        if(containsTrump) {
            int currentHighestRank = 0;
            int currentHighestIndex = -1;

            for(int i = 0; i < possibleWinningIndexes.size(); i++) {
                int currentIndex = possibleWinningIndexes.get(i);
                if(Cards.get(currentIndex).getRank() > currentHighestRank) {
                    currentHighestRank = Cards.get(currentIndex).getRank();
                    currentHighestIndex = currentIndex;
                }
                if(Cards.get(currentIndex).getRank() == 1) {
                    currentHighestIndex = currentIndex;
                    currentHighestRank = 25;
                }
            }
            winningIndex = currentHighestIndex;
        }
        else {
            for(int i = 0; i < Cards.size(); i++) {
                if(Cards.get(i).getFace() == game.getCurrentLeadSuit()) {
                    possibleWinningIndexes.add(i);
                }
            }

            int currentHighestRank = 0;
            int currentHighestIndex = -1;

            for(int i = 0; i < possibleWinningIndexes.size(); i++) {
                int currentIndex = possibleWinningIndexes.get(i);
                if(Cards.get(currentIndex).getRank() > currentHighestRank) {
                    currentHighestRank = Cards.get(currentIndex).getRank();
                    currentHighestIndex = currentIndex;
                }
                if(Cards.get(currentIndex).getRank() == 1) {
                    currentHighestIndex = currentIndex;
                    currentHighestRank = 25;
                }
            }

            winningIndex = currentHighestIndex;

        }






        for(int i =0 ; i < winningIndex; i++) {
            startPlayer = startPlayer.getNextPlayer();
        }



        game.setTrickWinningIndex(winningIndex);
        return startPlayer;
    }


    VBox calculateRoundScore(Pitch game) {
        game.setRoundSummaryInProgress(true);
        game.getScoreboard().setTurnPrompt(0);

        char trump = game.getCurrentTrumpSuit();

        VBox roundSummary = new VBox(10);
        roundSummary.setAlignment(Pos.CENTER);
        roundSummary.setStyle(roundSummaryStyle);

        ArrayList< ArrayList<Card> > playerTricks = new ArrayList< ArrayList<Card> >();

        Player iterator = game.getPlayer();
        ArrayList<Integer> gamePoints = new ArrayList<Integer>();
        for(int i = 0; i < game.getPlayerCount(); i++) {
            gamePoints.add(0);
            playerTricks.add(iterator.getTricks().getCards());
            iterator = iterator.getNextPlayer();
        }

        int playerLowTrumpWinner = -1;
        int playerHighTrumpWinner = -1;
        int playerJackTrumpWinner = -1;
        int playerGamePointsWinner = -1;
        int playerSmudgeWinner = -1;

        //figure out low trump winner && high trump winner && jack trump winner
        int lowTrumpRank = 30;
        int highTrumpRank = 0;
        int maxPointsScored = -1;

        System.out.println(trump);
        for(int i = 0; i < playerTricks.size(); i++) {
            ArrayList<Card> currentTricks = playerTricks.get(i);
            int pointsScored = 0;

            System.out.println("Player " + Integer.toString(i));

            for(int j = 0; j < currentTricks.size(); j++) {
                Card card = currentTricks.get(j);

                int rank = card.getRank();
                char face = card.getFace();

                System.out.println(Integer.toString(rank) + face);

                //increment scores
                switch(rank) {
                    case 10: pointsScored += 10; break;
                    case 11: pointsScored += 1; break;
                    case 12: pointsScored += 2; break;
                    case 13: pointsScored += 3; break;
                    case 1: pointsScored += 4; break;
                    default: pointsScored += 0; break;
                }


                //if ace set to higher value
                if(rank == 1) rank = 25;

                //see if rank is jack
                if(rank == 11 && face == trump) {
                    playerJackTrumpWinner = i;
                }

                if(rank > highTrumpRank && face == trump) {
                    highTrumpRank = rank;
                    playerHighTrumpWinner = i;
                }

                if(rank < lowTrumpRank && face == trump) {
                    lowTrumpRank = rank;
                    playerLowTrumpWinner = i;
                }

            }

            if(pointsScored == maxPointsScored) {
                playerGamePointsWinner = -1;
            }

            if(pointsScored > maxPointsScored) {
                maxPointsScored = pointsScored;
                playerGamePointsWinner = i;
            }


            if((currentTricks.size() == game.getPlayerCount() * 6) && playerJackTrumpWinner == i) {
                playerSmudgeWinner = i;
            }

        }


        //set iterator to smudge player
        iterator = game.getPlayer();
        for(int i = 0; i < playerSmudgeWinner; i++) {
            iterator = iterator.getNextPlayer();
        }


        //if they did not bet smudge, they will not win the point
        if(iterator.getCurrentBid() != 5) {
            playerSmudgeWinner = -1;
        }



        System.out.println(playerGamePointsWinner);
        System.out.print(playerHighTrumpWinner);

        System.out.print(playerLowTrumpWinner);
        System.out.println(playerJackTrumpWinner);
        System.out.println(playerSmudgeWinner);




        //score summary label
        Label scoreSummary = new Label("Round Summary");
        scoreSummary.setTextFill(textColor);
        scoreSummary.setStyle("-fx-font: 18px arial");

        //Game points winnner
        Label gamePointsWinner = new Label("Highest Game Points Winner (+1): Player " + Integer.toString((playerGamePointsWinner + 1)));
        if(playerGamePointsWinner == -1) gamePointsWinner.setText("No player won Highest Game Points (Tie)");
        gamePointsWinner.setTextFill(getColor(playerGamePointsWinner));

        //High trump winner
        Label highTrumpWinner = new Label("High Trump Winner (+1): Player " + Integer.toString((playerHighTrumpWinner + 1)));
        highTrumpWinner.setTextFill(getColor(playerHighTrumpWinner));

        //Low trump winner
        Label lowTrumpWinner = new Label("Low Trump Winner (+1): Player " + Integer.toString((playerLowTrumpWinner + 1)));
        lowTrumpWinner.setTextFill(getColor(playerLowTrumpWinner));

        //jack trump winner
        Label jackTrumpWinner = new Label("Jack Trump Winner (+1): Player "  + Integer.toString((playerJackTrumpWinner + 1)));
        if(playerJackTrumpWinner == -1) jackTrumpWinner.setText("No Player Won the Jack Trump");
        jackTrumpWinner.setTextFill(getColor(playerJackTrumpWinner));

        //smudge winner
        Label smudgeWinner = new Label("Smudge Winner (+1): "  + Integer.toString((playerSmudgeWinner + 1)));
        if(playerSmudgeWinner == -1) {
            smudgeWinner.setText("No player Won the Smudge");
            smudgeWinner.setOpacity(0.0);
        }
        smudgeWinner.setTextFill(getColor(playerSmudgeWinner));


        //player summary label
        Label playerSummary = new Label("Score Summary");
        playerSummary.setTextFill(textColor);
        playerSummary.setStyle("-fx-font: 18px arial;");

        roundSummary.getChildren().addAll(scoreSummary,gamePointsWinner,highTrumpWinner,lowTrumpWinner,jackTrumpWinner,smudgeWinner,playerSummary);

        //player score summary
        iterator = game.getPlayer();
        for(int i = 0; i < game.getPlayerCount(); i++) {
            Label roundLabel = new Label();
            String prompt = new String("");
            int roundScore = 0;
            if(i == playerGamePointsWinner) roundScore++;
            if(i == playerHighTrumpWinner) roundScore++;
            if(i == playerLowTrumpWinner) roundScore++;
            if(i == playerJackTrumpWinner) roundScore++;
            if(i == playerSmudgeWinner)  roundScore++;


            if(iterator.getCurrentBid() > 0) {
                if(i == 0) {
                    prompt += "You bid ";
                }else {
                    prompt += "Player " + Integer.toString(iterator.getPlayerNumber()) + " bid ";
                }
                prompt += Integer.toString(iterator.getCurrentBid()) + ", and ";
                if(roundScore >= iterator.getCurrentBid()) {
                    prompt += "won. (+" + Integer.toString(roundScore) + ")";
                } else {
                    prompt += "lost. (-" + Integer.toString(iterator.getCurrentBid()) + ")";
                    roundScore = -1 * iterator.getCurrentBid();
                }
            }
            else {
                roundScore = 0;
                if(i == 0) {
                    prompt = "You passed. (+0)";
                }
                else {
                    prompt = "Player " + Integer.toString(i + 1) + " passed. (+0)";
                }

            }


            roundLabel.setText(prompt);
            roundLabel.setTextFill(getColor(i));
            roundSummary.getChildren().add(roundLabel);

            game.getCurrentScores().set(i,game.getCurrentScores().get(i) + roundScore);

            iterator.resetBids();
            iterator = iterator.getNextPlayer();

        }

        //Next Round button

        Button nextRound = new Button("Next Round");
        nextRound.setOnAction( e -> {
            game.setRoundSummaryInProgress(false);
        });


        roundSummary.getChildren().add(nextRound);


        return roundSummary;
    }


    Color getColor(int playerIndex) {

        switch(playerIndex) {
            case 0: return player1;
            case 1: return player2;
            case 2: return player3;
            case 3: return player4;
            default: return Color.rgb(255,255,255);
        }
    }

}
