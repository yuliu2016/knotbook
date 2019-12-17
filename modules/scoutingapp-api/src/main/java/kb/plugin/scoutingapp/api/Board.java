package kb.plugin.scoutingapp.api;

import static kb.plugin.scoutingapp.api.Alliance.Blue;
import static kb.plugin.scoutingapp.api.Alliance.Red;

@SuppressWarnings("unused")
public enum Board {
    R1(Red), R2(Red), R3(Red),
    B1(Blue), B2(Blue), B3(Blue),
    RX(Red), BX(Blue);

    private Alliance alliance;

    public Alliance getAlliance() {
        return alliance;
    }

    Board(Alliance alliance) {
        this.alliance = alliance;
    }
}
