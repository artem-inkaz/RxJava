package ui.smartpro.rxjava.les9backpressure;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class Backpressure {

//Оператор с поддержкой backpressure
//Возьмем оператор range, который выдает последовательность чисел.

Observable.range(1, 10)
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
            }
        });
//
//
//Выведем результаты в лог с указанием времени
//
//0 onNext 1
//0 onNext 2
//0 onNext 3
//0 onNext 4
//0 onNext 5
//0 onNext 6
//0 onNext 7
//0 onNext 8
//0 onNext 9
//0 onNext 10
//0 onCompleted
//
//Время указано перед onNext. Видим, что range сразу же передал получателю все данные без каких-либо задержек.

//Теперь давайте добавим в onNext небольшую паузу. Т.е. как будто нам нужно время, чтобы обработать поступающие данные.

Observable.range(1, 10)
        .subscribeOn(Schedulers.computation())
        .doOnNext(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                log("post " + integer);
            }
        })
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
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
//Также я добавил оператор doOnNext, который будет срабатывать каждый раз, когда range посылает данные.
// В логах мы это увидим, как post.
//
//И я указал поток Schedulers.computation. Вся работа будет вестись в нем: и генерация данных и
// получение в onNext. Тем самым мы не будем блокировать UI поток своей задержкой.

//Результат:
//
//0 post 1
//0 onNext 1
//300 post 2
//300 onNext 2
//600 post 3
//600 onNext 3
//900 post 4
//900 onNext 4
//1200 post 5
//1200 onNext 5
//1500 post 6
//1500 onNext 6
//1800 post 7
//1800 onNext 7
//2100 post 8
//2100 onNext 8
//2400 post 9
//2400 onNext 9
//2700 post 10
//2700 onNext 10
//3000 onCompleted
//
//Теперь видно, что range уже не так мгновенно отправляет данные один за другим.
// Ему приходится ждать, пока получатель примет данные,
// т.к. отправка и получение происходят в одном потоке, и метод onNext каждый раз блокирует этот поток на 300 мсек.
//
//
//По логам видно чередование post и onNext.
// Т.е. поставщик отправляет данные, получатель принимает, поставщик отправляет, получатель принимает и т.д.
// Они работают последовательно в одном потоке, а значит со скоростью самого медленного из них,
// т.е. - получателя. Поставщик готов отправить все данные в первые же миллисекунды,
// но ему приходится ждать, пока медленный получатель их обрабатывает.
//
//
//Давайте разделим отправку и получение по разным потокам. Т.е. теперь получатель уже не будет блокировать отправителя.
//
//Добавляем observeOn с io потоком.

Observable.range(1, 10)
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
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
//Теперь данные отправляются в computation потоке, а принимаются в io потоке.

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
//300 onNext 2
//600 onNext 3
//900 onNext 4
//1200 onNext 5
//1500 onNext 6
//1800 onNext 7
//2100 onNext 8
//2400 onNext 9
//2700 onNext 10
//3000 onCompleted
//
//Разделение на потоки явно дало результат. Отправитель быстро отправил все данные без какого-либо
// блокирования со стороны получателя. А получатель спокойно принял данные со своей скоростью.
//
//Из этого можно сделать вывод, что данные где-то кэшировались. Т.е. отправитель их скинул в какой-то буфер,
// а получатель из этого буфера забирает и обрабатывает. И это действительно так.
// Оператор observeOn предоставляет буфер, который используется для передачи данных между отправителем
// и получателем. И в случае, когда получатель медленнее отправителя, этот буфер позволяет
// отправителю не привязываться к скорости получателя.
//
//Но, разумеется, есть ограничение. Буфер не бесконечен. По умолчанию, в RxJava для Android его
// размер составляет всего 16 элементов (вы можете изменить размер, явно указав значение в операторе observeOn).
//
//
//Давайте увеличим кол-во элементов range с 10 до 20, чтобы буфер заполнился.
//
//В предыдущем примере поменяйте параметры оператора range

Observable.range(1, 20)
//Теперь буфер переполнится после первых 16 отправленных элементов. И что будет с оставшимися 4-мя?

//Смотрим лог:
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
//300 onNext 2
//600 onNext 3
//900 onNext 4
//1200 onNext 5
//1500 onNext 6
//1800 onNext 7
//2100 onNext 8
//2400 onNext 9
//2700 onNext 10
//3000 onNext 11
//3300 onNext 12
//3600 onNext 13
//3600 post 17
//3600 post 18
//3600 post 19
//3600 post 20
//3900 onNext 14
//4200 onNext 15
//4500 onNext 16
//4800 onNext 17
//5100 onNext 18
//5400 onNext 19
//5700 onNext 20
//6000 onCompleted
//
//По логам видно, что range успешно отправляет (post) первые 16 элементов,
// а потом ему таки приходится снова ждать, пока медленный получатель заберет данные из буфера.
//
//Когда получатель обработал 13 элементов, observeOn решает, что буфер уже достаточно свободен и
// сообщает отправителю, что он может присылать новую порцию данных.
// И range отправляет оставшиеся 4 элемента (17, 18, 19, 20).
//
//А получатель абсолютно не в курсе всех этих взаимоотношений буфера и отправителя.
// Ему просто приходят данные из буфера observeOn, а он их обрабатывает.

//Эти примеры показали нам, что отправитель не бездумно шлет данные получателю.
// Если они работают в одном потоке, то отправителю приходится работать со скоростью получателя.
// А если мы разделим их на разные потоки, то вопросы синхронизации решает оператор observeOn.
// Он запрашивает у отправителя данные порциями и складывает их в буфер, откуда они уходят получателю.
// По мере освобождения буфера, observeOn снова запрашивает данные у отправителя и т.д.
//
//Вот эта возможность запросить данные у отправителя - это и есть backpressure.
// Т.е. это такой механизм обратной связи, когда получатель может повлиять на работу отправителя.

//Мы рассмотрели пример работы backpressure на операторе range.
// Он исправно отправлял данные небольшими порциями и ждал, пока получатель с ним разберется.
// Если вы посмотрите официальный документацию к оператору range, там есть пункт Backpressure, в котором написано:
//The operator honors backpressure from downstream and signals values on-demand (i.e., when requested).
//
//Что можно перевести как “оператор поддерживает Backpressure”.
}
