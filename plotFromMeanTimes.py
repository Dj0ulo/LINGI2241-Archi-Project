# -*- coding: utf-8 -*-
"""
Created on Sun Dec 20 16:29:42 2020

@author: gregoire
"""
import sys
import numpy as np
from matplotlib import pyplot as plt

simple = "tests/results-simple-l=3000-maxwords=5-maxres=10000.csv"
opti = "tests/rate-opti-l=1010-maxwords=8-maxres=10000-nbreq=1.csv"


def readData(filename):
    waits = []
    times = []
    lines = []
    words = []
    fd = open(filename, "r")
    PoissonMean = float(fd.readline())
    for x in fd:
        strings = x.split(';')
        types = int(strings[2])
        if types == 0 :
            types = 6
        words.append(len(strings[1])*types)
        waits.append(int(strings[4]))
        times.append(float(strings[5]))
        lines.append(float(strings[6]))
    fd.close()
    return lines, np.asarray(words), np.asarray(waits), np.asarray(times), PoissonMean


linesOpti, wordsOpti, waitsopti, timesopti, PoissonMeanopti = readData(opti)
linesSimple, wordsSimple, waitssimple, timessimple, PoissonMeansimple = readData(simple)



#print(len(timesopti), sum(timesopti)/len(timesopti))
# plt.hist(timessimple, bins=25, density=1, facecolor='blue', alpha=0.5, label="simple")
# plt.hist(timesopti, bins=25, density=1, facecolor='green', alpha=0.5, label="opti")
# plt.axvline(np.mean(timessimple), -0.005, np.max(np.append(timesopti,timessimple)), label='simple mean ('+np.mean(timessimple).astype('str')+')', c='b', dashes = (5, 2, 1, 2) )
# plt.axvline(np.mean(timesopti), -0.005, np.max(np.append(timesopti,timessimple)), label='opti mean ('+np.mean(timesopti).astype('str')+')', c='g', dashes = (5, 2, 1, 2) )

# plt.legend()

# plt.ylabel("Proportions of requests")
# plt.xlabel("time [ms]")

# plt.title("Distribution of the response time of the requests")

# plt.show()

    
plt.plot(linesOpti, timesopti, '.')
plt.show()

plt.plot(wordsOpti, timesopti, '.')
plt.show()