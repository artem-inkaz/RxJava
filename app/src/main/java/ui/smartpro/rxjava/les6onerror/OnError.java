package ui.smartpro.rxjava.les6onerror;

public class OnError {
//Метод onError
//Давайте рассмотрим пример

Observable<String> stringData = Observable.just("1", "2", "3", "4", "5");

Observable<Long> observable = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
            }
        });

observable.subscribe(new Action1<Long>() {
    @Override
    public void call(Long aLong) {
        log("onNext " + aLong);
    }
});
//
//
//stringData - Observable cо строковыми данными. Оператор map конвертирует строки в числа и в итоге
// мы получаем Observable<Long>. Подписываем Action и выводим в лог значение.
//
//Результат:
//onNext 1
//onNext 2
//onNext 3
//onNext 4
//onNext 5
//
//Все понятно и ожидаемо, String конвертируется в Long. Но что если мы передадим некорректную строку,
// которую не получится конвертировать в число? Давайте в строковых данных заменим "3" на "a".
//
//1
Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");
//
//
//В результате мы получим только первые две записи
//onNext 1
//onNext 2
//
//И после этого приложение вылетит с OnErrorNotImplementedException.
// В логах вы найдете, что ошибка была такой:
//java.lang.NumberFormatException: Invalid long: "a"
//
//map-оператор не смог из буквы "a" сделать число и хотел вызвать метод Observer.onError,
// чтобы передать туда NumberFormatException. Но т.к. в качестве Observer мы использовали один Action,
// то никакой реализации метода onError мы не предоставили. И поэтому получили OnErrorNotImplementedException.
//
//Чтобы обрабатывать ошибку, нам необходимо использовать полноценный Observer.
// Либо можем добавить еще один Action в метод subscribe:
//subscribe(Action onNext, Action onError). В этом случае Action onError будет принимать ошибки.

//Я использую Observer

Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");

Observable<Long> observable = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
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
//
//
//Теперь результат будет таков:
//onNext 1
//onNext 2
//onError java.lang.NumberFormatException: Invalid long: "a"
//
//Мы получили две первых записи в onNext, а затем ошибку в onError. На этом работа завершилась.
// Напомню, что так и должно быть - работа завершается после onError или onCompleted.
//
//Приложение не крэшнулось. Т.е. onError - это аналог try-catch. Он позволит обработать ошибку,
// а не свалиться с крэшем. onError рекомендуется использовать и в том случае,
// когда вероятность ошибки крайне мала. Даже если вы не будете обрабатывать ошибку,
// то хотя бы залогируйте ее.
//
//Кроме onError существует еще несколько способов обрабатывать ошибки.
}
