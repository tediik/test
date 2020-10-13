package com.jm.online_store.service.interf;

import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.StockFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StockService {

    Stock findStockById(Long id);

    void addStock(Stock stock);

    void deleteStockById(Long id);

    List<Stock> findAll();

    Page<Stock> findPage(Pageable page, StockFilterDto filterDto);

    List<Stock> findCurrentStocks();

    List<Stock> findFutureStocks();

    List<Stock> findPastStocks();

    List<Stock> findPublishedStocks();

    void updateStock(Stock stock);

    String updateStockImage(Long valueOf, MultipartFile imageStockFile) throws IOException;

    String deleteStockImage(Long stockId) throws IOException;
}
