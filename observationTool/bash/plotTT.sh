#!/bin/bash
FILE=$1
for FILE in *.tsv; do
	NICENAME=$(echo $FILE | awk '{split($0,a,"\-"); print a[1]}')
	echo "NICENAME: $NICENAME"
	#Probably should get a nice title for these
	gnuplot <<- EOF
		#get the ranges for the num clusters chart
		set terminal unknown
		plot "${FILE}" using 1:10, "" using 1:12, "" using 1:13
		clusterMax=GPVAL_DATA_Y_MAX
		# plot "${FILE}" using 1:11
		# areaMax=GPVAL_DATA_Y_MAX
		
		set terminal pdf enhanced color font ',17' size 29cm,21cm
		#Do normal things like plot
		set xlabel "Time"
		set ylabel "Density"
		set xrange[0:1000]
		set yrange[0:2.5]

		set output "${FILE}.pdf"
		set title "T and T: ${NICENAME}"
		plot "${FILE}" using 1:2 with lines lw 1 lt 1 title "StateDensity", "" using 1:3 with lines lw 1 lt 3 title "AgentDensity", \
		"" using 1:(column(14) * 2.5) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
        "" using 1:(column(15) * 2.5 - (2.5 * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
        "" using 1:(column(16) * 2.5- (2.5 * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"
	
		#Plot the stats
		set title "T and T (stats): ${NICENAME}"
		plot "${FILE}" using 1:4 with lines lw 1 lt 1 title "StateDensityStdDev", "" using 1:5 with lines lw 1 lt 2 title "AgentDensityStdDev", \
		"" using 1:6 with lines lw 1 lt 3 title "StateDensityMax", "" using 1:7 with lines lw 1 lt 4 title "AgentDensityMax", \
		"" using 1:8 with lines lw 1 lt 5 title "StateDensityMin", "" using 1:9 with lines lw 1 lt 6 title "AgentDensityMin", \
		"" using 1:(column(14) * 2.5) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
        "" using 1:(column(15) * 2.5 - (2.5 * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
        "" using 1:(column(16) * 2.5- (2.5 * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"

		
		#Plot the number of clusters
		set title "T and T (clusters): ${NICENAME}"
		set yrange[0:clusterMax];
		set ylabel "Number Of Clusters"
	
		plot "${FILE}" using 1:10 with lines lw 1 lt 1 title "NumberOfClusters", "" using 1:12 with lines lw 1 lt 2 title "Total unique clusters so far", \
		"" using 1:13 with lines lw 1 lt 3 title "Number of cluster BB intersections", \
		"" using 1:(column(14) * clusterMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
        "" using 1:(column(15) * clusterMax - (clusterMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
        "" using 1:(column(16) * clusterMax- (clusterMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"
		
		
		set title "T and T (area): ${NICENAME}"
		set yrange[0:areaMax]
		set ylabel "Average Area"
		plot "${FILE}" using 1:11 with lines lw 1 lt 10 title "AverageArea", \
		"" using 1:(column(14) * areaMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
        "" using 1:(column(15) * areaMax - (areaMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
        "" using 1:(column(16) * areaMax- (areaMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"
	EOF
done
