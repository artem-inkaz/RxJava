package ui.smartpro.rxjava.les7operators;

import java.util.concurrent.TimeUnit;

import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;

public class WithLatestFrom {
//withLatestFrom
//Также аналог zip, но ориентируется не на самый медленный Observable, а на основной Observable,
// к которому этот оператор и был применен.
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


observable1
        .withLatestFrom(observable2, new Func2<Long, Long, String>() {
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
//Мы используем оператор withLatestFrom для observable1.
// В оператор передаем observable2 и функцию.
// Функция будет срабатывать каждый раз, когда придет новый элемент из observable1.
// А из observable2 просто будет забираться последний полученный от него элемент.
//
//Результат:
//600 onNext 1 and 100
//900 onNext 2 and 100
//1200 onNext 3 and 101
//1500 onNext 4 and 102
//1800 onNext 5 and 102
//2100 onNext 6 and 103
//2400 onNext 7 and 103
//2700 onNext 8 and 104
//3000 onNext 9 and 105
//3000 onCompleted
//
//Функция срабатывает каждые 300 мсек, т.е. при каждом новом элементе из observable1.
// А из observable2 берется его последний на тот момент полученный элемент.
//
//В первые 300 мсек. функция не сработала, т.к. не было последнего значения observable2,
// оно пришло только после 500 мсек.
//
//
//Давайте поменяем местами observable2 и observable1.
// Т.е. используем оператор withLatestFrom для observable2 и передадим в него observable1.

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


observable2
        .withLatestFrom(observable1, new Func2<Long, Long, String>() {
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
//Результат:
//500 onNext 100 and 0
//1000 onNext 101 and 2
//1500 onNext 102 and 4
//2000 onNext 103 and 5
//2500 onNext 104 and 7
//3000 onNext 105 and 9
//3500 onNext 106 and 9
//4000 onNext 107 and 9
//4500 onNext 108 and 9
//5000 onNext 109 and 9
//5000 onCompleted
//
//Теперь функция срабатывает каждые 500 мсек, т.е. при каждом новом элементе из observable2.
// А из observable1 берется его последний на тот момент полученный элемент.
//
//
//Оператор withLatestFrom можно использовать и более, чем для двух Observable.
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


Observable<Long> observable3 = Observable.interval(800, TimeUnit.MILLISECONDS)
        .take(10)
        .map(new Func1<Long, Long>() {
            @Override
            public Long call(Long aLong) {
                return aLong + 1000;
            }
        });


observable1
        .withLatestFrom(observable2, observable3, new Func3<Long, Long, Long, String>() {
            @Override
            public String call(Long aLong, Long aLong2, Long aLong3) {
                return String.format("%s and %s and %s", aLong, aLong2, aLong3);
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
//Используем withLatestFrom для observable1 и передаем туда observable2, observable3 и функцию.
// Функция будет срабатывать каждый раз, когда придет новый элемент из observable1.
// А из observable2 и observable3 просто будут забираться последние полученные от них элементы.
//
//Результат
//900 onNext 2 and 100 and 1000
//1200 onNext 3 and 101 and 1000
//1500 onNext 4 and 102 and 1000
//1800 onNext 5 and 102 and 1001
//2100 onNext 6 and 103 and 1001
//2400 onNext 7 and 103 and 1002
//2700 onNext 8 and 104 and 1002
//3000 onNext 9 and 105 and 1002
//3000 onCompleted
//
//Функция срабатывает каждые 300 мсек, т.е. при каждом новом элементе из observable1.
// А из observable2 и observable3 берутся их последние на тот момент полученные элементы.
//
//В первые 300 и 600 мсек функция не сработала, т.к. не было последнего значения observable2 или
// observable3, они появились только после 800 мсек.
}
