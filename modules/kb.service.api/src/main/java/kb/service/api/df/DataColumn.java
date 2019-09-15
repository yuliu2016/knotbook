package kb.service.api.df;

import org.jetbrains.annotations.NotNull;

public interface DataColumn {

    int getSize();

    @NotNull
    String getName();

    String[] getTextValues();
}
