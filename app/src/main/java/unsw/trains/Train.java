package unsw.trains;

import java.util.List;
import java.util.ArrayList;

import unsw.utils.LoadType;
import unsw.utils.RouteType;
import unsw.utils.Direction;
import unsw.utils.Position;

public class Train {
    private String trainId;
    private String type;
    private Position position;
    private String visitedStationId;

    private List<String> route; // list of trackId
    private RouteType routeType;
    private List<Load> trainLoads;

    private double speed;
    private int maxSpeed;
    private int loadWeight;
    private int cargoWeight;

    private int maxLoad;

    private List<LoadType> canLoad;

    private String location;
    private String nextStation;
    private Direction direction;

    // !!! SHORTEN THE PARAMS INTO A CLASS TO REDUCE CODE SMELL
    public Train(String trainId, String type, String stationId, Position position, List<String> route,
            RouteType routeType, int speed, int maxSpeed, int loadWeight, int cargoWeight, int maxLoad,
            List<LoadType> canLoad) {
        this.trainId = trainId;
        this.type = type;
        this.position = position;
        this.visitedStationId = stationId; // spawn point
        this.route = route;
        this.routeType = routeType;

        List<Load> trainLoads = new ArrayList<>();
        this.trainLoads = trainLoads;

        this.speed = speed;
        this.loadWeight = loadWeight;
        this.cargoWeight = cargoWeight;

        this.maxLoad = maxLoad;

        this.canLoad = canLoad;

        this.location = stationId; // can become trackId

        this.direction = Direction.FORWARDS;
        // sets next station and direction

        this.findNextStation();

        this.maxSpeed = 0;

    }

    public String getTrainId() {
        return trainId;
    }

    public String getType() {
        return type;
    }

    public String getVisitedStationId() {
        return visitedStationId;
    }

    public void setVisitedStationId(String visitedStationId) {
        this.visitedStationId = visitedStationId;
    }

    public List<String> getRoute() {
        return route;
    }

    public List<Load> getTrainLoads() {
        return trainLoads;
    }

    public void setTrainLoads(List<Load> trainLoads) {
        this.trainLoads = trainLoads;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getLoadWeight() {
        return loadWeight;
    }

    public void setLoadWeight(int loadWeight) {
        this.loadWeight = loadWeight;
    }

    public int getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(int cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public int getMaxLoad() {
        return maxLoad;
    }

    public List<LoadType> getCanLoad() {
        return canLoad;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getNextStation() {
        return nextStation;
    }

    public void setNextStation(String nextStation) {
        this.nextStation = nextStation;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void moveTrain(Position nextPos) {
        Position newPosition = this.getPosition().calculateNewPosition(nextPos, this.getSpeed());
        this.setPosition(newPosition);
    }

    // finds next station along a route,
    // depending on LINEAR, CYCLICAL and direction
    public void findNextStation() {
        List<String> route = this.getRoute();
        String stationId = this.getVisitedStationId();
        int currIdx = route.indexOf(stationId);

        if (currIdx == route.size() - 1 && this.getDirection().equals(Direction.FORWARDS)) {
            if (this.getRouteType().equals(RouteType.CYCLICAL) && this instanceof BulletTrain) {
                this.setNextStation(route.get(0));
                return;
            } else {
                this.setDirection(Direction.BACKWARDS);
                this.setNextStation(route.get(currIdx - 1));
                return;
            }
        }

        if (currIdx == 0 && this.getDirection().equals(Direction.BACKWARDS)) {
            this.setDirection(Direction.FORWARDS);
            this.setNextStation(route.get(1));
            return;
        }

        if (this.getDirection().equals(Direction.FORWARDS)) {
            this.setNextStation(route.get(currIdx + 1));
            return;
        }

        if (this.getDirection().equals(Direction.BACKWARDS)) {
            this.setNextStation(route.get(currIdx - 1));
            return;
        }

    }

    // returns false if load cannot be onboarded
    public boolean boardAllowance(Load load) {
        List<LoadType> loadableType = this.getCanLoad();
        if (loadableType.contains(load.getLoadType()) && this.getMaxLoad() >= load.getWeight() + this.getLoadWeight()) {
            return true;
        }

        return false;
    }

    // if route has dest, return true
    public boolean routeHasDest(Load load) {
        List<String> route = this.getRoute();
        for (String stationId : route) {
            if (stationId.equals(load.getDest())) {
                return true;
            }
        }

        return false;
    }

    // changes load weight, adds load into trainLoads
    public boolean addLoad(Load addedLoad) {
        List<Load> loads = this.getTrainLoads();
        this.setLoadWeight(this.getLoadWeight() + addedLoad.getWeight());

        if (addedLoad instanceof Cargo || addedLoad instanceof PerishableCargo) {
            this.setCargoWeight(this.getCargoWeight() + addedLoad.getWeight());
            this.adjustSpeed();
        }

        loads.add(addedLoad);
        this.setTrainLoads(loads);

        return true;
    }

    public void removeLoad(Load deleteLoad) {
        List<Load> loads = this.getTrainLoads();
        this.setLoadWeight(this.getLoadWeight() - deleteLoad.getWeight());

        if (deleteLoad.getLoadType().equals(LoadType.Cargo)
                || deleteLoad.getLoadType().equals(LoadType.PerishableCargo)) {
            this.setCargoWeight(this.getCargoWeight() - deleteLoad.getWeight());
            this.adjustSpeed();
        }

        loads.remove(deleteLoad);
        this.setTrainLoads(loads);
    }

    // how cargoWeight affects speed
    public void adjustSpeed() {
        double decreasedSpeedPercent = this.getCargoWeight() * 0.01;
        double speedPercent = 100 - decreasedSpeedPercent;
        double toSlow = speedPercent * 0.01;
        double newSpeed = this.getMaxSpeed() * toSlow;
        this.setSpeed(newSpeed);
    }

    // all train loads perish
    public void perishing() {
        List<Load> loads = this.getTrainLoads();
        List<Load> removable = new ArrayList<>();
        for (Load load : loads) {
            if (load instanceof PerishableCargo) {
                int newMinsTilPerish = ((PerishableCargo) load).getMinsTillPerish() - 1;
                if (newMinsTilPerish == 0) {
                    removable.add(load);
                } else {
                    ((PerishableCargo) load).setMinsTillPerish(newMinsTilPerish);
                }
            }
        }

        for (Load toDel : removable) {
            loads.remove(toDel);
        }

        this.setTrainLoads(loads);
    }

}
