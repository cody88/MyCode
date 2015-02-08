#!/bin/bash

for i in $(seq 12 302);
do
	echo "$i:-"
	cut -d',' -f $i train.csv | grep 'NA' | wc -l
done
