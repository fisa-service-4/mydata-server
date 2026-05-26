package com.mydata.domain.mydata.service;

import com.mydata.domain.mydata.dto.request.ConnectRequest;
import com.mydata.domain.mydata.dto.response.ConnectResponse;
import com.mydata.domain.mydata.dto.response.ConnectionResponse;
import com.mydata.domain.mydata.dto.response.SyncResponse;

public interface MyDataService {

  ConnectResponse connect(Long userId, ConnectRequest request);

  ConnectionResponse getConnections(Long userId);

  SyncResponse sync(Long userId);
}
