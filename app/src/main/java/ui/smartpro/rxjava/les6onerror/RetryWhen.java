package ui.smartpro.rxjava.les6onerror;

public class RetryWhen {
//Оператор retryWhen
//Интересный оператор. Далеко не сразу удалось понять принцип его работы и
// как это можно использовать в своих целях. Но в итоге я продрался сквозь эти дебри
// (за исключением одного момента) и спешу поделиться с вами )
//
//Итак, у нас есть Observable (далее observableMain), в котором может возникнуть ошибка.
// Мы добавляем к нему оператор retryWhen. Для retryWhen нам необходимо использовать функцию:
//Func1<Observable<? extends Throwable>, Observable<?>>
//
//Т.е. на вход функции мы получаем Observable<? extends Throwable> (далее observableErrors),
// а вернуть нам надо Observable<?> (далее observableRetry).
//
//Давайте разбираться, что из себя представляет observableErrors, и как нам создать observableRetry.
//
//observableErrors - это Observable, который будет постить все возникающие при работе ошибки.
// Т.е. как только в observableMain возникнет ошибка, observableErrors перехватит ее и отправит своим подписчикам.
//
//Наша задача - на основе observableErrors создать свой observableRetry.
// Этот observableRetry будет использован оператором retryWhen для принятия решения, что делать с ошибкой.
// Т.е. retryWhen будет реагировать на события observableRetry:
//onNext - сигнал о том, что надо запускать retry для observableMain
//onError - observableMain завершится с onError
//onCompleted - observableMain завершится с onCompleted
//
//При этом тип данных observableRetry абсолютно не важен. Главное - какое событие пришло.
//
//Распишу схему еще раз, кратко, по пунктам:
//- в observableMain произошла ошибка
//- оператор retryWhen перехватывает ее и отправляет в observableErrors.
// Слушатели observableMain не получают ничего.
//- оператор retryWhen ждет событий от observableRetry, чтобы либо выполнить retry (в случае onNext),
// либо завершить observableMain с onError (в случае onError), либо завершить observableMain с onCompleted(в случае onCompleted)

//Давайте разберем этот оператор на примерах, чтобы стало понятнее.
//
//Сначала самый простой пример:

Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");

Observable<Long> observableMain = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
            }
        })
        .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Throwable> observableErrors) {
                log("retryWhen");
                return observableErrors;
            }
        });

observableMain.subscribe(new Observer<Long>() {
    @Override
    public void onCompleted() {
        log("onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        log("onError " + e);
    }

    @Override
    public void onNext(Long aLong) {
        log("onNext " + aLong);
    }
});
//В функции мы просто возвращаем полученный Observable.
// Т.е. в качестве observableRetry используем полученный observableErrors.
//
//Что это означает? Когда в observableMain произойдет ошибка, retryWhen перехватит ее и
// отправит в observableErrors. А т.к. в качестве observableRetry мы взяли observableErrors,
// значит retryWhen тут же получит onNext от observableRetry.
// А получение onNext от observableRetry оператор retryWhen воспринимает как сигнал к тому,
// что надо запустить retry для observableMain. И, соответственно, выполняется retry и
// мы снова начинаем получать данные от observableMain.
//
//Далее снова возникает ошибка конвертации, и все цепочка повторяется. И так далее.
// Т.е. мы тут опять вошли в бесконечный цикл. Т.е. такая реализация оператора retryWhen
// равнозначна просто оператору retry().
//
//Результат
//retryWhen
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//...
//
//В логах видно, что мы получаем данные пока не случается ошибка, а затем идет перезапуск и данные идут сначала.
//Обратите внимание, что функция в retryWhen выполнилась в самом начале.
// Это логично, т.к. retryWhen должен быть готов обрабатывать ошибки до начала передачи данных.
//
//Давайте поставим ограничение на количество попыток. Т.е. сделаем аналог оператора retry(count).
//
//retry выполняется оператором retryWhen при получении onNext от observableRetry.
// Значит, чтобы бесконечно не выполнять retry, а ограничиться несколькими попытками,
// надо чтобы observableRetry после нескольких onNext, отправил onError или onCompleted.
// Ограничить observableRetry мы можем оператором take.

Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");

Observable<Long> observableMain = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
            }
        })
        .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Throwable> observableErrors) {
                log("retryWhen");
                return observableErrors.take(3);
            }
        });

