package ui.smartpro.rxjava.les7operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class FlatMap {
//flatMap
//Чтобы лучше понять смысл оператора flatMap, рассмотрим небольшой пример. Пусть у нас есть пользователи и группы.
//
//Для примера возьмем 2 группы и 10 пользователей.
//Первая группа: User 1, User 2, User 3, User 4, User 5
//Вторая группа: User 6, User 7, User 8, User 9, User 10
//
//
//Класс группы выглядит так:

class UserGroup {
    final private List<String> users;

    public UserGroup(Collection<String> users) {
        this.users = new ArrayList<>(users);
    }

    public List getUsers() {
        return users;
    }
}
//Группа просто содержит в себе список пользователей в виде коллекции String.

//Список групп приходит нам извне и доступен в виде:

Observable<UserGroup> userGroupObservable = ...;

//Нам необходимо используя userGroupObservable получить всех пользователей из всех групп в одном Observable.
//
//Попробуем к userGroupObservable добавить map, где будем доставать пользователей из группы.

Observable<List<String>> observable = userGroupObservable
        .map(new Func1<UserGroup, List<String>>() {
            @Override
            public List<String> call(UserGroup userGroup) {
                return userGroup.getUsers();
            }
        });
//В map мы вытаскиваем список пользователей из группы и возвращаем его как результат работы функции.
//Но в итоге мы получаем Observable, который просто вернет нам два списка.
//
//Результат:
//onNext [User 1, User 2, User 3, User 4, User 5]
//onNext [User 6, User 7, User 8, User 9, User 10]
//onCompleted
//
//Это не то, что нам нужно. Нам нужен Observable, который будет возвращать пользователей по одному, а не списками.

//Перепишем map:

Observable<Observable<String>> observable = userGroupObservable
        .map(new Func1<UserGroup, Observable<String>>() {
            @Override
            public Observable<String> call(UserGroup userGroup) {
                return Observable.from(userGroup.getUsers());
            }
        });
//Теперь вместо списка пользователей функция создает Observable с пользователями.
// Т.е. мы из UserGroup получаем Observable<String>. И это как будто похоже на то, что нам нужно.
//
//Но результатом будет Observable<Observable<String>>.
// И подписавшись на такой observable мы получим в onNext элементы типа Observable<String>,
// потому что map преобразовал UserGroup в Observable<String> и отправил этот Observable дальше,
// как обычный элемент. В итоге мы и получили Observable, который возвращает элементы типа
// Observable<String>, т.е. Observable<Observable<String>>.
//
//Результат:
//onNext rx.Observable@4f18411
//onNext rx.Observable@2e44368
//onCompleted

//Получается, нам нужно чтобы оператор map не просто конвертировал UserGroup в Observable<String>,
// но и сразу вытаскивал из получившегося Observable его String элементы и отправлял их в основной поток,
// и на выходе мы как раз получили бы список пользователей. Именно это все и делает flatMap.
//
//flatMap требует функцию, которая принимает на вход какой-либо объект, а вернуть должна Observable.
//
//Перепишем код:

Observable<String> observable = userGroupObservable
        .flatMap(new Func1<UserGroup, Observable<String>>() {
            @Override
            public Observable<String> call(UserGroup userGroup) {
                return Observable.from(userGroup.getUsers());
            }
        });
//Мы передаем в функцию UserGroup, а возвращаем Observable<String>.
// flatMap вытащит из этого Observable элементы и отправит их в основной поток,
// и мы получим на выходе Observable<String>. В этом Observable и будут все пользователи из всех групп.
//
//Результат
//onNext User 1
//onNext User 2
//onNext User 3
//onNext User 4
//onNext User 5
//onNext User 6
//onNext User 7
//onNext User 8
//onNext User 9
//onNext User 10
//onCompleted

//Давайте немного поменяем пример. Предположим, что теперь нам необходимо получить не просто
// пользователей, а их детальную информацию. Пусть она также будет в формате String.
//
//Добавим к классу UserGroup метод для получения детальной информации по всем пользователям группы:
//
//1
Observable<String> getUsersDetails() {...}
//Реализацию я здесь приводить не буду. Будем считать, что внутри этого метода выполняется запрос
// к серверу за деталями для каждого пользователя.
//
//Перепишем пример:

Observable<String> observable = userGroupObservable
        .flatMap(new Func1<UserGroup, Observable<String>>() {
            @Override
            public Observable<String> call(UserGroup userGroup) {
                return userGroup.getUsersDetails();
            }
        });
//В результате работы функции для flatMap мы, как и требуется, возвращаем Observable.
// Этот Observable будет возвращать детали пользователей по мере выполнения запросов к серверу.
//
//Результат:
//400 onNext User 1 details
//600 onNext User 6 details
//800 onNext User 2 details
//1200 onNext User 7 details
//1200 onNext User 3 details
//1600 onNext User 4 details
//1800 onNext User 8 details
//2000 onNext User 5 details
//2400 onNext User 9 details
//3000 onNext User 10 details
//3000 onCompleted
//
//
//
//Обратите внимание на порядок пользователей. Видно, что обе группы делали запросы на сервер параллельно.
// Т.е. flatMap получил первую группу и запустил в работу ее Observable, полученный из функции.
// Затем, получил вторую группу и запустил ее Observable. И оба Observable работали параллельно.
//
//Т.е. это аналогично оператору merge, когда он выполняет сразу несколько Observable,
// которые ему надо объединить в один поток. И так же, как и у merge,
// у flatMap есть параметр maxConcurrent, который позволяет указать,
// сколько Observable могут работать одновременно.
//
//Добавим параметр maxConcurrent = 1.

Observable<String> observable = userGroupObservable
        .flatMap(new Func1<UserGroup, Observable<String>>() {
            @Override
            public Observable<String> call(UserGroup userGroup) {
                return userGroup.getUsersDetails();
            }
        }, 1);
//Результат:
//500 onNext User 1 details
//1000 onNext User 2 details
//1500 onNext User 3 details
//2000 onNext User 4 details
//2500 onNext User 5 details
//2700 onNext User 6 details
//2900 onNext User 7 details
//3100 onNext User 8 details
//3300 onNext User 9 details
//3500 onNext User 10 details
//3500 onCompleted
//
//Теперь одновременно может работать только один Observable. И сначала работает первая группа, затем вторая.
}
