package ui.smartpro.rxjava.les8retrofit;

import android.view.View;

import rx.functions.Action1;
import rx.functions.Func1;

public class Retrolambda {

//Retrolambda
//Лямбда-выражения пришли к нам с Java 8. Подробно о том, что это такое - можно почитать на хабре
// (https://habrahabr.ru/post/213805/ и https://habrahabr.ru/post/224593/).
// Также есть хорошая книга на русском (https://www.ozon.ru/context/detail/id/28001133/).
//
//Мы можем использовать лямбда-выражения вместо реализации интерфейса с одним методом.
// Попробуем на небольшом примере обработчика кнопки. Обычно мы пишем его так:

button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        log("click " + v);
    }
});

//а с лямбдой то же самое можно написать так:

button.setOnClickListener(v -> log("click " + v));
//Ушли ненужные строки, описывающие создание анонимного класса.

//Примеры лямбда-выражений в RxJava
//Рассмотрим простой Observable со строками и Observer-подписчиком.

Observable.just("1", "2", "3", "4", "5")
        .subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                log("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                log("onError " + e);
            }

            @Override
            public void onNext(String s) {
                log("onNext " + s);
            }
        });
//
//
//Этот же код мы можем записать следующим образом.

Observable.just("1", "2", "3", "4", "5")
        .subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log("onNext " + s);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        log("onError " + throwable);
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        log("onCompleted");
                    }
                });
//Вместо общего Observer, используем три Action. Каждый Action - это интерфейс с одним методом.
// А значит каждый Action мы можем заменить лямбда-выражением.
// Причем Android Studio сама может это сделать. Ставите курсор на Action1, жмете ALT+ENTER

//Заменив все Action, получим такой результат

Observable.just("1", "2", "3", "4", "5")
        .subscribe(
                s -> log("onNext " + s),
                throwable -> log("onError " + throwable),
                () -> log("onCompleted")
        );
//Поначалу такой код может показаться странным, но если привыкнуть, то можно оценить лаконичность и читабельность такого варианта.
//
//
//Добавим map-трансформацию из строки в число.

Observable.just("1", "2", "3", "4", "5")
        .map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                return Integer.parseInt(s);
            }
        })
        .subscribe(
                s -> log("onNext " + s),
                throwable -> log("onError " + throwable),
                () -> log("onCompleted")
        );
//Заметьте, что функция преобразования - это анонимный класс с одним методом.
//
//Преобразуем его в лямбда-выражение.

Observable.just("1", "2", "3", "4", "5")
        .map(s -> Integer.parseInt(s))
        .subscribe(
                s -> log("onNext " + s),
                throwable -> log("onError " + throwable),
                () -> log("onCompleted")
        );
//
//
//Сравните это с полным вариантом того же самого кода.

Observable.just("1", "2", "3", "4", "5")
       .map(new Func1<String, Integer>() {
           @Override
           public Integer call(String s) {
               return Integer.parseInt(s);
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
           public void onNext(Integer s) {
               log("onNext " + s);
           }
       });
//Студия поможет вам не только преобразовать существующий код, но и писать новые лямбда-выражения.
//
//Если, например, в методе subscribe нажать CTRL+ENTER, студия предложит создать лямбда-выражение
}
