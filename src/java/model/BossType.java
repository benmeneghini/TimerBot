package model;

public enum BossType {
    ENDGAME("ENDGAME"),
    MIDRAID("MIDRAID"),
    RING("RING"),
    EDL("EDL"),
    DL("DL"),
    FROZEN("FROZEN"),
    METEORIC("METEORIC"),
    WARDEN("WARDEN");

    private String string;

    private BossType(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}