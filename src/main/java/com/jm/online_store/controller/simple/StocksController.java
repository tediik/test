package com.jm.online_store.controller.simple;

import com.jm.online_store.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/stocks")
public class StocksController {

    private final StockService stockService;

    @Autowired
    public StocksController(StockService stockService) {
        this.stockService = stockService;
    }


    //Вывод всех акций на странице
    @GetMapping()
    public String getStocks(ModelMap model) {
        model.addAttribute("stocks", stockService.findAllStocks());
        return "stocks";
    }



}
