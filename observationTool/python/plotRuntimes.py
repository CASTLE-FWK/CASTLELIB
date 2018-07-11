import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
import numpy as np
import argparse
import csv
import pprint
import math


parser = argparse.ArgumentParser(description='Plot runtime results')
parser.add_argument('file', metavar="file", type=str, nargs=1, help='the file with the runtimes')
args = parser.parse_args()
fp = args.file[0]

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
		y.append(row[1])
plt.bar(x,y)
plt.xlabel("Configurations")
plt.ylabel("Runtime (s)")
plt.title("Runtimes for FlockOfBirds SG")
plt.legend()
plt.show()

