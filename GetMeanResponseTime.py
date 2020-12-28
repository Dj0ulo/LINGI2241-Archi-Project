# -*- coding: utf-8 -*-
"""
Created on Sun Dec 27 17:44:18 2020

@author: gregoire
"""
import numpy as np
import scipy.stats as st
import math



def readData(filename):

    waits = []
    Times = []
    fd = open(filename, "r")
    lambd = float(fd.readline())
    for x in fd:
        strings = x.split(';')
        wait = int(strings[4])
#        Words = int(strings[3])
        Time = float (strings[5])
        
        waits.append(wait)
        Times.append(Time)
    
    fd.close()
    return np.asarray(waits), np.asarray(Times), lambd


#%%
OnlyServS = "tests/lambda-simple.csv" #change with the service times file



waitsOnlyServS, timesOnlyServS, lS = readData(OnlyServS)

lamb = 1.0/3000 #change with the lambda you want to predict



def getMeanResponseTimeSimple(lamb, times):
    firstmoment = np.mean(times)
    secondmoment = st.moment(times, 2)
    rho = lamb*firstmoment
    return firstmoment + (lamb*secondmoment)/(2*(1-rho))
    

print(getMeanResponseTimeSimple(lamb, timesOnlyServS))

#%%
OnlyServO = "tests/lambda-opti-1000.csv" #change with the service times file


waitsOnlyServO, timesOnlyServO, lO = readData(OnlyServO)

lamb = 1.0/3000 #change with the lambda you want to predict


def getMeanResponseTimeOpti(lamb, times):
    m = 4
    ES = np.mean(times)
    mhu = 1/ES
    chi = lamb/(m*mhu)
    a = lamb/mhu
    pi0 = 0
    for i in range(m):
        pi0 += (a**i/math.factorial(i)) + (a**m / (math.factorial(m)*(1 - chi)))
    pi0 = 1/pi0
    ERMMm = (1/lamb) * ((a + ((chi * a**m * pi0) / ((1-chi)**2 * math.factorial(m)))))
    EWMMm = ERMMm - ES
    ES2 = st.moment(times, 2)
    EWMGm = ES2/(2*ES) * EWMMm
    ERMGm = ES + EWMGm
    return ERMGm

print(getMeanResponseTimeOpti(lamb, timesOnlyServO))



