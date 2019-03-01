package sample;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

import static sample.PitchConstants.*;
public class TrickList {
    FlowPane displayPane;

    Label Prompt;
    Label PromptDescription;
    ArrayList<HBox> tricks;



    private final double cardScaleFactor = .45;
    private final int placeHolderSize = (int)Math.floor(cardScaleFactor * (double)cardImageHeight);

    TrickList() {
        VBox PromptContainer = new VBox();
        Prompt = new Label("Tricks played");
        PromptDescription = new Label("Highlighted card \nindicates winner of trick");
        Prompt.setTextFill(textColor);
        PromptDescription.setTextFill(textColor);
        Prompt.setStyle("-fx-font: 18px arial");
        PromptDescription.setStyle("-fx-font: 12px arial");

        displayPane = new FlowPane(Orientation.VERTICAL);
        displayPane.setPadding(new Insets(2,2,2,6));
        displayPane.setStyle(rightSideBarStyle);

        displayPane.setVgap(10);
        tricks = new ArrayList<HBox>();

        PromptContainer.setAlignment(Pos.CENTER);
        PromptContainer.getChildren().addAll(Prompt,PromptDescription);
        displayPane.getChildren().add(PromptContainer);

        Rectangle placeholder = new Rectangle((int)(cardScaleFactor * cardImageWidth * 4) + 20 ,5);
        placeholder.setOpacity(0.0);
        displayPane.getChildren().add(placeholder);

        displayPane.setAlignment(Pos.CENTER);

        //displayPane.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,BorderWidths.DEFAULT)));
        initializeWhiteSpace();
    }




    public FlowPane View() {
        return this.displayPane;
    }



    public void clear() {
        for(int i = 0; i < 6; i++) addEmptyRow();;
    }
    private void addEmptyRow() {
        HBox row = new HBox();
        Rectangle r = new Rectangle(1,placeHolderSize);
        r.setOpacity(0);
        row.getChildren().add(r);
        displayPane.getChildren().remove(tricks.get(tricks.size() - 1));
        tricks.remove(tricks.size() - 1);
        tricks.add(0,row);
        displayPane.getChildren().add(2,row);
    }

    void initializeWhiteSpace() {
        //add five rows of whitespace to intialize
        for(int i = 0; i < 6; i++) {
            Rectangle r = new Rectangle(1,placeHolderSize);
            r.setOpacity(0);
            HBox placeholder = new HBox();
            placeholder.setSpacing(10);
            placeholder.getChildren().add(r);
            displayPane.getChildren().add(placeholder);
            tricks.add(placeholder);
        }

    }



    void addToList(ArrayList<CardView> trick, int winningIndex, int playerWon) {


        HBox row = new HBox();
        row.setSpacing(2);
        trick.forEach(t -> {
            CardView card = t;
            t.setScale(cardScaleFactor);
            row.getChildren().add(card.View());
        }
        );

        Color color;

        switch(playerWon){
            case 1: color = player1; break;
            case 2: color = player2; break;
            case 3: color = player3; break;
            case 4: color = player4; break;
            default: color = Color.BLACK; break;
        }
        trick.get(winningIndex).highlight(color);



        displayPane.getChildren().remove(tricks.get(tricks.size() - 1));
        tricks.remove(tricks.size() - 1);
        tricks.add(0,row);
        row.setAlignment(Pos.CENTER);
        displayPane.getChildren().add(2,row);

    }

}
