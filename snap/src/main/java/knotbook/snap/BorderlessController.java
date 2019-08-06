package knotbook.snap;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller implements window controls: maximise, minimise, move, and Windows Aero Snap.
 *
 * @author Nicolas Senet-Larson
 * @version 1.0
 */
@SuppressWarnings({"WeakerAccess", "DuplicatedCode", "SpellCheckingInspection"})
public class BorderlessController {
    private Stage primaryStage;
    protected Delta prevSize;
    protected Delta prevPos;
    protected boolean maximised;
    private boolean snapped;

    private Pane leftPane = BorderlessFXML.leftPane;
    private Pane rightPane = BorderlessFXML.rightPane;
    private Pane topPane = BorderlessFXML.topPane;
    private Pane bottomPane = BorderlessFXML.bottomPane;
    private Pane topLeftPane = BorderlessFXML.topLeftPane;
    private Pane topRightPane = BorderlessFXML.topRightPane;
    private Pane bottomLeftPane = BorderlessFXML.bottomLeftPane;
    private Pane bottomRightPane = BorderlessFXML.bottomRightPane;

    /**
     * The constructor.
     */
    public BorderlessController() {
        prevSize = new Delta();
        prevPos = new Delta();
        maximised = false;
        snapped = false;

        setResizeControl(leftPane, "left");
        setResizeControl(rightPane, "right");
        setResizeControl(topPane, "top");
        setResizeControl(bottomPane, "bottom");
        setResizeControl(topLeftPane, "top-left");
        setResizeControl(topRightPane, "top-right");
        setResizeControl(bottomLeftPane, "bottom-left");
        setResizeControl(bottomRightPane, "bottom-right");
    }


