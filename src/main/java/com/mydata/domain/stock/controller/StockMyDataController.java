package com.mydata.domain.stock.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stock MyData API", description = "증권 마이데이터 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/mydata/v1/stock")
public class StockMyDataController {}
