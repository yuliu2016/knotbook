package kb.core.view.util;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;

import java.util.Set;

// https://dlsc.com/2017/09/07/javafx-tip-28-pretty-list-view/
// https://stackoverflow.com/questions/16880115/javafx-2-2-how-to-force-a-redraw-update-of-a-listview
@SuppressWarnings("unused")
public class PrettyListView<T> extends ListView<T> {

    private ScrollBar vBar = new ScrollBar();
    private ScrollBar hBar = new ScrollBar();

    private FreshListViewSkin<T> skin;

    public PrettyListView() {
        super();

        skin = new FreshListViewSkin<>(this);
        setSkin(skin);

        skinProperty().addListener(it -> {
            // first bind, then add new scrollbars, otherwise the new bars will be found
            bindScrollBars();
            getChildren().addAll(vBar, hBar);
        });

        getStyleClass().add("pretty-list-view");

        vBar.setManaged(false);
        vBar.setOrientation(Orientation.VERTICAL);
        vBar.getStyleClass().add("pretty-scroll-bar");
        vBar.visibleProperty().bind(vBar.visibleAmountProperty().isNotEqualTo(0));

        hBar.setManaged(false);
        hBar.setOrientation(Orientation.HORIZONTAL);
        hBar.getStyleClass().add("pretty-scroll-bar");
        hBar.visibleProperty().bind(hBar.visibleAmountProperty().isNotEqualTo(0));
    }

    private void bindScrollBars() {
        final Set<Node> nodes = lookupAll("VirtualScrollBar");
        for (Node node : nodes) {
            if (node instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) node;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    bindScrollBars(vBar, bar);
                } else if (bar.getOrientation().equals(Orientation.HORIZONTAL)) {
                    bindScrollBars(hBar, bar);
                }
            }
        }
    }

    private void bindScrollBars(ScrollBar scrollBarA, ScrollBar scrollBarB) {
        scrollBarA.valueProperty().bindBidirectional(scrollBarB.valueProperty());
        scrollBarA.minProperty().bindBidirectional(scrollBarB.minProperty());
        scrollBarA.maxProperty().bindBidirectional(scrollBarB.maxProperty());
        scrollBarA.visibleAmountProperty().bindBidirectional(scrollBarB.visibleAmountProperty());
        scrollBarA.unitIncrementProperty().bindBidirectional(scrollBarB.unitIncrementProperty());
        scrollBarA.blockIncrementProperty().bindBidirectional(scrollBarB.blockIncrementProperty());
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        Insets insets = getInsets();
        double w = getWidth();
        double h = getHeight();
        final double prefWidth = vBar.prefWidth(-1);
        vBar.resizeRelocate(w - prefWidth - insets.getRight(), insets.getTop(), prefWidth, h - insets.getTop() - insets.getBottom());

        final double prefHeight = hBar.prefHeight(-1);
        hBar.resizeRelocate(insets.getLeft(), h - prefHeight - insets.getBottom(), w - insets.getLeft() - insets.getRight(), prefHeight);
    }


    private static class Flow<T> extends VirtualFlow<ListCell<T>> {
        @Override
        public void recreateCells() {
            super.recreateCells();
        }

        @Override
        public void rebuildCells() {
            super.rebuildCells();
        }
    }

    static class FreshListViewSkin<T> extends ListViewSkin<T> {

        private Flow<T> flow;

        @Override
        protected VirtualFlow<ListCell<T>> createVirtualFlow() {
            if (flow == null) {
                flow = new Flow<>();
            }
            return flow;
        }

        FreshListViewSkin(ListView<T> control) {
            super(control);
        }


        void rebuildCells() {
            flow.rebuildCells();
        }

        void recreateCells() {
            flow.recreateCells();
        }
    }

    public void recreateCells() {
        skin.recreateCells();
    }

    public void rebuildCells() {
        skin.rebuildCells();
    }
}