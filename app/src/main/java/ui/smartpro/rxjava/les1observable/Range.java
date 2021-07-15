package ui.smartpro.rxjava.les1observable;

import android.util.Log;

import io.reactivex.Observable;

public class Range {
    //range
    //Оператор range выдаст последовательность чисел

    //// create observable
    Observable<Integer> observable = Observable.range(10, 4);

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
    //Мы указываем, что начать необходимо с 10, а кол-во элементов 4

    //Результат:
    //onNext: 10
    //onNext: 11
    //onNext: 12
    //onNext: 13
    //onCompleted
}
