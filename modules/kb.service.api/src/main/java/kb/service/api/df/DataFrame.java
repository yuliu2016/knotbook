package kb.service.api.df;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused"})
public class DataFrame {
    private List<DataColumn> columns;

    public DataFrame(DataColumn... columns) {
        this(Arrays.asList(columns));
    }

    public DataFrame(List<DataColumn> columns) {
        this.columns = columns;
    }

    public List<DataColumn> getColumns() {
        return columns;
    }
}
