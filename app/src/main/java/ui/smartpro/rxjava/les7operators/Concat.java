package ui.smartpro.rxjava.les7operators;

import java.util.concurrent.TimeUnit;

import rx.functions.Func1;

public class Concat {
//concat
//Оператор concat - это merge c maxConcurrent = 1. Он всегда будет последовательно запускать переданные ему Observable.
//
//Рассмотрим на том же примере

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

observable1.concatWith(observable2)
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
//Используем concatWith, чтобы объединить два Observable.
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
//Результат тот же, что и при merge с maxConcurrent = 1,
// два Observable выдают элементы последовательно - сначала один, затем второй.
//
//
//
//Наглядный пример использования concat - это когда вам нужно получить какие-то данные,
// которые могут быть в одном из нескольких источников. Например, сначала данные надо поискать в кэше.
// Если их нет в кэше, то надо идти в БД. Если нет и в БД, то надо тянуть с сервера.
// Если и там нет, то надо выдать какую-то заглушку.
//
//Это может выглядеть примерно так:

//Observable<Data> cacheData = getFromCache();
//Observable<Data> dbData = readFromDb();
//Observable<Data> serverData = requestFromServer();
//Observable<Data> notFoundData = Observable.just(Data.NOT_FOUND);
//
//Observable<Data> observableData = Observable
//        .concat(cacheData, dbData, serverData, notFoundData).first();
//Четыре Observable - для получения данных из кэша, из БД, с сервера и
// статическая заглушка NOT_FOUND в случае, если ничего не нашлось.
// Именно в этом порядке передаем их в concat, и в конце добавляем оператор first,
// чтобы после первой же записи завершить всю последовательность.
//
//В итоге, concat будет запускать последовательно эти Observable, и какой-то из них вернет данные.
// После этого, оставшиеся Observable уже не будут запущены, и последовательность завершится.
}
