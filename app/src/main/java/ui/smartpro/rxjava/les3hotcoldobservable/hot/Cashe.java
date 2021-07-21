package ui.smartpro.rxjava.les3hotcoldobservable.hot;

import java.util.concurrent.TimeUnit;

public class Cashe {
//cache
//Еще один пример, как можно получить Hot Observable - это оператор cache. Observable,
// который будет получен в результате работы этого оператора,
// будет похож на результат операторов replay().autoConnect(). Он начинает работу при первом подписчике,
// хранит все элементы и выдает их каждому новому подписчику (даже если он пропустил).
//
//Рассмотрим на примере, аналогичном последнему примеру:

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
        .take(10)
        .cache();


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
}, 4000);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer1 unsubscribe");
        subscription1.unsubscribe();
    }
}, 5500);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer2 unsubscribe");
        subscription2.unsubscribe();
    }
}, 6500);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer1 subscribe");
        observable.subscribe(observer1);
    }
}, 7500);
//Создаем 2 Observer, создаем Observable, подписываем observer1 и observer2, отписываем оба, и
// снова подписываем observer1.
//
//Результат
//1500 observer1 subscribe
//2500 observer1 onNext value = 0
//3500 observer1 onNext value = 1
//4000 observer2 subscribe
//4000 observer2 onNext value = 0
//4000 observer2 onNext value = 1
//4500 observer1 onNext value = 2
//4500 observer2 onNext value = 2
//5500 observer1 unsubscribe
//5500 observer2 onNext value = 3
//6500 observer2 unsubscribe
//7500 observer1 subscribe
//7500 observer1 onNext value = 0
//7500 observer1 onNext value = 1
//7500 observer1 onNext value = 2
//7500 observer1 onNext value = 3
//7500 observer1 onNext value = 4
//7500 observer1 onNext value = 5
//8500 observer1 onNext value = 6
//9500 observer1 onNext value = 7
//10500 observer1 onNext value = 8
//11500 observer1 onNext value = 9
//11500 observer1 onCompleted
//
//Observable начинает работу после подписки observer1. Далее observer2, после того как подписался,
// сразу же получает элементы, которые он пропустил. Затем оба отписываются.
// Но Observable не прекращает работу. Это становится видно, когда observer1 снова подписывается.
// Он получает все элементы, которые уже были созданы ранее и продолжает получать их,
// пока последовательность не завершается вызовом onCompleted. Т.е. видно,
// что Observable не останавливает работу, когда не остается подписчиков.
//
//
//
//Рекомендую вам поработать с примерами, чтобы получить ответы на оставшиеся вопросы.
// Практика тут очень хорошо помогает понять теорию.
//
//В качестве примеров Cold Obervable можно привести операторы interval или from.
// Они создают типичные Cold Observable, которые работают для каждого подписчика отдельно.
//
//А Hot Observable - это, например, Observable, который сообщает о нажатиях на кнопку или о вводе символов в EditText.
// Тут уже события будут генерироваться независимо от того, подписан кто-либо или нет.
}
