package unsw.trains;

import unsw.utils.LoadType;

public class PerishableCargo extends Load {
    private int minsTillPerish;

    public PerishableCargo(String startStationId, String destStationId, String cargoId, int weight,
            int minsTillPerish) {
        super(startStationId, destStationId, cargoId, weight, LoadType.PerishableCargo);
        this.minsTillPerish = minsTillPerish;
    }

    public int getMinsTillPerish() {
        return minsTillPerish;
    }

    public void setMinsTillPerish(int minsTillPerish) {
        this.minsTillPerish = minsTillPerish;
    }

}
