package com.github.mrchcat.explorewithme.comments.dto;

import com.github.mrchcat.explorewithme.comments.model.CommentState;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.exception.ArgumentNotValidException;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

//TODO Событие может быть комментируемым или нет
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentAdminUpdateDto {
    private CommentState state;
    private Boolean editable;

    public void notNullAll(){
        if(state==null && editable==null){
            String message="Some of the update fields must have content";
            throw new ArgumentNotValidException(message);
        }

    }
}
