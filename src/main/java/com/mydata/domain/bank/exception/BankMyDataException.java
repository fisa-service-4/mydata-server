package com.mydata.domain.bank.exception;

import com.mydata.global.exception.BusinessException;
import com.mydata.global.exception.ErrorCode;

public class BankMyDataException extends BusinessException {

  public BankMyDataException(ErrorCode errorCode) {
    super(errorCode);
  }
}
