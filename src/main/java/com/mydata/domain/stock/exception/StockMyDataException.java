package com.mydata.domain.stock.exception;

import com.mydata.global.exception.BusinessException;
import com.mydata.global.exception.ErrorCode;

public class StockMyDataException extends BusinessException {

  public StockMyDataException(ErrorCode errorCode) {
    super(errorCode);
  }
}
