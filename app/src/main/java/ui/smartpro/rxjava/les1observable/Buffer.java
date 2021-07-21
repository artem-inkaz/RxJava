package ui.smartpro.rxjava.les1observable;

import android.util.Log;

import java.util.List;

public class Buffer {
//buffer
//Оператор buffer собирает элементы и по мере накопления заданного кол-ва отправляет их дальше одним пакетом.
//
//Создадим Observable из 8 чисел, и добавим к нему буфер с количеством элементов = 3.

// create observable
Observable<List<Integer>> observable = Observable
        .from(new Integer[]{1,2,3,4,5,6,7,8})
        .buffer(3);
//
// create observer
Observer<List<Integer>> observer = new Observer<List<Integer>>() {
    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: " + e);
    }

    @Override
    public void onNext(List<Integer> s) {
        Log.d(TAG, "onNext: " + s);
    }
};

// subscribe
observable.subscribe(observer);
//
//
//Результат:
//onNext: [1, 2, 3]
//onNext: [4, 5, 6]
//onNext: [7, 8]
//onCompleted
//
//Оператор разбил данные на блоки по 3 элемента. Обратите внимание,
// тип данных Observable в случае буфера будет не Integer, а List<Integer>.

//Существуют и более сложные операторы преобразования,
// которые из каждого элемента генерируют отдельную последовательность данных.
}
