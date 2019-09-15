package kb.service.api.df;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class IntColumn implements DataColumn {

    private String name;
    private int[] values;
    private boolean[] na;

    public IntColumn(@NotNull String name, int[] values) {
        this.name = name;
        this.values = values;
        this.na = new boolean[values.length];
    }

    public IntColumn(@NotNull String name, int[] values, boolean[] na) {
        this.name = name;
        this.values = values;
        this.na = na;
        assert na.length == values.length;
    }

    @Override
    public int getSize() {
        return 0;
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
            texts[i] = na[i] ? "---" : String.valueOf(values[i]);
        }
        return texts;
    }
}
