package ui.smartpro.rxjava.les5subscribeonobserveon;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public class Rx {
//RxJava содержит механизмы, который позволят вам указать, в каком потоке операторы будут выполнять свою работу.
// Например, вы легко можете сделать так, чтобы данные сгенерировались в одном потоке, обработались в другом,
// а получателю ушли в третьем.
//
//Но сначала давайте снова вернемся к созданию своего кастомного Observable, чтобы лучше понять,
// что и в каком потоке выполняется по умолчанию.

//Рассмотрим пример

final Observer<Integer> observer = new Observer<Integer>() {
    @Override
    public void onCompleted() {
        log("observer onCompleted");
    }

    @Override
    public void onError(Throwable e) {}

    @Override
    public void onNext(Integer vaule) {
        log("observer onNext value = " + vaule);
    }
};

Observable.OnSubscribe onSubscribe = new Observable.OnSubscribe<Integer>() {
    @Override
    public void call(Subscriber<? super Integer> subscriber) {
        log("call");
        for (int i = 0; i < 3; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext(i);
        }
        subscriber.onCompleted();
    }
};

Observable<Integer> observable = Observable.create(onSubscribe);

log("subscribe");
observable.subscribe(observer);

log("done");
// Мы создаем свою реализацию Observable в которой отправляем подписчику 3 элемента через каждые 100 мсек.
// Я здесь не использую проверку isUnsubscribed, чтобы не усложнять код.
//
//
//Результат:
//0 subscribe [main]
//0 call [main]
//100 observer onNext value = 0 [main]
//200 observer onNext value = 1 [main]
//300 observer onNext value = 2 [main]
//300 observer onCompleted [main]
//300 done [main]
//
//
//В квадратных скобках указано имя процесса. Видим, что все действия выполнились в main потоке.
// При вызове метода subscribe был вызван метод call, который блокировал поток вызовами метода sleep.
// Соответственно метод subscribe выполнялся 300 мсек.
// И только после отправки всех элементов вызов метода subscribe был завершен, и программа пошла дальше (done).
}
