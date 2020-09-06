# Advanced Database Systems
## Academic group project developed as part of Advanced Database System(COMP 6521)

## TWO PHASE MERGE SORT

### DESCRIPTION:
The two phase multiway merge sort is used to sort datasets that are extremely large and cannot fit into the main memory in a single instant of time. In such cases the dataset will be processed in two phases over extended periods of time to sort the data. In this technique we have number of input and output buffers to hold the dataset in execution. Merge sort is used in such scenarios since they are very useful in processing large data. The TPMMS is an external sorting algorithm

### PHASE 1:
Let’s assume we have M tuples in a relation of N bytes of B blocks size. We have a main memory whose capacity is limited to R bytes. So our first phase will have M*N byte size of data to be processed. R/B is the number of buffers in main memory under usage. During the course of phase 1, N tuples will fill the buffers in R and the execution takes place.

### PHASE 2:
After the end of first we will have sub lists of order MN/R. In this phase we will have an output buffer to write multiple sub lists into a single sub list. The number of input buffers to be used is calculated using the relation between main memory and number of blocks in it.

## FP GROWTH ALGORITHM

Before diving into FP Growth let's discuss the predecessor to FP Growth Algortihm called the Apriori Algorithm. Once we understand Apriori we can generalise why we need FP Growth algorithm

### Apriori algorithm:
1. Generating All Candidate Pairs and writing them back to file.
2. Reading and calculating the count of the frequent items.

### Issues in Apriori 
1. This approach was very slow because it involves lot of Input Output Operations.
2. It was also giving memory heap error.
3. The counting method iterates through all of the transactions each time.
ex: 1GB database stored in hard disk with block size 8KB require roughly 125,000 block reads for a single pass. for 10 passes require roughly 1,250,000 block reads. Assume it takes 12 ms to read a single block. That means it take roughly 3.5 hrs time for entire block reads.

### DESCRIPTION:
This algorithm is an improvement to the Apriori method. 
1. A frequent pattern is generated without the need for candidate generation. 
2. FP growth algorithm represents the database in the form of a tree called a frequent pattern tree or FP tree.

### Advantages Of FP Growth Algorithm
1. This algorithm needs to scan the database only twice when compared to Apriori which scans the transactions for each iteration.
2. The pairing of items is not done in this algorithm and this makes it faster.
3. The database is stored in a compact version in memory.
4. It is efficient and scalable for mining both long and short frequent patterns.
