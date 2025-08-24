package unsw.trains;

import java.util.List;
import java.util.ArrayList;

import unsw.utils.LoadType;
import unsw.utils.RouteType;
import unsw.utils.Position;

// 3500kg/50 passengers max

public class PassengerTrain extends Train {
    public PassengerTrain(String trainId, String type, String stationId, Position position, List<String> route,
            RouteType routeType) {
        super(trainId, type, stationId, position, route, routeType, 2, 2, 0, 0, 3500,
                new ArrayList<>(List.of(LoadType.Passenger)));
    }
}
