package ui.smartpro.rxjava.les4subject;

import java.util.concurrent.TimeUnit;

public class Async {
//AsyncSubject
//Выдает только последнее значение и только в момент, когда последовательность завершена.
//
//Пример:

final Observer<Long> observer1 = new Observer<Long>() {
    @Override
    public void onCompleted() {
        log("observer1 onCompleted");
    }

    @Override
    public void onError(Throwable e) {}

    @Override
    public void onNext(Long aLong) {
        log("observer1 onNext value = " + aLong);
    }
};

final Observer<Long> observer2 = new Observer<Long>() {
    @Override
    public void onCompleted() {
        log("observer2 onCompleted");
    }

    @Override
    public void onError(Throwable e) {}

    @Override
    public void onNext(Long aLong) {
        log("observer2 onNext value = " + aLong);
    }
};

final Observable<Long> observable = Observable
        .interval(1, TimeUnit.SECONDS)
        .take(4);

final AsyncSubject<Long> subject = AsyncSubject.create();

log("subject subscribe");
observable.subscribe(subject);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer1 subscribe");
        subject.subscribe(observer1);
    }
}, 1500);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer2 subscribe");
        subject.subscribe(observer2);
    }
}, 7500);
//Подписываем AsyncSubject на interval Observable, затем подписываем Observer1 на Subject и спустя время,
// достаточное, чтобы отработал и завершился interval, подписываем Observer2.
//
//Результат
//0 subject subscribe
//1500 observer1 subscribe
//4000 observer1 onNext value = 3
//4000 observer1 onCompleted
//7500 observer2 subscribe
//7500 observer2 onNext value = 3
//7500 observer2 onCompleted
//
//
//Observer1 подписался во время работы Subject, но получил только последнее значение в момент
// когда последовательность завершилась (onCompleted). Observer2, который подписался уже после
// завершения, также получил последнее значение.
//
//Этот Subject подходит для мониторинга выполнения какой-либо задачи, которая вернет только один результат.
// И любой подписчик сможет получить этот результат, даже если задача уже выполнена.
//
//У AsyncSubject есть методы:
//hasValue - приходил уже результат или еще нет
//getValue - получить результат
}
