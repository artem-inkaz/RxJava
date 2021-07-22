package ui.smartpro.rxjava.les9backpressure;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class Backpressureless {

//Оператор без поддержки backpressure
//Давайте рассмотрим оператор, который не поддерживает Backpressure. Например, interval.
//
//Посмотрим его описание в документации на предмет Backpressure:
//The operator generates values based on time and ignores downstream backpressure which may
// lead to MissingBackpressureException at some point in the chain.
// Consumers should consider applying one of the onBackpressureXXX operators as well.
//
//Пишут, что оператор генерирует данные основываясь на времени и игнорирует backpressure,
// что может привести к MissingBackpressureException. Используйте onBackpressure операторы.
//
//Давайте рассмотрим пример с interval и действительно словим MissingBackpressureException.
// А после этого разберемся, чем нам могут помочь операторы onBackpressure.
//
//Пример:

Observable.interval(100, TimeUnit.MILLISECONDS)
        .take(30)
        .subscribeOn(Schedulers.computation())
        .doOnNext(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                log("post " + aLong);
            }
        })
        .observeOn(Schedulers.io())
        .subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
                log("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                log("onError " + e);
            }

            @Override
            public void onNext(Long aLong) {
                log("onNext " + aLong);
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
//Итак, мы будем отправлять 30 элементов с интервалом в 100 мсек. А принимать и обрабатывать каждый элемент мы будем 500 мсек.
//
//
//Результат:
//
//100 post 0
//100 onNext 0
//200 post 1
//300 post 2
//400 post 3
//500 post 4
//600 post 5
//600 onNext 1
//700 post 6
//800 post 7
//900 post 8
//1000 post 9
//1100 post 10
//1100 onNext 2
//1200 post 11
//1300 post 12
//1400 post 13
//1500 post 14
//1600 post 15
//1600 onNext 3
//1700 post 16
//1800 post 17
//1900 post 18
//2000 post 19
//2100 post 20
//2100 onError rx.exceptions.MissingBackpressureException

//Отправитель постит элементы каждые 100 мсек, а получатель их обрабатывает по 500 мсек.
// Какое-то время они успешно работают параллельно за счёт того, что данные копятся в буфере observeOn.
// Но в момент, когда получатель обработал 3 элемента, а отправитель собирается запостить 21-й,
// буфер переполняется. И все останавливается с ошибкой MissingBackpressureException.
//
//Эта ошибка означает, что не сработал механизм backpressure. Т.е. у отправителя запросили ограниченную
// порцию данных, а он проигнорировал это и продолжил постить, что привело к переполнению буфера.
//
//На самом деле, у оператора interval не было другого выбора. Он должен постить элементы каждые 100 мсек,
// как мы от него и просили. И он просто не может ждать, пока получатель обработает данные.
// Т.е. он вполне официально не поддерживает backpressure, о чем и написано в хелпе.

}
