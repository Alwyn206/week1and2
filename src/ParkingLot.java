import java.util.*;

public class ParkingLot {

    enum Status {
        EMPTY, OCCUPIED, DELETED
    }

    class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status = Status.EMPTY;
    }

    private ParkingSpot[] table;
    private int size;

    public ParkingLot(int capacity) {
        table = new ParkingSpot[capacity];
        size = capacity;

        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // hash function
    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % size;
    }

    // park vehicle
    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].status == Status.OCCUPIED) {
            index = (index + 1) % size;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = Status.OCCUPIED;

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
    }

    // exit vehicle
    public void exitVehicle(String plate) {

        int index = hash(plate);

        while (table[index].status != Status.EMPTY) {

            if (table[index].status == Status.OCCUPIED &&
                    table[index].licensePlate.equals(plate)) {

                long duration = System.currentTimeMillis() - table[index].entryTime;

                double hours = duration / 3600000.0;
                double fee = hours * 5; // $5 per hour

                table[index].status = Status.DELETED;

                System.out.println("Spot #" + index +
                        " freed. Duration: " + String.format("%.2f", hours) +
                        " hours. Fee: $" + String.format("%.2f", fee));

                return;
            }

            index = (index + 1) % size;
        }

        System.out.println("Vehicle not found.");
    }

    // statistics
    public void getStatistics() {

        int occupied = 0;

        for (ParkingSpot spot : table) {
            if (spot.status == Status.OCCUPIED)
                occupied++;
        }

        double occupancy = (occupied * 100.0) / size;

        System.out.println("Occupancy: " + String.format("%.2f", occupancy) + "%");
    }

    // test
    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot(10);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}