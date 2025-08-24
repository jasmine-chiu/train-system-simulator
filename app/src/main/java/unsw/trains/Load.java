package unsw.trains;

import unsw.utils.LoadType;

public class Load {
    private String loadId;
    private LoadType loadType;
    private int weight;
    private String start;
    private String dest;

    public Load(String start, String dest, String loadId, int weight, LoadType loadType) {
        this.start = start;
        this.dest = dest;
        this.weight = weight;
        this.loadId = loadId;
        this.loadType = loadType;
    }

    public String getStart() {
        return start;
    }

    public String getDest() {
        return dest;
    }

    public String getLoadId() {
        return loadId;
    }

    public int getWeight() {
        return weight;
    }

    public LoadType getLoadType() {
        return loadType;
    }
}
