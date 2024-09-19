package com.github.mrchcat.explorewithme;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class EvmServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvmServiceApplication.class, args);
    }
}
