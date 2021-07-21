package ui.smartpro.rxjava.les5subscribeonobserveon;

import rx.functions.Func1;

public class ObserveOn {
//observeOn
//Для этого используется метод observeOn, добавим его при создании Observable.

Observable<Integer> observable = Observable
        .create(onSubscribe)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
//В методе observeOn я использовал шедулер AndroidSchedulers.mainThread(). Забегая чуть вперед - скажу,
// что это шедулер из библиотеки RxAndroid. Он будет выполнять работу в main потоке.
// Чтобы использовать его в коде, добавьте в gradle строку:
//compile 'io.reactivex:rxandroid:1.2.1'
//
//
//Теперь, благодаря observeOn, подписчик должен получить данные в main потоке.
// Оператор subscribeOn также остается с нами и гарантирует,
// что данные будут сгенерированы в отдельном потоке из IO-шедулера.
//
//Результат:
//0 subscribe [main]
//0 done [main]
//0 call [RxIoScheduler-2]
//100 observer onNext value = 0 [main]
//200 observer onNext value = 1 [main]
//300 observer onNext value = 2 [main]
//300 observer onCompleted [main]
//
//
//Метод call был выполнен в IO-потоке (указанном в subscribeOn), а методы подписчиков onNext и
// onCompleted были вызваны в main-потоке (указанном в observeOn).

//Важно понять разницу между операторами subscribeOn и observeOn. Давайте разберем этот момент подробнее.
// Рассмотрим абстрактный пример создания Observable.
//
//Это выглядит примерно так:

Observable observable = createOperator(...)
    .operator1(...)
    .operator2(...)
    .operator3(...)
    .operator4(...)
    .operator5(...);
// При создании Observable мы используем какой-либо один оператор создания (createOperator),
// а затем (если необходимо) добавляем операторы преобразования, фильтрации и т.п.
// В итоге у нас получается последовательность из нескольких операторов, которая дает нам итоговый Observable.
//
//Т.к. оператор создания один, то и subscribeOn нам надо будет использовать только один раз.
// И не важно, в каком месте последовательности операторов вы используете оператор subscribeOn,
// сразу после оператора создания или в конце последовательности.
//
//Т.е. вы можете сделать так:

Observable observable = createOperator(...)
    .subscribeOn(scheduler)
    .operator1(...)
    .operator2(...)
    .operator3(...)
    .operator4(...)
    .operator5(...);
// а можете так:

//Observable observable = createOperator(...)
    .operator1(...)
    .operator2(...)
    .operator3(...)
    .operator4(...)
    .operator5(...)
    .subscribeOn(scheduler);
// Результат будет одним и тем же. Где бы он ни был вызван, оператор subscribeOn задает, в каком потоке будет работать оператор создания.

//А вот с оператором observeOn все гораздо интереснее. Давайте еще раз взглянем на пример c subscribeOn:

Observable observable = createOperator(...)
    .subscribeOn(scheduler)
    .operator1(...)
    .operator2(...)
    .operator3(...)
    .operator4(...)
    .operator5(...);
//В этом случае subscribeOn указывает поток в котором будет выполнено создание данных.
// И в этом же потоке отработают все остальные операторы.
// И в этом же потоке данные будут отправлены подписчикам.
//
//Т.е. по умолчанию все операторы выполнятся в том же потоке, в котором выполнился оператор создания.
// А оператор observeOn позволяет нам это изменить и переключить поток,
// и последующие операторы будут работать уже в другом потоке.
//
//Давайте рассмотрим на примерах, так проще будет понять:

Observable observable = createOperator(...)
    .subscribeOn(scheduler)
    .observeOn(scheduler1)
    .operator1(...)
    .operator2(...)
    .operator3(...)
    .operator4(...)
    .operator5(...);
//Все операторы (кроме createOperator) будут выполнены в потоке scheduler1,
// потому что все операторы идут после observeOn. Получение данных будет выполнено в том же потоке,
// что и последний оператор, т.е. тоже в scheduler1.
//
//Переместим оператор observeOn чуть подальше в последовательности операторов:

Observable observable = createOperator(...)
    .subscribeOn(scheduler)
    .operator1(...)
    .operator2(...)
    .operator3(...)
    .observeOn(scheduler1)
    .operator4(...)
    .operator5(...);
//Только операторы 4 и 5 будут выполнены в потоке scheduler1, т.к. только два этих оператора идут
// после оператора observeOn(scheduler1). Операторы 1,2 и 3 будут выполнены в том, же потоке, в котором работал createOperator.
//Получение данных будет выполнено в том же потоке, что и последний оператор, т.е. в scheduler1.
//
//Добавим еще один observeOn:

Observable observable = createOperator(...)
    .subscribeOn(scheduler)
    .operator1(...)
    .observeOn(scheduler1)
    .operator2(...)
    .operator3(...)
    .observeOn(scheduler2)
    .operator4(...)
    .operator5(...);
//Оператор 1 будет выполнен в том же потоке, что и оператор создания, т.е. в scheduler.
// А вот далее observeOn переключает поток на scheduler1, и в этом потоке выполнятся операторы 2 и 3.
// Далее observeOn переключает поток на scheduler2, и в этом потоке выполнятся операторы 4,5 и
// получение данных подписчиками.
//
//Про оператор 1 поясню на всякий случай еще раз. Он выполнится в потоке scheduler не потому,
// что он идет после subscribeOn. Оператор subscribeOn задает поток только для оператора создания.
// А т.к. перед оператором 1 нет никакого оператора observeOn (который меняет поток),
// то оператор 1 будет выполнен в том же потоке, что и оператор создания.
//
//
//Проверим на практике. Используем map-функцию, которая будет просто умножать число на 10 и
// посмотрим в каком потоке она будет выполняться
//
//Напишем функцию:

Func1<Integer, Integer> func = new Func1<Integer, Integer>() {
    @Override
    public Integer call(Integer integer) {
        log("func " + integer);
        return integer * 10;
    }
};
//
//
//Перепишем создание Observable:

Observable<Integer> observable = Observable
       .create(onSubscribe)
       .subscribeOn(Schedulers.io())
       .map(func)
       .observeOn(AndroidSchedulers.mainThread());
//Добавили оператор map

//Результат:
//100 subscribe [main]
//200 done [main]
//200 call [RxIoScheduler-2]
//300 func 0 [RxIoScheduler-2]
//300 onNext: 0 [main]
//400 func 1 [RxIoScheduler-2]
//400 onNext: 10 [main]
//500 func 2 [RxIoScheduler-2]
//500 onNext: 20 [main]
//500 onCompleted [main]
//
//Функция выполнилась в IO-потоке, т.е. в том же потоке, что и оператор создания.
//
//Давайте используем оператор observeOn, чтобы указать для оператора map другой поток:

Observable<Integer> observable = Observable
        .create(onSubscribe)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation())
        .map(func)
        .observeOn(AndroidSchedulers.mainThread());
//Если пройтись по цепочке:
//- оператор create выполняется в IO-потоке, т.к. мы указали это в операторе subscribeOn
//- оператор map выполнится в computation-потоке, т.к. мы указали это в операторе observeOn, который идет перед map
//- подписчики получат данные (т.е. выполнится метод onNext) в потоке main,
// т.к. мы задали этот поток в последнем observeOn.
//
//
//Результат:
//100 subscribe [main]
//150 done [main]
//150 call [RxIoScheduler-2]
//250 func 0 [RxComputationScheduler-1]
//250 onNext: 0 [main]
//350 func 1 [RxComputationScheduler-1]
//350 onNext: 10 [main]
//450 func 2 [RxComputationScheduler-1]
//450 onNext: 20 [main]
//450 onCompleted [main]
//
//Метод call выполнился в IO-потоке. Оператор map с функцией func - в computation-потоке.
// Подписчик получает данные в main-потоке. Т.е. один Observable использует в работе сразу три потока,
// и это конечно не предел. Вы можете использовать сколько угодно операторов и хоть для каждого из них указывать свой поток.
}
