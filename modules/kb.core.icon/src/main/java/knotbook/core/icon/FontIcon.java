/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2015-2019 Andres Almiray
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package knotbook.core.icon;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.*;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.IkonHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FontIcon extends Text {
    protected StyleableIntegerProperty iconSize;
    protected StyleableObjectProperty<Paint> iconColor;
    private StyleableObjectProperty<Ikon> iconCode;

    public static FontIcon of(Ikon ikon) {
        return of(ikon, 16, Color.BLACK);
    }

    public static FontIcon of(Ikon ikon, int iconSize) {
        return of(ikon, iconSize, Color.BLACK);
    }

    public static FontIcon of(Ikon ikon, Color iconColor) {
        return of(ikon, 16, iconColor);
    }

    public static FontIcon of(Ikon iconCode, int iconSize, Color iconColor) {
        FontIcon icon = new FontIcon();
        icon.setIconCode(iconCode);
        icon.setIconSize(iconSize);
        icon.setIconColor(iconColor);
        return icon;
    }

    public FontIcon() {
        getStyleClass().setAll("ikonli-font-icon");
        setIconSize(16);
        setIconColor(Color.BLACK);

        fontProperty().addListener((v, o, n) -> {
            int size = (int) n.getSize();
            if (size != getIconSize()) {
                setIconSize(size);
            }
        });

        fillProperty().addListener((v, o, n) -> {
            Paint fill = getIconColor();
            if (!Objects.equals(fill, n)) {
                setIconColor(n);
            }
        });

        iconCodeProperty().addListener((v, o, n) -> {
            if (n != null) {
                IkonHandler ikonHandler = IkonResolver.getInstance().resolveIkonHandler(n.getDescription());
                setStyle(normalizeStyle(getStyle(), "-fx-font-family", "'" + ikonHandler.getFontFamily() + "'"));
                setText(String.valueOf(n.getCode()));
            }
        });
    }

    public FontIcon(String iconCode) {
        this();
        setIconLiteral(iconCode);
    }

    public FontIcon(Ikon iconCode) {
        this();
        setIconCode(iconCode);
    }

    public String toString() {
        Ikon iconCode = getIconCode();
        return (iconCode != null ? iconCode.getDescription() : "<undef>") + ":" + getIconSize() + ":" + getIconColor();
    }

    public IntegerProperty iconSizeProperty() {
        if (iconSize == null) {
            iconSize = new StyleableIntegerProperty(16) {
                @Override
                public CssMetaData<FontIcon, Number> getCssMetaData() {
                    return StyleableProperties.ICON_SIZE;
                }

                @Override
                public Object getBean() {
                    return FontIcon.this;
                }

                @Override
                public String getName() {
                    return "iconSize";
                }
            };
            iconSize.addListener((v, o, n) -> {
                Font font = FontIcon.this.getFont();
                FontIcon.this.setFont(Font.font(font.getFamily(), n.doubleValue()));
                FontIcon.this.setStyle(normalizeStyle(getStyle(), "-fx-font-size", n.intValue() + "px"));
            });
        }
        return iconSize;
    }

    public ObjectProperty<Paint> iconColorProperty() {
        if (iconColor == null) {
            iconColor = new StyleableObjectProperty<>(Color.BLACK) {
                @Override
                public CssMetaData<FontIcon, Paint> getCssMetaData() {
                    return StyleableProperties.ICON_COLOR;
                }

                @Override
                public Object getBean() {
                    return FontIcon.this;
                }

                @Override
                public String getName() {
                    return "iconColor";
                }
            };
            iconColor.addListener((v, o, n) -> FontIcon.this.setFill(n));
        }
        return iconColor;
    }

    public ObjectProperty<Ikon> iconCodeProperty() {
        if (iconCode == null) {
            iconCode = new StyleableObjectProperty<>() {
                @Override
                public CssMetaData<FontIcon, Ikon> getCssMetaData() {
                    return StyleableProperties.ICON_CODE;
                }

                @Override
                public Object getBean() {
                    return FontIcon.this;
                }

                @Override
                public String getName() {
                    return "iconCode";
                }
            };

            iconCode.addListener((v, o, n) -> {
                if (!iconCode.isBound()) {
                    FontIcon.this.setIconCode(n);
                }
            });
        }
        return iconCode;
    }

    public void setIconSize(int size) {
        if (size <= 0) {
            throw new IllegalStateException("Argument 'size' must be greater than zero.");
        }
        iconSizeProperty().set(size);
    }

    public int getIconSize() {
        return iconSizeProperty().get();
    }

    public void setIconColor(Paint paint) {
        iconColorProperty().set(requireNonNull(paint, "Argument 'paint' must not be null"));
    }

    public Paint getIconColor() {
        return iconColorProperty().get();
    }

    public Ikon getIconCode() {
        return iconCodeProperty().get();
    }

    public void setIconCode(Ikon iconCode) {
        iconCodeProperty().set(requireNonNull(iconCode, "Argument 'code' must not be null"));
    }

    private String normalizeStyle(String style, String key, String value) {
        int start = style.indexOf(key);
        if (start != -1) {
            int end = style.indexOf(";", start);
            end = end >= start ? end : style.length() - 1;
            style = style.substring(0, start) + style.substring(end + 1);
        }
        return style + key + ": " + value + ";";
    }

    public void setIconLiteral(String iconCode) {
        String[] parts = iconCode.split(":");
        setIconCode(IkonResolver.getInstance().resolveIkonHandler(parts[0]).resolve(parts[0]));
        resolveSize(iconCode, parts);
        resolvePaint(iconCode, parts);
    }

    public String getIconLiteral() {
        Ikon ikon = iconCodeProperty().get();
        return ikon != null ? ikon.getDescription() : null;
    }

    @SuppressWarnings("unchecked")
    private static class StyleableProperties {
        private static final CssMetaData<FontIcon, Number> ICON_SIZE =
                new CssMetaData<>("-fx-icon-size",
                        SizeConverter.getInstance(), 16.0) {

                    @Override
                    public boolean isSettable(FontIcon icon) {
                        return true;
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(FontIcon icon) {
                        return (StyleableProperty<Number>) icon.iconSizeProperty();
                    }
                };

        private static final CssMetaData<FontIcon, Paint> ICON_COLOR =
                new CssMetaData<>("-fx-icon-color",
                        PaintConverter.getInstance(), Color.BLACK) {

                    @Override
                    public boolean isSettable(FontIcon node) {
                        return true;
                    }

                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(FontIcon icon) {
                        return (StyleableProperty<Paint>) icon.iconColorProperty();
                    }
                };

        private static final CssMetaData<FontIcon, Ikon> ICON_CODE =
                new CssMetaData<>("-fx-icon-code",
                        FontIconConverter.getInstance(), null) {

                    @Override
                    public boolean isSettable(FontIcon node) {
                        return true;
                    }

                    @Override
                    public StyleableProperty<Ikon> getStyleableProperty(FontIcon icon) {
                        return (StyleableProperty<Ikon>) icon.iconCodeProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<>(Text.getClassCssMetaData());
            styleables.add(ICON_SIZE);
            styleables.add(ICON_COLOR);
            styleables.add(ICON_CODE);
            STYLEABLES = unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return FontIcon.getClassCssMetaData();
    }

    private void resolveSize(String iconCode, String[] parts) {
        if (parts.length > 1) {
            try {
                setIconSize(Integer.parseInt(parts[1]));
            } catch (NumberFormatException e) {
                throw invalidDescription(iconCode, e);
            }
        }
    }

    private void resolvePaint(String iconCode, String[] parts) {
        if (parts.length > 2) {
            Paint paint = resolvePaintValue(iconCode, parts[2]);
            if (paint != null) {
                setIconColor(paint);
            }
        }
    }

    private static Paint resolvePaintValue(String iconCode, String value) {
        try {
            return Color.valueOf(value);
        } catch (IllegalArgumentException e1) {
            try {
                return LinearGradient.valueOf(value);
            } catch (IllegalArgumentException e2) {
                try {
                    return RadialGradient.valueOf(value);
                } catch (IllegalArgumentException e3) {
                    throw invalidDescription(iconCode, e3);
                }
            }
        }
    }

    public static IllegalArgumentException invalidDescription(String description, Exception e) {
        throw new IllegalArgumentException("Description " + description + " is not a valid icon description", e);
    }
}