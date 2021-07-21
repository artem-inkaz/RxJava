package ui.smartpro.rxjava.les1observable;

public class Skip {
//skip
//Оператор skip пропустит первые элементы. Пропустим первые 2

// create observable
Observable<Integer> observable = Observable
        .from(new Integer[]{5,6,7,8,9})
        .skip(2);
//
//
//Результат:
//onNext: 7
//onNext: 8
//onNext: 9
//onCompleted
}
