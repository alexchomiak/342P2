package sample;

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
}
