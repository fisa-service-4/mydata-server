package com.mydata.domain.aggregation.exception;

import com.mydata.global.exception.BusinessException;
import com.mydata.global.exception.ErrorCode;

public class AggregationException extends BusinessException {

  public AggregationException(ErrorCode errorCode) {
    super(errorCode);
  }

  public AggregationException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
