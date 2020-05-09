package com.wangp.springbootes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author farling-wangp
 * @version 1.0
 * @date 2020/3/15 9:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "mysql_index", type = "article", shards = 1, replicas = 0)
public class Article {
    @Id
    private Integer id;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String subject;
    @Field(type = FieldType.Keyword)
    private String author;
    @Field(type = FieldType.Keyword)
    private String createTime;
    @Field(type = FieldType.Keyword)
    private String updateTime;
}
