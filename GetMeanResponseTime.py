# -*- coding: utf-8 -*-
"""
Created on Sun Dec 27 17:44:18 2020

@author: gregoire
"""
import sys
import numpy as np
from matplotlib import pyplot as plt
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
OnlyServS = "tests/lambda-simple.csv"
CurrentS = "tests/rate-simple-l=3500-maxres=50.csv"



waitCurrentS, timesCurrentS, PoissonMeanCurrentS = readData(CurrentS)
waitsOnlyServS, timesOnlyServS, lS = readData(OnlyServS)

lamb = PoissonMeanCurrentS



def getMeanResponseTimeSimple(lamb, times):
    firstmoment = np.mean(times)
#    print(firstmoment)
    secondmoment = st.moment(times, 2)
#    print(secondmoment)
    rho = lamb*firstmoment
#    print(rho)
#    print((lamb*secondmoment)/(2*(1-rho)))
    return firstmoment + (lamb*secondmoment)/(2*(1-rho))
    

print(getMeanResponseTimeSimple(lamb, timesOnlyServS))

#%%
OnlyServO = "tests/lambda-opti-1000.csv"
CurrentO = "tests/rate-opti-l=3000-maxres=1000.csv"


waitCurrentO, timesCurrentO, PoissonMeanCurrentO = readData(CurrentO)
waitsOnlyServO, timesOnlyServO, lO = readData(OnlyServO)

lamb = PoissonMeanCurrentO


def getMeanResponseTimeOpti(lamb, times):
    m = 4
    ES = np.mean(times)
#    print(ES)
    mhu = 1/ES
    chi = lamb/(m*mhu)
    a = lamb/mhu
    pi0 = 0
    for i in range(m):
        pi0 += (a**i/math.factorial(i)) + (a**m / (math.factorial(m)*(1 - chi)))
    pi0 = 1/pi0
#    print('pi0 : ', pi0)
    ERMMm = (1/lamb) * ((a + ((chi * a**m * pi0) / ((1-chi)**2 * math.factorial(m)))))
#    print('ERMMm : ',ERMMm)
    EWMMm = ERMMm - ES
#    print('EWMMm : ',EWMMm)
    ES2 = st.moment(times, 2)
    EWMGm = ES2/(2*ES) * EWMMm
    ERMGm = ES + EWMGm
    return ERMGm

print(getMeanResponseTimeOpti(PoissonMeanCurrentO, timesOnlyServO))








































