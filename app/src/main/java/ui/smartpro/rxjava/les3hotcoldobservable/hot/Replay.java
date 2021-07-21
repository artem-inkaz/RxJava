package ui.smartpro.rxjava.les3hotcoldobservable.hot;

import java.util.concurrent.TimeUnit;

public class Replay {
//replay
//Оператор replay аналогично оператору publish создает ConnectableObservable.
// Но вновь прибывшие подписчики будут получать элементы, которые они пропустили.

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
        .replay();

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
//Используем тот же пример, что и с publish.
//
//
//Результат
//0 observable connect
//2500 observer1 subscribe
//2500 observer1 onNext value = 0
//2500 observer1 onNext value = 1
//3000 observer1 onNext value = 2
//4000 observer1 onNext value = 3
//4500 observer2 subscribe
//4500 observer2 onNext value = 0
//4500 observer2 onNext value = 1
//4500 observer2 onNext value = 2
//4500 observer2 onNext value = 3
//5000 observer2 onNext value = 4
//5000 observer1 onNext value = 4
//6000 observer2 onNext value = 5
//6000 observer1 onNext value = 5
//6000 observer2 onCompleted
//6000 observer1 onCompleted
//
//Мы вызываем метод connect и работа началась. Спустя какое-то время мы подписываем получателей.
// Видно, что сразу же после подписки Observer получает элементы, которые он пропустил. observer1
// получил 0 и 1 сразу после того, как подписался (время 2500). А observer2 сразу после подписки
// (время 4500) получил 0,1,2 и 3. Ну и далее все как обычно - оба observer одновременно получают
// данные и onCompleted.
//
//Т.е. этот Hot Observable кэширует данные и отправляет их всем новым подписчикам, чтобы они ничего
// не пропустили.
//
//Метод replay имеет различные варианты, в которых вы можете указать кол-во хранимых элементов или
// время их хранения.
}
