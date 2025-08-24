package unsw.response.models;

import java.util.Objects;

import unsw.utils.TrackType;

/**
 * Represents a generic response for track information.
 *
 * @note You can't store this class as an attribute in another, and should just instantiate it
 *       when needed. In other words, there should never exist a "has-a" relationship between
 *       this class and any other class. Doing so will make you lose marks for domain modelling.
 *
 *       You shouldn't modify or move this file. Refer to the assignment specification.
 *
 * @author Daniel Khuu
 */
public final class TrackInfoResponse {
    /**
     * The unique ID of the track.
     */
    private final String trackId;
    /**
     * The ID of the station the track was created on (starting station).
     */
    private final String fromStationId;
    /**
     * The ID of the station the track was created to (ending station).
     */
    private final String toStationId;
    /**
     * An enum representing the type and state of the track, there are three such
     * variations:
     * NORMAL - unbreakable/'normal' track
     * UNBROKEN - breakable but not broken (still has durability left/ is usable)
     * BROKEN - breakable and broken (no durability left/is unusable)
     **/
    private final TrackType type;
    /**
     * The durability left on the track. This value is 10 for a NORMAL track.
     */
    private final int durability;

    public TrackInfoResponse(String trackId, String fromStationId, String toStationId, TrackType type, int durability) {
        this.trackId = trackId;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.type = type;
        this.durability = durability;
    }

    public final String getTrackId() {
        return trackId;
    }

    public final String getFromStationId() {
        return fromStationId;
    }

    public final String getToStationId() {
        return toStationId;
    }

    public final TrackType getType() {
        return type;
    }

    public final int getDurability() {
        return durability;
    }

    /**
     * fromStationId and toStationId may be in any order
     *
     * @param o
     * @return whether this equals o
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (o instanceof TrackInfoResponse other) {
            return Objects.equals(trackId, other.trackId)
                    && (Objects.equals(fromStationId, other.fromStationId)
                            || Objects.equals(fromStationId, other.toStationId))
                    && (Objects.equals(toStationId, other.fromStationId)
                            || Objects.equals(toStationId, other.toStationId))
                    && Objects.equals(type, other.type) && Objects.equals(durability, other.durability);
        }
        return false;
    }

    @Override
    public final String toString() {
        return "TrackInfoResponse [trackId=" + trackId + ", fromStationId=" + fromStationId + ", toStationId="
                + toStationId + ", type=" + type.toString() + ", durability=" + durability + "]";
    }
}
