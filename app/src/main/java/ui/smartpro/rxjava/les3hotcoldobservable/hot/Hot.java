package ui.smartpro.rxjava.les3hotcoldobservable.hot;

import java.util.concurrent.TimeUnit;

public class Hot {
//Hot Observable
//Hot Observable более соответствует привычному Observer паттерну, и работает независимо от своих подписчиков.
//
//Рассмотрим пример:

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

final ConnectableObservable<Long> observable = Observable
        .interval(1, TimeUnit.SECONDS)
        .take(6)
        .publish();

log("observable connect");
observable.connect();

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer1 subscribe");
        observable.subscribe(observer1);
    }
}, 2500);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer2 subscribe");
        observable.subscribe(observer2);
    }
}, 4500);
//
//Создаем два Observer. Затем создаем interval Observable, который возьмет только первые 6 элементов,
// и оператором publish делаем из него Hot Observable. Оператор publish создает объект ConnectableObervable.
// Этот объект имеет метод connect, который начнет работу Observable, независимо от того, подписан кто-то или нет.
//
//Мы вызываем метод connect, и далее подписываем два Observer после небольших пауз.
//
//
//Результат
//0 observable connect
//2500 observer1 subscribe
//3000 observer1 onNext value = 2
//4000 observer1 onNext value = 3
//4500 observer2 subscribe
//5000 observer1 onNext value = 4
//5000 observer2 onNext value = 4
//6000 observer1 onNext value = 5
//6000 observer2 onNext value = 5
//6000 observer1 onCompleted
//6000 observer2 onCompleted
//
//
//По логам видно, что первый Observer начал получать данные с третьего элемента (2).
// Он пропустил первые два элемента (0,1), т.к. подписался только через 2500 мсек после
// старта работы (connect). А второй Observable подписался через 4500 мсек после connect, и,
// тем самым, пропустил первые 4 элемента, начав получать данные с пятого (4).
//
//Далее оба Observer получают одни и те же данные одновременно. И в конце оба получают onCompleted.
//
//Таким образом, видно, что Hot Observable генерирует данные независимо от подписчиков.
// И все подписчики получают одни и те же данные в одно и то же время. Это ключевое отличие от Cold Observable,
// который для каждого подписчика начинает заново генерировать данные.
//
//Ключевой оператор в этом примере - это оператор publish.
// Именно он взял обычный Cold Observable, который мы создали операторами interval и take,
// обернул его в Hot Observable, и вернул нам как объект ConnectableObservable.
//
//Можно примерно предположить что происходит при вызове метода connect.
// Т.е. у нас есть Hot Observable, который является оберткой для Cold Observable,
// и мы вызываем его метод connect. В этот момент Hot Observable просто подписывается на Cold Observable,
// тем самым стартуя его работу. И далее, Hot Observable получает данные от Cold Observable и
// пересылает их всем своим подписчикам (если они есть).
//
//Стартовать ConnectableObservable можно методом connect. А как его остановить?
// Метод connect возвращает нам объект Subscription. Его метод unsubscribe можно использовать чтобы
// остановить работу Cold Observable внутри Hot Observable.
//
//Кроме connect, у класса ConnectableObservable есть метод autoConnect.
// Он позволит вам указать количество подписавшихся,
// по достижению которого, метод connect будет вызван автоматически.
//
//Советую вам немного потестировать последний пример, чтобы лучше понять принцип работы Hot Observable.
// Например, попробуйте сначала подписать первый Observable, а затем уже вызвать connect.
}
