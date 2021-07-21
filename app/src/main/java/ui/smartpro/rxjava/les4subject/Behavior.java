package ui.smartpro.rxjava.les4subject;

import java.util.concurrent.TimeUnit;

public class Behavior {
//BehaviorSubject
//Это ReplySubject с размером буфера = 1 и возможностью указать начальный элемент. Соответственно,
// в момент подписки Observer сразу получит от Subject его последний элемент.
// А если Subject еще сам ничего не получал, то он передаст в Observer дефолтное значение.
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

final BehaviorSubject<Long> subject = BehaviorSubject.create(-1L);

log("observer1 subscribe");
subject.subscribe(observer1);

postDelayed(new Runnable() {
  @Override
  public void run() {
      log("subject subscribe");
      observable.subscribe(subject);
  }
}, 2000);

postDelayed(new Runnable() {
  @Override
  public void run() {
      log("observer2 subscribe");
      subject.subscribe(observer2);
  }
}, 7500);
//Создаем BehaviorSubject c начальным значением -1, сразу подписываем на него Observer1,
// и чуть позже подписываем Subject на Observable.
// Спустя какое то время подписываем Observer2 на BehaviorSubject.
//
//
//Результат
//0 observer1 subscribe
//0 observer1 onNext value = -1
//2000 subject subscribe
//3000 observer1 onNext value = 0
//4000 observer1 onNext value = 1
//5000 observer1 onNext value = 2
//6000 observer1 onNext value = 3
//7000 observer1 onNext value = 4
//7500 observer2 subscribe
//7500 observer2 onNext value = 4
//8000 observer1 onNext value = 5
//8000 observer2 onNext value = 5
//9000 observer1 onNext value = 6
//9000 observer2 onNext value = 6
//10000 observer1 onNext value = 7
//10000 observer2 onNext value = 7
//11000 observer1 onNext value = 8
//11000 observer2 onNext value = 8
//12000 observer1 onNext value = 9
//12000 observer2 onNext value = 9
//12000 observer1 onCompleted
//12000 observer2 onCompleted
//
//Observer1 при подписке получил дефолтное значение, т.к. Subject еще не получал никаких данных.
// А Observer2 при подписке получил последний элемент, который был на тот момент в Subject - это 4.
//
//Самое очевидное применение BehaviorSubject - хранение какого-либо статуса.
// При подключении вы всегда получите либо текущее значение, либо дефолтное.
// Ну и далее будете получать новые значение при смене статуса.
}
