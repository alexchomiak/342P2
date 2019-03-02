package sample;

import java.util.ArrayList;
import java.util.Random;

public class AIPlayer extends Player{
    AIPlayer(Deck gameField, Deck currentTrick, Pitch parent) {
        super(gameField, currentTrick,parent);

    }

    private char optimalSuit;
    private void setOptimalSuit(char o){optimalSuit = o;}

    public void makeBestMove() {

        Card selectedCard = null;
        ArrayList<Card> cards = hand.getCards();

        //if first move, find highest ranking of optimal suit and place it down, else
        //loop through and play highest ranked trump card/lead card;
        if(cards.size() == 6 && this == parent.getStartPlayer()) {
            int highestRank = 0;
            for(int i = 0; i < cards.size(); i++) {
                if(cards.get(i).getRank() == 1 && cards.get(i).getFace() == this.optimalSuit) {
                    selectedCard = cards.get(i);
                    break;
                }
                if(cards.get(i).getRank() > highestRank && cards.get(i).getFace() == this.optimalSuit) {
                    selectedCard = cards.get(i);
                    highestRank = cards.get(i).getRank();
                }
            }
        }
        else {
            if(this != parent.getStartPlayer() && parent.getCurrentLeadSuit() != parent.getCurrentTrumpSuit()) {
                int maxRank = 0;
                for(int i = 0; i < cards.size(); i++) {
                    if(cards.get(i).getFace() == parent.getCurrentLeadSuit() && (cards.get(i).getRank() > maxRank || cards.get(i).getRank() == 1)) {
                        selectedCard = cards.get(i);
                        if(cards.get(i).getRank() == 1) break;
                        maxRank = cards.get(i).getRank();
                    }
                }
            } else if(parent.getCurrentTrumpSuit() == parent.getCurrentLeadSuit()) {
                int maxRank = 0;
                for(int i = 0; i < cards.size(); i++) {
                    if(cards.get(i).getFace() == parent.getCurrentTrumpSuit() && (cards.get(i).getRank() > maxRank || cards.get(i).getRank() == 1)) {
                        selectedCard = cards.get(i);
                        if(cards.get(i).getRank() == 1) break;
                        maxRank = cards.get(i).getRank();
                    }
                }
            }


        }


        //if at this point selected card is null, pick a random card from the deck
        if(selectedCard == null) {
            Random rand = new Random();
            selectedCard = cards.get(rand.nextInt(cards.size()));
        }

        makeMove(selectedCard);


        turnCompleted = true;

        startNextTurn();

    }

    void startTurn(boolean startingPlayer) {

        parent.getScoreboard().setTurnPrompt(playerNumber);

        turnStarted = true;
        this.startingPlayer = startingPlayer;
        if(hand.getCards().size() == 0) return;
        makeBestMove();
        if(turnCompleted) return;
        hand.setSelectedCard(null);
        hand.setSelectable(true);
    }


    void makeMove(Card move) {
        if(startingPlayer) {
            parent.setCurrentLeadSuit(move.getFace());
            turnStarted = true;

            if(parent.getTrickNum() == 1) {
                parent.setCurrentTrumpSuit(move.getFace());
                parent.getScoreboard().setTrumpSuit(move.getFace());
            }
        }

        currentTrick.addCard(move);
        hand.moveCardTo(gameField,move);
        hand.setSelectable(false);

    }

    void makeBid() {
        int bid = calculateBestBid();
        handleBid(bid);
    }



    //AI ALGORITHMS START
    int calculateBestBid() {
        int calculatedBid = 0;

        //calculate eligible bids
        ArrayList<Integer> currentBids = parent.getCurrentBids();
        ArrayList<Integer> possibleBids = new ArrayList<Integer>();
        possibleBids.add(0);
        for(int i = 2; i < 5; i++) {
            boolean containsI = false;
            for(int j = 0; j < currentBids.size(); j++) {
                if(currentBids.get(j) == i) containsI = true;
            }
            if(!containsI) possibleBids.add(i);
        }

        //calculate optimal bids per suit in hand, and take optimal hand while attempting to make lowest bid
        ArrayList<Integer> optimalSuitBids = new ArrayList<Integer>();
        ArrayList<Card> cards = hand.getCards();

        int maxBid = 0;
        char chosenSuit = 'E';
        System.out.println("Bid");
        for(int i = 0; i < 4; i++) {
            char suit = 'E';
            switch(i) {
                case 0: suit = 'C'; break;
                case 1: suit = 'D'; break;
                case 2: suit = 'H'; break;
                case 3: suit = 'S'; break;
            }

            boolean possibleGameHigh = false;
            boolean possibleHighTrump = false;
            boolean possibleLowTrump = false;
            boolean possibleJackTrump = false;

            int numScoringCards = 0;

            for(int j = 1; j < cards.size(); j++) {
                int rank = cards.get(j).getRank();
                if(cards.get(j).getFace() != suit) continue;

                if(rank == 1 || rank >= 10) numScoringCards++;

                if(rank == 11) possibleJackTrump = true;

                if(rank == 1 || rank > 10) possibleHighTrump = true;

                if(rank != 1 && rank < 4) possibleLowTrump = true;


            }

            if(numScoringCards >= 2) possibleGameHigh = true;

            int suitBid = 0;
            if(possibleGameHigh) suitBid++;
            if(possibleHighTrump && possibleGameHigh) suitBid++;
            if(possibleJackTrump || numScoringCards >= 3) suitBid++;
            if(possibleLowTrump && possibleGameHigh) suitBid++;
            if(suitBid == 4) suitBid++;
            optimalSuitBids.add(suitBid);

            System.out.println(suit + ": " + Integer.toString(suitBid) + " Scoring cards: " + Integer.toString(numScoringCards));
            if(suitBid > maxBid && suitBid > 1) {
                maxBid = suitBid;
                chosenSuit = suit;
            }
        }

        if(maxBid == 0) chosenSuit = cards.get(0).getFace();

        //determine bid amount, and if its worth it based off of current bids
        for(int i = 0; i < possibleBids.size(); i++) {
            if(possibleBids.get(i) <= maxBid) {
                if(possibleBids.get(i) == 0 && maxBid >= 2) continue;
                calculatedBid = possibleBids.get(i);
                break;
            }
        }

        setOptimalSuit(chosenSuit);
        return calculatedBid;
    }


    void calculateBestMove() {

    }

}
