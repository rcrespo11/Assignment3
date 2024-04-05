import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
class ConcurrentLinkedList {
    private final List<Integer> list;

    public ConcurrentLinkedList() {
        this.list = Collections.synchronizedList(new ArrayList<>());
    }

    public synchronized void insert(int num) {
        list.add(num);
    }

    public synchronized int removeHead() {
        if (!list.isEmpty()) {
            return list.remove(0);
        }
        return Integer.MIN_VALUE;
    }

    public synchronized boolean contains(int num) {
        return list.contains(num);
    }

    public synchronized boolean empty() {
        return list.isEmpty();
    }

    public synchronized int size() {
        return list.size();
    }
}

public class MinotaurParty {
    private static final int THREAD_COUNT = 4;
    private static final int NUM_GUESTS = 500_000;

    private static final int TASK_ADD_PRESENT = 0;
    private static final int TASK_WRITE_CARD = 1;
    private static final int TASK_SEARCH_FOR_PRESENT = 2;

    private static final Object mutex = new Object();

    private static Set<Integer> generateUnorderedSet(int size) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        return new HashSet<>(numbers);
    }

    private static void completeTask(int threadId, ConcurrentLinkedList list, Set<Integer> giftBag, Set<Integer> cards, PrintWriter printWriter) {
        while (cards.size() < NUM_GUESTS) {
            int task = ThreadLocalRandom.current().nextInt(3);

            switch (task) {
                case TASK_ADD_PRESENT:
                    synchronized (mutex) {
                        if (giftBag.isEmpty()) {
                            continue;
                        }
                        int num = giftBag.iterator().next();
                        giftBag.remove(num);
                        list.insert(num);
                        printWriter.println("Thread " + threadId + ": Added present for guest " + num);
                    }
                    break;
                case TASK_WRITE_CARD:
                    if (list.empty()) {
                        continue;
                    }
                    int guest = list.removeHead();
                    if (guest != Integer.MIN_VALUE) {
                        synchronized (mutex) {
                            cards.add(guest);
                            printWriter.println("Thread " + threadId + ": Wrote card for guest " + guest);
                        }
                    }
                    break;
                case TASK_SEARCH_FOR_PRESENT:
                    int randomGuest = ThreadLocalRandom.current().nextInt(NUM_GUESTS);
                    boolean found = list.contains(randomGuest);
                    printWriter.println("Thread " + threadId + ": Searched for present of guest " + randomGuest + ", found: " + found);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        ConcurrentLinkedList list = new ConcurrentLinkedList();
        Set<Integer> cards = new HashSet<>();
        Thread[] threads = new Thread[THREAD_COUNT];

        // Create PrintWriter object that wraps System.out
        PrintWriter printWriter = new PrintWriter(System.out);

        printWriter.println("Generating " + NUM_GUESTS + " numbers...");
        printWriter.flush();

        Set<Integer> giftBag = generateUnorderedSet(NUM_GUESTS);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> completeTask(threadId, list, giftBag, cards, printWriter));
            threads[i].start();
        }

        printWriter.println("Running " + THREAD_COUNT + " threads...");
        printWriter.flush();
        long startTime = System.currentTimeMillis();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        printWriter.println("Finished in " + duration + "ms");
        printWriter.flush();

        // Close the PrintWriter
        printWriter.close();
    }
}









