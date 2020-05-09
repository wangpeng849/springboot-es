package com.wangp.springbootes.dao;

import com.wangp.springbootes.model.Article;
import com.wangp.springbootes.model.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author farling-wangp
 * @version 1.0
 * @date 2020/3/9 21:52
 */
public interface ArticleRepository extends ElasticsearchRepository<Article,Long> {

}
