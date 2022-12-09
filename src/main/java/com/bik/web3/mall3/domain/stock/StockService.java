package com.bik.web3.mall3.domain.stock;

import com.bik.web3.mall3.bean.stock.StockShelveRequest;
import com.bik.web3.mall3.common.exception.Mall3Exception;
import com.bik.web3.mall3.common.exception.ResultCodes;
import com.bik.web3.mall3.domain.stock.entity.Stock;
import com.bik.web3.mall3.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 库存领域服务
 *
 * @author Mingo.Liu
 * @date 2022-12-09
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {
    private final StockRepository stockRepository;

    public void shelve(StockShelveRequest request) {
        Stock stock = stockRepository.findByIdAndUserId(request.getStockId(), request.getUserId())
                .orElseThrow(() -> new Mall3Exception(ResultCodes.DATA_NOT_EXISTS));
        if(null == stock){

        }
    }
}
