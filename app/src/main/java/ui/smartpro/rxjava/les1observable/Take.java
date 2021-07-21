package ui.smartpro.rxjava.les1observable;

import android.util.Log;

public class Take {
//take
//Оператор take возьмет только указанное количество первых элементов из переданной ему
// последовательности и сформирует из них новую последовательность. Возьмем первые три:

// create observable
Observable<Integer> observable = Observable
        .from(new Integer[]{5,6,7,8,9})
        .take(3);

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
//onNext: 5
//onNext: 6
//onNext: 7
//onCompleted
}
