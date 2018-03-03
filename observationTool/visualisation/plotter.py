import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
import numpy as np
import argparse
import csv
import pprint
import math

#0: parse cli args

parser = argparse.ArgumentParser(description='Plot metric results from the Observation Tool')
parser.add_argument('directory', metavar="dir", type=str, nargs=1, help="the directory that stores the results files and where the plots will be stored")
parser.add_argument('file', metavar="file", type=str, nargs=1, help='the file with the results')
parser.add_argument('steps_column', metavar="SC", type=int, nargs=1, help='the column that has the step counter')
parser.add_argument('starting_column', metavar="MSC", type=int, nargs=1, help='the column where the metric results start')
parser.add_argument('header_row', metavar="HR", type=int, nargs=1, help="the row with the header information")
parser.add_argument('save_dir', metavar="sd", type=str, nargs=1, help="save directory for plots")
args = parser.parse_args()
#1: Get file

# print `args.file[0]`
direc = args.directory[0]
fp = direc+"/"+args.file[0]
sd = direc+"/"+args.save_dir[0]
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
					if (item == "#runtime"):
						continue
					dictLoc = datamap[colcount]
					datadict[dictLoc].append(float(item))
				colcount = colcount + 1
		rowcount = rowcount + 1
print `"metric result file parsed. now to graph"`

stepsList = datadict["step"]

for key in datadict:
	if key == "step":
		continue
	keyNWS = ''.join(key.split())
	print("plotting "+key)
	x = datadict[key]
	fig = plt.figure(figsize=(11.69, 8.27))
	ax = fig.add_subplot(111)
	ax.set_title(key)
	ax.plot(x, label=key)
	
	print("\tmin is "+str(min(x)))
	print("\tmax is "+str(max(x)))
	rge = (max(x)-min(x))+1
	ss = rge / 5
	# print `ss`
	if (math.isnan(ss)):
		print("\tCan't plot due to NaN.")
		fig.clf()
		continue
	if (math.isinf(ss)):
		print("\tCan't plot due to Infinity.")
		fig.clf()
		continue
	# print`np.arange(min(x), max(x)+1, ss)`

	ax.yaxis.set_ticks(np.arange(min(x), max(x)+1, ss))
	
	# loc = plticker.MultipleLocator(base=1.0)
	# ax.xaxis.set_major_locator(loc)

	ax.yaxis.set_major_formatter(ticker.FormatStrFormatter('%0.3f'))
	# plt.xlim(stepsList)
	ax.set_xlabel("Steps")
	ax.set_ylabel(key)
	
	path = sd
	path+="/"+keyNWS+".pdf"
	path = path.replace("\\s+","")
	# fig.legend()
	fig.savefig(path, bbox_inches='tight')
	fig.clf()
	plt.close()

print("Finished plotting. Plot files are in "+sd)




# fig = plt.figure()
# fig.suptitle('asd')
# fig, ax_list = plt.subplots(2,2)
# plt.show()