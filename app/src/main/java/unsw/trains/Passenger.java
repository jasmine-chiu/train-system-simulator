package unsw.trains;

import unsw.utils.LoadType;

public class Passenger extends Load {
    public Passenger(String startStationId, String destStationId, String passengerId) {
        super(startStationId, destStationId, passengerId, 70, LoadType.Passenger);
    }
}
