package ui.smartpro.rxjava.les1observable;

import android.util.Log;

public class Filter {
//filter
//Оператор filter может отсеять только нужные элементы. Для этого необходимо создать функцию,
// в которой будет описан алгоритм фильтрации. Например, оставим только строки содержащие 5.

Func1<String, Boolean> filterFiveOnly = new Func1<String, Boolean>() {
    @Override
    public Boolean call(String s) {
        return s.contains("5");
    }
};
//В качестве типов мы указали String и Boolean. Данные типа String будут приходить на вход функции,
// а возвращать она должна Boolean - т.е. прошел элемент фильтр или нет.
//
//Используем функцию в операторе filter

// create observable
Observable<String> observable = Observable
        .from(new String[]{"15", "27", "34", "46", "52", "63"})
        .filter(filterFiveOnly);

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
//
//
//Результат:
//onNext: 15
//onNext: 52
//onCompleted
}
