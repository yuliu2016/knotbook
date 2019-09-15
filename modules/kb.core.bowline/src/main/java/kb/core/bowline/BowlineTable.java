package kb.core.bowline;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.PaintConverter;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class BowlineTable extends Control {

    public BowlineTable() {
        getStyleClass().add("bowline");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BowlineSkin(this);
    }

    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCSSMetaData();
    }

    private StyleableProperty<Paint> lineColorProperty =
            new SimpleStyleableObjectProperty<>(StyleableProperties.kLineColor);

    public static List<CssMetaData<? extends Styleable, ?>> getClassCSSMetaData() {
        return StyleableProperties.kCSSMetaData;
    }

    private static class StyleableProperties {
        private static final CssMetaData<BowlineTable, Paint> kLineColor = new CssMetaData<>(
                "-fx-line-color", PaintConverter.getInstance(), Color.GRAY) {
            @Override
            public boolean isSettable(BowlineTable styleable) {
                return true;
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(BowlineTable styleable) {
                return styleable.lineColorProperty;
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> kCSSMetaData;

        static {
            kCSSMetaData = List.of(kLineColor);
        }
    }
}