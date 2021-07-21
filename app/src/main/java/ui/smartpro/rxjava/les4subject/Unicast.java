package ui.smartpro.rxjava.les4subject;

import java.util.concurrent.TimeUnit;

public class Unicast {
//UnicastSubject
//Subject, на который можно подписать лишь одного получателя.
// И даже после того как этот один получатель отписался, никто больше не сможет подписаться.
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
    public void onError(Throwable e) {
        log("observer2 onError " + e.getMessage());
    }

    @Override
    public void onNext(Long aLong) {
        log("observer2 onNext value = " + aLong);
    }
};

final Observable<Long> observable = Observable
        .interval(1, TimeUnit.SECONDS)
        .take(20);

final UnicastSubject<Long> subject = UnicastSubject.create();

log("subject subscribe");
observable.subscribe(subject);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer1 subscribe");
        subscription1 = subject.subscribe(observer1);
    }
}, 2500);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer2 subscribe");
        subject.subscribe(observer2);
    }
}, 4500);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer1 unsubscribe");
        subscription1.unsubscribe();
    }
}, 6500);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer2 subscribe");
        subject.subscribe(observer2);
    }
}, 8500);
//
//
//Результат:
//0 subject subscribe
//2500 observer1 subscribe
//2500 observer1 onNext value = 0
//2500 observer1 onNext value = 1
//3000 observer1 onNext value = 2
//4000 observer1 onNext value = 3
//4500 observer2 subscribe
//4500 observer2 onError Only a single subscriber is allowed
//5000 observer1 onNext value = 4
//6000 observer1 onNext value = 5
//6500 observer1 unsubscribe
//8500 observer2 subscribe
//8500 observer2 onError Only a single subscriber is allowed
//
//Observer2 пытается подписаться и пока Observer1 подписан, и после того,
// как Observer1 отписался, но получает ошибку.
}
