package unsw.trains;

import unsw.utils.LoadType;

public class Cargo extends Load {
    public Cargo(String startStationId, String destStationId, String cargoId, int weight) {
        super(startStationId, destStationId, cargoId, weight, LoadType.Cargo);
    }

}
