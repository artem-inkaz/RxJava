package ui.smartpro.rxjava.les1observable;

public class Distinct {
//distinct
//Оператор distinct отсеет дубликаты
//
//1
//2
//3
//4
// create observable
Observable<Integer> observable = Observable
        .from(new Integer[]{5,9,7,5,8,6,7,8,9})
        .distinct();
//
//
//Результат:
//onNext: 5
//onNext: 9
//onNext: 7
//onNext: 8
//onNext: 6
//onCompleted
}
