package com.github.mrchcat.explorewithme.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Table(name = "events")
public class Location {

    @Column(name = "latitude")
    private double lat;

    @Column(name = "longitude")
    private double lon;
}
