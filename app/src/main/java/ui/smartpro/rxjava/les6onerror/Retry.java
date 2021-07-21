package ui.smartpro.rxjava.les6onerror;

public class Retry {
//Оператор retry
//Кроме обработчиков ошибок, в RxJava есть механизм для перезапуска Observable.
// Т.е., в случае ошибки, ваш Observer может сам еще раз подписаться и попробовать получить данные.
// Это может быть удобно, например, при нестабильном коннекте.
//
//Будем перезапускать подписку, пока она не закончится успешно.

Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");

Observable<Long> observable = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
            }
        })
        .retry();

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
//Мы добавили оператор retry к observable. Т.к. в этом примере ошибка в Observable будет возникать
// всегда, то использование retry приведет к бесконечному количеству попыток снова получить данные.
//
//Результат
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//…
//
//Логи уходят в бесконечность. Это хороший пример, как не нужно делать, если вероятность ошибки очень высока.
//
//Чтобы избежать бесконечных повторов, мы можем явно указать оператору retry число попыток.
// Перепишем последний пример, указав, что нам нужны всего две попытки.

Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");

Observable<Long> observable = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
            }
        })
        .retry(2);

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
//Мы указали число 2 в операторе retry.
//
//Результат
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//onError java.lang.NumberFormatException: Invalid long: "a"
//
//Подписчик попытался получить данные первый раз, затем еще два раза, и в итоге принял ошибку и
// прекратил попытки.

//Существует еще один вариант оператора retry, который исходя из ошибки и количества уже предпринятых
// попыток принимает решение о том, пытаться ли еще раз.

Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");

Observable<Long> observable = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
            }
        })
        .retry(new Func2<Integer, Throwable, Boolean>() {
            @Override
            public Boolean call(Integer retryCount, Throwable throwable) {
                log("retry retryCount " + retryCount + ", throwable = " + throwable);
                return retryCount < 3;
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
//В оператор retry мы передаем функцию, которая принимает на вход ошибку и количество предпринятых попыток,
// а на выходе должна вернуть boolean значение - стоит ли еще раз пытаться?
//В этом примере мы никак не учитываем тип ошибки, а просто проверяем число попыток - если оно меньше 3,
// то стоит попытаться еще раз. Как только число попыток будет >= 3, функция вернет false,
// подписчик больше не будет пытаться получать данные и смирится с тем, что надо получать ошибку.
//
//Результат
//onNext 1
//onNext 2
//retry retryCount 1, throwable = java.lang.NumberFormatException: Invalid long: "a"
//onNext 1
//onNext 2
//retry retryCount 2, throwable = java.lang.NumberFormatException: Invalid long: "a"
//onNext 1
//onNext 2
//retry retryCount 3, throwable = java.lang.NumberFormatException: Invalid long: "a"
//onError java.lang.NumberFormatException: Invalid long: "a"
//
//После каждой попытки получения данных, вызывается функция в retry, получает данные о количестве
// попыток и об ошибке и решает, продолжать ли попытки. В итоге, после трех попыток,
// отказываемся от дальнейшей борьбы и получаем ошибку в onError.
}
