package ui.smartpro.rxjava.les7operators;

import java.util.concurrent.TimeUnit;

public class ConcatMap {
//concatMap
//concatMap - это flatMap c maxConcurrent = 1
//
//Используем тот же пример:

Observable<String> observable = userGroupObservable
        .concatMap(new Func1<UserGroup, Observable<String>>() {
            @Override
            public Observable<String> call(UserGroup userGroup) {
                return userGroup.getUsersDetails();
            }
        });
//
//
//Результат
//300 onNext User 1 details
//600 onNext User 2 details
//900 onNext User 3 details
//1200 onNext User 4 details
//1500 onNext User 5 details
//1700 onNext User 6 details
//1900 onNext User 7 details
//2100 onNext User 8 details
//2300 onNext User 9 details
//2500 onNext User 10 details
//2500 onCompleted

//С помощью concatMap мы также можем сделать паузу между элементами, как ранее делали с помощью zip.
//
//Пример

Observable<UserGroup> userGroupObservable = Observable.just(null);

Observable<String> observable1 = Observable.just("A", "B", "C", "D", "E");

observable1
        .concatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                return Observable.just(s).delay(100, TimeUnit.MILLISECONDS);
            }
        })
        .subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                log("onCompleted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String next) {
                log("onNext " + next);
            }
        });
//Для каждого элемента observable1 мы создаем Observable, который содержит этот элемент (just) и
// выполняет его отложенный запуск (delay). И т.к. это concatMap,
// то в один момент времени может выполнятся только один Observable, и мы получаем очередь из
// элементов с паузами между ними в 100 мсек.
//
//Результат:
//100 onNext A
//200 onNext B
//300 onNext C
//400 onNext D
//500 onNext E
//500 onCompleted
}
