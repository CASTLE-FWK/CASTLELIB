from __future__ import print_function
import numpy as np
from sklearn.cluster import MeanShift, KMeans, estimate_bandwidth
import csv
import argparse
import pandas as pd
import matplotlib.colors as colors
from time import time
from sklearn.preprocessing import StandardScaler
from sklearn.cluster import Birch, MiniBatchKMeans, AffinityPropagation
from sklearn import metrics, svm
from sklearn.cross_validation import train_test_split
from itertools import cycle
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from sklearn.decomposition import PCA
from sklearn.metrics import precision_recall_curve
from sklearn.metrics import average_precision_score


def meanshift(X, y):
	#Lets try this MeanShift thing
	bandwidth = estimate_bandwidth(X, quantile=0.2, n_samples=2500)

	ms = MeanShift(bandwidth=bandwidth, bin_seeding=False)
	ms.fit(X)
	labels = ms.labels_
	cluster_centers = ms.cluster_centers_

	labels_unique = np.unique(labels)
	n_clusters_ = len(labels_unique)
	print("Number of estimated clusters: %d",  n_clusters_)

	plt.figure(1)
	plt.clf()

	for cc in cluster_centers:
		print(cc)

	colors = cycle('bgrcmykbgrcmykbgrcmykbgrcmyk')
	for k, col in zip(range(n_clusters_), colors):
			my_members = labels == k
			cluster_center = cluster_centers[k]
			plt.plot(X[my_members,0], X[my_members, 1], col + '.')
			plt.plot(cluster_center[0], cluster_center[1], 'o',markerfacecolor=col,
				markeredgecolor='k', markersize=14)
	plt.title("Estimated number of clusters: %d" % n_clusters_)
	plt.show()
def kmeans(X, y):
	##lets try this kmeans thing
	np.random.seed(5)
	estimators = {'k_means_em_2' : KMeans(n_clusters=2),
				  'k_means_em_8' : KMeans(n_clusters=8),
				  'k_means_iris_bad_init': KMeans(n_clusters=3, n_init=1,
	                 								init='random')}

	fignum = 1
	for name, est in estimators.items():
		# 
		fig = plt.figure(fignum, figsize=(12,9))
		plt.clf()
		print(name)
		
		ax = Axes3D(fig, rect=[0,0,0.95,1], elev=48, azim=134)

		plt.cla()
		est.fit(X)
		labels = est.labels_

		ax.scatter(X[:, 0], X[:, 1], X[:, 2], c=labels.astype(np.float))

		ax.w_xaxis.set_ticklabels([])
		ax.w_yaxis.set_ticklabels([])
		ax.w_zaxis.set_ticklabels([])
		ax.set_xlabel('SC')
		ax.set_ylabel('CE')
		ax.set_zlabel('WAT')
		plt.title(name)
		fignum = fignum + 1


	fig = plt.figure(fignum, figsize=(12, 9))
	plt.clf()
	ax = Axes3D(fig, rect=[0, 0, .95, 1], elev=48, azim=134)
	plt.title(name)
	plt.cla()

	for name, label in [('No Emergence',0), ('Emergence Detected',1)]:
		plt.title("Result")
		ax.text3D(X[y == label, 0].mean(),
	             X[y == label, 1].mean() + 1.5,
	             X[y == label, 2].mean(), name,
	             horizontalalignment='center',
	             bbox=dict(alpha=.5, edgecolor='w', facecolor='w'))

	y = np.choose(y, [0, 1, 2]).astype(np.float)
	ax.scatter(X[:, 0], X[:, 1], X[:, 2], c=y)

	ax.w_xaxis.set_ticklabels([])
	ax.w_yaxis.set_ticklabels([])
	ax.w_zaxis.set_ticklabels([])
	ax.set_xlabel('SC')
	ax.set_ylabel('CE')
	ax.set_zlabel('WAT')
	plt.show()
