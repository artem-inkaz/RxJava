package ui.smartpro.rxjava.les2subscription;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class Subscription {
//Subscription
//Чтобы получать события от Observable нам необходимо подписать на него Observer.
// Но что если в какой то момент мы больше не хотим ничего получать?
// Нам нужно будет отписаться. Для этого существует Subscription.
//
//Метод Observable.subscribe возвращает объект Subscription,
// с помощью которого мы в любой момент можем отписать Observer или Action от Observable вызвав метод unsubscribe.

//В качестве примера рассмотрим Observable interval, который каждую секунду будет отправлять событие Next.

// create observable
Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);

// create action
Action1<Long> action = new Action1<Long>() {
    @Override
    public void call(Long l) {
        Log.d(TAG, "onNext: " + l);
    }
};

// subscribe
final Subscription subscription = observable.subscribe(action);

// unsubscribe
getWindow().getDecorView().postDelayed(new Runnable() {
    @Override
    public void run() {
        Log.d(TAG, "unsubscribe");
        subscription.unsubscribe();
    }
}, 4500);
//При вызове метода subscribe мы получаем объект Subscription, и через какое то время
// (с помощью postDelayed) используем его метод unsubscribe, чтобы отписать Action от Observable.
//
//В итоге мы получим только первые несколько элементов.
//
//Результат:
//onNext: 0
//onNext: 1
//onNext: 2
//onNext: 3
//unsubscribe
//
//После того, как был вызван метод unsubscribe, нам перестали приходить события.
//
//В обязанности Observable входит проверять статус подписчика, и если он отписался,
// то не слать ему более никаких событий.
//
//А подписчик, со своей стороны должен отписываться, если данные ему больше не нужны.
// Иначе Observable будет хранить ссылку на подписчика и это может привести к утечке памяти,
// т.к. подписчик может запросто хранить неявную ссылку на Activity.
//
//В общем, это очень важно - следить за подпиской и отменять ее когда она больше не нужна.
// А когда данные закончатся успешно (onCompleted) или произойдет ошибка (onError), подписка отменится автоматически.

//CompositeSubscription
//CompositeSubscription - это объект, который поможет вам хранить все ваши подписки и отменить их всех сразу.
// Это может быть полезно, например, при закрытии Activity.

//Рассмотрим пример, с двумя подписками.

final Observable<Long> observable = Observable
       .interval(1, TimeUnit.SECONDS);

// subscribe observers
subscription1 = observable.subscribe(observer1);
subscription2 = observable.subscribe(observer2);

// add subscriptions to CompositeSubscription
CompositeSubscription compositeSubscription = new CompositeSubscription();
compositeSubscription.add(subscription1);
compositeSubscription.add(subscription2);

log("subscription1 is unsubscribed " + subscription1.isUnsubscribed());
log("subscription2 is unsubscribed " + subscription2.isUnsubscribed());

// unsubscribe CompositeSubscription
log("unsubscribe CompositeSubscription");
compositeSubscription.unsubscribe();

log("subscription1 is unsubscribed " + subscription1.isUnsubscribed());
log("subscription2 is unsubscribed " + subscription2.isUnsubscribed());
//Есть interval Observable и мы подписываем на него два Observer объекта.
// Тем самым мы получаем два Subscription объекта и добавляем их в CompositeSubscription методом add.
//
//Далее мы выводим в лог статус двух подписок. Для этого мы используем метод isUnsubscribed,
// который вернет true если подписка отменена, или false - если подписка еще действует.
//
//Затем у объекта CompositeSubscription мы вызываем метод unsubscribe. Этот метод отменит все подписки,
// содержащиеся в этом объекте. Т.е. в нашем случае это subscription1 и subscription2.
//
//После этого мы снова выводим в лог статус подписок.
//
//Результат:
//subscription1 is unsubscribed false
//subscription2 is unsubscribed false
//unsubscribe CompositeSubscription
//subscription1 is unsubscribed true
//subscription2 is unsubscribed true
//
//По логам видно, что метод CompositeSubscription.unsubscribe отменил все подписки,
// которые были добавлены в этот CompositeSubscription объект.

//Создание своего Observable
//Давайте создадим свой Observable. Для этого необходимо написать реализацию интерфейса OnSubscribe и
// передать ее в метод Observable.create.
//
//Создадим Observable, который будет похож на оператор interval. Он будет посылать числа от 0 до 9 с
// интервалом в одну секунду.

// create onSubscribe
Observable.OnSubscribe<Integer> onSubscribe = new Observable.OnSubscribe<Integer>() {
    @Override
    public void call(Subscriber<? super Integer> subscriber) {
        for (int i = 0; i < 10; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext(i);
        }
        subscriber.onCompleted();
    }
};

// create observable
Observable<Integer> observable = Observable.create(onSubscribe)
        .subscribeOn(Schedulers.io());

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
    public void onNext(Integer i) {
        Log.d(TAG, "onNext: " + i);
    }
};

