package com.wangp.springbootes.test;

import com.wangp.springbootes.SpringbootEsApplication;
import com.wangp.springbootes.dao.ItemRepository;
import com.wangp.springbootes.dao.KnowledgeRepository;
import com.wangp.springbootes.model.Item;
import com.wangp.springbootes.model.Knowledge;
import com.wangp.springbootes.util.HttpClientUtils;
import net.minidev.json.JSONObject;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import sun.font.Script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author farling-wangp
 * @version 1.0
* @date 2020/3/9 21:42
 */
@SpringBootTest(classes = SpringbootEsApplication.class)
@RunWith(SpringRunner.class)
public class EsTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void createIndex(){
        elasticsearchTemplate.createIndex(Item.class);
    }

    @Test
    public void deleteIndex(){
        elasticsearchTemplate.deleteIndex(Item.class);
    }

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void addDocument(){
        Item item = new Item(1L, "小米手机7", " 手机",
                "小米", 3499.00, "http://image.baidu.com/13123.jpg");
        itemRepository.save(item);
    }

    @Test
    public void insertList(){
        List<Item> list = new ArrayList<>();
        list.add(new Item(1L, "小米手机7", "手机", "小米", 3299.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Item(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Item(4L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.baidu.com/13123.jpg"));
        list.add(new Item(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.baidu.com/13123.jpg"));
        itemRepository.saveAll(list);
    }

    @Test
    public void update(){
        Item item = new Item(1L, "苹果XSMax", " 手机",
                "小米", 3499.00, "http://image.baidu.com/13123.jpg");
        itemRepository.save(item);
    }

    /** --------------- 查询 --------- **/
    @Test
    public void query(){
        Iterable<Item> all = itemRepository.findAll();
        for (Item item : all) {
            System.out.println(item);
        }
    }

    @Test
    public void queryByPrice(){
        List<Item> byPriceBetween = itemRepository.findByPriceBetween(4400, 4500);
        System.out.println(byPriceBetween);
    }

    @Test
    public void matchQuery(){
        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("title","我想买一个小米手机"));
        //获取结果
        Page<Item> items = itemRepository.search(queryBuilder.build());
        long totalElements = items.getTotalElements();
        System.out.println("total result have "+ totalElements);
        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    public void queryStringQuery(){
        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.queryStringQuery("我想买一个华为手机"));
        //获取结果
        Page<Item> items = itemRepository.search(queryBuilder.build());
        long totalElements = items.getTotalElements();
        System.out.println("total result have "+ totalElements);
        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    public void termQuery(){
        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("title","华为"));
        //获取结果
        Page<Item> items = itemRepository.search(queryBuilder.build());
        long totalElements = items.getTotalElements();
        System.out.println("total result have "+ totalElements);
        for (Item item : items) {
            System.out.println(item);
        }
    }


    @Test
    public void testBooleanQuery(){
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        builder.withQuery(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("title","华为"))
                        .must(QueryBuilders.matchQuery("brand","华为"))
        );

        // 查找
        Page<Item> page = this.itemRepository.search(builder.build());
        for(Item item:page){
            System.out.println(item);
        }
    }

    /* 分页
     * Page<item>：默认是分页查询，因此返回的是一个分页的结果对象，包含属性：
     *      totalElements：总条数
     *      totalPages：总页数
     *      Iterator：迭代器，本身实现了Iterator接口，因此可直接迭代得到当前页的数据
     *      其它属性
     */

    @Test
    public void searchByPage(){
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));
        // 分页：
        int page = 3;
        int size = 2;
        queryBuilder.withPageable(PageRequest.of(page,size));

        // 搜索，获取结果
        Page<Item> items = this.itemRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();
        System.out.println("总条数 = " + total);
        // 总页数
        System.out.println("总页数 = " + items.getTotalPages());
        // 当前页
        System.out.println("当前页：" + items.getNumber());
        // 每页大小
        System.out.println("每页大小：" + items.getSize());

        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    public void searchAndSort(){
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));

        // 排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));

        // 搜索，获取结果
        Page<Item> items = this.itemRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();
        System.out.println("总条数 = " + total);

        for (Item item : items) {
            System.out.println(item);
        }
    }



    /*
        聚合
     */
    @Test
    public void testAgg(){
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        builder.withSourceFilter(new FetchSourceFilter(new String[]{""},null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        builder.addAggregation(AggregationBuilders.terms("brands").field("brand"));
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Item> aggPage = (AggregatedPage<Item>) itemRepository.search(builder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称
            System.out.println(bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.out.println(bucket.getDocCount());
        }
    }
    //AggregationBuilders
    // （1）统计某个字段的数量
    //  ValueCountBuilder vcb=  AggregationBuilders.count("count_uid").field("uid");
    //（2）去重统计某个字段的数量（有少量误差）
    // CardinalityBuilder cb= AggregationBuilders.cardinality("distinct_count_uid").field("uid");
    //（3）聚合过滤
    //FilterAggregationBuilder fab= AggregationBuilders.filter("uid_filter").filter(QueryBuilders.queryStringQuery("uid:001"));
    //（4）按某个字段分组
    //TermsBuilder tb=  AggregationBuilders.terms("group_name").field("name");
    //（5）求和
    //SumBuilder  sumBuilder=	AggregationBuilders.sum("sum_price").field("price");
    //（6）求平均
    //AvgBuilder ab= AggregationBuilders.avg("avg_price").field("price");
    //（7）求最大值
    //MaxBuilder mb= AggregationBuilders.max("max_price").field("price");
    //（8）求最小值
    //MinBuilder min=	AggregationBuilders.min("min_price").field("price");
    //（9）按日期间隔分组
    //DateHistogramBuilder dhb= AggregationBuilders.dateHistogram("dh").field("date");
    //（10）获取聚合里面的结果
    //TopHitsBuilder thb=  AggregationBuilders.topHits("top_result");
    //（11）嵌套的聚合
    //NestedBuilder nb= AggregationBuilders.nested("negsted_path").path("quests");
    //（12）反转嵌套
    //AggregationBuilders.reverseNested("res_negsted").path("kps ");

    @Test
    public void testAgg2(){
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        builder.withSourceFilter(new FetchSourceFilter(new String[]{""},null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        builder.addAggregation(AggregationBuilders.max("max_price").field("price"));
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Item> aggPage = (AggregatedPage<Item>) itemRepository.search(builder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("max_price");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即最大值
            System.out.println(bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.out.println(bucket.getDocCount());
        }
    }

    @Test
    public void testSubAgg(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
                AggregationBuilders
                        .terms("brands").field("brand")
                        .subAggregation(AggregationBuilders.avg("priceAvg").field("price")) // 在品牌聚合桶内进行嵌套聚合，求平均值
                        .subAggregation(AggregationBuilders.max("max_price").field("price"))
                );
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Item> aggPage = (AggregatedPage<Item>) this.itemRepository.search(queryBuilder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称  3.5、获取桶中的文档数量
            System.out.println(bucket.getKeyAsString() + "，共" + bucket.getDocCount() + "台");
            // 3.6.获取子聚合结果：
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
            System.out.println("平均售价：" + avg.getValue());

//            // 3.7.获取子聚合结果：  ??????
//            InternalAvg max = (InternalAvg) bucket.getAggregations().asMap().get("max_price");
//            System.out.println("最高售价：" + avg.getValue());


            /**
             *  knowledge 测试
             */

        }

    }


    /**
     *  knowledge Test
     */

    @Autowired
    private KnowledgeRepository knowledgeRepository;

    @Test
    public void createKnowledgeIndex(){
        elasticsearchTemplate.createIndex(Knowledge.class);
    }

    @Test
    public void addField() throws IOException {
        String url = "http://localhost:9200/knowledge_base_system/_mapping/knowledge";
        Map<String,Map> map = new HashMap<>();
        Map<String,Map> fieldName = new HashMap<>();
        Map<String,String> type = new HashMap<>();

        type.put("type","keyword");
        fieldName.put("java",type);
        map.put("properties",fieldName);
        String body = HttpClientUtils.sendPostDataByJson(url, JSONObject.toJSONString(map),"utf-8");
        System.out.println(body);
    }

    @Test
    public void deleteKnowledgeIndex() {
        elasticsearchTemplate.deleteIndex(Knowledge.class);
    }

    @Test
    public void queryKnowledge() {
        Iterable<Knowledge> items = knowledgeRepository.findAll(Sort.by("id").ascending());
        items.forEach(item -> System.out.println("item = " + item));
    }

    /**
     * 自定义查询
     */
    @Test
    public void searchKnowledge() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("describe", "我要用传送"));
        // 搜索，获取结果
        Page<Knowledge> items = knowledgeRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();
        System.out.println("total = " + total);
        items.forEach(item -> System.out.println("item = " + item));
    }

    @Test
    public void searchKnowledgeByPage() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("describe", "黑色的机器"));
//        queryBuilder.withQuery(QueryBuilders.queryStringQuery("机"));
        // 分页：
        int page = 0;
        int size = 3;
        queryBuilder.withPageable(PageRequest.of(page, size));
        // 搜索，获取结果
        Page<Knowledge> items = knowledgeRepository.search(queryBuilder.build());
        long total = items.getTotalElements();
        System.out.println("总条数 = " + total);
        System.out.println("总页数 = " + items.getTotalPages());
        System.out.println("当前页：" + items.getNumber());
        System.out.println("每页大小：" + items.getSize());
        items.forEach(item -> System.out.println("item = " + item));
    }

}
