package ui.smartpro.rxjava.les9backpressure;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

public class Operators {
//Операторы OnBackpressure
//Рассмотрим, как нам могут помочь операторы onBackpressure.
//
//Для примеров будем использовать свой Observable. Создаем OnSubscribe

Observable.OnSubscribe<Integer>
        onSubscribe = new Observable.OnSubscribe<Integer>() {
    @Override
    public void call(Subscriber<? super Integer> subscriber) {
        int i = 1;
        while (i <= 20) {
            if (subscriber.isUnsubscribed()) {
                return;
            }
            subscriber.onNext(i++);
        }
        subscriber.onCompleted();
    }
};
//Он будет отправлять 20 чисел. Т.е. получился очень упрощенный аналог оператора range.
// Но у нашей реализации есть одно очень важное отличие от стандартного range - она не поддерживает
// backpressure. Т.е. когда observeOn будет просить определенное количество элементов,
// наш код это проигнорирует. Он просто будет посылать данные пока все не отправит или
// пока получатель не отпишется.
//
//
//На основе OnSubscribe создадим Observable, подпишемся на него, сделаем паузу в 100 мсек при получении,
// и разделим на разные потоки генерацию и получение элементов.

Observable.create(onSubscribe)
        .subscribeOn(Schedulers.computation())
        .doOnNext(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                log("post " + integer);
            }
        })
        .observeOn(Schedulers.io())
        .subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                log("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                log("onError " + e);
            }

            @Override
            public void onNext(Integer integer) {
                log("onNext " + integer);

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
//Т.е. делаем все то же самое, что мы делали ранее в примере с range и interval, чтобы увидеть работу backpressure механизма.

//Результат:
//
//0 post 1
//0 onNext 1
//0 post 2
//0 post 3
//0 post 4
//0 post 5
//0 post 6
//0 post 7
//0 post 8
//0 post 9
//0 post 10
//0 post 11
//0 post 12
//0 post 13
//0 post 14
//0 post 15
//0 post 16
//0 post 17
//0 post 18
//0 post 19
//0 post 20
//100 onError rx.exceptions.MissingBackpressureException
//
//Предсказуемо получаем MissingBackpressureException, потому что буфер observeOn переполнился.

//onBackpressureBuffer
//Добавим оператор onBackpressureBuffer в цепочку операторов между Observable и observeOn.

Observable.create(onSubscribe)
        .subscribeOn(Schedulers.computation())
        .doOnNext(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                log("post to buffer " + integer);
            }
        })
        .onBackpressureBuffer()
        .doOnNext(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                log("post from buffer " + integer);
            }
        })
        .observeOn(Schedulers.io())
        .subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                log("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                log("onError " + e);
            }

            @Override
            public void onNext(Integer integer) {
                log("onNext " + integer);

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
//Оператор onBackpressureBuffer - это буфер-посредник между Observable и observeOn.
// И этот посредник поддерживает backpressure. Он будет принимать от отправителя все элементы,
// хранить их в буфере и выдавать их по запросу observeOn.
//
//Также я добавил еще один doOnNext, чтобы видеть, как элементы будут проходить onBackpressureBuffer:
//post to buffer - это элемент был отправлен из Observable в onBackpressureBuffer.
//post from buffer - элемент был отправлен из onBackpressureBuffe в observeOn.
//
//Так мы явно увидим, в чем заключается работа onBackpressureBuffer.
//
//Результат:
//
//0 post to buffer 1
//0 post from buffer 1
//0 onNext 1
//0 post to buffer 2
//0 post from buffer 2
//0 post to buffer 3
//0 post from buffer 3
//0 post to buffer 4
//0 post from buffer 4
//0 post to buffer 5
//0 post from buffer 5
//0 post to buffer 6
//0 post from buffer 6
//0 post to buffer 7
//0 post from buffer 7
//0 post to buffer 8
//0 post from buffer 8
//0 post to buffer 9
//0 post from buffer 9
//0 post to buffer 10
//0 post from buffer 10
//0 post to buffer 11
//0 post from buffer 11
//0 post to buffer 12
//0 post from buffer 12
//0 post to buffer 13
//0 post from buffer 13
//0 post to buffer 14
//0 post from buffer 14
//0 post to buffer 15
//0 post from buffer 15
//0 post to buffer 16
//0 post from buffer 16
//0 post to buffer 17
//0 post to buffer 18
//0 post to buffer 19
//0 post to buffer 20
//100 onNext 2
//200 onNext 3
//300 onNext 4
//400 onNext 5
//500 onNext 6
//600 onNext 7
//700 onNext 8
//800 onNext 9
//900 onNext 10
//1000 onNext 11
//1100 onNext 12
//1200 post from buffer 17
//1200 post from buffer 18
//1200 post from buffer 19
//1200 post from buffer 20
//1200 onNext 13
//1300 onNext 14
//1400 onNext 15
//1500 onNext 16
//1600 onNext 17
//1700 onNext 18
//1800 onNext 19
//1900 onNext 20
//2000 onCompleted

//Давайте подробно разберем этот лог.
//
//Сначала мы видим, как элементы выходят из Observable, проходят через onBackpressureBuffer и
// отправляются в observeOn.
//post to buffer 1
//post from buffer 1
//post to buffer 2
//post from buffer 2
//...
//
//Это происходит для первых 16 элементов. После этого буфер observeOn полностью заполнен и
// отправителю надо подождать, пока у него попросят новые элементы.
//
//Но Observable ждать не умеет, он продолжает постить, пока не отправит все свои 20 элементов.
//post to buffer 17
//post to buffer 18
//post to buffer 19
//post to buffer 20
//
//Но onBackpressureBuffer уже не шлет их дальше в observeOn. Он сохраняет их в своем буфере.
//
//observeOn приостанавливает получение данных от onBackpressureBuffer и отправляет элементы получателю
//onNext 2
//onNext 3
//onNext 4
//onNext 5
//onNext 6
//onNext 7
//onNext 8
//onNext 9
//onNext 10
//onNext 11
//onNext 12
//
//Буфер observeOn постепенно освобождается и observeOn просит новую порцию данных у onBackpressureBuffer.
// И onBackpressureBuffer отправляет то, что он хранил у себя в буфере
//post from buffer 17
//post from buffer 18
//post from buffer 19
//post from buffer 20
//
//
//Ну и после этого все данные доходят до получателя
//onNext 13
//onNext 14
//onNext 15
//onNext 16
//onNext 17
//onNext 18
//onNext 19
//onNext 20
//onCompleted

//Оператор onBackpressureBuffer взял на себя реализацию backpressure механизма и хранил у себя
// в буфере элементы, которые пока что не могли быть отправлены дальше.
//
//Но у onBackpressureBuffer есть серьезный минус. Он может спровоцировать OutOfMemory.
// Если он накопит в буфере слишком много элементов, то приложению просто не хватит памяти.
//
//Как вариант, при использовании onBackpressureBuffer вы можете установить лимит на размер буфера,
// но после достижения этого лимита вы получите уже знакомую ошибку - MissingBackpressureException.
//
//При установленном лимите вы также можете указать еще два параметра:
//- Action, который будет выполнен при переполнении буфера (если установлен лимит).
//- поведение оператора при заполнении буфера (заменять старые элементы новыми или не принимать новые)

//onBackpressureDrop
//Оператор onBackpressureBuffer хранил в буфере все элементы, которые пока не могут быть отправлены дальше.
// А оператор onBackpressureDrop будет просто игнорировать такие элементы.
//
//Заменим в коде предыдущего примера onBackpressureBuffer на onBackpressureDrop.

//Результат
//
//0 post to drop 1
//0 post from drop 1
//0 onNext 1
//0 post to drop 2
//0 post from drop 2
//0 post to drop 3
//0 post from drop 3
//0 post to drop 4
//0 post from drop 4
//0 post to drop 5
//0 post from drop 5
//0 post to drop 6
//0 post from drop 6
//0 post to drop 7
//0 post from drop 7
//0 post to drop 8
//0 post from drop 8
//0 post to drop 9
//0 post from drop 9
//0 post to drop 10
//0 post from drop 10
//0 post to drop 11
//0 post from drop 11
//0 post to drop 12
//0 post from drop 12
//0 post to drop 13
//0 post from drop 13
//0 post to drop 14
//0 post from drop 14
//0 post to drop 15
//0 post from drop 15
//0 post to drop 16
//0 post from drop 16
//0 post to drop 17
//0 post to drop 18
//0 post to drop 19
//0 post to drop 20
//100 onNext 2
//200 onNext 3
//300 onNext 4
//400 onNext 5
//500 onNext 6
//600 onNext 7
//700 onNext 8
//800 onNext 9
//900 onNext 10
//1000 onNext 11
//1100 onNext 12
//1200 onNext 13
//1300 onNext 14
//1400 onNext 15
//1500 onNext 16
//1600 onCompleted
//
//Поначалу та же картина: первые 16 элементов приходят в onBackpressureDrop и идут дальше,
// а оставшиеся 4 дальше не идут, т.к. буфер observeOn уже заполнен.
//
//Далее получатель обрабатывает элементы в onNext. При этом буфер observeOn постепенно освобождается,
// observeOn готов к получению новых данных, и он просит у onBackpressureDrop новую порцию данных.
// Но onBackpressureDrop ничего не может предоставить, т.к. он ничего не хранит, он просто отбросил
// элементы (17,18,19,20), которые не смог отправить дальше.
//
//Минус этого оператора в том, что вы теряете элементы, которые пришли в тот момент,
// когда они пока не могли быть обработаны.

//Частично это можно исправить таким образом:

.onBackpressureDrop(new Action1<Integer>() {
    @Override
    public void call(Integer integer) {
        log("dropped " + integer);
    }
})
//onBackpressureDrop может принимать на вход Action, в котором вы будете получать все
// отбрасываемые элементы, и вы можете их куда-нибудь сохранить, например, или просто залогировать.

//onBackpressureLatest
//Оператор onBackpressureLatest также будет игнорировать новые элементы, если их пока нельзя отправить
// дальше. Но при этом он всегда будет хранить последний полученный им элемент.
//
//Используем этот оператор в прошлом примере вместо onBackpresureDrop

//Результат:
//
//0 post to latest 1
//0 post from latest 1
//0 onNext 1
//0 post to latest 2
//0 post from latest 2
//0 post to latest 3
//0 post from latest 3
//0 post to latest 4
//0 post from latest 4
//0 post to latest 5
//0 post from latest 5
//0 post to latest 6
//0 post from latest 6
//0 post to latest 7
//0 post from latest 7
//0 post to latest 8
//0 post from latest 8
//0 post to latest 9
//0 post from latest 9
//0 post to latest 10
//0 post from latest 10
//0 post to latest 11
//0 post from latest 11
//0 post to latest 12
//0 post from latest 12
//0 post to latest 13
//0 post from latest 13
//0 post to latest 14
//0 post from latest 14
//0 post to latest 15
//0 post from latest 15
//0 post to latest 16
//0 post from latest 16
//0 post to latest 17
//0 post to latest 18
//0 post to latest 19
//0 post to latest 20
//100 onNext 2
//200 onNext 3
//300 onNext 4
//400 onNext 5
//500 onNext 6
//600 onNext 7
//700 onNext 8
//800 onNext 9
//900 onNext 10
//1000 onNext 11
//1100 onNext 12
//1200 post from latest 20
//1200 onNext 13
//1300 onNext 14
//1400 onNext 15
//1500 onNext 16
//1600 onNext 20
//1700 onCompleted
//
//Все как в случае onBackpressureDrop, но, когда оператор observeOn готов снова принимать элементы,
// onBackpressureLatest отправит ему последний полученный им элемент (20).

//Таким образом вы можете использовать onBackpressure операторы в качестве посредников между
// подписчиком/оператором и Observable, не поддерживающим backpressure.
}
