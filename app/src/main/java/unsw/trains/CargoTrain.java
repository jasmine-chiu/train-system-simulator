package unsw.trains;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.LoadType;
import unsw.utils.RouteType;
import unsw.utils.Position;

// 5000kg cargo max
// slowed 0.01% per kg of cargo

public class CargoTrain extends Train {
    public CargoTrain(String trainId, String type, String stationId, Position position, List<String> route,
            RouteType routeType) {
        super(trainId, type, stationId, position, route, routeType, 3, 3, 0, 0, 5000,
                new ArrayList<>(List.of(LoadType.Cargo, LoadType.PerishableCargo)));

        this.setMaxSpeed(3);
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
