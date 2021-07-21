package ui.smartpro.rxjava.les4subject;

import java.util.concurrent.TimeUnit;

public class ReplaySubject {
//ReplaySubject
//Похож на PublishSubject, но имеет буфер, который хранит данные и передает их подписчикам в момент подписки.
//
//Рассмотрим тот же самый пример, но используем ReplaySubject вместо PublishSubject

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
        .take(10);

final ReplaySubject<Long> subject = ReplaySubject.create();

log("subject subscribe");
observable.subscribe(subject);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer1 subscribe");
        subject.subscribe(observer1);
    }
}, 3500);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer2 subscribe");
        subject.subscribe(observer2);
    }
}, 5500);

postDelayed(new Runnable() {
    @Override
    public void run() {
        subject.onNext(100L);
    }
}, 7500);
//
//
//Результат
//0 subject subscribe
//3500 observer1 subscribe
//3500 observer1 onNext value = 0
//3500 observer1 onNext value = 1
//3500 observer1 onNext value = 2
//4000 observer1 onNext value = 3
//5000 observer1 onNext value = 4
//5500 observer2 subscribe
//5500 observer2 onNext value = 0
//5500 observer2 onNext value = 1
//5500 observer2 onNext value = 2
//5500 observer2 onNext value = 3
//5500 observer2 onNext value = 4
//6000 observer1 onNext value = 5
//6000 observer2 onNext value = 5
//7000 observer1 onNext value = 6
//7000 observer2 onNext value = 6
//7500 observer1 onNext value = 100
//7500 observer2 onNext value = 100
//8000 observer1 onNext value = 7
//8000 observer2 onNext value = 7
//9000 observer1 onNext value = 8
//9000 observer2 onNext value = 8
//10000 observer1 onNext value = 9
//10000 observer2 onNext value = 9
//10000 observer1 onCompleted
//10000 observer2 onCompleted
//
//
//Результат тот же, но при подписке Observer сразу получает все данные, которые он пропустил.
//
//У ReplySubject есть несколько разновидностей метода create, где вы можете указать кол-во хранимых
// в буфере элементов или время хранения.
//
//И есть несколько методов, связанных с хранимыми данными:
//
//getValue - получить последний элемент
//getValues - получить все хранимые данные
//hasAnyValue - хранит ли Subject какие-либо данные
//size - кол-во хранимых данных
}