    /**
     * Reference to main application.
     *
     * @param primaryStage the main application stage.
     */
    protected void setMainApp(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Maximise on/off the application.
     */
    protected void maximise() {
        Rectangle2D screen;
        if (Screen.getScreensForRectangle(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth() / 2,
                primaryStage.getHeight() / 2).size() == 0) {
            screen = Screen.getScreensForRectangle(primaryStage.getX(), primaryStage.getY(),
                    primaryStage.getWidth(), primaryStage.getHeight()).get(0).getVisualBounds();
        } else {
            screen = Screen.getScreensForRectangle(primaryStage.getX(), primaryStage.getY(),
                    primaryStage.getWidth() / 2, primaryStage.getHeight() / 2).get(0).getVisualBounds();
        }

        if (maximised) {
            primaryStage.setWidth(prevSize.x);
            primaryStage.setHeight(prevSize.y);
            primaryStage.setX(prevPos.x);
            primaryStage.setY(prevPos.y);
            isMaximised(false);
        } else {
            // Record position and size, and maximise.
            if (!snapped) {
                prevSize.x = primaryStage.getWidth();
                prevSize.y = primaryStage.getHeight();
                prevPos.x = primaryStage.getX();
                prevPos.y = primaryStage.getY();
            } else if (!screen.contains(prevPos.x, prevPos.y)) {
                if (prevSize.x > screen.getWidth())
                    prevSize.x = screen.getWidth() - 20;

                if (prevSize.y > screen.getHeight())
                    prevSize.y = screen.getHeight() - 20;

                prevPos.x = screen.getMinX() + (screen.getWidth() - prevSize.x) / 2;
                prevPos.y = screen.getMinY() + (screen.getHeight() - prevSize.y) / 2;
            }

            primaryStage.setX(screen.getMinX());
            primaryStage.setY(screen.getMinY());
            primaryStage.setWidth(screen.getWidth());
            primaryStage.setHeight(screen.getHeight());

            isMaximised(true);
        }
    }

    /**
     * Minimise the application.
     */
    protected void minimise() {
        primaryStage.setIconified(true);
    }

    /**
     * Set a node that can be pressed and dragged to move the application around.
     *
     * @param node the node.
     */
    protected void setMoveControl(final Node node) {
        final Delta delta = new Delta();
        final Delta eventSource = new Delta();

        // Record drag deltas on press.
        node.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
                delta.x = mouseEvent.getX();
                delta.y = mouseEvent.getY();

                if (maximised || snapped) {
                    delta.x = (prevSize.x * (mouseEvent.getX() / primaryStage.getWidth()));
                    delta.y = (prevSize.y * (mouseEvent.getY() / primaryStage.getHeight()));
                } else {
                    prevSize.x = primaryStage.getWidth();
                    prevSize.y = primaryStage.getHeight();
                    prevPos.x = primaryStage.getX();
                    prevPos.y = primaryStage.getY();
                }

                eventSource.x = mouseEvent.getScreenX();
                eventSource.y = node.prefHeight(primaryStage.getHeight());
            }
        });

        // Dragging moves the application around.
        node.setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
                // Move x axis.
                primaryStage.setX(mouseEvent.getScreenX() - delta.x);

                if (snapped) {
                    // Aero Snap off.
                    Rectangle2D screen = Screen.getScreensForRectangle(mouseEvent.getScreenX(),
                            mouseEvent.getScreenY(), 1, 1).get(0).getVisualBounds();

                    primaryStage.setHeight(screen.getHeight());

                    if (mouseEvent.getScreenY() > eventSource.y) {
                        primaryStage.setWidth(prevSize.x);
                        primaryStage.setHeight(prevSize.y);
                        snapped = false;
                    }
                } else {
                    // Move y axis.
                    primaryStage.setY(mouseEvent.getScreenY() - delta.y);
                }

                // Aero Snap off.
                if (maximised) {
                    primaryStage.setWidth(prevSize.x);
                    primaryStage.setHeight(prevSize.y);
                    isMaximised(false);
                }
            }
        });

        // Maximise on double click.
        node.setOnMouseClicked(mouseEvent -> {
            if ((mouseEvent.getButton().equals(MouseButton.PRIMARY)) && (mouseEvent.getClickCount() == 2)) {
                maximise();
            }
        });

        // Aero Snap on release.
        node.setOnMouseReleased(mouseEvent -> {
            if ((mouseEvent.getButton().equals(MouseButton.PRIMARY)) && (mouseEvent.getScreenX() != eventSource.x)) {
                Rectangle2D screen = Screen.getScreensForRectangle(mouseEvent.getScreenX(),
                        mouseEvent.getScreenY(), 1, 1).get(0).getVisualBounds();

                // Aero Snap Left.
                if (mouseEvent.getScreenX() == screen.getMinX()) {
                    primaryStage.setY(screen.getMinY());
                    primaryStage.setHeight(screen.getHeight());

                    primaryStage.setX(screen.getMinX());
                    primaryStage.setWidth(Math.max(screen.getWidth() / 2, primaryStage.getMinWidth()));

                    snapped = true;
                }

                // Aero Snap Right.
                else if (mouseEvent.getScreenX() == screen.getMaxX() - 1) {
                    primaryStage.setY(screen.getMinY());
                    primaryStage.setHeight(screen.getHeight());

                    primaryStage.setWidth(Math.max(screen.getWidth() / 2, primaryStage.getMinWidth()));
                    primaryStage.setX(screen.getMaxX() - primaryStage.getWidth());

                    snapped = true;
                }

                // Aero Snap Top.
                else if (mouseEvent.getScreenY() == screen.getMinY()) {
                    if (!screen.contains(prevPos.x, prevPos.y)) {
                        if (prevSize.x > screen.getWidth())
                            prevSize.x = screen.getWidth() - 20;

                        if (prevSize.y > screen.getHeight())
                            prevSize.y = screen.getHeight() - 20;

                        prevPos.x = screen.getMinX() + (screen.getWidth() - prevSize.x) / 2;
                        prevPos.y = screen.getMinY() + (screen.getHeight() - prevSize.y) / 2;
                    }

                    primaryStage.setX(screen.getMinX());
                    primaryStage.setY(screen.getMinY());
                    primaryStage.setWidth(screen.getWidth());
                    primaryStage.setHeight(screen.getHeight());
                    isMaximised(true);
                }
            }
        });
    }

    /**
     * Set pane to resize application when pressed and dragged.
     *
     * @param pane      the pane the action is set to.
     * @param direction the resize direction. Diagonal: 'top' or 'bottom' + 'right' or 'left'.
     */
    private void setResizeControl(Pane pane, final String direction) {
        pane.setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
                double width = primaryStage.getWidth();
                double height = primaryStage.getHeight();

                // Horizontal resize.
                if (direction.endsWith("left")) {
                    if ((width > primaryStage.getMinWidth()) || (mouseEvent.getX() < 0)) {
                        primaryStage.setWidth(width - mouseEvent.getScreenX() + primaryStage.getX());
                        primaryStage.setX(mouseEvent.getScreenX());
                    }
                } else if ((direction.endsWith("right"))
                        && ((width > primaryStage.getMinWidth()) || (mouseEvent.getX() > 0))) {
                    primaryStage.setWidth(width + mouseEvent.getX());
                }

                // Vertical resize.
                if (direction.startsWith("top")) {
                    if (snapped) {
                        primaryStage.setHeight(prevSize.y);
                        snapped = false;
                    } else if ((height > primaryStage.getMinHeight()) || (mouseEvent.getY() < 0)) {
                        primaryStage.setHeight(height - mouseEvent.getScreenY() + primaryStage.getY());
                        primaryStage.setY(mouseEvent.getScreenY());
                    }
                } else if (direction.startsWith("bottom")) {
                    if (snapped) {
                        primaryStage.setY(prevPos.y);
                        snapped = false;
                    } else if ((height > primaryStage.getMinHeight()) || (mouseEvent.getY() > 0)) {
                        primaryStage.setHeight(height + mouseEvent.getY());
                    }
                }
            }
        });

        // Record application height and y position.
        pane.setOnMousePressed(mouseEvent -> {
            if ((mouseEvent.isPrimaryButtonDown()) && (!snapped)) {
                prevSize.y = primaryStage.getHeight();
                prevPos.y = primaryStage.getY();
            }
        });

        // Aero Snap Resize.
        pane.setOnMouseReleased(mouseEvent -> {
            if ((mouseEvent.getButton().equals(MouseButton.PRIMARY)) && (!snapped)) {
                Rectangle2D screen = Screen.getScreensForRectangle(mouseEvent.getScreenX(),
                        mouseEvent.getScreenY(), 1, 1).get(0).getVisualBounds();

                if ((primaryStage.getY() <= screen.getMinY()) && (direction.startsWith("top"))) {
                    primaryStage.setHeight(screen.getHeight());
                    primaryStage.setY(screen.getMinY());
                    snapped = true;
                }

                if ((mouseEvent.getScreenY() >= screen.getMaxY()) && (direction.startsWith("bottom"))) {
                    primaryStage.setHeight(screen.getHeight());
                    primaryStage.setY(screen.getMinY());
                    snapped = true;
                }
            }
        });

        // Aero Snap resize on double click.
        pane.setOnMouseClicked(mouseEvent -> {
            if ((mouseEvent.getButton().equals(MouseButton.PRIMARY)) && (mouseEvent.getClickCount() == 2)
                    && ((direction.equals("top")) || (direction.equals("bottom")))) {
                Rectangle2D screen = Screen.getScreensForRectangle(primaryStage.getX(), primaryStage.getY(),
                        primaryStage.getWidth() / 2, primaryStage.getHeight() / 2).get(0).getVisualBounds();

                if (snapped) {
                    primaryStage.setHeight(prevSize.y);
                    primaryStage.setY(prevPos.y);
                    snapped = false;
                } else {
                    prevSize.y = primaryStage.getHeight();
                    prevPos.y = primaryStage.getY();
                    primaryStage.setHeight(screen.getHeight());
                    primaryStage.setY(screen.getMinY());
                    snapped = true;
                }
            }
        });
    }

    protected void isMaximised(Boolean maximised) {
        this.maximised = maximised;
        setResizable(!maximised);
    }

    protected void setResizable(Boolean bool) {
        leftPane.setDisable(!bool);
        rightPane.setDisable(!bool);
        topPane.setDisable(!bool);
        bottomPane.setDisable(!bool);
        topLeftPane.setDisable(!bool);
        topRightPane.setDisable(!bool);
        bottomLeftPane.setDisable(!bool);
        bottomRightPane.setDisable(!bool);
    }
}
