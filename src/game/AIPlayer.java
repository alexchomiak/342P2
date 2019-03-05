package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AIPlayer extends Player{
    public AIPlayer(Deck gameField, Deck currentTrick, Pitch parent) {
        super(gameField, currentTrick,parent);

    }

    private ArrayList<Integer> optimalSuitBids;

    public void makeBestMove() {
        //this card selects the best card in the AI's current deck
        //and makes the best selection it can

        //set selection to null initially
        Card selectedCard = null;

        //get current hand
        ArrayList<Card> cards = hand.getCards();

        //sort arraylist in ascending order by rank
        Collections.sort(cards, (o1,o2) -> o1.getRank() > o2.getRank() ? 1 : -1);

        //if first move, find highest ranking of optimal suit and place it down, else
        //loop through and play highest ranked trump card/lead card;
        if(this == parent.getStartPlayer()) {
            //chosen played suit char
            char chosenPlayedSuit;

            //initialize maxBid to -1
            int maxbid = -1;

            //loop through optimal suit bids set by
            //the bidding stage of the program
            for(int i = 0; i < optimalSuitBids.size(); i++) {
                //if it is greater than the current maxbid
                if(optimalSuitBids.get(i) > maxbid){
                    boolean cardStillExistInHand = false;

                    //if any card of the bid suit exists in the hand, set cardStillExistInHand to true
                    for(int j = 0; j < cards.size(); j++) {
                        if(cards.get(j).getFace() == getSuit(i)) {
                            cardStillExistInHand = true;
                        }
                    }

                    //set max bid to i if the cardStill exists in the hand
                    if(cardStillExistInHand) maxbid = i;
                }
            }

            //set chosenPlayedSuit
            chosenPlayedSuit = getSuit(maxbid);

            //find highest rank of chosen played suit
            int highestRank = 0;
            for(int i = 0; i < cards.size(); i++) {
                //if it is an ace, set it to the selected card and play it
                if(cards.get(i).getRank() == 1 && cards.get(i).getFace() == chosenPlayedSuit) {
                    selectedCard = cards.get(i);
                    break;
                }
                if(cards.get(i).getRank() > highestRank && cards.get(i).getFace() == chosenPlayedSuit) {
                    //other wise, set the current highest found rank to i, and set the selected card
                    //to cards.get(i)
                    selectedCard = cards.get(i);
                    highestRank = cards.get(i).getRank();
                }
            }


        }
        else {
            //if not first to move, grab current trick from playfield
            ArrayList<Card> currentTrick = parent.getCurrentTrick().getCards();

            //decide if current trick contains trump suit
            boolean currentTrickContainsTrump = false;

            //set initial maxLead and maxTrump rankings
            int maxLeadRank = -1;
            int maxTrumpRank = -1;

            //loop through each card in the current trick and decide the highest ranked cards of the lead and trump
            for(int i = 0; i < currentTrick.size(); i++) {
                if(currentTrick.get(i).getFace() == parent.getCurrentTrumpSuit()) {
                    //if current card is trump, set currentTrickContainsTrump to true
                    currentTrickContainsTrump = true;

                    //if current card is greater than current max trump rank
                    if(currentTrick.get(i).getRank() > maxTrumpRank) {
                        //update max trump rank
                        //if rank is 1, card is ace, and set max trump rank to arbitrarily high value
                        if(currentTrick.get(i).getRank() == 1) maxTrumpRank = 15;
                        else maxTrumpRank = currentTrick.get(i).getRank();
                    }
                }

                //other wise, if current card is lead and greater than maxlead rank
                //update maxlead rank
                if(currentTrick.get(i).getRank() > maxLeadRank && currentTrick.get(i).getFace() == parent.getCurrentLeadSuit()) {
                    //update max lead rank
                    //if ace, set to arbitrarily high value similar to before
                    if(currentTrick.get(i).getRank() == 1) maxLeadRank = 15;
                    else maxLeadRank = currentTrick.get(i).getRank();
                }
            }

            //set booleans used for calculation
            //if a card is ace, 10, J, Q, or K, it is consider scoring
            boolean maxTrumpRankIsScoring = (maxTrumpRank == 15) || (maxTrumpRank == 10) || (maxTrumpRank == 11) || (maxTrumpRank == 12) || (maxTrumpRank ==13);
            boolean maxLeadRankIsScoring = (maxLeadRank == 15) || (maxLeadRank == 10) || (maxLeadRank == 11) || (maxLeadRank == 12) || (maxLeadRank ==13);

            //booleans for current hand
            boolean handContainsTrump = false;
            boolean handContainsLead = false;

            //loop through hand and set hand state booleans
            for(int i = 0; i < cards.size(); i++) {
                if(cards.get(i).getFace() == parent.getCurrentLeadSuit()) handContainsLead = true;
                if(cards.get(i).getFace() == parent.getCurrentTrumpSuit()) handContainsTrump = true;
            }

            //if current trick contains trump, look for optimal trump card to play
            if(currentTrickContainsTrump && handContainsTrump) {
                //initialize highest rank to 0
                int highestRank = 0;
                for(int i = 0; i < cards.size(); i++) {
                    //if there exists a card that can beat the current max trump rank on the trick
                    //and is higher than the current highest trump in the hand
                    //set selected card to that card and update highest rank
                    if(cards.get(i).getRank() > maxTrumpRank && cards.get(i).getFace() == parent.getCurrentTrumpSuit() && cards.get(i).getRank() > highestRank) {
                        //if the card is an ace, set to arbitrarily high value, otherwise set to cards rank
                        if(cards.get(i).getRank() == 1) highestRank = 15;
                        else highestRank = cards.get(i).getRank();

                        //update selected card
                        selectedCard = cards.get(i);

                        //if max trump is scoring, tries to capture it by placing  highest trump available
                        if(maxTrumpRankIsScoring) continue;
                        else break; //else, just satisfied with higher rank than current rump rank
                    }
                }

                //if cant beat current trump card,
                //and hand does not contain lead,
                //place lowest trump card in hand if cant match lead
                if(selectedCard == null && !handContainsLead) {
                    int minRank = 16; // set min rank to arbitrarily high value

                    //loop through hand looking for lowest ranked trump
                    for(int i = 0; i < cards.size(); i++) {
                        if(cards.get(i).getRank() < minRank && cards.get(i).getFace() == parent.getCurrentTrumpSuit()) {
                            //set min rank to current rank of card if is lower than current min rank
                            minRank = cards.get(i).getRank();

                            //set selected card to current card
                            selectedCard = cards.get(i);
                        }
                    }
                }
            }

            //if playable trump card not found,
            //loop through hand looking for playable lead card
            if(handContainsLead && selectedCard == null) {
                //set highest rank to 0
                int highestRank = 0;

                //loop through hand looking through hand
                for(int i = 0; i < cards.size(); i++) {
                    //if hand already contains trump, dont bother playing highest ranked lead card
                    if(handContainsTrump && parent.getCurrentTrumpSuit() != parent.getCurrentLeadSuit()) continue;

                    //if rank is higher than current lead rank, and current high rank,
                    //update selected card to card in hand
                    if(cards.get(i).getRank() > maxLeadRank && cards.get(i).getFace() == parent.getCurrentLeadSuit() && cards.get(i).getRank() > highestRank) {
                        //if card is ace, set to arbitrarily high value
                        if(cards.get(i).getRank() == 1) highestRank = 15;
                        else highestRank = cards.get(i).getRank();
                        selectedCard = cards.get(i);

                        //if max lead is scoring, tries to capture it by placing highest lead available
                        if(maxLeadRankIsScoring) continue;
                        else break;
                    }
                }

                //if selected card is null, look for lowest lead card to place
                if(selectedCard == null) {
                    int minRank = 16;
                    for(int i = 0; i < cards.size(); i++) {
                        if(cards.get(i).getRank() < minRank && cards.get(i).getFace() == parent.getCurrentLeadSuit()) {
                            //update min rank
                            minRank = cards.get(i).getRank();

                            //update selected card
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

        //make the move
        makeMove(selectedCard);

        //set turn completed to true
        turnCompleted = true;

        //start next turn
        startNextTurn();
    }

    public void startTurn(boolean startingPlayer) {
        //this function starts the ai players turn

        //update scoreboard turn prompt
        if(parent.getScoreboard() != null) parent.getScoreboard().setTurnPrompt(playerNumber);

        //set turnstarted to ture
        turnStarted = true;

        //if starting player, updated starting player boolean
        this.startingPlayer = startingPlayer;

        //if hand is empty return
        if(hand.getCards().size() == 0) return;

        //otherwise make best move
        makeBestMove();
    }


    private void makeMove(Card move) {
        //makeMove function
        if(startingPlayer) {
            //if startplayer, set game lead suit
            parent.setCurrentLeadSuit(move.getFace());

            //if trick number is 1st
            if(parent.getTrickNum() == 1) {
                //update trump suit if first trick
                parent.setCurrentTrumpSuit(move.getFace());

                //update scoreboard trump suit
                parent.getScoreboard().setTrumpSuit(move.getFace());
            }
        }

        //add card to current trick
        currentTrick.addCard(move);

        //move card to gamefield
        hand.moveCardTo(gameField,move);

    }

    public void makeBid() {
        //calculate best bid and handle bid
        int bid = calculateBestBid();
        handleBid(bid);
    }




    //AI ALGORITHMS START
    public int calculateBestBid() {
        int calculatedBid = 0;

        //calculate eligible bids
        ArrayList<Integer> currentBids = parent.getCurrentBids();
        ArrayList<Integer> possibleBids = new ArrayList<Integer>();
        possibleBids.add(0);

        //loops through current bids and adds values that werent bidded yet
        //to eligible bid values
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

        //initialize max bid to 0
        int maxBid = 0;

        //loop through each suit finding a possible bid for each suit
        for(int i = 0; i < 4; i++) {
            char suit = 'E';

            //get current suit based off of index
            suit = getSuit(i);

            //possible scoring bools
            boolean possibleGameHigh = false;
            boolean possibleHighTrump = false;
            boolean possibleLowTrump = false;
            boolean possibleJackTrump = false;

            //scoring card count
            int numScoringCards = 0;

            for(int j = 0; j < cards.size(); j++) {
                //loop through hand looking for scoring cards

                //if card is not current suit continue
                if(cards.get(j).getFace() != suit) continue;

                //get card rank
                int rank = cards.get(j).getRank();

                //if card is ace or rank >= 10, increment scoring card count for suit
                if(rank == 1 || rank >= 10) numScoringCards++;

                //if rank is jack, set possibleJackTrump to true
                if(rank == 11) possibleJackTrump = true;

                //if rank is ace or greater than 11, set possible high trump to true
                if(rank == 1 || rank > 11) possibleHighTrump = true;

                //if rank is < 4 and not an ace, set possible low trump to true
                if(rank != 1 && rank < 4) possibleLowTrump = true;


            }

            //if there are 2 scoring cards or more, set possible game high to true
            if(numScoringCards >= 2) possibleGameHigh = true;

            //initialize suit bid to 0
            int suitBid = 0;

            //increment suitbid based off these requirements
            if(possibleGameHigh) suitBid++;
            if(possibleHighTrump && possibleGameHigh) suitBid++;
            if(possibleJackTrump || numScoringCards >= 3) suitBid++;
            if(possibleLowTrump && possibleGameHigh) suitBid++;
            if(suitBid == 4) suitBid++;

            //add bid to optimal suitbids
            optimalSuitBids.add(suitBid);


            //if suitbid is greater than maxbid
            //set maxbid to suitbid
            if(suitBid > maxBid && suitBid > 1) {
                maxBid = suitBid;
            }

        }



        //determine bid amount, and if its worth it based off of current bids
        for(int i = 0; i < possibleBids.size(); i++) {
            if(possibleBids.get(i) <= maxBid) {
                if(possibleBids.get(i) == 0 && maxBid >= 2) continue;
                calculatedBid = possibleBids.get(i);
                break;
            }
        }

        //set optimalsuitbids data member to optimal suit bids calculated
        this.optimalSuitBids = optimalSuitBids;

        //return the calculated bid amount
        return calculatedBid;
    }


    private char getSuit(int i) {
        //get suit according to index (Utility function)
        char suit = 'E';
        switch(i) {
            case 0: suit = 'C'; break;
            case 1: suit = 'D'; break;
            case 2: suit = 'H'; break;
            case 3: suit = 'S'; break;
        }
        return suit;
    }

    //return parent game instance
    public Pitch getParent(){return this.parent;}

}
