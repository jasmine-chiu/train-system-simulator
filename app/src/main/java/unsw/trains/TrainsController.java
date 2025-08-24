package unsw.trains;

import java.util.ArrayList;
import java.util.List;

import unsw.exceptions.InvalidRouteException;
import unsw.response.models.*;

import unsw.utils.*;

/**
 * The controller for the Trains system.
 *
 * The method signatures here are provided for you. Do NOT change the method signatures.
 */
public class TrainsController {
    // Add any fields here if necessary
    private List<Station> stations = new ArrayList<>();
    private List<Track> tracks = new ArrayList<>();
    private List<Train> trains = new ArrayList<>();
    private List<Load> loads = new ArrayList<>();

    public void createStation(String stationId, String type, double x, double y) {
        switch (type) {
        case "PassengerStation":
            stations.add(new PassengerStation(stationId, type, x, y));
            break;
        case "CargoStation":
            stations.add(new CargoStation(stationId, type, x, y));
            break;
        case "CentralStation":
            stations.add(new CentralStation(stationId, type, x, y));
            break;
        case "DepotStation":
            stations.add(new DepotStation(stationId, type, x, y));
            break;
        default:
            break;
        }
    }

    public void createTrack(String trackId, String fromStationId, String toStationId) {
        tracks.add(new Track(trackId, fromStationId, toStationId));
    }

    public void createTrain(String trainId, String type, String stationId, List<String> route)
            throws InvalidRouteException {

        Station getPos = IdHelper.findStationWithId(stationId, stations);
        Position trainPos = getPos.getPosition();

        RouteType routeType = IdHelper.findRouteType(route, tracks);

        if (!getPos.checkMaxTrain()) {
            return;
        }

        Train trainToAdd;
        switch (type) {
        case "PassengerTrain":
            trainToAdd = new PassengerTrain(trainId, type, stationId, trainPos, route, routeType);
            break;
        case "CargoTrain":
            trainToAdd = new CargoTrain(trainId, type, stationId, trainPos, route, routeType);
            break;
        case "BulletTrain":
            trainToAdd = new BulletTrain(trainId, type, stationId, trainPos, route, routeType);
            break;
        default:
            trainToAdd = null;
            break;
        }

        boolean inserted = IdHelper.sortTrainIds(trainToAdd, trains);

        if (!inserted) {
            System.out.println("Failed to add train");
            return;
        }
    }

    public List<String> listStationIds() {
        List<String> stationIds = new ArrayList<>();

        for (Station station : stations) {
            stationIds.add(station.getStationId());
        }

        return stationIds;
    }

    public List<String> listTrackIds() {
        List<String> trackIds = new ArrayList<>();

        for (Track track : tracks) {
            trackIds.add(track.getTrackId());
        }

        return trackIds;
    }

    public List<String> listTrainIds() {
        List<String> trainIds = new ArrayList<>();

        for (Train train : trains) {
            trainIds.add(train.getTrainId());
        }

        return trainIds;
    }

    public TrainInfoResponse getTrainInfo(String trainId) {
        Train t = IdHelper.findTrainWithId(trainId, trains);
        List<LoadInfoResponse> newLoadList = SimulateHelper.createLoadInfo(t.getTrainLoads());

        return new TrainInfoResponse(t.getTrainId(), t.getLocation(), t.getType().toString(), t.getPosition(),
                newLoadList);
    }

    public StationInfoResponse getStationInfo(String stationId) {
        Station s = IdHelper.findStationWithId(stationId, stations);
        List<LoadInfoResponse> newLoadList = SimulateHelper.createLoadInfo(s.getStationLoads());
        List<TrainInfoResponse> newTrainList = SimulateHelper.createTrainInfo(s.getStationTrains());

        return new StationInfoResponse(s.getStationId(), s.getType().toString(), s.getPosition(), newLoadList,
                newTrainList);
    }

    public TrackInfoResponse getTrackInfo(String trackId) {
        Track t = IdHelper.findTrackWithId(trackId, tracks);

        return new TrackInfoResponse(t.getTrackId(), t.getFromStationId(), t.getToStationId(), t.getType(),
                t.getDurability());
    }

    public void simulate() {
        // perish @ stations
        SimulateHelper.perishStation(stations);

        for (Train train : trains) {
            // perish for each train
            train.perishing();

            Position currPos = train.getPosition();
            Station nextStation = IdHelper.findStationWithId(train.getNextStation(), stations);
            Position nextStationPos = nextStation.getPosition();

            // checks if train location is track, or station
            boolean onTrack = SimulateHelper.onTrack(train, tracks);
            if (!onTrack) {
                // train is stopped @ station
                SimulateHelper.stoppedAtStation(train, stations, tracks);
            } else {
                boolean inboundCheck = SimulateHelper.inboundDirection(train, nextStationPos, stations);
                if (currPos.isInBound(nextStationPos, train.getSpeed()) && inboundCheck) {
                    // train is inbound
                    if (nextStation.addTrain(train)) {
                        SimulateHelper.setUpStop(train, stations, tracks);
                        continue;
                    } else {
                        continue;
                    }
                }
            }

            Station newNext = IdHelper.findStationWithId(train.getNextStation(), stations);
            train.moveTrain(newNext.getPosition());
        }
    }

    /**
     * Simulate for the specified number of minutes. You should NOT modify
     * this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public void createPassenger(String startStationId, String destStationId, String passengerId) {
        Station toAdd = IdHelper.findStationWithId(startStationId, stations);
        Station dest = IdHelper.findStationWithId(destStationId, stations);

        if (toAdd.loadCheck(LoadType.Passenger) && dest.loadCheck(LoadType.Passenger)) {
            Passenger loadToAdd = new Passenger(startStationId, destStationId, passengerId);
            toAdd.addLoad(loadToAdd);
            IdHelper.sortLoadIds(loadToAdd, loads);
        }
    }

    public void createCargo(String startStationId, String destStationId, String cargoId, int weight) {
        Station toAdd = IdHelper.findStationWithId(startStationId, stations);
        Station dest = IdHelper.findStationWithId(destStationId, stations);

        if (toAdd.loadCheck(LoadType.Cargo) && dest.loadCheck(LoadType.Cargo)) {
            Cargo loadToAdd = new Cargo(startStationId, destStationId, cargoId, weight);
            toAdd.addLoad(loadToAdd);
            IdHelper.sortLoadIds(loadToAdd, loads);
        }
    }

    public void createPerishableCargo(String startStationId, String destStationId, String cargoId, int weight,
            int minsTillPerish) {
        Station toAdd = IdHelper.findStationWithId(startStationId, stations);
        Station dest = IdHelper.findStationWithId(destStationId, stations);

        if (toAdd.loadCheck(LoadType.PerishableCargo) && dest.loadCheck(LoadType.PerishableCargo)) {
            PerishableCargo loadToAdd = new PerishableCargo(startStationId, destStationId, cargoId, weight,
                    minsTillPerish);
            toAdd.addLoad(loadToAdd);
            IdHelper.sortLoadIds(loadToAdd, loads);
        }
    }

    public void createTrack(String trackId, String fromStationId, String toStationId, boolean isBreakable) {
        // Todo: Task ci
    }

    public void createPassenger(String startStationId, String destStationId, String passengerId, boolean isMechanic) {
        // Todo: Task cii
    }
}
