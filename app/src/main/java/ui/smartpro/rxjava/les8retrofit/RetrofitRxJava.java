package ui.smartpro.rxjava.les8retrofit;

import java.util.List;

import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

//Пример с RxJava
//Рассмотрим, как мы можем использовать RxJava при работе с Retrofit
//
//Перепишем интерфейс с методом message:
public interface WebApi {

    @GET("messages{page}.json")
    Observable<List<Message>> messages(@Path("page") int page);

}

//Теперь метод будет возвращать Observable.
public class RetrofitRxJava {

//Создаем экземпляр Retrofit:

Retrofit retrofit = new Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .baseUrl("https://rawgit.com/startandroid/data/master/messages/")
        .build();
//Все так же, как и раньше, но добавляем использование RxJavaCallAdapter, который сможет обернуть ответ на запрос в Observable.

//Создание реализации интерфейса остается без изменений.

WebApi webApi = retrofit.create(WebApi.class);

//Вызываем метод messages

Observable<List<Message>> observable = webApi.messages(1);
//Вместо Call получаем Observable.
//
//Остается подписаться на него:

observable
       .subscribeOn(Schedulers.io())
       .observeOn(AndroidSchedulers.mainThread())
       .subscribe(new Observer<List<Message>>() {
           @Override
           public void onCompleted() {
               log("onCompleted");
           }

           @Override
           public void onError(Throwable e) {
               log("onError " + e);
           }

           @Override
           public void onNext(List<Message> messages) {
               log("onNext " + messages.size());
           }
       });
//Также необходимо указать потоки, в которых будет выполнен запрос и получен результат.
//
//Теперь при выполнении запроса, данные мы получим в onNext:
//onNext 50
//onCompleted

//Обработка ошибок
//Снова отключу интернет, чтобы получить ошибку.
//
//Результат:
//onError java.net.SocketTimeoutException: connect timed out
//
//Ошибка пришла в onError.

//Включу интернет и попробую использовать несуществующий URL

Observable<List<Message>> observable = webApi.messages(0);
//
//
//Результат:
//onError retrofit2.adapter.rxjava.HttpException: HTTP 404
//
//Ошибка снова пришла в OnError.

//Синхронный запуск
//Если вы не в UI-потоке и вам необходимо выполнить запрос синхронно, можно сделать так:

List<Message> list = null;
try {
    list = observable.toBlocking().first();
} catch (Exception e) {
    e.printStackTrace();
}
//Метод toBlocking сделает Observable синхронным, а first вернет первый элемент,

}
