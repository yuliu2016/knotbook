package knotbook.snap;

import javafx.scene.Cursor;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

//@SuppressWarnings("ALL")
class BorderlessFXML {

    static final AnchorPane pane = new AnchorPane();


    static final Pane topLeftPane = new Pane();

    static {
        topLeftPane.setOpacity(0.0);
        topLeftPane.setPrefHeight(5.0);
        topLeftPane.setPrefWidth(10.0);
        AnchorPane.setLeftAnchor(topLeftPane, 0.0);
        AnchorPane.setTopAnchor(topLeftPane, 0.0);
        topLeftPane.setCursor(Cursor.NW_RESIZE);
    }


    static final Pane topRightPane = new Pane();

    static {
        topRightPane.setOpacity(0.0);
        topRightPane.setPrefHeight(5.0);
        topRightPane.setPrefWidth(10.0);
        AnchorPane.setRightAnchor(topRightPane, 0.0);
        AnchorPane.setTopAnchor(topRightPane, 0.0);
        topRightPane.setCursor(Cursor.NE_RESIZE);
    }


    static final Pane bottomRightPane = new Pane();

    static {
        bottomRightPane.setOpacity(0.0);
        bottomRightPane.setPrefHeight(5.0);
        bottomRightPane.setPrefWidth(10.0);
        AnchorPane.setRightAnchor(bottomRightPane, 0.0);
        AnchorPane.setBottomAnchor(bottomRightPane, 0.0);
        bottomRightPane.setCursor(Cursor.SE_RESIZE);
    }


    static final Pane bottomLeftPane = new Pane();

    static {
        bottomLeftPane.setOpacity(0.0);
        bottomLeftPane.setPrefHeight(5.0);
        bottomLeftPane.setPrefWidth(10.0);
        AnchorPane.setLeftAnchor(bottomLeftPane, 0.0);
        AnchorPane.setBottomAnchor(bottomLeftPane, 0.0);
        bottomLeftPane.setCursor(Cursor.SW_RESIZE);
    }


    static final Pane leftPane = new Pane();

    static {
        leftPane.setOpacity(0.0);
        leftPane.setPrefWidth(5.0);
        AnchorPane.setLeftAnchor(leftPane, 0.0);
        AnchorPane.setTopAnchor(leftPane, 5.0);
        AnchorPane.setBottomAnchor(leftPane, 5.0);
        leftPane.setCursor(Cursor.W_RESIZE);
    }


    static final Pane rightPane = new Pane();

    static {
        rightPane.setOpacity(0.0);
        rightPane.setPrefWidth(5.0);
        AnchorPane.setRightAnchor(rightPane, 0.0);
        AnchorPane.setTopAnchor(rightPane, 5.0);
        AnchorPane.setBottomAnchor(rightPane, 5.0);
        rightPane.setCursor(Cursor.E_RESIZE);
    }


    static final Pane topPane = new Pane();

    static {
        topPane.setOpacity(0.0);
        topPane.setPrefHeight(5.0);
        AnchorPane.setTopAnchor(topPane, 0.0);
        AnchorPane.setLeftAnchor(topPane, 10.0);
        AnchorPane.setRightAnchor(topPane, 10.0);
        topPane.setCursor(Cursor.N_RESIZE);
    }


    static final Pane bottomPane = new Pane();

    static {
        bottomPane.setOpacity(0.0);
        bottomPane.setPrefHeight(5.0);
        AnchorPane.setBottomAnchor(bottomPane, 0.0);
        AnchorPane.setLeftAnchor(bottomPane, 10.0);
        AnchorPane.setRightAnchor(bottomPane, 10.0);
        bottomPane.setCursor(Cursor.S_RESIZE);
    }


    static {
        pane.getChildren().addAll(
                topLeftPane, topRightPane, bottomRightPane, bottomLeftPane,
                leftPane, rightPane, topPane, bottomPane
        );
    }

}
