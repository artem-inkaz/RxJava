package ui.smartpro.rxjava.les1observable;

import android.util.Log;

import rx.Observable;
import rx.functions.Action1;


public class Action {
//Action
//В наших примерах мы создавали Observer с тремя методами. И этот Observer умел ловить все три типа
// событий. Но бывают случаи, когда нам требуется, например, только событие Next и вместо Observer
// мы можем использовать его сокращенную версию - Action.
//
//Пример:

// create observable
Observable<String> observable = Observable.from(new String[]{"one", "two", "three"});

// create action
Action1<String> action = new Action1<String>() {
    @Override
    public void call(String s) {
        Log.d(TAG, "onNext: " + s);
    }
};

// subscribe
observable.subscribe(action);
//
//
//Мы подписываем не Observer, а Action. И этот Action будет получать только Next события.
//
//
//
//Результат:
//onNext: one
//onNext: two
//onNext: three
//
//
//
//Всего есть три варианта метода subscribe, в которых мы можем использовать Action:
//- subscribe(Action1<? super T> onNext)
//- subscribe(Action1<? super T> onNext, Action1<java.lang.Throwable> onError)
//- subscribe(Action1<? super T> onNext, Action1<java.lang.Throwable> onError, Action0 onCompleted)
//
//Мы использовали первый. Соответственно, если вам нужно добавить обработку Error и Completed, используйте второй и третий вариант.
//
//Но учитывайте, что если вы не ловите событие Error, то в случае какой-либо ошибки у вас вылетит Exception.
}
