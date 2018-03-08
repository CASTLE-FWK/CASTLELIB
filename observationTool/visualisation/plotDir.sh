#!/bin/bash
DIR=$1
EXPNAME=$2
RED='\033[1;31m'
NC='\033[0m' # No Color

for f in $(ls $DIR/$EXPNAME*.tsv); do
	f=${f##*/}
	PLTDIR=${f%.*}"Plots"
	echo "${RED}Plotting .tsv files in $PLTDIR ${NC}"
	echo "${RED}Making directories for plots in $DIR/$PLTDIR ${NC}"
	mkdir $DIR/$PLTDIR
	python plotter.py $DIR $f 0 1 0 $PLTDIR
	echo  "${RED}Concatenating pdf files into $DIR/${f%.*}${NC}"
	pdfunite $DIR/$PLTDIR/*.pdf $DIR/${f%.*}.pdf
done;
