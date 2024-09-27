package com.github.mrchcat.explorewithme.comments.dto;

import com.github.mrchcat.explorewithme.comments.model.CommentState;
import com.github.mrchcat.explorewithme.exception.ArgumentNotValidException;
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
