package com.github.mrchcat.explorewithme.event.dto;

import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.model.Location;
import com.github.mrchcat.explorewithme.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public class EventCreateDto {
        private String title;
        private String annotation;
        private String description;
        private Category category;
        private LocalDateTime eventDate;
        private Location location;
        private boolean paid;
        private long participantLimit;
        private boolean requestModeration;
}
