package unsw.trains;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.LoadType;

public class DepotStation extends Station {
    public DepotStation(String stationId, String type, double x, double y) {
        super(stationId, type, x, y, 8, new ArrayList<>(List.of(LoadType.NONE)));
    }

}
