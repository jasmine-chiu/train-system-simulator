package unsw.trains;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.LoadType;

public class PassengerStation extends Station {
    public PassengerStation(String stationId, String type, double x, double y) {
        super(stationId, type, x, y, 2, new ArrayList<>(List.of(LoadType.Passenger)));
    }
}
