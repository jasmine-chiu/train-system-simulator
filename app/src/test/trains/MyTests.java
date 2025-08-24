package trains;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import unsw.response.models.LoadInfoResponse;
import unsw.response.models.StationInfoResponse;
import unsw.response.models.TrackInfoResponse;
import unsw.response.models.TrainInfoResponse;
import unsw.trains.TrainsController;
import unsw.utils.Position;
import unsw.utils.TrackType;

public class MyTests {
    // Write your tests here
    @Test
    public void testListStationIds() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CentralStation", 0, 0);
        controller.createStation("s2", "DepotStation", 0, 10);
        controller.createStation("s3", "CargoStation", 0, 20);

        assertEquals(3, controller.listStationIds().size());
        assertEquals(List.of("s1", "s2", "s3"), controller.listStationIds());
    }

    @Test
    public void testListTrackIds() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CentralStation", 0, 0);
        controller.createStation("s2", "DepotStation", 0, 10);
        controller.createStation("s3", "CargoStation", 0, 20);

        controller.createTrack("t1-2", "s1", "s2");
        controller.createTrack("t2-3", "s2", "s3");

        assertEquals(2, controller.listTrackIds().size());
        assertEquals(List.of("t1-2", "t2-3"), controller.listTrackIds());
    }

    @Test
    public void testListTrainIds() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CentralStation", 0, 0);
        controller.createStation("s2", "DepotStation", 0, 10);
        controller.createStation("s3", "CargoStation", 0, 20);

        controller.createTrack("t1-2", "s1", "s2");
        controller.createTrack("t2-3", "s2", "s3");

        assertDoesNotThrow(() -> {
            controller.createTrain("t1", "PassengerTrain", "s2", List.of("s1", "s2", "s3"));
            controller.createTrain("t2", "BulletTrain", "s2", List.of("s1", "s2"));
            controller.createTrain("t3", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        assertEquals(3, controller.listTrainIds().size());
        assertEquals(List.of("t1", "t2", "t3"), controller.listTrainIds());
    }

    @Test
    public void testGetTrainInfo() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CentralStation", 0, 0);
        controller.createStation("s2", "DepotStation", 10, 10);

        controller.createTrack("t1-2", "s1", "s2");

        assertDoesNotThrow(() -> {
            controller.createTrain("t1", "PassengerTrain", "s1", List.of("s1", "s2"));
        });

        TrainInfoResponse info = controller.getTrainInfo("t1");
        assertEquals("t1", info.getTrainId());
        assertEquals("s1", info.getLocation());
        assertEquals("PassengerTrain", info.getType());
        assertEquals(new Position(0, 0), info.getPosition());
    }

    @Test
    public void testGetStationInfo() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CentralStation", 100, 100);

        StationInfoResponse info = controller.getStationInfo("s1");
        assertEquals("s1", info.getStationId());
        assertEquals("CentralStation", info.getType());
        assertEquals(new Position(100, 100), info.getPosition());
    }

    @Test
    public void testGetTrackInfo() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "DepotStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 10);

        controller.createTrack("t1-2", "s1", "s2");

        TrackInfoResponse info = controller.getTrackInfo("t1-2");
        assertEquals("t1-2", info.getTrackId());
        assertEquals("s1", info.getFromStationId());
        assertEquals("s2", info.getToStationId());
        assertEquals(TrackType.NORMAL, info.getType());
    }

    @Test
    public void testDontPickUp() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "PassengerStation", 0, 0);
        controller.createStation("s2", "PassengerStation", 0, 10);
        controller.createTrack("t1-2", "s1", "s2");

        controller.createPassenger("s1", "s2", "p1");

        assertDoesNotThrow(() -> {
            controller.createTrain("t1", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        controller.simulate();

        assertEquals(List.of(new LoadInfoResponse("p1", "Passenger")), controller.getStationInfo("s1").getLoads());
        assertEquals(0, controller.getTrainInfo("t1").getLoads().size());
    }

    @Test
    public void testDontCreate() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "DepotStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 10);
        controller.createTrack("t1-2", "s1", "s2");

        assertDoesNotThrow(() -> {
            controller.createTrain("t1", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        controller.createPassenger("s1", "s2", "p1");
        controller.createCargo("s1", "s2", "c1", 400);

        controller.simulate();

        assertEquals(0, controller.getTrainInfo("t1").getLoads().size());
    }

    @Test
    public void testInvalidDest() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CentralStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 10);
        controller.createTrack("t1-2", "s1", "s2");

        assertDoesNotThrow(() -> {
            controller.createTrain("t1", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        controller.createPassenger("s1", "s2", "p1");
        controller.createPassenger("s1", "s2", "p2");
        controller.createCargo("s1", "s2", "c1", 220);

        assertEquals(1, controller.getStationInfo("s1").getLoads().size());
        assertEquals(List.of(new LoadInfoResponse("c1", "Cargo")), controller.getStationInfo("s1").getLoads());
    }

    @Test
    public void testCreatePerishable() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "PassengerStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 10);
        controller.createStation("s3", "CentralStation", 10, 10);

        controller.createPerishableCargo("s2", "s3", "pc1", 500, 10);

        assertEquals(List.of(new LoadInfoResponse("pc1", "PerishableCargo")),
                controller.getStationInfo("s2").getLoads());

        for (int i = 0; i < 9; i++) {
            controller.simulate();
        }

        assertEquals(List.of(new LoadInfoResponse("pc1", "PerishableCargo")),
                controller.getStationInfo("s2").getLoads());

        controller.simulate();

        assertEquals(0, controller.getStationInfo("s2").getLoads().size());
    }

    @Test
    public void testExpire() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 10);

        controller.createTrack("t1-2", "s1", "s2");

        for (int i = 0; i < 3; i++) {
            controller.createPerishableCargo("s1", "s2", "pc" + i, 100, i + 1);
        }

        for (int i = 0; i < 4; i++) {
            controller.simulate();
        }

        assertEquals(0, controller.getStationInfo("s1").getLoads().size());
    }

    @Test
    public void testWontPickUpPersihable() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 10);
        assertDoesNotThrow(() -> {
            controller.createTrack("t1-2", "s1", "s2");
        });
        controller.createPerishableCargo("s1", "s2", "pc1", 100, 2);

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        assertEquals(List.of(new LoadInfoResponse("pc1", "PerishableCargo")),
                controller.getStationInfo("s1").getLoads());

        controller.simulate();
        assertEquals(0, controller.getTrainInfo("train1").getLoads().size());
        assertEquals(1, controller.getStationInfo("s1").getLoads().size());
    }

    @Test
    public void testTrainPicksNDropsPerishable() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 10);
        controller.createTrack("t1-2", "s1", "s2");
        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        controller.createPerishableCargo("s1", "s2", "pc1", 100, 30);

        assertEquals(List.of(new LoadInfoResponse("pc1", "PerishableCargo")),
                controller.getStationInfo("s1").getLoads());

        for (int i = 0; i < 2; i++) {
            controller.simulate();
        }

        assertEquals(List.of(new LoadInfoResponse("pc1", "PerishableCargo")),
                controller.getTrainInfo("train1").getLoads());

        for (int i = 0; i < 5; i++) {
            controller.simulate();
        }

        assertEquals(0, controller.getTrainInfo("train1").getLoads().size());
    }

    @Test
    public void testPerishableCheckAllows() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 10);
        controller.createStation("s3", "CargoStation", 0, 20);
        controller.createStation("s4", "CargoStation", 0, 30);
        controller.createStation("s5", "CargoStation", 0, 40);

        controller.createTrack("t1-2", "s1", "s2");
        controller.createTrack("t2-3", "s2", "s3");
        controller.createTrack("t3-4", "s3", "s4");
        controller.createTrack("t4-5", "s4", "s5");

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "CargoTrain", "s2", List.of("s1", "s2", "s3", "s4", "s5"));
        });

        controller.createPerishableCargo("s2", "s5", "pc1", 100, 200);

        assertEquals(List.of(new LoadInfoResponse("pc1", "PerishableCargo")),
                controller.getStationInfo("s2").getLoads());

        controller.simulate();

        for (int i = 0; i < 2; i++) {
            controller.simulate();
        }

        assertEquals(List.of(new LoadInfoResponse("pc1", "PerishableCargo")),
                controller.getTrainInfo("train1").getLoads());

        for (int i = 0; i < 5; i++) {
            controller.simulate();
        }

        assertEquals(1, controller.getTrainInfo("train1").getLoads().size());
    }

    @Test
    public void testPerishableCheckAllowsBackward() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 10);
        controller.createStation("s3", "CargoStation", 0, 20);
        controller.createStation("s4", "CargoStation", 0, 30);
        controller.createStation("s5", "CargoStation", 0, 40);

        controller.createTrack("t1-2", "s1", "s2");
        controller.createTrack("t2-3", "s2", "s3");
        controller.createTrack("t3-4", "s3", "s4");
        controller.createTrack("t4-5", "s4", "s5");

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "CargoTrain", "s2", List.of("s1", "s2", "s3", "s4", "s5"));
        });

        controller.createPerishableCargo("s4", "s2", "pc1", 100, 50);

        assertEquals(List.of(new LoadInfoResponse("pc1", "PerishableCargo")),
                controller.getStationInfo("s4").getLoads());

        for (int i = 0; i < 10; i++) {
            controller.simulate();
        }

        assertEquals(List.of(new LoadInfoResponse("pc1", "PerishableCargo")),
                controller.getTrainInfo("train1").getLoads());

        for (int i = 0; i < 20; i++) {
            controller.simulate();
        }

        assertEquals(0, controller.getTrainInfo("train1").getLoads().size());
    }

    @Test
    public void testPerishableCheckDenies() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 10);
        controller.createStation("s3", "CargoStation", 0, 20);
        controller.createStation("s4", "CargoStation", 0, 30);
        controller.createStation("s5", "CargoStation", 0, 40);

        controller.createTrack("t1-2", "s1", "s2");
        controller.createTrack("t2-3", "s2", "s3");
        controller.createTrack("t3-4", "s3", "s4");
        controller.createTrack("t4-5", "s4", "s5");

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "CargoTrain", "s4", List.of("s1", "s2", "s3", "s4", "s5"));
        });

        controller.createPerishableCargo("s4", "s1", "pc1", 100, 5);

        assertEquals(List.of(new LoadInfoResponse("pc1", "PerishableCargo")),
                controller.getStationInfo("s4").getLoads());

        controller.simulate();

        for (int i = 0; i < 2; i++) {
            controller.simulate();
        }

        assertEquals(0, controller.getTrainInfo("train1").getLoads().size());
    }

    @Test
    public void testPerishableCycle() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 0, 10);
        controller.createStation("s3", "CargoStation", 0, 20);
        controller.createStation("s4", "CargoStation", 0, 30);

        controller.createTrack("t1-2", "s1", "s2");
        controller.createTrack("t2-3", "s2", "s3");
        controller.createTrack("t3-4", "s3", "s4");
        controller.createTrack("t4-1", "s4", "s1");

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "BulletTrain", "s4", List.of("s1", "s2", "s3", "s4"));
        });

        controller.createPerishableCargo("s4", "s1", "pc1", 100, 20);

        assertEquals(List.of(new LoadInfoResponse("pc1", "PerishableCargo")),
                controller.getStationInfo("s4").getLoads());

        controller.simulate();

        for (int i = 0; i < 2; i++) {
            controller.simulate();
        }

        assertEquals(1, controller.getTrainInfo("train1").getLoads().size());
    }
}
