package com.mydata.domain.mydata.service;

import com.mydata.domain.mydata.dto.request.ConnectRequest;
import com.mydata.domain.mydata.dto.response.ConnectResponse;
import com.mydata.domain.mydata.dto.response.ConnectionResponse;
import com.mydata.domain.mydata.dto.response.SyncResponse;

public interface MyDataService {

  ConnectResponse connect(ConnectRequest request, String firebaseUid);

  ConnectionResponse getConnections(String firebaseUid);

  SyncResponse sync(String firebaseUid);
}
