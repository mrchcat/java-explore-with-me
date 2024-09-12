package com.github.mrchcat.explorewithme;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@SpringBootApplication
@Controller
public class EvmServiceApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        var context = SpringApplication.run(EvmServiceApplication.class, args);
        handMadeClientTest(context);
    }

    private static void handMadeClientTest(ConfigurableApplicationContext context) throws IOException, InterruptedException {
        StatHttpClient client = context.getBean(StatHttpClient.class);
        RequestCreateDto createDto1 = RequestCreateDto.builder()
                .app("myapp1")
                .ip(InetAddress.getByName("127.00.33.1"))
                .uri("/myuri1")
                .timestamp(LocalDateTime.of(2024, 1, 1, 1, 1))
                .build();
        RequestCreateDto createDto2 = RequestCreateDto.builder()
                .app("myapp1")
                .ip(InetAddress.getByName("127.00.33.1"))
                .uri("/myuri1")
                .timestamp(LocalDateTime.of(2024, 1, 1, 1, 1))
                .build();
        RequestCreateDto createDto3 = RequestCreateDto.builder()
                .app("myapp2")
                .ip(InetAddress.getByName("127.00.33.3"))
                .uri("/myuri2")
                .timestamp(LocalDateTime.of(2024, 1, 1, 1, 1))
                .build();
        client.addRequest(createDto1);
        client.addRequest(createDto2);
        client.addRequest(createDto3);
        RequestQueryParamDto query1 = RequestQueryParamDto.builder()
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 1, 1, 1, 1))
                .uris(new String[]{"/myuri1","/myuri2"})
                .unique(true)
                .build();
        List<RequestStatisticDto> result1 = client.getRequestStatistic(query1);
        log.info("{}",result1);
        RequestQueryParamDto query2 = RequestQueryParamDto.builder()
                .start(LocalDateTime.of(2022, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 1, 1, 1, 1))
                .uris(new String[]{"/myuri1","/myuri2"})
                .unique(false)
                .build();
        List<RequestStatisticDto> result2 = client.getRequestStatistic(query2);
        log.info("{}",result2);
    }
}
