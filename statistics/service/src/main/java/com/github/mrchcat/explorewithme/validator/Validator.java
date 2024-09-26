package com.github.mrchcat.explorewithme.validator;

import com.github.mrchcat.explorewithme.RequestQueryParamDto;
import com.github.mrchcat.explorewithme.exceptions.ArgumentNotValidException;

public class Validator {

    public static void validate(RequestQueryParamDto queryParams) {
        var start = queryParams.getStart();
        var end = queryParams.getEnd();
        if (end.isBefore(start)) {
            throw new ArgumentNotValidException("date of end can not precede date of start");
        }
    }
}
