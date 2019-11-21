package kb.service.api.ui;

import javafx.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchBar {
    private OptionBar optionBar = new OptionBar();
    private List<Integer> filteredIndices = null;
    private List<OptionItem> items = null;
    private Function<Integer, Boolean> handler = null;

    {
        optionBar.textProperty().addListener((o, ov, nv) -> {
            String q = nv.trim();
            if (!q.equals(ov == null ? null : ov.trim())) {
                if (q.isEmpty()) {
                    if (optionBar.isShowing()) {
                        setAll();
                    }
                } else updateSearch(q);
            }
        });
        optionBar.setOnEnterPressed(e -> {
            if (handler != null) {
                int i = filteredIndices.get(optionBar.getSelectedItem());
                if (handler.apply(i)) {
                    optionBar.setShowing(false);
                }
            }
        });
    }

    private void setAll() {

    }

    private void updateSearch(String q) {
        List<Pair<Integer, boolean[]>> res = IntStream.range(0, items.size())
                .mapToObj(i -> new Pair<>(i, OptionItem.parse(items.get(i).getName(), q)))
                .filter(x -> x.getValue() != null)
                .sorted((a, b) -> OptionItem.compare(a.getValue(), b.getValue()))
                .collect(Collectors.toList());
        filteredIndices = res.stream().map(Pair::getKey).collect(Collectors.toList());
        optionBar.getItems().setAll(res.stream().map(o -> items.get(o.getKey())).collect(Collectors.toList()));
    }

    public void setItems(List<OptionItem> items) {
        optionBar.getItems().setAll(items);
    }

    public void setHint(String hint) {
        optionBar.setHint(hint);
    }

    public OptionBar toOptionBar() {
        return optionBar;
    }
}
