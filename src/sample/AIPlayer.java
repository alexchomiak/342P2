package sample;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class AIPlayer extends Player{
    public AIPlayer(Deck gameField, Deck currentTrick, Pitch parent) {
        super(gameField, currentTrick,parent);

    }

    private ArrayList<Integer> optimalSuitBids;

    public void makeBestMove() {

        Card selectedCard = null;
        ArrayList<Card> cards = hand.getCards();

        //sort arraylist in ascending order by rank
        Collections.sort(cards, (o1,o2) -> o1.getRank() > o2.getRank() ? 1 : -1);

        System.out.println("test sort");
        System.out.println(cards.get(0).getRank());
        System.out.println(cards.get(cards.size() - 1).getRank());
        System.out.println("----------");

        //if first move, find highest ranking of optimal suit and place it down, else
                //loop through and play highest ranked trump card/lead card;
        if(this == parent.getStartPlayer()) {

            char chosenPlayedSuit;
            int maxbid = -1;
            for(int i = 0; i < optimalSuitBids.size(); i++) {
                if(optimalSuitBids.get(i) > maxbid){
                    boolean cardStillExistInHand = false;

                    for(int j = 0; j < cards.size(); j++) {
                        if(cards.get(j).getFace() == getSuit(i)) {
                            cardStillExistInHand = true;
                        }
                    }

                    if(cardStillExistInHand) maxbid = i;
                }
            }
            chosenPlayedSuit = getSuit(maxbid);

            int highestRank = 0;
            for(int i = 0; i < cards.size(); i++) {
                if(cards.get(i).getRank() == 1 && cards.get(i).getFace() == chosenPlayedSuit) {
                    selectedCard = cards.get(i);
                    break;
                }
                if(cards.get(i).getRank() > highestRank && cards.get(i).getFace() == chosenPlayedSuit) {
                    selectedCard = cards.get(i);
                    highestRank = i;
                }
            }


        }
        else {

            ArrayList<Card> currentTrick = parent.getCurrentTrick().getCards();
            System.out.println("Current trick size " + Integer.toString(currentTrick.size()));

            //decide if current trick contains trump suit
            boolean currentTrickContainsTrump = false;
            int maxLeadRank = -1;
            int maxTrumpRank = -1;
            for(int i = 0; i < currentTrick.size(); i++) {
                if(currentTrick.get(i).getFace() == parent.getCurrentTrumpSuit()) {
                    currentTrickContainsTrump = true;
                    if(currentTrick.get(i).getRank() > maxTrumpRank) {
                        if(currentTrick.get(i).getRank() == 1) maxTrumpRank = 15;
                        else maxTrumpRank = currentTrick.get(i).getRank();
                    }


                }

                if(currentTrick.get(i).getRank() > maxLeadRank && currentTrick.get(i).getFace() == parent.getCurrentLeadSuit()) {
                    if(currentTrick.get(i).getRank() == 1) maxLeadRank = 15;
                    else maxLeadRank = currentTrick.get(i).getRank();
                }
            }

            //set booleans used for calculation
            boolean maxTrumpRankIsScoring = (maxTrumpRank == 15) || (maxTrumpRank == 10) || (maxTrumpRank == 11) || (maxTrumpRank == 12) || (maxTrumpRank ==13);
            boolean maxLeadRankIsScoring = (maxLeadRank == 15) || (maxLeadRank == 10) || (maxLeadRank == 11) || (maxLeadRank == 12) || (maxLeadRank ==13);
            boolean handContainsTrump = false;
            boolean handContainsLead = false;
            for(int i = 0; i < cards.size(); i++) {
                if(cards.get(i).getFace() == parent.getCurrentLeadSuit()) handContainsLead = true;
                if(cards.get(i).getFace() == parent.getCurrentTrumpSuit()) handContainsTrump = true;
            }


            //if current trick contains trump, look for optimal trump card to play
            if(currentTrickContainsTrump && handContainsTrump) {
                int highestRank = 0;
                for(int i = 0; i < cards.size(); i++) {
                    if(cards.get(i).getRank() > maxTrumpRank && cards.get(i).getFace() == parent.getCurrentTrumpSuit() && cards.get(i).getRank() > highestRank) {
                        if(cards.get(i).getRank() == 1) highestRank = 15;
                        else highestRank = cards.get(i).getRank();
                        selectedCard = cards.get(i);

                        //if max trump is scoring, tries to capture it by placing  highest trump available
                        if(maxTrumpRankIsScoring) continue;
                        else break; //else, just satisfied with higher rank than current rump rank
                    }
                }

                //if cant beat current trump card, place lowest trump card in hand if cant match lead
                if(selectedCard == null && !handContainsLead) {
                    int minRank = 16;
                    for(int i = 0; i < cards.size(); i++) {
                        if(cards.get(i).getRank() < minRank && cards.get(i).getFace() == parent.getCurrentTrumpSuit()) {
                            minRank = cards.get(i).getRank();
                            selectedCard = cards.get(i);
                        }
                    }
                }
            }


            if(handContainsLead && selectedCard == null) {
                int highestRank = 0;
                for(int i = 0; i < cards.size(); i++) {
                    if(handContainsTrump && parent.getCurrentTrumpSuit() != parent.getCurrentLeadSuit()) continue;;
                    if(cards.get(i).getRank() > maxLeadRank && cards.get(i).getFace() == parent.getCurrentLeadSuit() && cards.get(i).getRank() > highestRank) {
                        if(cards.get(i).getRank() == 1) highestRank = 15;
                        else highestRank = cards.get(i).getRank();
                        selectedCard = cards.get(i);

                        if(maxLeadRankIsScoring) continue;
                        else break;
                    }
                }

                //set lowest lead card if selected is null, place lowest leading suit
                if(selectedCard == null) {
                    int minRank = 16;
                    for(int i = 0; i < cards.size(); i++) {
                        if(cards.get(i).getRank() < minRank && cards.get(i).getFace() == parent.getCurrentLeadSuit()) {
                            minRank = cards.get(i).getRank();
                            selectedCard = cards.get(i);
                        }
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

    public void startTurn(boolean startingPlayer) {

        parent.getScoreboard().setTurnPrompt(playerNumber);

        turnStarted = true;
        this.startingPlayer = startingPlayer;
        if(hand.getCards().size() == 0) return;
        makeBestMove();
        if(turnCompleted) return;
        hand.setSelectedCard(null);
        hand.setSelectable(true);
    }


    private void makeMove(Card move) {
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

    public void makeBid() {
        int bid = calculateBestBid();
        handleBid(bid);
    }




    //AI ALGORITHMS START
    private int calculateBestBid() {
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
            suit = getSuit(i);

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
            }

            if(suitBid > maxBid) {
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

        this.optimalSuitBids = optimalSuitBids;
        return calculatedBid;
    }


    private char getSuit(int i) {
        char suit = 'E';
        switch(i) {
            case 0: suit = 'C'; break;
            case 1: suit = 'D'; break;
            case 2: suit = 'H'; break;
            case 3: suit = 'S'; break;
        }
        return suit;
    }

}
