package ui.smartpro.rxjava.les1observable

class ObservableObserver {

//Создание Observable выглядит так:

Observable<String> observable = Observable.from(new String[]{"one", "two", "three"});
//Observable<String> - это описание означает, что Observable будет предоставлять данные типа String,
// т.е. каждое событие Next, которое он будет генерировать, будет приходить с объектом типа String.
// Метод Observable.from создает для нас Observable, который возьмет данные из указанного String
// массива и передаст их получателям

//Создаем получателя, т.е. Observer:

Observer<String> observer = new Observer<String>() {
    @Override
    public void onNext(String s) {
        log("onNext: " + s);
    }

    @Override
    public void onError(Throwable e) {
        log("onError: " + e);
    }

    @Override
    public void onCompleted() {
        log("onCompleted");
    }
};
//Observer<String> - получатель данных типа String.
// Напомню, что он от Observable ожидает получения событий трех типов Next, Error и Completed.
// И под каждый тип у Observer есть свой одноименный метод:
//onNext(String s) - в этот метод будут приходить данные
//onError(Throwable e) - будет вызван в случае какой-либо ошибки и на вход получит данные об ошибке
//onCompleted() - уведомление о том, что все данные переданы

//Оба объекта созданы, осталось подписать Observer на Observable методом subscribe:

observable.subscribe(observer);

//Сразу после подписки Observable передаст в Observer все данные  (в метод onNext) и сигнал о том,
// что передача завершена (метод onCompleted).
//
//Результат:
//onNext: one
//onNext: two
//onNext: three
//onCompleted
//
//
//Этот простой пример призван показать взаимодействие между Observable и Observer.
// Мы использовали в нем Observable, который умеет передавать данные из предоставленного ему массива.
// Но это только один из видов Observable. Дальше мы научимся создавать различные Observable.
}