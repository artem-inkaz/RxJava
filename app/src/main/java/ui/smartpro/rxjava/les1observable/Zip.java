package ui.smartpro.rxjava.les1observable;

import android.util.Log;

public class Zip {
//zip
//Оператор zip попарно сопоставит элементы из двух Observable.
// Из каждой пары элементов с помощью функции будет получен один элемент, который будет добавлен
// в итоговый Observable.
//
//Сначала нам необходимо создать функцию, в которой мы задаем как из двух элементов получить один.
// В нашем примере мы просто соединим их в одну строку через двоеточие.

Func2<Integer, String, String> zipIntWithString = new Func2<Integer, String, String>() {
    @Override
    public String call(Integer i, String s) {
        return s + ": " + i;
    }
};
//В качестве типов мы указали <Integer, String, String>. Первые два, Integer и String -
// это типы данных двух Observable, которые мы будем склеивать. Третий тип, String -
// это какой тип данных мы хотим получить на выходе, в итоговом Observable.
//
//Методом from создаем первую последовательность, с типом Integer.
// Затем в вызове zipWith создаем вторую последовательность с типом String и
// указываем созданную ранее функцию zipIntWithString

// create observable
Observable<String> observable = Observable
        .from(new Integer[]{1,2,3})
        .zipWith(Observable.from(new String[]{"One", "Two", "Three"}), zipIntWithString);

// create observer
Observer<String> observer = new Observer<String>() {
    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: " + e);
    }

    @Override
    public void onNext(String s) {
        Log.d(TAG, "onNext: " + s);
    }
};

// subscribe
observable.subscribe(observer);
//В результате получим одну последовательность с типом String, состоящую из результатов работы функции
// с парами элементов из двух последовательностей Integer и String.
//
//
//
//Результат:
//onNext: One: 1
//onNext: Two: 2
//onNext: Three: 3
//onCompleted
}
