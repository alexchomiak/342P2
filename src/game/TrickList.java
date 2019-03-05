package game;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

import static game.PitchConstants.*;
public class TrickList {
    private FlowPane displayPane;
    private Label Prompt;
    private Label PromptDescription;
    private ArrayList<HBox> tricks;
    private final double cardScaleFactor = .45;
    private final int placeHolderSize = (int)Math.floor(cardScaleFactor * (double)cardImageHeight);

    public TrickList() {
        //initialize VBox for trick
        VBox PromptContainer = new VBox();

        //intialize header prompt label and prompt description label
        Prompt = new Label("Tricks played");
        PromptDescription = new Label("Highlighted card \nindicates winner of trick");

        //set text fill color
        Prompt.setTextFill(textColor);
        PromptDescription.setTextFill(textColor);

        //set styles
        Prompt.setStyle("-fx-font: 18px arial");
        PromptDescription.setStyle("-fx-font: 12px arial");

        //initialize flowpane for trick list
        displayPane = new FlowPane(Orientation.VERTICAL);
        displayPane.setPadding(new Insets(2,2,2,6));

        //set style of flowpane
        displayPane.setStyle(rightSideBarStyle);

        //set spacing
        displayPane.setVgap(10);

        //initialize tricks list
        tricks = new ArrayList<HBox>();

        //set alignment of prompt container
        PromptContainer.setAlignment(Pos.CENTER);
        PromptContainer.getChildren().addAll(Prompt,PromptDescription);

        //add promptcontainer to displayPane
        displayPane.getChildren().add(PromptContainer);

        //add placeholder rectangle for spacing
        Rectangle placeholder = new Rectangle((int)(cardScaleFactor * cardImageWidth * 4) + 20 ,5);
        placeholder.setOpacity(0.0);
        displayPane.getChildren().add(placeholder);

        //set displaypane alignment
        displayPane.setAlignment(Pos.CENTER);

        //initialize trick list with white space hboxes (for spacing purposes)
        initializeWhiteSpace();
    }

    //returns tricklist flowpane
    public FlowPane View() {
        return this.displayPane;
    }

    //clear out trick list (adds 6 empty rows)
    public void clear() {
        for(int i = 0; i < 6; i++) addEmptyRow();;
    }

    private void addEmptyRow() {
        //initialize hbox for row
        HBox row = new HBox();

        //initialize placeholder for row
        Rectangle r = new Rectangle(1,placeHolderSize);

        //set opacity to 0
        r.setOpacity(0);

        //add placeholder to row
        row.getChildren().add(r);

        //remove last box in displaypane
        displayPane.getChildren().remove(tricks.get(tricks.size() - 1));

        //remove last trick from tricks array
        tricks.remove(tricks.size() - 1);

        //add new row to tricks
        tricks.add(0,row);

        //add the new row to displaypane
        displayPane.getChildren().add(2,row);
    }

    private void initializeWhiteSpace() {
        //add five rows of whitespace to intialize
        for(int i = 0; i < 6; i++) {
            //intialize placeholder rectangle
            Rectangle r = new Rectangle(1,placeHolderSize);

            //set opacity to 0
            r.setOpacity(0);

            //add placeholder hbox
            HBox placeholder = new HBox();

            //set spacing
            placeholder.setSpacing(10);

            //add row to placeholder
            placeholder.getChildren().add(r);

            //add placeholder to displaypane
            displayPane.getChildren().add(placeholder);

            //add placeholder to tricks
            tricks.add(placeholder);
        }

    }

    public void addToList(ArrayList<Card> trick, int winningIndex, int playerWon) {
        //intialize row to be added
        HBox row = new HBox();

        //set row spacing
        row.setSpacing(2);

        //add each card to the row hbox
        trick.forEach(t -> {
            Card card = t;
            t.setScale(cardScaleFactor);
            row.getChildren().add(card.View());
        }
        );


        //find color of player that won the trick
        Color color;
        switch(playerWon){
            case 1: color = player1; break;
            case 2: color = player2; break;
            case 3: color = player3; break;
            case 4: color = player4; break;
            default: color = Color.BLACK; break;
        }

        //highlight the winning card in the trick
        trick.get(winningIndex).highlight(color);

        //remove last trick from displaypane
        displayPane.getChildren().remove(tricks.get(tricks.size() - 1));

        //remove last trick from tricks
        tricks.remove(tricks.size() - 1);

        //add new row to beginning of tricks
        tricks.add(0,row);

        //set row alignment to center
        row.setAlignment(Pos.CENTER);

        //add row to displaypane
        displayPane.getChildren().add(2,row);

    }

}
