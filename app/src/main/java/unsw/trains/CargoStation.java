package unsw.trains;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.LoadType;

public class CargoStation extends Station {
    public CargoStation(String stationId, String type, double x, double y) {
        super(stationId, type, x, y, 4, new ArrayList<>(List.of(LoadType.Cargo, LoadType.PerishableCargo)));
    }
}
