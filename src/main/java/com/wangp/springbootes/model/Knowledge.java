package com.wangp.springbootes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author farling-wangp
 * @version 1.0
 * @date 2020/5/9 11:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "mysql_index",shards = 1,replicas = 0,type = "knowledge")
public class Knowledge {
    @Id
    private Integer id;
    @Field(type = FieldType.Keyword)
    private String workStation;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String question;

    private String describe;
}
