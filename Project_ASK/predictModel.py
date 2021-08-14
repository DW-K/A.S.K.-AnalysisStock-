import os

from sklearn.metrics import accuracy_score
from sklearn.preprocessing import StandardScaler
from sklearn.svm import SVC
import pandas as pd
import numpy as np

from sklearn.model_selection import KFold

from combineDataset import getData

from Path import RESULT_PATH_COMBINE, RESULT_PATH_MODEL

from keras.models import Sequential
from keras.layers import Dense
from keras.callbacks import EarlyStopping, ModelCheckpoint
from keras.layers import LSTM


filePath_n = "202184"
filePath_s = "20210812"
fileName_s = rf"와이지엔터테인먼트_20210812_s.xlsx"
fileName_n = rf"YG엔터_2021812_n.xlsx"


def predict():          # 평균검증 정확도 :   0.5454200000000001
    fileName = 'result'
    try:
        df = pd.read_excel(rf'{RESULT_PATH_COMBINE}\{fileName}.xlsx')
    except Exception as e:
        print(e)
        df = getData(filePath_n, fileName_n, filePath_s, fileName_s)
    
    df.fillna(method='bfill', inplace=True)   # 결측치 전날 값으로 채우기
    
    X = df[df.columns.difference(['isUp'])]
    y = df['isUp'].astype(int)
    sc = StandardScaler()
    X_std = sc.fit_transform(X)

    kfold = KFold(n_splits=5)
    cv_accuracy = []
    n_iter = 0
    svm_model = SVC(kernel='rbf', C=8, gamma=0.1)
    for train_idx, test_idx in kfold.split(X_std):
        X_train, X_test = X_std[train_idx], X_std[test_idx]
        y_train, y_test = y[train_idx], y[test_idx]

        svm_model.fit(X_train, y_train)
        # 예측
        fold_pred = svm_model.predict(X_test)

        # 정확도 측정
        n_iter += 1
        accuracy = np.round(accuracy_score(y_test, fold_pred), 4)
        print(f'\n{n_iter} 교차검증 정확도 : {accuracy} , 학습 데이터 크기 : {X_train.shape[0]} , 검증 데이터 크기 : {X_test.shape[0]}')
        cv_accuracy.append(accuracy)

    print('\n평균검증 정확도 :  ', np.mean(cv_accuracy))


def predict2():     # 평균검증 정확도 :   0.51998
    fileName = 'result'
    try:
        df = pd.read_excel(rf'{RESULT_PATH_COMBINE}\{fileName}.xlsx')
    except Exception as e:
        print(e)
        df = getData(filePath_n, fileName_n, filePath_s, fileName_s)

    df.fillna(method='bfill', inplace=True)  # 결측치 전날 값으로 채우기

    X = df[df.columns.difference(['isUp'])]
    y = df['isUp'].astype(int)
    sc = StandardScaler()
    X_std = sc.fit_transform(X)

    kfold = KFold(n_splits=5)
    cv_accuracy = []
    n_iter = 0
    svm_model = SVC(kernel='rbf', C=8, gamma=0.2)
    for train_idx, test_idx in kfold.split(X_std):
        X_train, X_test = X_std[train_idx], X_std[test_idx]
        y_train, y_test = y[train_idx], y[test_idx]

        svm_model.fit(X_train, y_train)
        # 예측
        fold_pred = svm_model.predict(X_test)

        # 정확도 측정
        n_iter += 1
        accuracy = np.round(accuracy_score(y_test, fold_pred), 4)
        print(f'\n{n_iter} 교차검증 정확도 : {accuracy} , 학습 데이터 크기 : {X_train.shape[0]} , 검증 데이터 크기 : {X_test.shape[0]}')
        cv_accuracy.append(accuracy)

    print('\n평균검증 정확도 :  ', np.mean(cv_accuracy))


def predict3():
    fileName = 'result'
    try:
        df = pd.read_excel(rf'{RESULT_PATH_COMBINE}\{fileName}.xlsx')
    except Exception as e:
        print(e)
        df = getData(filePath_n, fileName_n, filePath_s, fileName_s)

    df.fillna(method='bfill', inplace=True)  # 결측치 전날 값으로 채우기

    X = df[df.columns.difference(['isUp'])]
    y = df['isUp'].astype(int)
    sc = StandardScaler()
    X_std = sc.fit_transform(X)

    kfold = KFold(n_splits=5)
    cv_accuracy = []
    n_iter = 0
    svm_model = SVC(kernel='rbf', C=8, gamma=0.2)
    for train_idx, test_idx in kfold.split(X_std):
        X_train, X_test = X_std[train_idx], X_std[test_idx]
        y_train, y_test = y[train_idx], y[test_idx]

        model = Sequential()
        model.add(LSTM(16,
                       input_shape=(X_std.shape[1], X_std.shape[2]),
                       activation='relu',
                       return_sequences=False)
                  )
        model.add(Dense(1))

        model.compile(loss='mean_squared_error', optimizer='adam')
        early_stop = EarlyStopping(monitor='val_loss', patience=5)
        filename = os.path.join(RESULT_PATH_MODEL, 'tmp_checkpoint.h5')
        checkpoint = ModelCheckpoint(filename, monitor='val_loss', verbose=1, save_best_only=True, mode='auto')

        history = model.fit(X_train, y_train,
                            epochs=200,
                            batch_size=16,
                            validation_data=(X_test, y_test),
                            callbacks=[early_stop, checkpoint])
        # weight 로딩
        model.load_weights(filename)
        # 예측
        fold_pred = model.predict(X_test)

        # 정확도 측정
        n_iter += 1
        accuracy = np.round(accuracy_score(y_test, fold_pred), 4)
        print(f'\n{n_iter} 교차검증 정확도 : {accuracy} , 학습 데이터 크기 : {X_train.shape[0]} , 검증 데이터 크기 : {X_test.shape[0]}')
        cv_accuracy.append(accuracy)

    print('\n평균검증 정확도 :  ', np.mean(cv_accuracy))


if __name__ == "__main__":
    predict3()