// subscribe
observable.subscribe(observer);
//Интерфейс OnSubscribe имеет всего один метод call, который будет вызван в момент подписки.
// Т.е. когда мы у созданного Observable вызовем метод subscribe и передадим туда Observer,
// будет вызван метод OnSubscribe.call с передачей в него этого Observer в обертке Subscriber.
// Теперь у нас в OnSubscribe есть Observer и мы можем посылать ему данные, вызывая его метод onNext.
// А в конце вызываем onCompleted, сообщая о том, что передача данных завершена.
//
//Чтобы из OnSubscribe получить Observable, мы передаем реализацию OnSubscribe в метод Observable.create()
// и получаем готовый Observable, в котором будет работать наш код из OnSubscribe.
// Я там еще использовал оператор subscribeOn, чтобы пауза в одну секунду не блокировала основной поток.
// Пока просто не обращайте на это внимание, в одном из следующих уроков мы это подробно разберем.
//
//
//
//Результат:
//onNext: 0
//onNext: 1
//onNext: 2
//onNext: 3
//onNext: 4
//onNext: 5
//onNext: 6
//onNext: 7
//onNext: 8
//onNext: 9
//onCompleted

//Наша текущая реализация Observable некорректна, потому что она не проверяет статус подписки.
// Мы можем убедиться в этом, добавив вызов метода unsubscribe .

// create onSubscribe
Observable.OnSubscribe<Integer> onSubscribe = new Observable.OnSubscribe<Integer>() {
    @Override
    public void call(Subscriber<? super Integer> subscriber) {
        for (int i = 0; i < 10; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext(i);
        }
        subscriber.onCompleted();
    }
};

// create observable
Observable<Integer> observable = Observable.create(onSubscribe)
        .subscribeOn(Schedulers.io());

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
    public void onNext(Integer i) {
        Log.d(TAG, "onNext: " + i);
    }
};

// subscribe
final Subscription subscription = observable.subscribe(observer);

// unsubscribe
getWindow().getDecorView().postDelayed(new Runnable() {
    @Override
    public void run() {
        Log.d(TAG, "unsubscribe");
        subscription.unsubscribe();
    }
}, 4500);
//Тот же код, плюс мы добавили вызов метода unsubscribe спустя какое то время.

//Результат:
//onNext: 0
//onNext: 1
//onNext: 2
//onNext: 3
//unsubscribe
//onNext: 4
//onNext: 5
//onNext: 6
//onNext: 7
//onNext: 8
//onNext: 9
//onCompleted

//Мы отписываемся, но события продолжают приходить. Как я уже писал выше, проверять статус подписки -
// это обязанность Observable. Давайте добавим эту проверку в OnSubscribe:

// create onSubscribe
Observable.OnSubscribe<Integer> onSubscribe = new Observable.OnSubscribe<Integer>() {
    @Override
    public void call(Subscriber<? super Integer> subscriber) {
        for (int i = 0; i < 10; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (subscriber.isUnsubscribed()) {
                return;
            }
            subscriber.onNext(i);
        }

        if (subscriber.isUnsubscribed()) {
            return;
        }
        subscriber.onCompleted();
    }
};
//
// create observable
Observable<Integer> observable = Observable.create(onSubscribe)
        .subscribeOn(Schedulers.io());

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
    public void onNext(Integer i) {
        Log.d(TAG, "onNext: " + i);
    }
};
//
// subscribe
final Subscription subscription = observable.subscribe(observer);

// unsubscribe
getWindow().getDecorView().postDelayed(new Runnable() {
    @Override
    public void run() {
        Log.d(TAG, "unsubscribe");
        subscription.unsubscribe();
    }
}, 4500);
//Мы постоянно проверяем статус подписки методом isUnsubscribed. И как только выясняем,
// что подписчик отписался, прекращаем работу Observable. Потому что Observable не должен ничего
// отправлять подписчику, если тот отписался.

//Результат:
//onNext: 0
//onNext: 1
//onNext: 2
//onNext: 3
//unsubscribe

//Итак, если вы создаете свой Observable, то вам необходимо мониторить состояние подписки у Subscriber.
// Это необходимо делать по двум причинам.
//
//1) После того как подписчик отписался, вам нельзя отправлять ему какие-либо события.
// Т.е. технические конечно вы можете это сделать, но контракт RxJava это запрещает.
// Подписчик будет рассчитывать, что после вызова unsubscribe, он больше ничего не получит.
// Если вы не будете это соблюдать, то можете нарушить логику чужого кода, который использует ваш Observable.
//
//2) После того как подписчик отписался, вам необходимо завершать работу вашего Observable.
// Потому что пока Observable работает, он держит ссылку на подписчика.
// А если подписчик, например, держит ссылку на Activity, которая уже закрылась, то получаем утечку памяти.
//
//
//
//Когда Observable вызовет onError или onCompleted, вы автоматически будете отписаны.
// Но пока этого не произошло, пока вы подписаны и получаете данные, вам необходимо самим следить за
// подпиской и отписываться, как только вы понимаете, что данные вам больше не нужны (например,
// Activity закрылось). Иначе может случиться утечка памяти.

//Мы рассмотрели очень простой пример создания своего Observable. При каждом вызове Observable.subscribe,
// будет вызван метод OnSubscribe.call.
// И каждый новый подписчик в этом примере будет стартовать новый цикл for и будет получать свои данные независимо от других.
// Такое поведение может немного запутать,
// т.к. хотелось бы иметь возможность реализовать классическую схему паттерна
// Наблюдатель, когда генерация событий выполняется один раз для всех подписчиков, а не для каждого отдельно,
// и все подписавшиеся получают одни и те же события одновременно.
}
