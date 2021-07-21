package ui.smartpro.rxjava.les1observable;

import android.util.Log;

public class Merge {
//Операторы объединения
//
//
//merge
//Оператор merge объединит элементы из двух Observable в один Observable

// create observable
Observable<Integer> observable = Observable
        .from(new Integer[]{1,2,3})
        .mergeWith(Observable.from(new Integer[]{6,7,8,9}));

// create observer
Observer<Integer> observer = new Observer<Integer>() {
    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: " + e);
    }

    @Override
    public void onNext(Integer s) {
        Log.d(TAG, "onNext: " + s);
    }
};

// subscribe
observable.subscribe(observer);
//
//
//Результат:
//onNext: 1
//onNext: 2
//onNext: 3
//onNext: 6
//onNext: 7
//onNext: 8
//onNext: 9
//onCompleted
//
//
//
//Есть еще оператор concat, который делает примерно то же самое, но чуть по другому.
}
