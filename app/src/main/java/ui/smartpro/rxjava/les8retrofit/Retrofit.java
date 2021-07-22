package ui.smartpro.rxjava.les8retrofit;


//Пример без RxJava
//Рассмотрим пример, когда на сервере лежит json-файл, и мы хотим его скачать.
//
//Файл находится здесь:
//https://rawgit.com/startandroid/data/master/messages/messages1.json
//
//Внутри - коллекция данных типа Message.

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class Message {

    @SerializedName("id")
    private int id;

    @SerializedName("time")
    private long time;

    @SerializedName("text")
    private String text;

    @SerializedName("image")
    private String image;

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }
}
//Retrofit после загрузки json-файла сам сможет конвертировать его содержимое в List<Messages>.
//
//
//Для начала рассмотрим, как использовать Retrofit без RxJava.
// Чтобы описать метод, который мы будем использовать для загрузки данных, нам необходимо создать интерфейс.

public interface WebApi {

    @GET("messages{page}.json")
    Call<List<Message>> messages(@Path("page") int page);

}
//В интерфейса описываем метод messages для получения содержимого json файла.
//
//"messages{page}.json" - это последний сегмент URL-пути файла. Вместо page будет подставляться число, которые мы будем передавать в этот метод.
//
//
//
//Создаем экземпляр Retrofit:

public class Retrofit {

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://rawgit.com/startandroid/data/master/messages/")
            .build();

//Указываем, что нужна конвертация из json в объекты (GsonConverterFactory), и прописываем базовый URL.
// Именно к этому URL потом и будет прибавляться тот остаток пути,
// который мы в интерфейсе прописали - messages{page}.json.

//Просим Retrofit создать реализацию нашего интерфейса

WebApi webApi = retrofit.create(WebApi.class);
//
//
//Теперь можем вызывать метод messages

Call<List<Message>> call = webApi.messages(1);
//
//
//Передаем на вход число 1 в качестве page. Это значит, что Retrofit будет искать файл по адресу:
//https://rawgit.com/startandroid/data/master/messages/messages1.json
//
//В результате вызова messages мы получаем объект Call.
// Он содержит в себе всю информацию: что и откуда грузить, и в каком виде возвращать.

//Осталось запустить Call и, тем самым, отправить наконец-то запрос на сервер:

call.enqueue(new Callback<List<Message>>() {
    @Override
    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
        if (response.isSuccessful()) {
            log("onResponse, messages count " +
                    (response != null ? response.body().size() : 0));
        } else {
            log("onResponse error: " + response.code());
        }
    }

    @Override
    public void onFailure(Call<List<Message>> call, Throwable t) {
        log("onFailure " + t);
    }
});
//Запускаем асинхронный метод enqueue и передаем ему Callback, в который придет результат запроса.
// Сам запрос будет выполнен в отдельном потоке, а результат придет нам в UI-потоке.
//
//В onResponse мы получим объект Response. Чтобы добраться до данных, надо вызвать метод response.body().
// Тем самым мы получим List<Messages>. Выводим в лог количество полученных с сервера записей.
//А если запрос завершился не успешно, то выводим в лог текст ошибки.
//
//Метод onFailure будет вызван, если запрос не был выполнен.

//Запускаем и смотрим лог:
//onResponse, messages count 50
//
//Мы получили данные. В файле оказалось 50 Message записей.
    
//Обработка ошибок
//Попробуем спровоцировать ошибку. Я выключу интернет на компьютере и эмулятор не сможет добраться до файла.
//
//Результат:
//onFailure java.net.SocketTimeoutException: connect timed out
//
//В onFailure мы получили подробную информацию об ошибке.

//Теперь я включу интернет, но использую несуществующий URL. Для этого передам 0 в качестве path.

Call<List<Message>> call = webApi.messages(0);
//Retrofit будет пытаться достать данные из URL:
//https://rawgit.com/startandroid/data/master/messages/messages0.json
//
//но такого файла нет на сервере.

//Результат
//onResponse error: 404
//
//Запрос был выполнен, но вернул HTTP ошибку 404, которая пришла в onResponse.

//Синхронный запуск
//Если вы не в UI-потоке и вам надо выполнить запрос синхронно, то вместо enqueue необходимо вызывать метод execute.

Response<List<Message>> response = null;
try {
   response = call.execute();
} catch (
    IOException e) {
   e.printStackTrace();
}
//Этот вызов блокирует ваш текущий поток и, в итоге, вернет результат или свалится с ошибкой.

}
