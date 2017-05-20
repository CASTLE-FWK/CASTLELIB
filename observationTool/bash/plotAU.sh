#!/bin/bash

for FILE in *.tsv; do
	NICENAME=$(echo $FILE | awk '{split($0,a,"\-"); print a[1]}')
	echo "NICENAME: $NICENAME"
	gnuplot <<- EOF
		set terminal unknown
		plot "${FILE}" using 1:2, "" using 1:3, "" using 1:4, "" using 1:5
		auMax=GPVAL_DATA_Y_MAX
		
		set terminal pdf enhanced color font ',17' size 29cm,21cm
		set xlabel "Time"
		set ylabel "Score"
		set xrange[0:1000];
		set yrange[0:(auMax + (auMax*0.1))];
		
		set title "Working Over Adaptive Time: ${NICENAME}"

		set output "${FILE}.pdf"
		plot "${FILE}" using 1:2 with lines lw 1 lt 1 title "MTTR", "" using 1:3 with lines lw 1 lt 2 title "MTTF", \
        "" using 1:4 with lines lw 1 lt 3 title "Availability", "" using 1:5 with lines lw 1 lt 4 title "Unavailability", \
        "" using 1:(column(8) * auMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
        "" using 1:(column(9) * auMax - (auMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
        "" using 1:(column(10) * auMax- (auMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"
	EOF
done
