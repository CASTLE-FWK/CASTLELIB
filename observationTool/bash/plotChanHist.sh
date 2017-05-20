#!/bin/bash
# junk="distributions//"
# for FILE in $1/*.tsv 
# do
# 	plotID=$(echo ${FILE} | awk '{split($0,a,"-"); print a[1]}')
# 	path=$plotID
# 	plotID=${plotID#$junk}
# 	echo "plotting "$plotID
# 	FILE=$1
# 	gnuplot <<- EOF
# 		set xlabel "Steps"
# 		set ylabel "Number of registered state chagnes"
# 		# set yrange[0.00:1.00]
# 		set xrange[0:5000]
# 		set terminal pdf
# 		set output "$path.pdf" 
# 		set style fill solid border -1
#		plot "${FILE}" using 1:2 with points pointtype 7 pointsize 0.2 lt rgb "red" title ""
# 	EOF
# done
FILE=$1
name=$2
xRange=$3
stats=$4
gnuplot <<- EOF
		set xlabel "Number Of Agents"
		set ylabel "Y_{it}"
		# set yrange[0.00:1.00]
		# set xrange[0:$xRange]
		set auto x
		set style data histogram
		set style histogram cluster gap 1
		set style fill solid border -1
		set boxwidth 0.6
		# set xtic rotate by -45 scale 0
		set terminal pdf enhanced color font ',17'
		set output "${FILE}.pdf" 
		set title "Chan11 Yit: ${name} ${stats}"
		set key off

		# set style data dots pointsize 5
		plot "${FILE}" using 2:xtic(1) #pointtype 7 pointsize 0.2 lt rgb "red" title ""
EOF
