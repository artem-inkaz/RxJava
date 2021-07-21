package ui.smartpro.rxjava.les4subject;

import rx.functions.Action1;
import rx.subjects.SerializedSubject;

public class Serialazied {
//SerializedSubject
//Метод Observer.onNext (и соответственно Subject.onNext) не является потокобезопасным.
// Вызывая его из разных потоков, вы можете получить непредсказуемые результаты.
//
//Рассмотрим пример как не надо делать:

final PublishSubject<Long> subject = PublishSubject.create();

final Action1<Long> action = new Action1<Long>() {

    private long sum = 0;

    @Override
    public void call(Long aLong) {
        sum += aLong;
    }

    @Override
    public String toString() {
        return "sum = " + sum;
    }
};

subject.subscribe(action);

new Thread() {
    @Override
    public void run() {
        super.run();
        for (int i = 0; i < 100000; i++) {
            subject.onNext(1L);
        }
        log("first thread done");
    }
}.start();

new Thread() {
    @Override
    public void run() {
        super.run();
        for (int i = 0; i < 100000; i++) {
            subject.onNext(1L);
        }
        log("second thread done");
    }
}.start();

postDelayed(new Runnable() {
    @Override
    public void run() {
        log(action.toString());
    }
}, 2000);
//Создаем PublishSubject и подписываем на него Action, который все получаемые данные будет суммировать
// в переменную sum. Далее запускаем два потока, и в каждом из них, мы 100 000 раз отправляем
// в Subject значение 1. И через 2 секунды (чтобы потокам уж точно хватило времени выполниться)
// выводим в лог значение переменной sum.
//
//Если работа с потоками вам пока незнакома, то вы можете предположить,
// что в результате мы получим sum = 200 000, т.к. один поток отправит 100 000 и другой столько же.
// Но, к сожалению, не все так просто.
//
//Результат
//first thread done
//second thread done
//sum = 180786
//
//В итоге мы получили совсем не 200 000, потому что два разных потока читают/пишут одну переменную и
// неизбежно возникают коллизии. И при каждом запуске результат будет разным.
//
//Как вариант, вы можете доработать Action так, чтобы он был потокобезопасен.
// Либо можно использовать SerializedSubject, который сам со своей стороны сделает вызов метода onNext потокобезопасным.
//
//Немного изменим пример, используя SerializedSubject:

final PublishSubject<Long> subject = PublishSubject.create();

final SerializedSubject<Long, Long> serializedSubject = new SerializedSubject<>(subject);

final Action1<Long> action = new Action1<Long>() {

    private long sum = 0;

    @Override
    public void call(Long aLong) {
        sum += aLong;
    }

    @Override
    public String toString() {
        return "sum = " + sum;
    }
};

subject.subscribe(action);

new Thread() {
    @Override
    public void run() {
        super.run();
        for (int i = 0; i < 100000; i++) {
            serializedSubject.onNext(1L);
        }
        log("first thread done");
    }
}.start();

new Thread() {
    @Override
    public void run() {
        super.run();
        for (int i = 0; i < 100000; i++) {
            serializedSubject.onNext(1L);
        }
        log("second thread done");
    }
}.start();

postDelayed(new Runnable() {
    @Override
    public void run() {
        log(action.toString());
    }
}, 2000);
//Мы добавили создание SerializedSubject из PublishSubject, и далее в потоках используем уже именно его.
// Т.е. SerializedSubject является оберткой для других Subject и позволяет сделать их потокобезопасными.
//
//Результат
//second thread done
//first thread done
//sum = 200000
//
//Потоки будут работать немного дольше, но зато результат будет корректным.
//
//И раз уж мы заговорили о потоках, в RxJava есть отличные инструменты по работе с ними,
// и следующий урок курса будет именно об этом.
//
//
//Общие методы
//Напоследок несколько слов об общих методах для всех Subject
//
//Метод toSerialized - создает SerializedSubject обертку для вашего Subject.
//
//Метод hasObservers подскажет есть ли у вашего Subject подписчики.
//
//Метод asObservable - вернет Observable обертку для вашего Subject. Это может быть полезным,
// когда в вашем классе есть Subject, и вам надо предоставить его для внешних подписчиков.
// Но если вы вытащите наружу Subject, то любой сможет вызвать его методы onNext, onError и onCompleted.
// Скорее всего это нарушит логику работы вашего класса. Поэтому вы можете вернуть просто Observable,
// используя метод asObservable, и внешние объекты смогут только подписываться.
}
