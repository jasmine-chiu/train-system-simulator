package unsw.trains;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.Position;
import unsw.utils.IdHelper;
import unsw.utils.LoadType;

/**
 *
 *
 */
public class Station {
    private String stationId;
    private String type;
    private Position position;
    private int maxTrains;

    private List<LoadType> canLoad;
    private List<Train> stationTrains;
    private List<Load> stationLoads;

    public Station(String stationId, String type, double x, double y, int maxTrains, List<LoadType> canLoad) {
        this.stationId = stationId;
        this.type = type;
        this.position = new Position(x, y);
        this.maxTrains = maxTrains;
        this.canLoad = canLoad;

        this.stationTrains = new ArrayList<Train>();
        this.stationLoads = new ArrayList<Load>();
    }

    public String getStationId() {
        return stationId;
    }

    public String getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getMaxTrains() {
        return maxTrains;
    }

    public List<LoadType> getCanLoad() {
        return canLoad;
    }

    public List<Train> getStationTrains() {
        return stationTrains;
    }

    public void setStationTrains(List<Train> stationTrains) {
        this.stationTrains = stationTrains;
    }

    public List<Load> getStationLoads() {
        return stationLoads;
    }

    public void setStationLoads(List<Load> stationLoads) {
        this.stationLoads = stationLoads;
    }

    // returns false if train cannot be added (max is hit)
    public boolean checkMaxTrain() {
        if (this.getMaxTrains() == this.getStationTrains().size()) {
            return false;
        }

        return true;
    }

    // returns true if train is added
    public boolean addTrain(Train train) {
        if (!this.checkMaxTrain()) {
            return false;
        }

        List<Train> updatedTrains = this.getStationTrains();
        updatedTrains.add(train);
        this.setStationTrains(updatedTrains);
        return true;
    }

    public void removeTrain(Train train) {
        List<Train> updatedTrains = this.getStationTrains();
        updatedTrains.remove(train);
        this.setStationTrains(updatedTrains);
    }

    public void addLoad(Load load) {
        List<Load> stationLoads = this.getStationLoads();
        IdHelper.sortLoadIds(load, stationLoads);
        this.setStationLoads(stationLoads);

    }

    public void removeLoad(Load deleteLoad) {
        List<Load> stationLoads = this.getStationLoads();
        stationLoads.remove(deleteLoad);
        this.setStationLoads(stationLoads);
    }

    // returns true if creatable at a station
    public boolean loadCheck(LoadType loadType) {
        if (this.getCanLoad().contains(loadType)) {
            return true;
        }

        return false;
    }

    // all station loads perish
    public void perishing() {
        List<Load> stationLoads = this.getStationLoads();
        List<Load> removable = new ArrayList<>();
        for (Load toPerish : stationLoads) {
            if (toPerish.getLoadType().equals(LoadType.PerishableCargo)) {
                int newMinsTilPerish = ((PerishableCargo) toPerish).getMinsTillPerish() - 1;
                if (newMinsTilPerish == 0) {
                    removable.add(toPerish);
                } else {
                    ((PerishableCargo) toPerish).setMinsTillPerish(newMinsTilPerish);
                }
            }
        }

        for (Load toDel : removable) {
            stationLoads.remove(toDel);
        }

        this.setStationLoads(stationLoads);
    }
}
