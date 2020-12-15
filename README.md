# Project archi - Brief description
## Intro
- Analyze performance
	- response time : 
	    - if high, reason : cpu/disk/network/memory/... ?
	    - how many customers before response time too high ?
- Implement in **JAVA**
- **2** versions of the server :
	- simple : linear search
	- optimized (as we want)
- Goal
	- measure the performance 
	- show how the optimizations impact performance
	- compare with results from a queueing station model
- Server and client **on different computers**
## Specifications
- [Dataset](https://drive.google.com/u/0/uc?id=1S6bmXruIk6F76ZHP4K4Uh70PW0EU4spO&export=download) :
	- File with each line : `<type>@@@<sentence>\n`
		- type : Integer
		- sentence : String
	- File read once at startup of the server
- Requests :
	- Format : `<types>;<regex>\n`
		- types  (optional) : comma-separated list of integers
		- regex : string containing a regular expression
### Task 1: Implementation
- [java.net.ServerSocket](http://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html)
- server multi-threaded
### Task 2: Measurements
- Average response time (response time : delay seen by the **client**)
- Send requests at random times (even when the previous is not done yet)
- Up to 100 clients TCP **or** 1 client with many TCP connections (course about the stochastic properties of many independent clients) 
- Combinations :
	- different request rate
	- different difficulties
- Show plots : most important factor ? (cpu/disk/network/memory/...)
	- [network monitor](http://www.binarytides.com/linux-commands-monitor-network/)
	- [performance monitor](https://www.tecmint.com/command-line-tools-to-monitor-linux-performance/)
- Explain what modifications, why, what expectations ?
### Task 3: Modeling
- Analyze the server with *queuing station model* (see the course about it)
- Select most appropriate model (arrival rate of requests, number of server threads, etc.)
- Calculate mean response time with same parameters as in task 2
## What to turn out
Zip file with :
- source code
- report (max 4 pages) describing :
	- name, email, noma
	- implementation
	- optimization : how + impact (plot)
	- measurement setup (hardware, network,...)
	- workload of the test
	- results of measurements + modeling