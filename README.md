# Assignment 3

The assignment uses multithreading as a solution for assignment 3

To compile the programs:

javac MinotaurParty.java

javac Temperature.java

To execute the programs, no user input is needed. Simply run:

java MinotaurParty

java Temperature

# Problem 1

This approach utilizes a linked list structure, where synchronization is ensured through mutexes for operations like insertion, removal, and other shared methods. Two unordered sets named cards and giftBag are established. As each servant, executes, it randomly selects a task. TASK_ADD_PRESENT involves adding a gift to the linked list. Here, the servant acquires a lock on the gift bag, removes the first item, and orderly inserts it into the linked list. TASK_WRITE_CARD signifies a servant removing a gift from the linked list to compose a thank-you card. The servant simply removes the head of the linked list and inserts the guest's ID into the cards set. Lastly, TASK_SEARCH_FOR_PRESENT represents the minotaur's random requests for servants to check if a specific guest is present in the list.
The program typically concludes within 20 to 30 seconds. However, the presence of print statements may prolong execution to about 50 to 60 seconds. One notable limitation of this solution lies in its extensive use of locks to synchronize insertion, removal, and search operations in the linked list, as well as for managing the gift bag and card sets. Moreover, the randomness inherent in task selection by each thread may impact execution time, potentially resulting in shorter durations if fewer searches are initiated by the minotaur.

# Problem 2

This approach employs a single mutex and two shared vectors to tackle the given problem. The task involves eight threads writing 60 random sensor readings per hour, amounting to 480 readings hourly. To manage this, I utilize a vector of length 480, with each thread writing to its designated portion: thread #1 handles indices 0 - 59, thread #2 manages indices 60 - 119, and so forth. This segmentation ensures that each thread operates exclusively within its allocated segment, obviating the necessity for locks during writing operations. Additionally, a second vector named sensorsReady is employed. Before capturing a reading, a sensor flags sensorsReady[threadIndex] as false, toggling it to true post-reading. Subsequently, the thread periodically verifies this array to ensure that all sensors have completed their readings before proceeding. At the conclusion of each hour, the first thread, designated as the reporter, generates a report detailing the five highest and lowest temperatures, alongside the largest temperature difference and the corresponding interval. The execution duration of this solution varies based on the runtime "hours." For a simulated 72-hour operation (the default duration).