observableMain.subscribe(new Observer<Long>() {
    @Override
    public void onCompleted() {
        log("onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        log("onError " + e);
    }

    @Override
    public void onNext(Long aLong) {
        log("onNext " + aLong);
    }
});
//Мы указали take(3). Значит по мере поступления ошибок в observableErrors,
// только три первых пойдут дальше в onNext, а затем выполнится onCompleted.
//
//
//Результат
//retryWhen
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//onNext 1
//onNext 2
//onCompleted
//
//А вот логи показывают немного неожиданный для меня результат. retry должен был выполниться 3 раза.
// Но он выполнился почему-то только 2 раза.
//
//Я думаю, что последний retry просто не успевает сработать.
// Т.е. от observableRetry приходит первый onNext и выполняется retry.
// Затем приходит второй onNext и выполняется retry. Затем приходит третий onNext и сразу же за ним onCompleted.
// И этот onCompleted сообщает, что вся работа закончена и ничего больше делать нельзя,
// в том числе и никаких retry. Возможно, onNext ставит вызов retry в какую-то очередь,
// и третий retry сработал уже после onCompleted.
//
//В общем, это все мои догадки и точно я пока не могу ничего сказать.
// Если выясню, то дополню урок.
//
//Также обратите внимание, что мы получили onCompleted, а не onError.
// Т.е. в этой реализации мы даже не узнаем, что была какая-то ошибка.
// Это не всегда может быть удобно, поэтому изменим реализацию так, чтобы получать ошибку в onError.

Observable<String> stringData = Observable.just("1", "2", "a", "4", "5");

Observable<Long> observableMain = stringData
        .map(new Func1<String, Long>() {
            @Override
            public Long call(String s) {
                return Long.parseLong(s);
            }
        })
        .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Throwable> observableErrors) {
                return observableErrors
                        .zipWith(Observable.range(1, 3), new Func2<Throwable, Integer, Observable>() {
                            @Override
                            public Observable call(Throwable throwable, Integer integer) {
                                if (integer < 3) {
                                    return Observable.just(0L);
                                } else {
                                    return Observable.error(throwable);
                                }
                            }
                        })
                        .flatMap(new Func1<Observable, Observable<?>>() {
                            @Override
                            public Observable<?> call(Observable observable) {
                                return observable;
                            }
                        });

            }
        });

observableMain.subscribe(new Observer<Long>() {
    @Override
    public void onCompleted() {
        log("onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        log("onError " + e);
    }

    @Override
    public void onNext(Long aLong) {
        log("onNext " + aLong);
    }
});
//Создание observableRetry стало значительно сложнее. Сначала мы с помощью оператора zipWith соединяем
// попарно элементы observableErrors и range(1, 3). Т.е. первая ошибка попадет в zip-функцию вместе с числом 1,
// вторая ошибка - с числом 2, третья - с числом 3. Т.е. число здесь используем как счетчик ошибок.
// В функции мы смотрим на этот счетчик и если он меньше 3,
// то в качестве результата возвращаем Observable с одним элементом Observable.just(0L).
// А если счетчик достиг 3, то возвращаем Observable, который вернет ошибку,
// которая пришла из observableErrors.
//
//Т.е. для первых двух ошибок наш observableRetry запостит onNext, который приведет к тому,
// что будет вызван retry. А при третьей ошибки observableRetry запостит onError.
//
//Напомню, что абсолютно без разницы, какой тип данных будет в observableRetry.
// Я в этом примере передал 0. Главное - что этот 0 пойдет как onNext, а значит вызовет retry.
// Вы можете вместо Observable.just(0L) вернуть, например Observable.timer(5, TimeUnit.SECONDS).
// Этот Oservable тоже вернет 0L, но сделает это через 5 секунд.
// А значит и retry будет выполнен через 5 секунд после ошибки.
//
//
//Функция в zipWith возвращает Observable, т.е. на выходе из zipWith мы получаем Observable<Observable>.
// Чтобы вернутся к нормальному Observable используем flatMap. Возможно, это пока непонятно,
// но не беспокойтесь, в следующем уроке мы подробно рассмотрим этот оператор.
}
