package ui.smartpro.rxjava.les9backpressure;

public class RequestData {
//Как запрашивать данные у отправителя
//Напоследок я хотел бы показать, как именно получатель запрашивает данные у отправителя.
// Я несколько раз говорил об этом в уроке и понимаю, что это может звучать необычно для паттерна
// Наблюдатель, когда получатель обычно просто смиренно ждет.
//
//В RxJava подписчик может наследовать класс Subscriber. И этот класс имеет метод request.
// Именно он и предназначен для запроса элементов.
//
//
//Рассмотрим пример:

Observable.range(1, 10)
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onStart() {
                super.onStart();
                request(1);
            }

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
//В подписчике в методе onStart мы запрашиваем один элемент.
//
//Результат будет таков:
//
//onNext 1
//
//
//Мы запросили один элемент и получили только один элемент.
//
//Если не вызывать метод request (что мы обычно и не делаем), то по умолчанию отправитель будет
// посылать данные пока они не закончатся. Но вызвав метод request, мы переводим отправителя в
// режим backpressure. В этом режиме он будет посылать данные только по request запросу.
// И снова переключить его в режим без поддержки backpressure можно отправив request с значением Long.MAX_VALUE.
//
//
//Давайте запросим чуть больше элементов, например, 5.

Observable.range(1, 10)
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onStart() {
                super.onStart();
                request(5);
            }

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
//Результат
//
//onNext 1
//onNext 2
//onNext 3
//onNext 4
//onNext 5

//А теперь запросим один при старте подписки, и будет запрашивать еще один каждый раз при получении нового элемента.

Observable.range(1, 10)
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onStart() {
                super.onStart();
                request(1);
            }

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
                request(1);
            }
        });
//
//
//Результат:
//
//onNext 1
//onNext 2
//onNext 3
//onNext 4
//onNext 5
//onNext 6
//onNext 7
//onNext 8
//onNext 9
//onNext 10
//onCompleted
//
//Теперь мы получили все данные, т.к. постоянно запрашивали их методом request, пока они не закончились.

//Мы рассмотрели механизмы, которые используются в работе с Observable, не поддерживающими backpressure.
// А если у вас с backpressure все в порядке, но вы просто хотите немного просеять входящий поток,
// то используйте операторы sample, throttle, buffer, window и т.п.
}
