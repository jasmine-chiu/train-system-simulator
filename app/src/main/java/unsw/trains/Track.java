package unsw.trains;

import unsw.utils.TrackType;

public class Track {
    private String trackId;
    private TrackType type;
    private String fromStationId;
    private String toStationId;
    private int durability;

    public Track(String trackId, String fromStationId, String toStationId) {
        this.trackId = trackId;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;

        this.type = TrackType.NORMAL;
        this.durability = 10;
    }

    public String getTrackId() {
        return trackId;
    }

    public TrackType getType() {
        return type;
    }

    public String getFromStationId() {
        return fromStationId;
    }

    public String getToStationId() {
        return toStationId;
    }

    public int getDurability() {
        return durability;
    }

}
