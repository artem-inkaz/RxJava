package ui.smartpro.rxjava.les1observable;

import android.util.Log;

public class Map {
//map
//Оператор map преобразует все элементы последовательности.
// Для этого нам необходимо написать функцию преобразования. Например конвертация из String в Integer.
// Создаем Func1

Func1<String, Integer> stringToInteger = new Func1<String, Integer>() {
    @Override
    public Integer call(String s) {
        return Integer.parseInt(s);
    }
};
//
//
//Объект Func1 - это функция, через которую будет проходить каждый элемент последовательности.
// Этот объект требует от нас указания входного и выходного типов. Мы указали ему,
// что на вход придет String, а на выходе нам нужно получить Integer.
// И в его методе call мы написали код преобразования.
//
//Теперь эту функцию мы передаем в оператор map

// create observable
Observable<Integer> observable = Observable
        .from(new String[]{"1", "2", "3", "4", "5", "6"})
        .map(stringToInteger);
//
//
// create observer
Observer<Integer> observer = new Observer<Integer>() {
    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: " + e);
    }

    @Override
    public void onNext(Integer s) {
        Log.d(TAG, "onNext: " + s);
    }
};
//
//// subscribe
observable.subscribe(observer);
//Обратите внимание, оператор map мы вызываем сразу после оператора from.
// Тем самым на вход map придет последовательнось строк сгенерированная в from.
// А в результате работы map мы уже получим последовательность чисел.
// В Observer данные придут уже как Integer

//Результат
//onNext: 1
//onNext: 2
//onNext: 3
//onNext: 4
//onNext: 5
//onNext: 6
//onCompleted

//Еще раз разберем, что получилось. Оператором from мы создали Observable с типом данных String.
// А оператором map мы из Observable<String> получили новый Observable с типом данных Integer.
// Т.е. оператор map не изменил исходный Observable<String>, а создал новый поверх него.
// Этот созданный Observable<Integer> получает данные из Observable<String>,
// преобразует их в Integer и шлет дальше, как будто он сам их сгенерировал.

//Попробуем спровоцировать ошибку преобразования. Заменим число 4 на букву а.

// create observable
Observable<Integer> observable = Observable
        .from(new String[]{"1", "2", "3", "a", "5", "6"})
        .map(stringToInteger);
//
//
//Результат:
//onNext: 1
//onNext: 2
//onNext: 3
//onError: java.lang.NumberFormatException: Invalid int: "a"
//
//Мы получили ошибку в метод onError, и, тем самым, последовательность завершилась.
// Есть, конечно, специальные операторы, которые умеют обрабатывать ошибку и продолжать работу,
}
