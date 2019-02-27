package sample;

import java.util.Random;

public class AIPlayer extends Player{
    AIPlayer(Deck gameField, Deck currentTrick, Pitch parent) {
        super(gameField, currentTrick,parent);

    }


    public void makeBestMove() {

        Random rand = new Random();
        int index = rand.nextInt(hand.getCards().size());
        makeMove(hand.getCards().get(index));


        turnCompleted = true;



        if(getNextPlayer().getCompleted() == false) {
            getNextPlayer().startTurn(false);
        }

    }

    void startTurn(boolean startingPlayer) {
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

        handleBid(-1);
    }
}
