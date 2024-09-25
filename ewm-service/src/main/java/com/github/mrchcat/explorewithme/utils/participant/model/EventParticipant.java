package com.github.mrchcat.explorewithme.utils.participant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class EventParticipant {
    @Id
    long id;
    int participants;
}
