package ui.smartpro.rxjava.les1observable;

import android.util.Log;

public class All {
//all
//Оператор all позволяет узнать все ли элементы удовлетворяют указанному условию. Условие нам необходимо оформить в виде функции.
//Например, создадим проверку, что все элементы меньше 10.

Func1<Integer, Boolean> lessThanTen = new Func1<Integer, Boolean>() {
    @Override
    public Boolean call(Integer i) {
        return i < 10;
    }
};
//
//
//Применим ее к последовательности чисел

// create observable
Observable<Boolean> observable = Observable
        .from(new Integer[]{1,2,3,4,5,6,7,8})
        .all(lessThanTen);

// create observer
Observer<Boolean> observer = new Observer<Boolean>() {
    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: " + e);
    }

    @Override
    public void onNext(Boolean s) {
        Log.d(TAG, "onNext: " + s);
    }
};

// subscribe
observable.subscribe(observer);
//
//
//В результате мы получим Boolean последовательность из одного элемента.
// Этот элемент скажет нам, все ли элементы последовательности подошли под условие.
//
//Результат:
//onNext: true
//onCompleted
}
