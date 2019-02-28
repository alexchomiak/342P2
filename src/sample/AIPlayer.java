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

        Random rand = new Random();


        int bid = 0;

        switch(rand.nextInt(10)) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                bid = 0;
                break;
            case 7:
                bid = 2;
                break;
            case 8:
                bid = 3;
                break;
            case 9:
                bid = 4;
                break;
            case 10:
                bid = 5;
                break;
        }

        handleBid(bid);
    }
}
