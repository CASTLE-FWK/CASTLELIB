import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
import numpy as np
import argparse
import csv
import pprint
import math


parser = argparse.ArgumentParser(description='Plot runtime results')
parser.add_argument('file', metavar="file", type=str, nargs=1, help='the file with the runtimes')
parser.add_argument('sysname', metavar="sysname", type=str, nargs=1, help='the name of the system')
args = parser.parse_args()
fp = args.file[0]
sysname = args.sysname[0]

#make path
path=fp.replace(".tsv",".pdf")
if path == fp:
	print "nope"
	exit()

print path

x = []
y = []
with open(fp, 'rb') as tsvin:
	#a: check its a TSV
	if not (fp.endswith(".tsv")):
		print `'File is not a tsv. Exiting...'`
		exit()

	tsvin = csv.reader(tsvin, delimiter='\t')

	for row in tsvin:
		x.append(row[0])
		y.append(float(row[1]))
# plt.bar(x,y)
fig = plt.figure(figsize=(11.69, 8.27))
ax = fig.add_subplot(111)


ax.bar(range(len(x)), y, tick_label=x)
ax.set_xlabel("Configurations")
ax.set_ylabel("Runtime (s)")
ax.set_title("Runtimes for "+sysname)
# ax.legend()
# ax.show()
plt.savefig(path, bbox_inches='tight')
fig.clf()
plt.close()

