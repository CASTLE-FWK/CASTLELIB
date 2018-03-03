import matplotlib.pyplot as plt
import numpy as np
import argparse
import csv
import pprint

#0: parse cli args

parser = argparse.ArgumentParser(description='Plot metric results from the Observation Tool')
parser.add_argument('file', metavar="file", type=str, nargs=1, help='the file with the results')
parser.add_argument('steps_column', metavar="SC", type=int, nargs=1, help='the column that has the step counter')
parser.add_argument('starting_column', metavar="MSC", type=int, nargs=1, help='the column where the metric results start')
parser.add_argument('header_row', metavar="HR", type=int, nargs=1, help="the row with the header information")
args = parser.parse_args()
#1: Get file

# print `args.file[0]`
fp = args.file[0]
sc = args.steps_column[0]
msc = args.starting_column[0]
hr = args.header_row[0]

with open(fp, 'rb') as tsvin:
	#a: check its a TSV
	if not (fp.endswith(".tsv")):
		print `'File is not a tsv. Exiting...'`
		exit()

	tsvin = csv.reader(tsvin, delimiter='\t')
	rowcount = 0
	datadict = {}
	datamap = {}
	for row in tsvin:
		#2: store into dict
		#a: row 0 is useful information
		if rowcount == hr:
			colcount = 0
			for item in row:
				if (len(item) == 0):
					continue
				item = item.replace("#","")		
				if colcount == sc:
					datadict[item] = []
					datamap[colcount] = item
				elif colcount >= msc:
					datadict[item] = []
					datamap[colcount] = item

				colcount = colcount + 1
		else:
			colcount = 0;
			for item in row:
				#get dict thing
				if colcount in datamap:
					dictLoc = datamap[colcount]
					datadict[dictLoc].append(item)



				colcount = colcount + 1

		rowcount = rowcount + 1
	pprint.pprint(datadict)	
	pprint.pprint(datamap)






# fig = plt.figure()
# fig.suptitle('asd')
# fig, ax_list = plt.subplots(2,2)
# plt.show()