package com.github.mrchcat.explorewithme.controller;

import com.github.mrchcat.explorewithme.RequestCreateDTO;
import com.github.mrchcat.explorewithme.RequestStatisticDTO;
import com.github.mrchcat.explorewithme.service.StatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRequest(@RequestBody @Valid RequestCreateDTO createDTO) {
        log.info("received request to add {}", createDTO);
        statService.addRequest(createDTO);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestStatisticDTO> getRequestStatistic(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                 @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                 @RequestParam(name = "uris", required = false) String[] uris,
                                                 @RequestParam(name = "unique", required = false,
                                                         defaultValue = "false") boolean unique) {
        log.info("received request for request statistics with parameters: start={},end={}, uris={},unique={}",
                start, end, Arrays.toString(uris),unique);
        return statService.getRequestStatistic(start, end, uris, unique);
    }

    //
}
