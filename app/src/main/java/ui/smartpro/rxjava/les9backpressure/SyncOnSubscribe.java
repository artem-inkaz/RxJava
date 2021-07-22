package ui.smartpro.rxjava.les9backpressure;

import rx.Observable;
import rx.functions.Func0;

public class SyncOnSubscribe {

//SyncOnSubscribe
//Создать свой Observable, который поддерживает backpressure - достаточно сложно,
// поэтому вместо этого рекомендуется использовать уже готовые операторы, такие как range, from и т.д.
//
//Но если все-таки необходимо создать свой Observable, то RxJava предоставляет backpressure обертку - SyncOnSubscribe.
//
//
//Снова рассмотрим пример, который отправляет числа от 1 до 20:

Observable.OnSubscribe<Integer>
        onSubscribe = SyncOnSubscribe.createStateful(
        new Func0<Integer>() {
            @Override
            public Integer call() {
                return 1;
            }
        },
        new Func2<Integer, Observer<? super Integer>, Integer>() {
            @Override
            public Integer call(Integer integer, Observer<? super Integer> observer) {
                log("call " + integer);
                if (integer <= 20) {
                    observer.onNext(integer);
                } else {
                    observer.onCompleted();
                }
                return integer + 1;
            }
        }
);
//Метод createStateful создает OnSubscribe, который будет поддерживать backpressure.
//
//На вход нам необходимо передать две функции.
//
//Первая - должна возвращать начальное состояние. Мы возвращаем
// 1. Далее это начальное состояние пойдет во вторую функцию как первый параметр.
//
//Вторая функция берет значение (текущее состояние), и либо отправляет его подписчику (если <= 20),
// либо завершает последовательность. А вернуть вторая функция должна новое состояние.
// В нашем случае мы просто увеличиваем число на 1 и получаем это новое состояние,
// которое снова придет к нам в следующем вызове функции.
// Т.е. каждый следующий вызов функции будет получать значение, которое вернул предыдущий вызов.
//
//Не очень тривиально, зато эта обертка обеспечит поддержку backpressure для вашего OnSubscribe.

//Кроме createStateful, существуют также createStateless (вообще без состояния) и createSingleState
// (состояние - это всегда один и тот же объект). Какой из них выбрать - зависит от вашей задачи.
}
