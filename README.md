# Concepts related to Cpu caching
Keywords: Cpu cache, cache line, Context switching, Memory Padding, MESI protocal

Cpu cache Latency
L1 : ~1.2ns, Local to cpu
L2 : ~3ns, Local to cpu
L3 : ~12ns, Shared

MESI cpu protocal:
M: Modify
E: Exclusive
S: Sharded
I: Invalid

When CPU1 and CPU2 share memory. They have to made to get ownership of the memory.
If CPU1 made an update S -> M, this will transit CPU2 memory line to S -> I.
When CPU2 made an access, it has to load again from L3/main memory. Also cause CPU1 move from S -> I.
So whichever access, has to load from L3/main memory, and didn't utilise L1/L2 performance.
This is call Context Switching.


Memory Padding:
Technique to avoid memory shared so Context Switching can be avoid.
Keep Cpu memory at M and E state and reload from L3/main memory

Ref: https://medium.com/@teivah/go-and-cpu-caches-af5d32cc5592