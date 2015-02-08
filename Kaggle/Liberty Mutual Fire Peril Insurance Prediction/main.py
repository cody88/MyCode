import numpy as np
import pandas as pd
from sklearn.linear_model import Ridge,SGDRegressor,ElasticNet
from sklearn.decomposition import KernelPCA
from sklearn import cross_validation,preprocessing
from sklearn.ensemble import AdaBoostRegressor,GradientBoostingRegressor,RandomForestRegressor

def convert_input(x):
	x = x.strip()
	if x=='A':
		return 1
	elif x=='B':
		return 2
	elif x=='C':
		return 3
	elif x=='D':
		return 4
	elif x=='E':
		return 5
	elif x=='F':
		return 6
	elif x=='Z':
		return 0
	elif x=='A1':
		return 1
	elif x=='B1':
		return 2
	elif x=='C1':
		return 3
	elif x=='D1':
		return 4
	elif x=='D2':
		return 5
	elif x=='D3':
		return 6
	elif x=='D4':
		return 7
	elif x=='E1':
		return 8
	elif x=='E2':
		return 9
	elif x=='E3':
		return 10
	elif x=='E4':
		return 11
	elif x=='E5':
		return 12
	elif x=='E6':
		return 13
	elif x=='F1':
		return 14
	elif x=='G1':
		return 15
	elif x=='G2':
		return 16
	elif x=='H1':
		return 17
	elif x=='H2':
		return 18
	elif x=='H3':
		return 19
	elif x=='I1':
		return 20
	elif x=='J1':
		return 21
	elif x=='J2':
		return 22
	elif x=='J3':
		return 23
	elif x=='J4':
		return 24
	elif x=='J5':
		return 25
	elif x=='J6':
		return 26
	elif x=='K1':
		return 27
	elif x=='L1':
		return 28
	elif x=='M1':
		return 29
	elif x=='N1':
		return 30
	elif x=='O1':
		return 31
	elif x=='O2':
		return 32
	elif x=='P1':
		return 33
	elif x=='R1':
		return 34
	elif x=='R2':
		return 35
	elif x=='R3':
		return 36
	elif x=='R4':
		return 37
	elif x=='R5':
		return 38
	elif x=='R6':
		return 39
	elif x=='R7':
		return 40
	elif x=='R8':
		return 41
	elif x=='S1':
		return 42
	else:
		return -2

train = pd.read_csv('train.csv', usecols=['target', 'var4', 'var7', 'var8', 'var9', 'var10', 'var11', 'var13', 'var15', 'var17', 'geodemVar1', 'geodemVar2', 'geodemVar3', 'geodemVar4', 'geodemVar31', 'geodemVar32', 'weatherVar1', 'weatherVar103', 'weatherVar153'], converters={'var4':convert_input, 'var7':convert_input, 'var8':convert_input, 'var9':convert_input})

test = pd.read_csv('test.csv', usecols=['var4', 'var7', 'var8', 'var9', 'var10', 'var11', 'var13', 'var15', 'var17', 'geodemVar1', 'geodemVar2', 'geodemVar3', 'geodemVar4', 'geodemVar31', 'geodemVar32', 'weatherVar1', 'weatherVar103', 'weatherVar153'], converters={'var4':convert_input, 'var7':convert_input, 'var8':convert_input, 'var9':convert_input})


sample = pd.read_csv('sampleSubmission.csv', low_memory=False)
 
tr = train[['var4', 'var7', 'var8', 'var9', 'var10', 'var11', 'var13', 'var15', 'var17', 'geodemVar1', 'geodemVar2', 'geodemVar3', 'geodemVar4', 'geodemVar31', 'geodemVar32', 'weatherVar1', 'weatherVar103', 'weatherVar153']]
ts = test[['var4', 'var7', 'var8', 'var9', 'var10', 'var11', 'var13', 'var15', 'var17', 'geodemVar1', 'geodemVar2', 'geodemVar3', 'geodemVar4', 'geodemVar31', 'geodemVar32', 'weatherVar1', 'weatherVar103', 'weatherVar153']]
 
tr = np.nan_to_num(np.array(tr))
ts = np.nan_to_num(np.array(ts))

#PCA
scaler = preprocessing.StandardScaler()
scaler.fit(tr)
tr = scaler.transform(tr)
ts = scaler.transform(ts)
#pca = KernelPCA(n_components=20, kernel='rbf')
#tr = pca.fit_transform(tr)

clf = GradientBoostingRegressor(verbose=1)
#clf = AdaBoostRegressor(Ridge(), n_estimators=200, learning_rate=0.1)
#clf = SGDRegressor(alpha=0.1, eta0=0.000001, shuffle=True, n_iter=10)
#scores = cross_validation.cross_val_score(clf, tr, train['target'].values, cv=800, scoring='mean_squared_error', n_jobs=-1, verbose=4)
#print("Accuracy: %0.2f (+/- %0.2f)" % (scores.mean(), scores.std() * 2))
clf.fit(tr, train['target'].values)
print clf.feature_importances_
preds = clf.predict(ts)
 
sample['target'] = preds
 
sample.to_csv('submission2.csv', index = False)
