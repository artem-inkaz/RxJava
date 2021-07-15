package ui.smartpro.rxjava.les1observable;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class Interval {
//interval
//Оператор interval выдает последовательность long чисел начиная с 0.
// Мы можем указать временной интервал, через который числа будут приходить.
// Укажем 500 мсек.

// create observable
Observable<Long> observable = Observable.interval(500, TimeUnit.MILLISECONDS);

// create observer
Observer<Long> observer = new Observer<Long>() {
    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: " + e);
    }

    @Override
    public void onNext(Long s) {
        Log.d(TAG, "onNext: " + s);
    }
};

// subscribe
observable.subscribe(observer);
//
//
//Теперь каждые 500 мсек в Observer будет приходить все увеличивающееся значение, начиная с 0.
//
//
//
//Результат:
//onNext: 0
//onNext: 1
//onNext: 2
//onNext: 3
//...
//
//Обратите внимание, что в логах не будет метода onCompleted. Вернее, когда нибудь он, наверно,
// все таки придет, когда достигнет значения, максимально доступного для Long. Но ждать придется долго.
}