def birch_minibatch(X, y):
	#Lets try Birch and MiniBatchKMeans
	#Use all colors that matplotlib provides by default.
	colors_ = cycle(colors.cnames.keys())

	fig = plt.figure(figsize=(12, 4))
	fig.subplots_adjust(left=0.04, right=0.98, bottom=0.1, top=0.9)

	# Compute clustering with Birch with and without the final clustering step
	# and plot.
	birch_models = [Birch(threshold=1.7, n_clusters=None),
	                Birch(threshold=1.7, n_clusters=100)]
	final_step = ['without global clustering', 'with global clustering']

	for ind, (birch_model, info) in enumerate(zip(birch_models, final_step)):
	    t = time()
	    birch_model.fit(X)
	    time_ = time() - t
	    print("Birch %s as the final step took %0.2f seconds" % (
	          info, (time() - t)))

	    # Plot result
	    labels = birch_model.labels_
	    centroids = birch_model.subcluster_centers_
	    n_clusters = np.unique(labels).size
	    print("n_clusters : %d" % n_clusters)

	    ax = fig.add_subplot(1, 3, ind + 1)
	    for this_centroid, k, col in zip(centroids, range(n_clusters), colors_):
	        mask = labels == k
	        ax.plot(X[mask, 0], X[mask, 1], 'w',
	                markerfacecolor=col, marker='.')
	        if birch_model.n_clusters is None:
	            ax.plot(this_centroid[0], this_centroid[1], '+', markerfacecolor=col,
	                    markeredgecolor='k', markersize=5)
	    ax.set_ylim([-25, 25])
	    ax.set_xlim([-25, 25])
	    ax.set_autoscaley_on(False)
	    ax.set_title('Birch %s' % info)

	# Compute clustering with MiniBatchKMeans.
	mbk = MiniBatchKMeans(init='k-means++', n_clusters=100, batch_size=100,
	                      n_init=10, max_no_improvement=10, verbose=0,
	                      random_state=0)
	t0 = time()
	mbk.fit(X)
	t_mini_batch = time() - t0
	print("Time taken to run MiniBatchKMeans %0.2f seconds" % t_mini_batch)
	mbk_means_labels_unique = np.unique(mbk.labels_)

	ax = fig.add_subplot(1, 3, 3)
	for this_centroid, k, col in zip(mbk.cluster_centers_,
	                                 range(n_clusters), colors_):
	    mask = mbk.labels_ == k
	    ax.plot(X[mask, 0], X[mask, 1], 'w', markerfacecolor=col, marker='.')
	    ax.plot(this_centroid[0], this_centroid[1], '+', markeredgecolor='k',
	            markersize=5)
	ax.set_xlim([-25, 25])
	ax.set_ylim([-25, 25])
	ax.set_title("MiniBatchKMeans")
	ax.set_autoscaley_on(False)
	plt.show()
def affprop(X, labels_true):
	##############################################################################
	# Compute Affinity Propagation
	af = AffinityPropagation(preference=-50).fit(X)
	cluster_centers_indices = af.cluster_centers_indices_
	labels = af.labels_
	print(len(labels))

	n_clusters_ = len(cluster_centers_indices)

	print('Estimated number of clusters: %d' % n_clusters_)
	print("Homogeneity: %0.3f" % metrics.homogeneity_score(labels_true, labels))
	print("Completeness: %0.3f" % metrics.completeness_score(labels_true, labels))
	print("V-measure: %0.3f" % metrics.v_measure_score(labels_true, labels))
	print("Adjusted Rand Index: %0.3f"
	      % metrics.adjusted_rand_score(labels_true, labels))
	print("Adjusted Mutual Information: %0.3f"
	      % metrics.adjusted_mutual_info_score(labels_true, labels))
	print("Silhouette Coefficient: %0.3f"
	      % metrics.silhouette_score(X, labels, metric='sqeuclidean'))

	##############################################################################
	# Plot result
	plt.close('all')
	plt.figure(1)
	plt.clf()

	colors = cycle('bgrcmykbgrcmykbgrcmykbgrcmyk')
	for k, col in zip(range(n_clusters_), colors):
	    class_members = labels == k
	    cluster_center = X[cluster_centers_indices[k]]
	    plt.plot(X[class_members, 0], X[class_members, 1], col + '.')
	    plt.plot(cluster_center[0], cluster_center[1], 'o', markerfacecolor=col,
	             markeredgecolor='k', markersize=14)
	    for x in X[class_members]:
	        plt.plot([cluster_center[0], x[0]], [cluster_center[1], x[1]], col)

	plt.title('Estimated number of clusters: %d' % n_clusters_)
	plt.show()
