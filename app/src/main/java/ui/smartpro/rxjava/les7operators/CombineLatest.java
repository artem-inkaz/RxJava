package ui.smartpro.rxjava.les7operators;

import java.util.concurrent.TimeUnit;

import rx.functions.Func1;
import rx.functions.Func2;

public class CombineLatest {
//combineLatest
//Похож на zip - также берет элементы из нескольких Observable и собирает из них один.
// Но отличается тем, что не ждет, когда придет самый медленный элемент пары,
// а просто берет последние полученные элементы с каждого Observable каждый раз когда придет
// новый элемент из любого Observable.
//
//Пример:

Observable<Long> observable1 = Observable.interval(300, TimeUnit.MILLISECONDS)
        .take(10);

Observable<Long> observable2 = Observable.interval(500, TimeUnit.MILLISECONDS)
        .take(10)
        .map(new Func1<Long, Long>() {
            @Override
            public Long call(Long aLong) {
                return aLong + 100;
            }
        });

Observable
        .combineLatest(observable1, observable2, new Func2<Long, Long, String>() {
            @Override
            public String call(Long aLong, Long aLong2) {
                return String.format("%s and %s", aLong, aLong2);
            }
        })
        .subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                log("onCompleted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String next) {
                log("onNext " + next);
            }
        });
//
//
//Результат:
//500 onNext 0 and 100
//600 onNext 1 and 100
//900 onNext 2 and 100
//1000 onNext 2 and 101
//1200 onNext 3 and 101
//1500 onNext 4 and 101
//1500 onNext 4 and 102
//1800 onNext 5 and 102
//2000 onNext 5 and 103
//2100 onNext 6 and 103
//2400 onNext 7 and 103
//2500 onNext 7 and 104
//2700 onNext 8 and 104
//3000 onNext 9 and 104
//3000 onNext 9 and 105
//3500 onNext 9 and 106
//4000 onNext 9 and 107
//4500 onNext 9 and 108
//5000 onNext 9 and 109
//5000 onCompleted
//
//
//По временным меткам видно, что combineLatest срабатывает каждый раз когда приходит элемент из
// какого-либо из двух Observable. А по значениям видно,
// что в функцию передаются последние имеющиеся на этот момент элементы.
//
//Обратите внимание, что функция не сработала, когда observable1 выдал первый элемент через 300 мсек.
// Так произошло, потому что observable2 еще не выдал на тот момент никакого значения, а,
// следовательно, функция не смогла взять его последнее значение - его просто еще не было.
}
