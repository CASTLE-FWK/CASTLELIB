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
gnuplot <<- EOF
		set xlabel "Steps"
		set ylabel "Number of registered state changes" offset 2
		# set yrange[0.00:1.00]
		set xrange[0:5000]
		set terminal pdf enhanced color font ',17'
		set output "${FILE}_${i}.pdf" 
		# set style data dots pointsize 5
		plot "${FILE}" using 1:$i with points pointtype 7 pointsize 0.2 lt rgb "red" title ""
EOF
