package ui.smartpro.rxjava.les6onerror;

import rx.functions.Func1;

public class OnErrorReturn {
//Оператор onErrorReturn
//Этот оператор позволит перехватить ошибку и вместо нее передать значение.
//
//Перепишем создание observable, добавим onErrorReturn

Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");

Observable<Long> observable = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
            }
        })
        .onErrorReturn(new Func1<Throwable, Long>() {
            @Override
            public Long call(Throwable throwable) {
                log("onErrorReturn " + throwable);
                return 0L;
            }
        });

observable.subscribe(new Observer<Long>() {
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
    }
});
//В onErrorReturn нам необходимо создать функцию, которая на вход принимает Throwable,
// а на выходе требует число. Т.е. в этой функции вы сможете определить какая именно ошибка произошла,
// и в зависимости от этого отправить значение. Я буду в любом случае отправлять значение 0.
//
//Результат:
//onNext 1
//onNext 2
//onErrorReturn java.lang.NumberFormatException: Invalid long: "a"
//onNext 0
//onCompleted
//
//Мы получаем первые два значения в onNext. Затем происходит ошибка, которая попадает в onErrorReturn,
// и вместо нее мы получаем значение 0 в onNext. На этом последовательность завершается вызовом onCompleted.
//
//Здесь важно понять, что несмотря на то, что onErrorReturn ловит ошибку и передает какое-то значение
// в onNext, в итоге это все равно приводит к тому, что поток данных завершается (onCompleted).
//
//Т.е. такая обработка ошибок в текущем примере - не самый удачный выбор. Гораздо правильнее будет
// обернуть метод Long.parseLong в try-catch и при возникновении ошибки возвращать 0. Тогда данные продолжат поступать, пока не закончатся.
//
//Но я все-таки продолжу использовать этот пример, т.к. тема урока не try-catch, а обработка ошибок в Rx.
// И мне нужен код, который будет генерировать ошибку, чтобы мы могли рассмотреть возможности обработки.
}
