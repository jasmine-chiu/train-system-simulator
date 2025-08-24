package unsw.trains;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.LoadType;
import unsw.utils.RouteType;
import unsw.utils.Position;

public class BulletTrain extends Train {
    public BulletTrain(String trainId, String type, String stationId, Position position, List<String> route,
            RouteType routeType) {
        super(trainId, type, stationId, position, route, routeType, 5, 5, 0, 0, 5000,
                new ArrayList<>(List.of(LoadType.Passenger, LoadType.Cargo, LoadType.PerishableCargo)));

        this.setMaxSpeed(5);
    }

    @Override
    public int getMaxSpeed() {
        return super.getMaxSpeed();
    }

    @Override
    public void setMaxSpeed(int maxSpeed) {
        super.setMaxSpeed(maxSpeed);
    }
}
