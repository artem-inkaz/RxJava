package ui.smartpro.rxjava.les7operators;

import java.util.concurrent.TimeUnit;

import rx.functions.Func1;

public class Merge {
//Операторы объединения позволяют объединять элементы из нескольких потоков в один.
// Они весьма полезны и часто используются в работе. RxJava предлагает нам несколько таких операторов
// и поначалу в них можно немного запутаться. Я решил сделать отдельный урок,
// чтобы подробно рассмотреть отличия между ними.
//
//
//merge
//Оператор merge просто объединяет элементы из нескольких Observable в один. Рассмотрим объединение двух Observable.

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

observable1.mergeWith(observable2)
        .subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
                log("onCompleted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                log("onNext " + aLong);
            }
        });
//observable1 сгенерирует элементы от 0 до 9 с интервалом в 300 мсек.
//observable2 - от 100 до 109 с интервалом в 500 мсек.
//
//Объединяем два этих Observable с помощью оператора mergeWith, и выводим в лог результат:
//300 onNext 0
//500 onNext 100
//600 onNext 1
//900 onNext 2
//1000 onNext 101
//1200 onNext 3
//1500 onNext 4
//1500 onNext 102
//1800 onNext 5
//2000 onNext 103
//2100 onNext 6
//2400 onNext 7
//2500 onNext 104
//2700 onNext 8
//3000 onNext 9
//3000 onNext 105
//3500 onNext 106
//4000 onNext 107
//4500 onNext 108
//5000 onNext 109
//5000 onCompleted
//(число в начале каждой строки лога – миллисекунды)
//
//Элементы из двух Observable идут параллельно друг другу.
// И когда оба потока закончились, мы получаем onCompleted.
//
//В чем отличие от того, чтобы просто подписаться на два этих Observable?
// В том, что merge вернет нам только один onCompleted, когда закончится последний из двух этих Observable.

//Чтобы смержить сразу несколько Observable, можно использовать статический метод Observable.merge.
// Существует несколько способов задать множество Observable, которые вы хотите объединить:
// перечисление (до 9 Observable), массив (Observable[]), итератор (Iterable<Observable>)
// и даже Observable (Observable<Observable>).
//
//
//
//Метод merge позволяет задать maxConcurrent - количество Observable,
// которые могут работать параллельно.
//
//В нашем примере мы использовали два Observable, и они работали параллельно друг другу.
// Т.е. мы были подписаны сразу на оба. Перепишем пример так, чтобы они работали последовательно,
// т.е. друг за другом. Для этого используем метод Observable.merge.
// Передадим туда два наших Observable и укажем maxConcurrent = 1.
// Это будет означать, что только один Observable может предоставлять элементы в один момент времени.
// Т.е. мы сначала подписываемся на один Observable, ждем пока он не закончится,
// затем подписываемся на второй Observable.

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

Observable<Long>[] observableArray = new Observable[] {observable1, observable2};

Observable.merge(observableArray, 1)
        .subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
                log("onCompleted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                log("onNext " + aLong);
            }
        });
//Помещаем Observable в массив observableArray и передаем его в метод merge вместе с maxConcurrent = 1 .
//
//Результат
//300 onNext 0
//600 onNext 1
//900 onNext 2
//1200 onNext 3
//1500 onNext 4
//1800 onNext 5
//2100 onNext 6
//2400 onNext 7
//2700 onNext 8
//3000 onNext 9
//3500 onNext 100
//4000 onNext 101
//4500 onNext 102
//5000 onNext 103
//5500 onNext 104
//6000 onNext 105
//6500 onNext 106
//7000 onNext 107
//7500 onNext 108
//8000 onNext 109
//8000 onCompleted
//
//Сначала отработал первый observable1, затем observable2.
}
