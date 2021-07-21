package ui.smartpro.rxjava.les6onerror;

import rx.functions.Action1;
import rx.functions.Func1;

public class Action {
//Action
//Как вы наверно заметили, эти обработчики ловят ошибку, и она более не попадает в onError.
// А значит мы снова можем использовать Action, вместо полноценного Observer.

Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");

Observable<Long> observable = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
            }
        })
        .onErrorResumeNext(Observable.just(8L, 9L, 10L));

observable.subscribe(new Action1<Long>() {
    @Override
    public void call(Long aLong) {
        log("onNext " + aLong);
    }
});
//
//Результат
//onNext 1
//onNext 2
//onNext 8
//onNext 9
//onNext 10
//
//На этот раз ничего не крэшит без onError, т.к. у нас есть обработчик onErrorResumeNext.
}
