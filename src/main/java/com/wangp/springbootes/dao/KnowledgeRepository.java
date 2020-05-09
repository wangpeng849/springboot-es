package com.wangp.springbootes.dao;

import com.wangp.springbootes.model.Knowledge;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author wangp
 * @Date 2020/5/9
 * @Version 1.0
 */
public interface KnowledgeRepository extends ElasticsearchRepository<Knowledge,Long> {
}
