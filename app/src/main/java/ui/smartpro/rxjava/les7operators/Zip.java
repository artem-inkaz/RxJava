package ui.smartpro.rxjava.les7operators;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;

public class Zip {
//zip
//Оператор zip позволяет соединять друг с другом элементы из разных Observable.
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
        .zipWith(observable2, new Func2<Long, Long, String>() {
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
//Для observable1 используем оператор zipWith, в который передаем observable2 и функцию соединения.
// В этой функции мы будем получать элементы из observable1 и observable2 и соединять их так,
// как нам это необходимо. В данном примере мы просто соединяем их в строку через and.
//
//В результате работы оператора zip, мы из двух Observable<Long> получили один Observable<String>.
//
//500 onNext 0 and 100
//1000 onNext 1 and 101
//1500 onNext 2 and 102
//2000 onNext 3 and 103
//2500 onNext 4 and 104
//3000 onNext 5 and 105
//3500 onNext 6 and 106
//4000 onNext 7 and 107
//4500 onNext 8 and 108
//5000 onNext 9 and 109
//5000 onCompleted
//
//Обратите внимание на время. Оно совпадает со временем генерирования элементов observable2.
// Элементы из observable1 приходили быстрее, но zip-функция выполняется только когда есть элементы
// с обоих Observable. Поэтому каждому элементу из observable1 приходилось ждать, когда придет
// соответствующий элемент из observable2. Т.е. zip генерирует элементы со скоростью самого
// медленного из переданных ему Observable.

//Существует также вариант zipWith, который позволяет соединить ваш Observable с коллекцией Iterable.

Observable<Long> observable1 = Observable.interval(300, TimeUnit.MILLISECONDS)
        .take(10);

List<String> strings = Arrays.asList("a", "b", "c", "d", "e", "f", "g");

observable1
        .zipWith(strings, new Func2<Long, String, String>() {
            @Override
            public String call(Long aLong, String string) {
                return String.format("%s and %s", aLong, string);
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
//Коллекция strings содержит несколько строк. Причем их меньше, чем количество элементов,
// которое выдаст observable1. Т.е. явно не получится собрать пары из всех элементов.
//
//Результат:
//300 onNext 0 and a
//600 onNext 1 and b
//900 onNext 2 and c
//1200 onNext 3 and d
//1500 onNext 4 and e
//1800 onNext 5 and f
//2100 onNext 6 and g
//2100 onCompleted
//
//Как только элементы заканчиваются в одном из источников, то zip прекращает работу.
// Т.е. количество элементов, исходящих из zip равно количеству элементов самого немногочисленного Observable.

//Статический метод Observable.zip дает возможность объединять более, чем два Observable.
//
//Пример с тремя Observable:

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


Observable
        .zip(observable1, observable2, observable3, new Func3<Long, Long, Long, String>() {
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
//
//
//Результат:
//800 onNext 0 and 100 and 1000
//1600 onNext 1 and 101 and 1001
//2400 onNext 2 and 102 and 1002
//3200 onNext 3 and 103 and 1003
//4000 onNext 4 and 104 and 1004
//4800 onNext 5 and 105 and 1005
//5600 onNext 6 and 106 and 1006
//6400 onNext 7 and 107 and 1007
//7200 onNext 8 and 108 and 1008
//8000 onNext 9 and 109 and 1009
//8000 onCompleted
//
//zip ждет самый медленный Observable, чтобы собрать элементы из трех Observable в один.

//С помощью zip можно искусственно сделать паузу между элементами вашего основного Observable.
//
//Пример:

Observable<String> observable1 = Observable.just("A", "B", "C", "D", "E");
Observable<Long> observable2 = Observable.interval(100, TimeUnit.MILLISECONDS);

Observable
        .zip(observable1, observable2, new Func2<String, Long, String>() {
            @Override
            public String call(String s, Long aLong) {
                return s;
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
//Есть observable1 который выдает строки. Если на них просто подписаться, то они все придут вам мгновенно.
// А нам, например, необходимо, чтобы строки приходили с паузой в 100 мсек.
// Для этого мы просто создаем interval со значением 100 мсек и оператором zip присоединяем к основному Observable.
// Функция ничего не делает, просто шлет дальше строку, которую она получила.
//
//Результат:
//100 onNext A
//200 onNext B
//300 onNext C
//400 onNext D
//500 onNext E
//500 onCompleted
//
//Т.к. zip работает со скоростью самого медленного из Observable, то мы получим паузу 100 мсек в работе observable1.
}
