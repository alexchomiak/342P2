package game;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import static game.PitchConstants.*;
import java.util.ArrayList;

public class PitchScoreCalculator {
    public PitchScoreCalculator(){}

    public Player calculateTrickWinner(Pitch game){
        //grab currentTrick from game
        Deck currentTrick = game.getCurrentTrick();

        //grab startplayer from game
        Player startPlayer = game.getStartPlayer();

        //grab cards from current trick
        ArrayList<Card> Cards = currentTrick.getCards();

        //set winning index to 0
        int winningIndex = 0;

        //possible winning indexes arraylist
        ArrayList<Integer> possibleWinningIndexes = new ArrayList<Integer>();

        //set containsTrump boolean
        boolean containsTrump = false;

        //check for trump suit
        for(int i = 0; i < Cards.size(); i++) {
            if(Cards.get(i).getFace() == game.getCurrentTrumpSuit()) {
                possibleWinningIndexes.add(i);
                containsTrump = true;
            }
        }

        //if trick contains trump,
        //look for highest trump rank
        if(containsTrump) {
            int currentHighestRank = 0;
            int currentHighestIndex = -1;

            //loop through each possible winning index and look for the highest
            //ranked trump card in the trick
            for(int i = 0; i < possibleWinningIndexes.size(); i++) {
                int currentIndex = possibleWinningIndexes.get(i);

                //if it is higher than the current rank, set the currentHighestIndex
                //to the current index
                if(Cards.get(currentIndex).getRank() > currentHighestRank) {
                    currentHighestRank = Cards.get(currentIndex).getRank();
                    currentHighestIndex = currentIndex;
                }

                //if is an ace, do the same, and set current highest rank
                //to arbitrarily high value
                if(Cards.get(currentIndex).getRank() == 1) {
                    currentHighestIndex = currentIndex;
                    currentHighestRank = 25;
                }
            }

            //set winning index to current highest index
            winningIndex = currentHighestIndex;
        }
        else {
            //loop through each card looking for lead suit
            //and add possible winning indexes to lead
            for(int i = 0; i < Cards.size(); i++) {
                if(Cards.get(i).getFace() == game.getCurrentLeadSuit()) {
                    possibleWinningIndexes.add(i);
                }
            }

            int currentHighestRank = 0;
            int currentHighestIndex = -1;

            //loop through each possible winning index and look for the highest ranked
            //lead card in the trick
            for(int i = 0; i < possibleWinningIndexes.size(); i++) {
                int currentIndex = possibleWinningIndexes.get(i);

                //if its higher than the current highest rank
                //set the current highest to the current cards rank
                //and set currentHighest index to index
                if(Cards.get(currentIndex).getRank() > currentHighestRank) {
                    currentHighestRank = Cards.get(currentIndex).getRank();
                    currentHighestIndex = currentIndex;
                }

                //if it is ace doe the same, and set the current highest rank to
                //an arbitrarily high value
                if(Cards.get(currentIndex).getRank() == 1) {
                    currentHighestIndex = currentIndex;
                    currentHighestRank = 25;
                }
            }

            //set winning index to current highest index
            winningIndex = currentHighestIndex;
        }

        //set start player of next trick
        for(int i = 0 ; i < winningIndex; i++) {
            startPlayer = startPlayer.getNextPlayer();
        }

        //set trick winning index
        game.setTrickWinningIndex(winningIndex);
        return startPlayer;
    }


    public VBox calculateRoundScore(Pitch game) {
        //this function calculates the given round score
        //and returns a vbox with the round summary
        //it updates the parent games score and checks for winners

        //set round summary state to true
        game.setRoundSummaryInProgress(true);

        //hide turn prompt
        if(game.getScoreboard() != null)  game.getScoreboard().setTurnPrompt(0);

        //get current trump suit
        char trump = game.getCurrentTrumpSuit();

        //initialize round summary vbox
        VBox roundSummary = new VBox(10);
        roundSummary.setAlignment(Pos.CENTER);
        roundSummary.setStyle(roundSummaryStyle);

        //get playerTricks into 2D array
        ArrayList< ArrayList<Card> > playerTricks = new ArrayList< ArrayList<Card> >();

        //add each players won tricks array to the playerTricks array
        Player iterator = game.getPlayer();
        ArrayList<Integer> gamePoints = new ArrayList<Integer>();
        for(int i = 0; i < game.getPlayerCount(); i++) {
            //add 0 to gamepoints
            gamePoints.add(0);

            //add array to player tricks
            playerTricks.add(iterator.getTricks().getCards());

            //go to next player
            iterator = iterator.getNextPlayer();
        }


        //initialize winning indexes
        int playerLowTrumpWinner = -1;
        int playerHighTrumpWinner = -1;
        int playerJackTrumpWinner = -1;
        int playerGamePointsWinner = -1;
        int playerSmudgeWinner = -1;

        //figure out low trump winner && high trump winner && jack trump winner
        int lowTrumpRank = 30;
        int highTrumpRank = 0;
        int maxPointsScored = -1;

        for(int i = 0; i < playerTricks.size(); i++) {
            //loop through each players won tricks and calculate scores

            ArrayList<Card> currentTricks = playerTricks.get(i);
            int pointsScored = 0;

            //loop through each card in trick
            for(int j = 0; j < currentTricks.size(); j++) {
                Card card = currentTricks.get(j);

                int rank = card.getRank();
                char face = card.getFace();


                //increment score count for player
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

                //see if rank is greater than currrent high trump
                if(rank > highTrumpRank && face == trump) {
                    highTrumpRank = rank;
                    playerHighTrumpWinner = i;
                }

                //see if rank is lower than current low trump
                if(rank < lowTrumpRank && face == trump) {
                    lowTrumpRank = rank;
                    playerLowTrumpWinner = i;
                }

            }

            //if there is a tie for points scored, set winner to -1
            if(pointsScored == maxPointsScored) {
                playerGamePointsWinner = -1;
            }

            //if pointsScored > maxPoints scored, update game points winner
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

            //show score summary for each player
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

            //set text and fill
            roundLabel.setText(prompt);
            roundLabel.setTextFill(getColor(i));

            //add to roundsummary
            roundSummary.getChildren().add(roundLabel);

            //update player score
            game.getCurrentScores().set(i,game.getCurrentScores().get(i) + roundScore);

            //reset player bids
            iterator.resetBids();

            //go to next player
            iterator = iterator.getNextPlayer();

        }

        //check winners
        boolean playersWon = false;
        for(int i = 0; i < game.getCurrentScores().size(); i++) {
            if(game.getCurrentScores().get(i) >= scoreLimit) {
                playersWon = true;
            }
        }

        //Next Round/EndGame prompt button
        String promptText = "Next Round";
        if(playersWon) promptText = "End Game (Winner Found)";
        Button nextRound = new Button(promptText);


        //set nextround button event listener based off
        //game is finished or not
        if(!playersWon) {
            nextRound.setOnAction( e -> {
                game.setRoundSummaryInProgress(false);
            });
        }
        else {
            nextRound.setOnAction( e -> {
                game.end();
            });
        }

        //add next button to roundsummary
        roundSummary.getChildren().add(nextRound);

        //return roundsummary
        return roundSummary;
    }


    Color getColor(int playerIndex) {
        //Utility function for getting player color and text coloring
        switch(playerIndex) {
            case 0: return player1;
            case 1: return player2;
            case 2: return player3;
            case 3: return player4;
            default: return Color.rgb(255,255,255);
        }
    }

}
