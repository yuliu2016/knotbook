package kb.service.api.df;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@SuppressWarnings("unused")
public class StringColumn implements DataColumn {

    private String name;
    private String[] values;

    public StringColumn(String name, String[] values) {
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
        return Arrays.copyOf(values, values.length);
    }
}
