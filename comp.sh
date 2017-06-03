#!/bin/bash

ant; java -jar fnc.jar -c train_stances.formatted.csv train_bodies.formatted.csv -u test_stances_unlabeled.formatted.csv test_bodies.formatted.csv -j 8 -k 107
mv results.csv submission-107.csv

ant; java -jar fnc.jar -c train_stances.formatted.csv train_bodies.formatted.csv -u test_stances_unlabeled.formatted.csv test_bodies.formatted.csv -j 8 -k 106
mv results.csv submission-106.csv

ant; java -jar fnc.jar -c train_stances.formatted.csv train_bodies.formatted.csv -u test_stances_unlabeled.formatted.csv test_bodies.formatted.csv -j 8 -k 104
mv results.csv submission-104.csv

ant; java -jar fnc.jar -c train_stances.formatted.csv train_bodies.formatted.csv -u test_stances_unlabeled.formatted.csv test_bodies.formatted.csv -j 8 -k 103
mv results.csv submission-103.csv
