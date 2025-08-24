package unsw.utils;

import unsw.trains.Train;
import unsw.trains.Track;
import unsw.response.models.LoadInfoResponse;
import unsw.trains.Load;
import unsw.trains.Station;

import java.util.List;

public class IdHelper {
    public static Train findTrainWithId(String trainId, List<Train> trains) {
        for (Train train : trains) {
            if (train.getTrainId().equals(trainId)) {
                return train;
            }
        }
        return null;
    }

    public static Track findTrackWithId(String trackId, List<Track> tracks) {
        for (Track track : tracks) {
            if (track.getTrackId().equals(trackId)) {
                return track;
            }
        }
        return null;
    }

    public static Track findTrackWithStationId(String toStationId, String fromStationId, List<Track> tracks) {
        for (Track track : tracks) {
            if ((track.getToStationId().equals(fromStationId) && track.getFromStationId().equals(toStationId))
                    || (track.getToStationId().equals(toStationId) && track.getFromStationId().equals(fromStationId))) {
                return track;
            }
        }

        return null;
    }

    public static Station findStationWithId(String stationId, List<Station> stations) {
        for (Station station : stations) {
            if (station.getStationId().equals(stationId)) {
                return station;
            }
        }
        return null;
    }

    public static Load findLoadWithId(String loadId, List<Load> loads) {
        for (Load load : loads) {
            if (load.getLoadId().equals(loadId)) {
                return load;
            }
        }
        return null;
    }

    public static int findInfoWithId(String loadId, List<LoadInfoResponse> loads) {
        for (LoadInfoResponse load : loads) {
            if (load.getLoadId().equals(loadId)) {
                return loads.indexOf(load);
            }
        }
        return -1;
    }

    // checks if route could be cyclical
    public static RouteType findRouteType(List<String> route, List<Track> tracks) {
        String first = route.get(0);
        String last = route.get(route.size() - 1);

        for (Track track : tracks) {
            if (track.getFromStationId().equals(last) && track.getToStationId().equals(first)
                    || track.getFromStationId().equals(first) && track.getToStationId().equals(last)) {
                return RouteType.CYCLICAL;
            }
        }
        return RouteType.LINEAR;
    }

    public static boolean sortTrainIds(Train trainToAdd, List<Train> trains) {
        boolean inserted = false;

        for (int i = 0; i < trains.size(); i++) {
            Train curr = trains.get(i);
            curr.getTrainId();

            if (trainToAdd.getTrainId().compareTo(curr.getTrainId()) < 0) {
                trains.add(i, trainToAdd);
                inserted = true;
                break;
            }
        }

        if (!inserted) {
            trains.add(trainToAdd);
            inserted = true;
        }

        return inserted;
    }

    public static void sortLoadIds(Load loadToAdd, List<Load> loads) {
        for (int i = 0; i < loads.size(); i++) {
            Load curr = loads.get(i);
            curr.getLoadId();

            if (loadToAdd.getLoadId().compareTo(curr.getLoadId()) < 0) {
                loads.add(i, loadToAdd);
                return;
            }
        }

        loads.add(loadToAdd);
    }
}
