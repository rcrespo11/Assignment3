import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class Temperature {
    private static final int THREAD_COUNT = 8;
    private static final int MINUTES = 60;
    private static int HOURS = 72; // Default value
    private static final Lock mutex = new ReentrantLock();

    public static void main(String[] args) {
        
        int[] sensorReadings = new int[THREAD_COUNT * MINUTES];
        boolean[] sensorsReady = new boolean[THREAD_COUNT];
        Thread[] threads = new Thread[THREAD_COUNT];

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> measureTemperature(threadId, sensorReadings, sensorsReady));
            threads[i].start();
        }

        long startTime = System.currentTimeMillis();

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Finished in " + duration + "ms");
    }

    private static boolean allSensorsReady(int caller, boolean[] sensors) {
        for (int i = 0; i < sensors.length; i++) {
            if (!sensors[i] && caller != i) {
                return false;
            }
        }
        return true;
    }

    private static void printLargestDifference(int[] sensorReadings) {
        int step = 10;
        int startInterval = 0;
        int maxDifference = Integer.MIN_VALUE;

        for (int threadIndex = 0; threadIndex < THREAD_COUNT; threadIndex++) {
            int offset = threadIndex * MINUTES;

            for (int i = offset; i < MINUTES - step + 1; i++) {
                int max = Integer.MIN_VALUE;
                int min = Integer.MAX_VALUE;

                for (int j = i; j < i + step; j++) {
                    max = Math.max(max, sensorReadings[j]);
                    min = Math.min(min, sensorReadings[j]);
                }

                int diff = max - min;

                if (diff > maxDifference) {
                    maxDifference = diff;
                    startInterval = i;
                }
            }
        }

        System.out.println("Largest temperature difference: " + maxDifference + "F"
                + " starting at minute " + startInterval
                + " and ending at minute " + (startInterval + 10));
    }

    private static void printHighestTemperatures(int[] sensorReadings) {
        Set<Integer> temperatures = new HashSet<>();

        for (int i = sensorReadings.length - 1; i >= 0; i--) {
            temperatures.add(sensorReadings[i]);

            if (temperatures.size() == 5) {
                break;
            }
        }

        System.out.print("Highest temperatures: ");
        for (int temperature : temperatures) {
            System.out.print(temperature + "F ");
        }
        System.out.println();
    }

    private static void printLowestTemperatures(int[] sensorReadings) {
        Set<Integer> temperatures = new HashSet<>();

        for (int temperature : sensorReadings) {
            temperatures.add(temperature);

            if (temperatures.size() == 5) {
                break;
            }
        }

        System.out.print("Lowest temperatures: ");
        for (int temperature : temperatures) {
            System.out.print(temperature + "F ");
        }
        System.out.println();
    }

    private static void generateReport(int hour, int[] sensorReadings) {
        System.out.println("[Hour " + (hour + 1) + " report]");

        printLargestDifference(sensorReadings);

        Arrays.sort(sensorReadings);

        printHighestTemperatures(sensorReadings);
        printLowestTemperatures(sensorReadings);

        System.out.println();
    }

    private static void measureTemperature(int threadId, int[] sensorReadings, boolean[] sensorsReady) {
        Random random = new Random();

        for (int hour = 0; hour < HOURS; hour++) {
            for (int minute = 0; minute < MINUTES; minute++) {
                sensorsReady[threadId] = false;
                sensorReadings[minute + (threadId * MINUTES)] = random.nextInt(171) - 100;
                sensorsReady[threadId] = true;

              
                while (!allSensorsReady(threadId, sensorsReady)) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (threadId == 0) {
                mutex.lock();
                generateReport(hour, sensorReadings);
                mutex.unlock();
            }
        }
    }
}
