package kb.plugin.scoutingapp.api.v6;

import kb.plugin.scoutingapp.api.Board;

import java.util.List;

/**
 * Compatible with V5 Entry
 */
@SuppressWarnings("unused")
public interface V6Entry {
    List<V6DataPoint> getDataPoints();

    String getScout();

    Board getBoard();

    String getMatch();

    String getComments();

    String getTeam();

    int getTimestamp();

    String getEncoded();

    int getUndone();

    int count(int type);

    V6DataPoint lastValue(int type);

    boolean isFocused(int type, int time, int timeWindow);
}
