package unsw.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import unsw.response.models.LoadInfoResponse;
import unsw.response.models.TrainInfoResponse;
import unsw.trains.*;

public class SimulateHelper {
    // for stopped trains,
    // offloads, then onboards, then sets new location to next track
    // and removes train from station
    public static void stoppedAtStation(Train train, List<Station> stations, List<Track> tracks) {
        Station currStation = IdHelper.findStationWithId(train.getVisitedStationId(), stations);
        Station nextStation = IdHelper.findStationWithId(train.getNextStation(), stations);
        Station stoppedAt = IdHelper.findStationWithId(train.getLocation(), stations);

        offload(currStation, train);
        onboard(currStation, train, stations);

        Track location = IdHelper.findTrackWithStationId(train.getVisitedStationId(), train.getNextStation(), tracks);
        train.setLocation(location.getTrackId());

        if (!train.getPosition().equals(nextStation.getPosition())) {
            stoppedAt.removeTrain(train);
        }
    }

    // returns true if train is currently on a train track, not a station
    public static boolean onTrack(Train train, List<Track> tracks) {
        Track currTrack = IdHelper.findTrackWithId(train.getLocation(), tracks);
        if (currTrack != null) {
            return true;
        }

        return false;
    }

    // for inbound trains, sets up position change to be stopped at next station
    public static void setUpStop(Train train, List<Station> stations, List<Track> tracks) {
        Station currStation = IdHelper.findStationWithId(train.getNextStation(), stations);
        Position currStationPos = currStation.getPosition();

        train.setLocation(currStation.getStationId());
        train.setPosition(currStationPos);
        train.setVisitedStationId(currStation.getStationId());

        train.findNextStation();
    }

    // returns false if not inbound
    public static boolean inboundDirection(Train train, Position nextStationPos, List<Station> stations) {
        String nextStationId = train.getNextStation();

        if (train.getVisitedStationId().equals(nextStationId)) {
            return false;
        }

        return true;
    }

    // returns true if the train CAN pick up perishable
    public static boolean perishCheck(Train train, PerishableCargo cargo, List<Station> stations) {
        List<String> cargoRoute = getCargoRoute(train, cargo.getStart(), cargo.getDest());
        double totalDist = getRouteDist(cargoRoute, stations);
        double speed = train.getSpeed();

        double predicted = totalDist / speed;
        if (cargo.getMinsTillPerish() > predicted) {
            return true;
        }

        return false;
    }

    // onboarding loads
    public static void onboard(Station station, Train train, List<Station> stations) {
        List<Load> stationLoad = station.getStationLoads();
        List<Load> removable = new ArrayList<>();
        for (Load load : stationLoad) {
            if (train.boardAllowance(load) && train.routeHasDest(load)) {
                if (load instanceof PerishableCargo && !perishCheck(train, (PerishableCargo) load, stations)) {
                    continue;
                }
                if (train.addLoad(load)) {
                    removable.add(load);
                }
            }
        }

        for (Load offloadStation : removable) {
            stationLoad.remove(offloadStation);
        }

        station.setStationLoads(stationLoad);
    }

    // offloading loads
    public static void offload(Station station, Train train) {
        List<Load> loads = train.getTrainLoads();
        List<Load> removable = new ArrayList<>();
        for (Load load : loads) {
            if (load.getDest().equals(station.getStationId())) {
                removable.add(load);
            }
        }

        for (Load toDel : removable) {
            train.removeLoad(toDel);
        }
    }

    // creates list of loadInfoResponses
    public static List<LoadInfoResponse> createLoadInfo(List<Load> loads) {
        List<LoadInfoResponse> newList = new ArrayList<>();
        for (Load load : loads) {
            LoadInfoResponse res = new LoadInfoResponse(load.getLoadId(), load.getLoadType().toString());
            newList.add(res);
        }

        return newList;
    }

    // creates list of trainInfoResponses
    public static List<TrainInfoResponse> createTrainInfo(List<Train> trains) {
        List<TrainInfoResponse> newList = new ArrayList<>();
        for (Train train : trains) {
            TrainInfoResponse res = new TrainInfoResponse(train.getTrainId(), train.getLocation(),
                    train.getType().toString(), train.getPosition());
            newList.add(res);
        }

        return newList;
    }

    public static void perishStation(List<Station> stations) {
        for (Station station : stations) {
            station.perishing();
        }
    }

