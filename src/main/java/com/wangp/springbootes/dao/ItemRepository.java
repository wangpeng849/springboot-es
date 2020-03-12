package com.wangp.springbootes.dao;

import com.wangp.springbootes.model.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author farling-wangp
 * @version 1.0
 * @date 2020/3/9 21:52
 */
public interface ItemRepository extends ElasticsearchRepository<Item,Long> {

    /**
     * @Description:根据价格区间查询
     * @param price1
     * @param price2
     * @return
     */
    List<Item> findByPriceBetween(double price1, double price2);
}
