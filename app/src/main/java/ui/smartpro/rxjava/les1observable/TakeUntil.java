package ui.smartpro.rxjava.les1observable;

import android.util.Log;

public class TakeUntil {
//Операторы условий
//
//takeUntil
//Оператор takeUntil будет брать элементы пока не попадется элемент, удовлетворяющий определенному
// условию. Это условие нам необходимо оформить в виде функции.
//
//Например, создадим условие, что элемент равен 5.

Func1<Integer, Boolean> isFive = new Func1<Integer, Boolean>() {
    @Override
    public Boolean call(Integer i) {
        return i == 5;
    }
};
И используем в операторе

// create observable
Observable<Integer> observable = Observable
        .from(new Integer[]{1,2,3,4,5,6,7,8})
        .takeUntil(isFive);

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
//Новая последовательность содержит те же элементы, но заканчивается на элементе 5.
//
//
//
//Результат:
//onNext: 1
//onNext: 2
//onNext: 3
//onNext: 4
//onNext: 5
//onCompleted
}
