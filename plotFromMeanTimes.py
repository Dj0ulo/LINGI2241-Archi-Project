# -*- coding: utf-8 -*-
"""
Created on Sun Dec 20 16:29:42 2020

@author: gregoire
"""

import numpy as np
from matplotlib import pyplot as plt



def readData():
    Requests = []
    Times = []
    fd = open("MeanTimes.txt", "r")
    PoissonMean = float(fd.readline())
    for x in fd:
        strings = x.split(';')
        NbRequests = int(strings[0])
        Time = float (strings[1])
        
        Requests.append(NbRequests)
        Times.append(Time)
    
    fd.close()
    return np.asarray(Requests), np.asarray(Times), PoissonMean



x, y, PoissonMean = readData()

print(x)
print(y)
plt.plot(x, y, '-b')
#plt.plot(label="Random method")
plt.xlabel("Number of requests")
plt.ylabel("Mean time per request")

plt.title("Plot the mean time per requests depending on the number of requests with arrival time with Poisson mean of " + str(PoissonMean) + " seconds")
#plt.legend()
plt.show()