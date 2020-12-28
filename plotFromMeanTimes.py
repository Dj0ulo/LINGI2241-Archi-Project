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


simple = "tests/results-simple-l=3000-maxwords=5-maxres=10000.csv"
opti = "tests/results-opti-l=3000-maxwords=5-maxres=10000.csv"


def readData(filename):

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



waitsopti, timesopti, PoissonMeanopti = readData(opti)
waitssimple, timessimple, PoissonMeansimple = readData(simple)



#print(len(timesopti), sum(timesopti)/len(timesopti))
plt.hist(timessimple, bins=25, density=1, facecolor='blue', alpha=0.5, label="simple")
plt.hist(timesopti, bins=25, density=1, facecolor='green', alpha=0.5, label="opti")
plt.axvline(np.mean(timessimple), -0.005, np.max(np.append(timesopti,timessimple)), label='simple mean ('+np.mean(timessimple).astype('str')+')', c='b', dashes = (5, 2, 1, 2) )
plt.axvline(np.mean(timesopti), -0.005, np.max(np.append(timesopti,timessimple)), label='opti mean ('+np.mean(timesopti).astype('str')+')', c='g', dashes = (5, 2, 1, 2) )

plt.legend()

plt.ylabel("Proportions of requests")
plt.xlabel("time [ms]")

plt.title("Distribution of the response time of the requests")
 
 
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
plt.show()