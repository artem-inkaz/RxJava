package ui.smartpro.rxjava.les1observable;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FromCallable {

//fromCallable
//Если у вас есть синхронный метод, который вам надо сделать асинхронным, то оператор fromCallable поможет вам

//Например, есть метод longAction

private int longAction(String text) {
   log("longAction");

   try {
       TimeUnit.SECONDS.sleep(1);
   } catch (InterruptedException e) {
       e.printStackTrace();
   }

   return Integer.parseInt(text);
}

//И затем можно создавать Observable из Callable

Observable.fromCallable(new CallableLongAction("5"))
       .subscribeOn(Schedulers.io())
       .observeOn(AndroidSchedulers.mainThread())
       .subscribe(new Action1<Integer>() {
           @Override
           public void call(Integer integer) {
               log("onNext " + integer);
           }
       });
//Получившийся Observable запустит метод longAction и вернет вам результат в onNext.
//
//Операторы observeOn и subscribeOn здесь управляют потоками (thread) и обеспечивают асинхронность вызова.
// Подробно мы поговорим о них в одном из следующих уроков.
}

//    Необходимо обернуть его в Callable

class CallableLongAction implements Callable<Integer> {

    private final String data;

    public CallableLongAction(String data) {
        this.data = data;
    }

    @Override
    public Integer call() throws Exception {
        return longAction(data);
    }
}
