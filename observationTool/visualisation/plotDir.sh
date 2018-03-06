#!/bin/bash
DIR=$1
EXPNAME=$2
for f in $(ls $DIR/$EXPNAME*.tsv); do
	f=${f##*/}
	PLTDIR=${f%.*}"Plots"
	#echo $PLTDIR
	mkdir $DIR/$PLTDIR
	python plotter.py $DIR $f 0 1 0 $PLTDIR
	pdfunite $DIR/$PLTDIR/*.pdf $DIR/${f%.*}.pdf
#	echo ${f%.*}
done;