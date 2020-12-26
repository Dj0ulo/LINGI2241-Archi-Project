# -*- coding: utf-8 -*-
"""
Created on Sun Dec 20 16:29:42 2020

@author: gregoire
"""
import sys
import numpy as np
from matplotlib import pyplot as plt

argv = sys.argv;
# filename = "tests/rate-"+argv[1]+"-l="+argv[2]+"-maxres=1000.csv"
filename = "tests/cache-opti-l=3000-maxwords=8-maxres=50.csv"
print(filename)
def readData():
    waits = []
    Times = []
    fd = open(filename, "r")
    PoissonMean = float(fd.readline())
    for x in fd:
        strings = x.split(';')
        wait = int(strings[4])
        Words = int(strings[3])
        Time = float (strings[5])
        
        waits.append(wait)
        Times.append(Time)
    
    fd.close()
    return np.asarray(waits), np.asarray(Times), PoissonMean



waits, times, PoissonMean = readData()

print(len(times), sum(times)/len(times))
plt.hist(times, bins=100, density=1, facecolor='green', alpha=0.75)

# x = []
# for nb in nbIter:
#     if nb not in x:
#         x.append(nb)

# y = []
# for nb in x:
#     timefornb = []
#     for i in range(len(times)):
#         if nbIter[i] == nb:
#             timefornb.append(times[i])
#     y.append(np.mean(timefornb))
    
# plt.plot(x, y, '-b')
# plt.xlabel("Number of requests")
# plt.ylabel("Mean time per request")

# plt.title("Plot the mean time per requests depending on the number of requests with arrival time with Poisson mean of " + str(PoissonMean) + " seconds")
plt.show()