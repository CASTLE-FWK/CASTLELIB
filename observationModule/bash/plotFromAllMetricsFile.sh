#/bin/bash
#
#Plot all results from an (system)_allMetrics file. This will be brutal
#
#
FILE=$1
FNAME=`basename $FILE .tsv`
echo $FNAME
NICENAME=$(echo $FNAME | awk '{split($0,a,"\_"); print a[1]}')
echo "NICENAME: $NICENAME"
#This is going to hurt
xRange=1000
#Plot ChanGol iT and zT first
#Column 5, 6
gnuplot <<- EOF
	set terminal unknown
	plot "${FILE}" using 1:5
	itMax=GPVAL_DATA_Y_MAX
	set terminal unknown
	plot "${FILE}" using 1:7
	ztMax=GPVAL_DATA_Y_MAX
	set terminal unknown
    plot "${FILE}" using 1:21
	perfMax=GPVAL_DATA_Y_MAX
	set terminal unknown
	plot "${FILE}" using 1:7
	tooleMax=GPVAL_DATA_Y_MAX
	set terminal unknown
	plot "${FILE}" using 1:8
	scMax=GPVAL_DATA_Y_MAX
	set terminal unknown
	plot "${FILE}" using 1:13, "" using 1:14
	clusterMax=GPVAL_DATA_Y_MAX
	plot "${FILE}" using 1:11
	areaMax=GPVAL_DATA_Y_MAX
	set terminal unknown
	plot "${FILE}" using 1:15, "" using 1:16, "" using 1:17
	entropyMax=GPVAL_DATA_Y_MAX
	set terminal unknown
    plot "${FILE}" using 1:18
	watMax=GPVAL_DATA_Y_MAX
	set terminal unknown
	plot "${FILE}" using 1:19, "" using 1:20
	auMax=GPVAL_DATA_Y_MAX
	

	set xrange[0:$xRange]
	set terminal pdf enhanced color font ',17' size 29cm,21cm
	set output "${FILE}.pdf" 
	set xlabel "Steps"

	set title "Chan11 I_t:  ${NICENAME}"
	set ylabel "I_t"
	set yrange[0:itMax];

	plot "${FILE}" using 1:5 with lines title "Chan I_t", \
	"" using 1:(column(2) * itMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
    "" using 1:(column(3) * itMax - (itMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
    "" using 1:(column(4) * itMax- (itMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"

    set title "Chan11 Z_t:  ${NICENAME}"
	set ylabel "Z_t"
	set yrange[0:ztMax];

    plot "${FILE}" using 1:6 with lines title "Chan Z_t", \
	"" using 1:(column(2) * ztMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
    "" using 1:(column(3) * ztMax - (ztMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
    "" using 1:(column(4) * ztMax- (ztMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"


#Plot OToole 
#Column 7
	
	set xlabel "Steps"
	set ylabel "Percentage of Agents with SSignif"
	# set yrange[0.00:1.00]
	set xrange[0:$xRange]
	# set terminal pdf enhanced color font ',17' size 29cm,21cm
	#set output "${FILE}.pdf" 
	set title "OToole14: ${NICENAME}"
	
	set ylabel "SS"
	set yrange[0:tooleMax]		

	# set style data dots pointsize 5
	plot "${FILE}" using 1:7 with lines title "Agents with SSignif", \
	"" using 1:(column(2) * tooleMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
    "" using 1:(column(3) * tooleMax - (tooleMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
    "" using 1:(column(4) * tooleMax- (tooleMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"


#Plot SC
#Column 8
	
	
	set xlabel "Steps"
	set ylabel "Complexity"
	
	set xrange[0:$xRange]
	# set terminal pdf enhanced color font ',17'
	#set output "${FILE}.pdf" 
	set title "System Complexity: ${NICENAME}"
	set yrange[0:scMax]
	
	plot "${FILE}" using 1:8 with lines title "Complexity", \
	"" using 1:(column(2) * scMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
    "" using 1:(column(3) * scMax - (scMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
    "" using 1:(column(4) * scMax- (scMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"


#Plot OD
#Column 9
#?????

#Plot All The Clusters
#Columns, 10, 11, 12, 13, 14
	#get the ranges for the num clusters chart
	
	
	# set terminal pdf enhanced color font ',17' size 29cm,21cm
	#Do normal things like plot
	set xlabel "Time"
	set ylabel "Density"
	set xrange[0:$xRange]
	set yrange[0:2.5]

	#set output "${FILE}.pdf"
	set title "Cluster Analysis: ${NICENAME}"
	plot "${FILE}" using 1:10 with lines lw 1 lt 1 title "StateDensity", "" using 1:11 with lines lw 1 lt 3 title "AgentDensity", \
	"" using 1:(column(2) * 2.5) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
    "" using 1:(column(3) * 2.5 - (2.5 * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
    "" using 1:(column(4) * 2.5- (2.5 * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"

	#Plot the number of clusters
	set title "Cluster Analysis (cluster counts): ${NICENAME}"
	set yrange[0:clusterMax];
	set ylabel "Number Of Clusters"

	plot "${FILE}"  using 1:13 with lines lw 1 lt 2 title "Total unique clusters so far", \
	"" using 1:14 with lines lw 1 lt 3 title "Number of cluster BB intersections", \
	"" using 1:(column(2) * clusterMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
    "" using 1:(column(3) * clusterMax - (clusterMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
    "" using 1:(column(4) * clusterMax- (clusterMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"
	
	
	set title "Cluster Analysis (area): ${NICENAME}"
	set yrange[0:areaMax]
	set ylabel "Average Area"
	plot "${FILE}" using 1:12 with lines lw 1 lt 10 title "AverageArea", \
	"" using 1:(column(2) * areaMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
  	"" using 1:(column(3) * areaMax - (areaMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
  	"" using 1:(column(4) * areaMax- (areaMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"


#Plot all the Entropy Calclulations
#Columns 15, 16, 17
	
	
	# set terminal pdf enhanced color font ',17' size 29cm,21cm
	set xlabel "Time"
	set ylabel "Entropy"
	set xrange[0:$xRange];
	set yrange[0:(entropyMax + (entropyMax*0.1))];
	
	set title "Entropy Over Time: ${NICENAME}"

	#set output "${FILE}.pdf"
	plot "${FILE}" using 1:15 with lines lw 1 lt 1 title "ShannonEntropy", "" using 1:16 with lines lw 1 lt 2 title "ShannonEntropyOfStateChange", \
	"" using 1:17 with lines lw 1 lt 3 title "ConditionalEntropy", \
	"" using 1:(column(2) * entropyMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
    "" using 1:(column(3) * entropyMax - (entropyMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
    "" using 1:(column(4) * entropyMax- (entropyMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"


#Plot WAT
#Column 18
	
	
	# set terminal pdf enhanced color font ',17' size 29cm,21cm
	set xlabel "Time"
	set ylabel "Working / Adaptive T"
	set xrange[0:$xRange];
	set yrange[0:(watMax + (watMax*0.1))];		
	set title "Working Over Adaptive Time: ${NICENAME}"
	#set output "${FILE}.pdf"
	plot "${FILE}" using 1:18 with lines lw 1 lt 1 title "WAT Score", \
	"" using 1:(column(2) * watMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
    "" using 1:(column(3) * watMax - (watMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
    "" using 1:(column(4) * watMax- (watMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"


#Plot AU
#Column 19, 20
	
	
	# set terminal pdf enhanced color font ',17' size 29cm,21cm
	set xlabel "Time"
	set ylabel "Score"
	set xrange[0:$xRange];
	set yrange[0:(auMax + (auMax*0.1))];
	
	set title "Working Over Adaptive Time: ${NICENAME}"

	#set output "${FILE}.pdf"
	plot "${FILE}" using 1:19 with lines lw 1 lt 3 title "Availability", "" using 1:20 with lines lw 1 lt 4 title "Unavailability", \
    "" using 1:(column(2) * auMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
    "" using 1:(column(3) * auMax - (auMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
    "" using 1:(column(4) * auMax- (auMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"


#Plot PerfSit
#Column 21
	
	
	# set terminal pdf enhanced color font ',17' size 29cm,21cm
	set xlabel "Time"
	set ylabel "Situational Performance"
	set xrange[0:$xRange];
	set yrange[0:(perfMax + (perfMax*0.1))];
	
	set title "Situational Performance over time (perf(sit)): ${NICENAME}"

	#set output "${FILE}.pdf"
	plot "${FILE}" using 1:21 with lines lw 1 lt 1 title "Perf(sit) Score", \
	"" using 1:(column(2) * perfMax) with points pointtype 8 pointsize 1.0 title "Real Event (Emergence)", \
    "" using 1:(column(3) * perfMax - (perfMax * 0.1)) with points pointtype 10 pointsize 1.0 title "Real Event (Stability)", \
    "" using 1:(column(4) * perfMax- (perfMax * 0.2)) with points pointtype 12 pointsize 1.0 title "Real Event (Criticality)"
EOF

#Plot MSSE
#Column 22, 23, 24, 25, 26, 27
#
#
#

#Plot LBR Matches
#Column 28, 29, 30