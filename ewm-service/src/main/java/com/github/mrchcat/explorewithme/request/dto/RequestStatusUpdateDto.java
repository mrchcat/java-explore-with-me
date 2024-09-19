package com.github.mrchcat.explorewithme.request.dto;

import com.github.mrchcat.explorewithme.request.model.RequestUpdateStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestStatusUpdateDto {
    @NotNull
    Set<Long> requestIds;
    @NotNull
    RequestUpdateStatus status;
}
