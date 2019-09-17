package kb.service.api.df;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class DoubleColumn implements DataColumn {


    private String name;
    private double[] values;

    public DoubleColumn(String name, double[] values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public int getSize() {
        return values.length;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getTextValues() {
        String[] texts = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            texts[i] = Double.isNaN(values[i]) ? "---" : String.valueOf(values[i]);
        }
        return texts;
    }
}
