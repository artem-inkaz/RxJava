package ui.smartpro.rxjava.les7operators;

import java.util.concurrent.TimeUnit;

import rx.functions.Func1;

public class Amb {
//amb
//amb берет несколько Observable и ждет, кто из них первый пришлет данные.
// Далее, оператор будет возвращать элементы только из этого первого Observable.
//
//Пример:

Observable<Long> observable1 = Observable.interval(1000, 300, TimeUnit.MILLISECONDS)
        .take(10);

Observable<Long> observable2 = Observable.interval(700, 500, TimeUnit.MILLISECONDS)
        .take(10)
        .map(new Func1<Long, Long>() {
            @Override
            public Long call(Long aLong) {
                return aLong + 100;
            }
        });

Observable.amb(observable1, observable2)
        .subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
                log("onCompleted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long next) {
                log("onNext " + next);
            }
        });
//observable1 стартует через 1000 мсек, а observable2 через 700. Оба этих Observable передаем в amb.
//
//Результат
//700 onNext 100
//1200 onNext 101
//1700 onNext 102
//2200 onNext 103
//2700 onNext 104
//3200 onNext 105
//3700 onNext 106
//4200 onNext 107
//4700 onNext 108
//5200 onNext 109
//5200 onCompleted
//
//
//observable2 сработал первый, и мы получаем только его элементы. А observable1 проигнорирован.
}
