package kb.service.api.df;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class IntColumn implements DataColumn {

    private String name;
    private int[] values;

    public IntColumn(@NotNull String name, int[] values) {
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
            texts[i] = values[i] == Integer.MIN_VALUE ? "---" : String.valueOf(values[i]);
        }
        return texts;
    }
}
