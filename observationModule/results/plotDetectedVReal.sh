#!/bin/bash
SYSTEM_NAME=$1
CURR_PATH=${PWD}
COUNT=0
for DIR in ${SYSTEM_NAME}/*/
do
	for FILE in ${DIR}best/*.tsv	
	do
		#Strip data filename of path and extension
		F_NAME=$(basename "$FILE")
		F_NAME_NO_EXT="${F_NAME%.*}"
		
		# Add charts directory if not existing
		mkdir -p "${DIR}charts/"
		
		#Say what's happening
		echo "Plotting " $FILE "in ${DIR}charts/${F_NAME_NO_EXT}.pdf"
		TITLE=`echo "$F_NAME_NO_EXT" | tr '_' '-'`
		gnuplot <<- EOF
			set xlabel "Steps"
			set ylabel "Event Detected"
			set ytics("No Event" 0, "Event" 1)
			# set yrange[0.00:1.00]
			set xrange[0:1000]
			# set terminal pdf enhanced color font ',17' size 29cm,10cm
			set terminal pdf color font ',17' size 29cm,10cm
			set output "${DIR}charts/${F_NAME_NO_EXT}.pdf" 
			set title "${F_NAME_NO_EXT}"
			set key right center

			# set style fill solid border -1
			plot "${FILE}" using 1:(column(2)) with points pointtype 8 pointsize 1.0 title "Real Events", \
			"" using 1:(column(3)+0.03) with points pointtype 10 pointsize 1.0 title "Detected Events"			
		EOF
		COUNT=$((COUNT+1))
	done
done
echo $COUNT "charts plotted"
