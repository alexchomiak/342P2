package sample;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static sample.PitchConstants.cardImageHeight;
import static sample.PitchConstants.cardImageWidth;
import static sample.PitchConstants.hoverAnimationOffset;

public class CardView {
    private String imgSrc;

    /*
    private static final int cardImageWidth = Pit;
    private static final int cardImageHeight = 160;
    private static final int hoverAnimationOffset = 30;
*/

    private int width = 0;
    private int height = 0 ;
    private int hoverOffset = 0 ;


    double scaleFactor = 1.0;

    private boolean mouseOver = false;

    Image cardImage;
    Rectangle cardRect;
    Card card;

    Deck parent;

    boolean cardIsSelectable = false;


    //overload constructor for just 3 parameters passed
    CardView(Deck parent, int rank, char face) {
        this(parent, rank, face, false);
    }


    CardView(Deck parent, int rank, char face, boolean selectable) {
        //set object data members
        this.cardIsSelectable = selectable;

        card = new Card(rank,face);
        setScale(1.0);


        this.parent = parent;





    }


    //handle click on card
    private void handleClick() {
        //if card is not currently selectable, ignore click event
        if (!cardIsSelectable) return;

        //if mouse is not currently over defined area, ignore click event
        if (!mouseOver) return;


        parent.setSelectedCard(this.card);
        parent.removeCard(this.card);
    }

    private boolean overCard;

    private void handleHover(MouseEvent e) {
        //if card is not currently selectable, ignore hover event
        if (!cardIsSelectable) return;

        //if hover y value is greater than the hoverAnimationOffset portion of the card,
        //reset styles and return
        if (overCard && e.getY() > this.y + (height - (hoverOffset + 10))) {
            cardRect.setStrokeWidth(0);
            cardRect.setTranslateY(this.y);
            this.mouseOver = false;
            return;
        }


        if (overCard) {
            cardRect.setStroke(Color.GREENYELLOW);
            cardRect.setStrokeWidth(2);

            if (this.x > -1 && this.y > -1) {
                cardRect.setTranslateY(this.y - hoverOffset);
            }

            this.mouseOver = true;

        } else {
            cardRect.setStrokeWidth(0);

            if (this.x > -1 && this.y > -1) {
                cardRect.setTranslateY(this.y);
            }

            this.mouseOver = false;

        }
    }


    //returns imageView object created
    Rectangle View() {
        return this.cardRect;
    }


    void changeCard(int rank, char face) {
        this.card = new Card(rank, face);


        //if rank is greater than 1 and less than 11, there is a card corresponding to that rank
        if (rank > 1 && rank < 11) {
            //set image source accordingly
            imgSrc = "/Assets/PlayingCards/" + Integer.toString(rank) + face + ".png";
        } else {
            //otherwise, it is an Ace, Jack, Queen or Heart
            //and set image source accordingly
            switch (rank) {
                case 1:
                    imgSrc = "/Assets/PlayingCards/" + "A" + face + ".png";
                    break;
                case 11:
                    imgSrc = "/Assets/PlayingCards/" + "J" + face + ".png";
                    break;
                case 12:
                    imgSrc = "/Assets/PlayingCards/" + "Q" + face + ".png";
                    break;
                case 13:
                    imgSrc = "/Assets/PlayingCards/" + "K" + face + ".png";
                    break;
            }
        }


        try {
            //intialize cardimage
            cardImage = new Image(imgSrc);

            //initialize card as rectangle
            cardRect = new Rectangle(width, height);

            //set rectangle background to card image
            cardRect.setFill(new ImagePattern(cardImage));




            //add listeners
            cardRect.setOnMouseClicked(e -> handleClick());
            cardRect.setOnMouseEntered(e -> this.overCard = true);
            cardRect.setOnMouseExited(e -> {
                this.overCard = false;
                //makes sure on exit, handle is called and resets card position
                this.handleHover(e);
            });
            cardRect.setOnMouseMoved(e -> handleHover(e));



            //set card stroketype
            cardRect.setStrokeType(StrokeType.INSIDE);


        } catch (Exception e) {
            //if the image is not loaded, an exception will be thrown, and the image file path will
            //be thrown to aid debugging
            System.out.println("Image with path " + imgSrc + " was not found!");

        }
    }



    private int x;
    private int y;

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    void setX(int x) {
        this.x = x;
        this.cardRect.setTranslateX(x);
    }

    void setY(int y) {
        this.y = y;
        this.cardRect.setTranslateY(y);
    }

    void rotate(double deg) {
        this.cardRect.setRotate(deg);
    }

    void setCardIsSelectable(boolean s) {
        this.cardIsSelectable = s;
    }

    void setScale(double scale) {
        scaleFactor = scale;
        hoverOffset = (int)Math.floor((double)hoverAnimationOffset * scale);
        width = (int)Math.floor((double)cardImageWidth * scale);
        height = (int)Math.floor((double)cardImageHeight * scale);
        //rerender card
        changeCard(this.card.getRank(), this.card.getFace());
    }

    double getScale(){return this.scaleFactor;}

    void highlight(Color color){
        cardRect.setStrokeWidth(2);
        cardRect.setStroke(color);

    }


}
