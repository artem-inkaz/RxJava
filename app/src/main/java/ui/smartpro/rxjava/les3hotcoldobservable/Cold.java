package ui.smartpro.rxjava.les3hotcoldobservable;

import java.util.concurrent.TimeUnit;

public class Cold {
//Cold Observable
//Давайте на примере двух подписчиков посмотрим, как поведет себя Observable.
//
//Для примера возьмем оператор interval:

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

log("observable create");
final Observable<Long> observable = Observable
        .interval(1, TimeUnit.SECONDS)
        .take(5);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer1 subscribe");
        observable.subscribe(observer1);
    }
}, 3000);

postDelayed(new Runnable() {
    @Override
    public void run() {
        log("observer2 subscribe");
        observable.subscribe(observer2);
    }
}, 5500);
//
//Cоздаем два Observer объекта. Затем Создаем Observable, который генерирует числа каждую 1 секунду
// (оператор interval) и ограничим его последовательность 5-ю элементами (оператор take).
//
//Далее, подписываем observer1 через 3000 мсек после запуска, а observer2 через 5500 мсек после запуска.
//
//Метод log - выводит в лог текст и время вызова. Не привожу здесь его код,
// т.к. он не имеет отношения к теме. Метод postDelayed - выполняет предоставленный Runnable
// через указанное количество мсек.
//
//Результат:
//0 observable create
//3000 observer1 subscribe
//4000 observer1 onNext value = 0
//5000 observer1 onNext value = 1
//5500 observer2 subscribe
//6000 observer1 onNext value = 2
//6500 observer2 onNext value = 0
//7000 observer1 onNext value = 3
//7500 observer2 onNext value = 1
//8000 observer1 onNext value = 4
//8000 observer1 onCompleted
//8500 observer2 onNext value = 2
//9500 observer2 onNext value = 3
//10500 observer2 onNext value = 4
//10500 observer2 onCompleted
//
//
//Число в начале каждой строки - это время, прошедшее от запуска.
//
//Сначала создается observable. Затем, через 3 секунды подписывается observer1 и
// начинает получать данные с 0. Затем, еще через 2.5 секунды подписывается observer2 и
// тоже начинает получать данные с 0. Но это никак не сказывается на observer1,
// который продолжает получать свои данные (2, 3, 4) и в итоге получает onCompleted.
// И observer2 продолжает получать свои оставшиеся данные и onCompleted.
//
//Этот пример показывает, что:
//
//1) Observable для каждого нового подписчика начинает генерировать данные с начала.
//И observer1 и observer2 получали данные, начиная с 0, хотя подписались в разное время.
//
//2) Observable начинает свою работу в момент подписки.
//observer1 подписался на Observable через 3 секунды после создания. И он начал получать данные с начала,
// т.е. с 0. Значит, все эти три секунды, Observable ничего не делал.
//
//Очень важно понимать два этих принципа.
//
//Т.е. просто создание Observable не приводит ни к чему. Этот созданный Observable ничего не будет делать.
// Он начнет работу только когда кто-либо подпишется на него. И для каждого нового подписчика
// он будет начинать работу заново, независимо от предыдущих подписчиков.
// Такой Observable называется Cold Observable.
}
