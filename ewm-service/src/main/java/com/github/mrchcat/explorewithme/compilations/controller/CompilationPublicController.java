package com.github.mrchcat.explorewithme.compilations.controller;

import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.compilations.dto.CompilationDto;
import com.github.mrchcat.explorewithme.compilations.service.CompilationService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<CompilationDto> getAllCompilations(
            @RequestParam(name = "pinned", required = false) Boolean pinned,
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Positive Integer size) {

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        log.info("Public API: received request to get all compilations with parameters pinned={} from={}, size={}",
                pinned, from, size);
        return compilationService.getAllDto(pinned,pageable);
    }

    @GetMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    CompilationDto getCompilationById(@PathVariable(name = "compilationId") long compilationId) {
        log.info("Public API: received request get compilation with id={}", compilationId);
        return compilationService.getDtoById(compilationId);
    }

}
