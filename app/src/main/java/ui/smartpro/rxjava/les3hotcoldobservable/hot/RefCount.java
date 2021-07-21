package ui.smartpro.rxjava.les3hotcoldobservable.hot;

import java.util.concurrent.TimeUnit;

public class RefCount {
//refCount
//Мы разобрались с принципом работы ConnectableObservable. Вызываем метод connect -
// он начинает работу и возвращает нам Subscription. Вызываем у этого Subscription метод unsubscribe -
// работа прекращается. Ну а наличие/отсутствие подписчиков не влияет ни на что.
//
//Но для ConnectableObservable существует возможность сделать так,
// чтобы он начинал работать при первом появившемся подписчике, и заканчивал после того,
// как отпишется последний.
//
//Можно провести аналогию с обычным офисом. Первый человек, который приходит утром - включает свет.
// А кто последний вечером уходит - выключает. Соответственно, первый подписчик стартует работу,
// когда подписывается, а последний отписавшийся ее останавливает.
//
//Чтобы это реализовать, у класса ConnectableObservable есть оператор refCount.
// Он из ConnectableObservable сделает Observable, который будет генерировать элементы только пока есть подписчики.
//
//Почему от ConnectableObservable мы снова возвращаемся к Observable?
// Потому что нам более не нужен метод connect. Работа начнется сама после того,
// как подпишется первый подписчик. Но надо понимать, что это будет Hot Observable,
// т.к. он будет раздавать одни и те же данные всем подписчикам, а не стартовать работу заново для каждого из них.
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
        .take(6)
        .publish()
        .refCount();


postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer1 subscribe");
        subscription1 = observable.subscribe(observer1);
    }
}, 1500);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer2 subscribe");
        subscription2 = observable.subscribe(observer2);
    }
}, 3000);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer1 unsubscribe");
        subscription1.unsubscribe();
    }
}, 5000);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer2 unsubscribe");
        subscription2.unsubscribe();
    }
}, 6000);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer1 subscribe");
        observable.subscribe(observer1);
    }
}, 6500);
//
//
//Создаем Observable, подписываем на него observer1, затем observer2, затем отписываем оба и
// снова подписываем observer1.
//
//Результат:
//1500 observer1 subscribe
//2500 observer1 onNext value = 0
//3000 observer2 subscribe
//3500 observer1 onNext value = 1
//3500 observer2 onNext value = 1
//4500 observer1 onNext value = 2
//4500 observer2 onNext value = 2
//5000 observer1 unsubscribe
//5500 observer2 onNext value = 3
//6000 observer2 unsubscribe
//6500 observer1 subscribe
//7500 observer1 onNext value = 0
//8500 observer1 onNext value = 1
//9500 observer1 onNext value = 2
//10500 observer1 onNext value = 3
//11500 observer1 onNext value = 4
//12500 observer1 onNext value = 5
//12500 observer1 onCompleted
//
//Observable начал работать в момент подписки observer1. Далее подписывается observer2 и
// они вместе получают данные, затем поочередно отписываются.
// Когда отписывается observer2 (время 6000), Observable прекращает работу, т.к. подписчиков больше нет.
// По логам это не видно, но когда мы еще раз подписываем observer1 (время 6500),
// работа начинается заново, а значит предыдущая была завершена.
//
//
//Обратите внимание на операторы используемые при создании Observable. Сначала это interval и take.
// Они дают нам Observable. Затем publish оборачивает его в ConnectableObservable.
// А оператор refCount оборачивает все это снова в Observable. И это нормально,
// т.к. Observable обычно представляет из себя кучу оберток.
// У Observable, кстати, есть оператор share, который как раз равен комбинации операторов publish и refCount.
//
//Разумеется, вместо publish вы можете использовать replay, чтобы создать ConnectableObservable,
// и далее использовать refCount. Получившийся Observable будет также стартовать и останаливаться в
// завимости от наличия подписчиков, но при это добавится кэш и все новые подписчики при подписке будут
// получать пропущенные ими данные.
//
//
//Как видите, Hot Observable - это вовсе необязательно ConnectableObservable.
// Это может быть и обычный Observable. Главное, что все подписчики получают одни и те же данные.
// И Observable не стартует работу заново для каждого нового подписчика.
}
