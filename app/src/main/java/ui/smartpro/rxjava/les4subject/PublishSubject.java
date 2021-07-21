package ui.smartpro.rxjava.les4subject;

import java.util.concurrent.TimeUnit;

public class PublishSubject {
//PublishSubject
//Самый обычный Subject, без каких-либо опций. Принимает данные и отдает их всем текущим подписчикам.
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
        .take(10);

final PublishSubject<Long> subject = PublishSubject.create();

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
//Создаем interval Observable, создаем PublishSubject методом create и подписываем его на Observable.
// Interval начинает генерировать данные. Спустя некоторое время подписываем Observer1 на
// Subject и затем Observer2. И еще чуть позже сами отправляем элемент в Subject методом onNext.
//
//
//
//Результат
//0 subject subscribe
//3500 observer1 subscribe
//4000 observer1 onNext value = 3
//5000 observer1 onNext value = 4
//5500 observer2 subscribe
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
//Видно, что Observer получает только те данные, которые приходят после того,
// как он подписался на Subject. Данные, которые приходили раньше, он не получает.
//
//Для эксперимента вы можете добавить еще один Observable и подписать на него Subject.
// В этом случае Subject будет получать данные от двух Observable и передавать их своих подписчикам.
// Как только один из Observable передаст onCompleted, Subject закончит работу,
// даже если второй Observable еще что-то передает.
//
//
//PublishSubject можно использовать, например, как EventBus. Мы передаем ему событие в onNext,
// а он рассылает его всем подписчикам.
}
