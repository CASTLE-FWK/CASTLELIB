FILE=$1
name=$2
xRange=$3
yRange=$4
stats=$5
stepNumber=$(echo ${FILE%.tsv} | awk '{ split($0,a,"_"); print a[3]}')
gnuplot <<- EOF
		set xlabel "X"
		set ylabel "Y"
		# set yrange[0.00:25]
		# set xrange[0.00:25]
		set xrange[0:$xRange-1]
		set yrange[0:$yRange-1]
		set terminal pdf enhanced color font ',17'
		set output "${FILE}.pdf" 
		set title "T and T: $(echo $name | sed 's/\_/\-/g').pdf ${stats} Step:${stepNumber}"
		set key off
		set mxtics 5
		set mytics 5
		set grid ytics mxtics lt 1 lw 1 lc rgb "#bbbbbb"
		set grid xtics mytics lt 1 lw 1 lc rgb "#bbbbbb"

		plot "${FILE}" using 2:3:(column(4)/2):(column(5)/2) w boxxyerrorbars lw 2
		# set style data dots pointsize 5
		#plot "${FILE}" using 1:4 with lines, \
		#"" using 1:(column(3) * 0.5) with points pointtype 7 pointsize 0.1 #pointtype 7 pointsize 0.2 lt rgb "red" title ""
EOF
