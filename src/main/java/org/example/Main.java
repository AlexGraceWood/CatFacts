package org.example;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.config.RequestConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // URL ресурса с данными
        String url = "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";

        // Настройка HttpClient
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // таймаут на подключение
                        .setSocketTimeout(30000)    // таймаут на получение ответа
                        .setRedirectsEnabled(false) // запрет на редиректы
                        .build())
                .build();

        try {
            // Создание запроса
            HttpGet request = new HttpGet(url);

            // Выполнение запроса
            CloseableHttpResponse response = httpClient.execute(request);

            // Чтение данных из ответа
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            // Закрываем соединение
            response.close();

            // Маппинг JSON в список объектов
            ObjectMapper mapper = new ObjectMapper();
            List<CatFact> facts = mapper.readValue(result.toString(), new TypeReference<List<CatFact>>() {});

            // Фильтрация фактов
            List<CatFact> filteredFacts = facts.stream()
                    .filter(fact -> fact.getUpvotes() != null && fact.getUpvotes() > 0)
                    .collect(Collectors.toList());

            // Вывод результата
            filteredFacts.forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
