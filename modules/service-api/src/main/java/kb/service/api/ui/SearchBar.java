package kb.service.api.ui;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchBar {
    private OptionBar optionBar = new OptionBar();
    private List<Integer> filteredIndices = null;
    private List<OptionItem> referenceItems = null;
    private IntConsumer handler = null;

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
                handler.accept(i);
            }
        });
    }

    private void setAll() {
        for (OptionItem item : referenceItems) {
            item.setHighlight(null);
        }
        optionBar.getItems().setAll(referenceItems);
        filteredIndices = IntStream.range(0, referenceItems.size()).boxed().collect(Collectors.toList());
    }

    private void updateSearch(String q) {
        for (OptionItem item : referenceItems) {
            item.setHighlight(OptionItem.parse(item.getName(), q));
        }
        filteredIndices = IntStream.range(0, referenceItems.size())
                .filter(x -> referenceItems.get(x).getHighlight() != null)
                .boxed()
                .sorted((a, b) -> OptionItem.compare(
                        referenceItems.get(b).getHighlight(),
                        referenceItems.get(a).getHighlight()
                ))
                .collect(Collectors.toList());
        optionBar.getItems().setAll(filteredIndices.stream()
                .map(o -> referenceItems.get(o))
                .collect(Collectors.toList())
        );
    }

    public void setHandler(IntConsumer handler) {
        this.handler = handler;
    }

    public void setItems(List<OptionItem> items) {
        referenceItems = items;
        setAll();
    }

    public void setHint(String hint) {
        optionBar.setHint(hint);
    }

    public OptionBar toOptionBar() {
        return optionBar;
    }
}