    // depending on the direction of the train and the type of route,
    // determines the route from start and dest for perishable
    // returns string of stations visited in order that pc passes
    public static List<String> getCargoRoute(Train train, String startId, String destId) {
        List<String> route = train.getRoute();
        int startIdx = route.indexOf(startId);
        int endIdx = route.indexOf(destId);

        List<String> cargoRoute = new ArrayList<>();

        // adjacent stations
        if ((endIdx == startIdx + 1) || (startIdx == endIdx + 1)) {
            cargoRoute.add(route.get(startIdx));
            cargoRoute.add(route.get(endIdx));

        } else if (train.getType().equals("BulletTrain") && train.getRouteType().equals(RouteType.CYCLICAL)) {
            // CYCLICAL routes (only words for BulletTrain)
            cargoRoute = findBulletRoute(startIdx, endIdx, route);

        } else if (train.getDirection().equals(Direction.FORWARDS) && startIdx < endIdx) {
            // LINEAR routes
            // fwd linear
            cargoRoute = cutStartEnd(startIdx, endIdx, route);

        } else if (train.getDirection().equals(Direction.BACKWARDS) && startIdx > endIdx) {
            // bwd linear
            cargoRoute = reverseCutStartEnd(startIdx, endIdx, route);

        } else if (train.getDirection().equals(Direction.BACKWARDS) && startIdx < endIdx) {
            // bwd => bwd to start, fwd to end
            cargoRoute = startBackwards(startIdx, route);
            List<String> toEndFwd = cutStartEnd(startIdx, endIdx, route);

            for (String station : toEndFwd) {
                cargoRoute.add(station);
            }

        } else if (train.getDirection().equals(Direction.FORWARDS) && startIdx > endIdx) {
            // fwd => fwd to end, bwd to start
            cargoRoute = startForwards(startIdx, route);
            List<String> toEndBwd = reverseCutStartEnd(startIdx, endIdx, route);

            for (String station : toEndBwd) {
                cargoRoute.add(station);
            }
        }

        return cargoRoute;
    }

    public static List<String> cutStartEnd(int startIdx, int endIdx, List<String> route) {
        List<String> newRoute = new ArrayList<>();

        for (int i = startIdx; i < endIdx + 1; i++) {
            newRoute.add(route.get(i));
        }

        return newRoute;
    }

    // where startIdx > endIdx
    public static List<String> reverseCutStartEnd(int startIdx, int endIdx, List<String> route) {
        List<String> newRoute = cutStartEnd(endIdx, startIdx, route);
        Collections.reverse(newRoute);

        return newRoute;
    }

    // for an array, travels left then right (-ve then +ve)
    // goes from startIdx -> station of lowestIdx (0) -> back to startIdx
    public static List<String> startBackwards(int startIdx, List<String> route) {
        List<String> newRoute = new ArrayList<>();

        if (startIdx == 0) {
            return newRoute;
        }

        for (int i = startIdx; i > 0; i--) {
            newRoute.add(route.get(i));
        }

        for (int j = 0; j < startIdx; j++) {
            newRoute.add(route.get(j));
        }

        return newRoute;
    }

    // for an array, travels right then left (+ve then -ve)
    // goes from startIdx -> station of largestIdx -> back to startIdx
    public static List<String> startForwards(int startIdx, List<String> route) {
        List<String> newRoute = new ArrayList<>();

        if (startIdx == route.size() - 1) {
            return newRoute;
        }

        for (int i = startIdx; i < route.size() - 1; i++) {
            newRoute.add(route.get(i));
        }

        for (int j = route.size() - 1; j > startIdx; j--) {
            newRoute.add(route.get(j));
        }

        return newRoute;
    }

    // considers cyclical route
    public static List<String> findBulletRoute(int startIdx, int endIdx, List<String> route) {
        List<String> bulletRoute = new ArrayList<>();

        if (startIdx < endIdx) {
            // linear
            bulletRoute = cutStartEnd(startIdx, endIdx, route);
        } else {
            // cyclical
            if ((startIdx == 0 && endIdx == route.size() - 1) || (startIdx == route.size() - 1 && endIdx == 0)) {
                bulletRoute.add(route.get(endIdx));
                bulletRoute.add(route.get(startIdx));
            } else {
                // end idx -> end; 0 -> startIdx
                if (startIdx < route.size() - 1) {
                    for (int i = startIdx; i < route.size(); i++) {
                        bulletRoute.add(route.get(i));
                    }
                }

                if (endIdx != 0) {
                    for (int i = 0; i < endIdx + 1; i++) {
                        bulletRoute.add(route.get(i));
                    }
                }
            }
        }

        return bulletRoute;
    }

    // adds the distance btwn each station on given route
    public static double getRouteDist(List<String> stationIds, List<Station> stations) {
        double dist = 0;
        for (int i = 0; i < stationIds.size() - 1; i++) {
            Station start = IdHelper.findStationWithId(stationIds.get(i), stations);
            Position startPos = start.getPosition();
            Station end = IdHelper.findStationWithId(stationIds.get(i + 1), stations);
            Position endPos = end.getPosition();

            dist += startPos.distance(endPos);
        }

        return dist;
    }
}