def nonlinearSVM(training_data, training_labels, testing_data, testing_labels):

	# xx, yy = np.meshgrid(np.linspace(-3, 3, 500),
 #                     np.linspace(-3, 3, 500))
	# fit the model
	clf = svm.NuSVC()
	clf.fit(training_data, training_labels)

	# plot the decision function for each datapoint on the grid
	Z = clf.decision_function(testing_data)
	# Z = Z.reshape(testing_data.shape)
	print(Z)
	# exit()
	plt.imshow(Z, interpolation='nearest',
	           extent=(testing_data.min(), testing_data.max(), testing_data.min(), testing_data.max()), aspect='auto',
	           origin='lower', cmap=plt.cm.PuOr_r)
	contours = plt.contour(testing_data, testing_data, Z, levels=[0], linewidths=2,
	                       linetypes='--')
	plt.scatter(X[:, 0], X[:, 1], s=30, c=Y, cmap=plt.cm.Paired)
	plt.xticks(())
	plt.yticks(())
	plt.axis([-3, 3, -3, 3])
	plt.show()

def prc(X,y):
	n_metrics = len(X[0,:])
	print(n_metrics)
	precision = dict()
	recall = dict()
	thresholds = dict()
	average_precision = dict()
	for i in range(n_metrics):
		precision[i], recall[i], thresholds[i] = precision_recall_curve(y, X[:,i])
		average_precision[i] = average_precision_score(y, X[:,i])

	for i in range(n_metrics):
		print(thresholds[i])

	y_big = np.tile(y,(n_metrics,1))
	
	precision["micro"], recall["micro"], _ = precision_recall_curve(y_big.ravel(), X.ravel())
	
	
	
	average_precision["micro"] = average_precision_score(y_big, np.transpose(X), average="micro")
	plt.clf()
	plt.plot(recall[0], precision[0], label='Precision-Recall curve')
	plt.xlabel('Recall')
	plt.ylabel('Precision')
	plt.ylim([0.0, 1.05])
	plt.xlim([0.0, 1.0])
	plt.title('Precision-Recall example: AUC={0:0.2f}'.format(average_precision[0]))
	plt.legend(loc="upper right")
	plt.show()

	# Plot Precision-Recall curve for each class
	plt.clf()
	# plt.plot(recall["micro"], precision["micro"],label='micro-average Precision-recall curve (area = {0:0.2f})'''.format(average_precision["micro"]))
	for i in range(n_metrics):
		plt.plot(recall[i], precision[i], label='Precision-recall curve of class {0} (area = {1:0.2f})'''.format(i, average_precision[i]))

	plt.xlim([0.0, 1.0])
	plt.ylim([0.0, 1.05])
	plt.xlabel('Recall')
	plt.ylabel('Precision')
	plt.title('Extension of Precision-Recall curve to multi-class')
	plt.legend(loc="upper right")
	plt.show()


#Because we like being nice
parser = argparse.ArgumentParser()
parser.add_argument('-f','--files',nargs='+',type=str,required=True)
args = parser.parse_args()
X = None
y = None
em = 1
st = 2
cr = 3

currTest = cr
#For each file specified, run the clustering
files = args.files
for aFile in files:
	print(aFile)
   	data_file = pd.read_csv(aFile, header = 0, delimiter = "\t")
	original_headers = list(data_file.columns.values)
	
	numpy_array = data_file.values

	#I should dictionary this, it'd be nicer
	#X_b = numpy_array[:,(7,15,16)]
	# X_b = numpy_array[:, (6,7,9,10,11,13,14,15,16,17,18,19,20,27,28,29)]
	X_b = numpy_array[:, 6:30]
	tmpX = X_b[:,:]
	if (y is None):
		y = numpy_array[:, currTest].astype(int)
		y = abs(y)
	else:
		tY = abs(numpy_array[:, currTest].astype(int))
		y = np.concatenate((y,tY))

	for j, it in enumerate(X_b[:,0]):
		for i, item in enumerate(X_b[j,:]):
			if (i > 0):
				x = abs(X_b[j,i] - X_b[j-1,i])
				if x == 0:
					tmpX[j,i] = 0
				else:
					tmpX[j,i] = x
	if (X is not None):
		X = np.concatenate((X,tmpX))
	else:
		X = tmpX

# pca = PCA(n_components=3)
# X_p = pca.fit_transform(X)

# training_data, testing_data, training_labels, testing_labels = train_test_split(X,y, test_size=0.2, random_state=42)
#kmeans(X,y)
# kmeans(X_p,y)
# meanshift(X,y)
# affprop(X,y)
#nonlinearSVM(training_data,training_labels,testing_data,testing_labels)
prc(X,y)


print("le finished")

