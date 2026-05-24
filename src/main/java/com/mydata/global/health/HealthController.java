package com.mydata.global.health;

import com.mydata.global.response.ApiResponse;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping("/health")
  public ApiResponse<Map<String, String>> health() {

    return ApiResponse.success(Map.of("status", "ok"), "health-check");
  }
}
