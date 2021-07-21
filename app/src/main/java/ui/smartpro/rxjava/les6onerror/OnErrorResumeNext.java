package ui.smartpro.rxjava.les6onerror;

public class OnErrorResumeNext {
//Оператор onErrorResumeNext
//Этот оператор аналогичен оператору onErrorReturn, но позволяет вместо ошибки отправить в
// Observer не одно значение, а несколько - в виде Observable.

Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");

Observable<Long> observable = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
            }
        })
        .onErrorResumeNext(Observable.just(8L, 9L, 10L));

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
//В onErrorResumeNext передаем Observable, который вернет значения 8,9,10
//
//Результат:
//onNext 1
//onNext 2
//onNext 8
//onNext 9
//onNext 10
//onCompleted
//
//Получаем первые два значения, а после ошибки - значения из последовательности, указанной в onErrorResumeNext.

//Оператор onErrorResumeNext имеет еще одну реализацию, в которой можно не просто указать новую
// последовательность, но и учесть при этом, какая ошибка произошла.

Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");

Observable<Long> observable = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
            }
        })
        .onErrorResumeNext(new Func1<Throwable, Observable<? extends Long>>() {
            @Override
            public Observable<? extends Long> call(Throwable throwable) {
                log("onErrorResumeNext " + throwable);
                return Observable.just(8L, 9L, 10L);
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
//В onErrorResumeNext используем функцию, на вход которой получаем Throwable.
// В зависимости от типа Throwable можем создать необходимую последовательность значений.
// В данном примере я в любом случае возвращаю последовательность Observable.just(8L, 9L, 10L).
//
//Результат
//onNext 1
//onNext 2
//onErrorResumeNext java.lang.NumberFormatException: Invalid long: "a"
//onNext 8
//onNext 9
//onNext 10
//onCompleted

//Оператор onExceptionResumeNext
//Этот оператор аналогичен оператору onErrorResumeNext, но не поймает ошибки Throwable и Error, только Exception.
}
