package game;

import javafx.scene.layout.FlowPane;

import java.util.ArrayList;

public class Deck {
    //data members
    private ArrayList<Card> cards;
    private boolean selectable = false;
    private boolean renderable = false;
    private Card selectedCard;
    private FlowPane displayPane;

    //Deck constructor
    public Deck(FlowPane displayPane) {
        //set display pane to given flowpane
        this.displayPane = displayPane;

        //initialize selectable to false
        this.selectable = false;

        //set renderable to whether a displaypane was described
        this.renderable = displayPane != null;

        //initalize cards to a new arraylist of cards
        cards = new ArrayList<Card>();
        selectedCard = null;
    }


    public void addCard(Card card) {
        //this function addeds a newCard to the deck
        Card newCard;

        //if display pane is provided, load image source in new card, otherwise initialize card otherwise
        if(displayPane == null) newCard = new Card(this,card.getRank(),card.getFace(),selectable,false);
        else newCard = new Card(this,card.getRank(),card.getFace(),selectable,true);

        //add card to cards arraylist
        cards.add(newCard);

        //if renderable, update displaypane and reRender the displaypane
        if(renderable) {
            displayPane.getChildren().add(newCard.View());
            reRender();
        }

    }

    //remove card from deck
    public boolean removeCard(Card card) {
        //attempt to remove card from deck, return boolean of success
       if(moveCardTo(null, card)) {
           //if card is removed, and deck is renderable
           //rerender pane and return
           if(renderable) reRender();
           return true;
       }
       else {
           return false;
       }
    }


    public FlowPane getDisplayPane(){return  this.displayPane;}

    public boolean moveCardTo(Deck dest, Card card) {
        //move card to destination deck
        for(int i = 0; i < cards.size(); i++) {
            //loop through group until card is found
            Card c = cards.get(i);
            if(c.getRank() == card.getRank() && c.getFace() == card.getFace()) {
                if(dest != null) {
                    //if dest != null, move card to destination deck
                    dest.addCard(cards.get(i));
                }

                //if the card was renderable, remove it from parent display pane
                if(renderable) {
                    displayPane.getChildren().remove(cards.get(i).View());
                }

                //remove card from current deck
                cards.remove(i);

                //if renderable, rerender deck
                if(renderable) reRender();
                return true;
            }
        }

        return false;

    }




    //set selected card in deck
    public void setSelectedCard(Card selection) {
        //if selection is null, set selection to null
        if(selection == null) {
            this.selectedCard = null;
            return;
        }

        //else, loop through cards and find the selected card, and set selected card
        //data member to that card
        for(int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);

            if(c.getRank() == selection.getRank() && c.getFace() == selection.getFace()) {
                selectedCard = selection;
            }
        }
    }


    public void clearDeck() {
        //clears deck
        if(renderable){
            //if renderable, remove each cards view from display pane
            for(int i = 0; i < cards.size(); i++) {
                displayPane.getChildren().remove(cards.get(i).View());
            }
        }

        //clear out cards array
        cards.clear();
    }

    public void reRender() {
        //this function rerenders the card pane every time it is changed
        //and provides each card with the correct translations to provide
        //the fan effect that the user sees in their hand

        //set offsets of cards
        int angularOffset = 15;
        int yOffset = 50;

        for(int i = 0; i < cards.size(); i++) {

            //set initial Y translation of card to 0
            cards.get(i).setY(0);

            //set size variable
            int size = cards.size() - 1;

            //offset
            int offset;

            //if size is odd, render middle two objects to have half the angular offset
            if(size % 2 == 1) {
                if(i <= size/2) {
                    if(i == size/2) {
                        //set middle left object translations
                        cards.get(i).rotate(-angularOffset/2);
                        cards.get(i).setY(0);
                    }
                    else {
                        //set leftmost objects translationg
                        offset = (size/2) - i;
                        cards.get(i).rotate(-1*((offset * angularOffset) + angularOffset/2));
                        cards.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));
                    }
                }
                else {
                    if(i == size/2 + 1) {
                        //set middle right object translation
                        cards.get(i).rotate(angularOffset/2);
                        cards.get(i).setY(0);
                    }
                    else {
                        //set rightmost object translations
                        offset = i - ((size/2) + 1);
                        cards.get(i).rotate((offset * angularOffset) + angularOffset/2);
                        cards.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));

                    }
                }
            }
            else {
                //if size is even, set middle object to have no rotation
                if(i == size / 2) {
                    //set middle object translations
                    cards.get(i).rotate(0);
                    cards.get(i).setY(angularOffset / 2);
                }
                else {
                    if( i < size/2) {
                        //set leftmost object translations
                        offset = size/2 - i;
                        cards.get(i).rotate(offset * -angularOffset);
                        cards.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));
                    }
                    else{
                        //set rightmost object translations
                        offset = i - size/2;
                        cards.get(i).rotate(offset * angularOffset);
                        cards.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));
                    }
                }
            }
        }
    }

    //get selected card in deck
    public Card getSelectedCard() {return this.selectedCard;}
    public ArrayList<Card> getCards() {return this.cards;}

    //set if cardviews in deck are selectable by mouse or not
    public void setSelectable(boolean s) {
        this.selectable = s;
        //loop through and set each card to selectable boolean
        for(int i = 0; i < cards.size(); i++) {
            cards.get(i).setCardIsSelectable(s);
        }
    }
    //set if renderable for other purposes
    public void setRenderable(boolean r) {
        this.renderable = r;
    }


}
