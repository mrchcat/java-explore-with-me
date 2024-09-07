package com.github.mrchcat.explorewithme.controller;

import com.github.mrchcat.explorewithme.RequestCreateDTO;
import com.github.mrchcat.explorewithme.RequestStatisticDTO;
import com.github.mrchcat.explorewithme.service.StatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
    public List<RequestStatisticDTO> getRequests(@RequestParam LocalDateTime start,
                                                 @RequestParam LocalDateTime end,
                                                 @RequestParam(required = false) String[] uris,
                                                 @RequestParam(required = false, defaultValue = "false") boolean unique) {
        log.info("received request to get ");
        return statService.getRequestStatistic(start, end, uris, unique);
    }

}
