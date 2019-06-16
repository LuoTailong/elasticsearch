package cn.itcast.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import cn.itcast.bean.Document;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import java.util.Iterator;

public class Score {
    public static void main(String[] args) throws Exception {
//        createIndex();
//        testCreateIndexMapping_boost();
//        createDocument();
        search();
    }

    private static void createIndex() {
        TransportClient client = Connection.getClient();
        client.admin().indices().prepareCreate("blog").get();
    }

    private static void testCreateIndexMapping_boost() throws Exception {
        TransportClient client = Connection.getClient();
        /**
         * 格式：
         * "mappings" : {
         *      "document" : {
         *          "dynamic" : "false",
         *          "properties" :{
         *              "id" :  { "type" : "string" },
         *              "content" : { "type" : "string" },
         *              "comment" : {"type" : "string"},
         *              "author" : { "type" : "string" }
         *          }
         *      }
         * }
         */
        //构建json的数据格式，创建映射
        XContentBuilder mappingBuilder = XContentFactory.jsonBuilder().startObject()
                .startObject("document")
                .startObject("properties")
                .startObject("id").field("type", "integer").field("store", "yes")
                .endObject()
                .startObject("title").field("type", "string").field("store", "yes").field("analyzer", "ik_max_word")
                .endObject()
                .startObject("content").field("type", "string").field("store", "yes").field("analyzer", "ik_max_word")
                .endObject()
                .startObject("comment").field("type", "string").field("store", "yes").field("analyzer", "ik_max_word")
                .endObject()
                .endObject()
                .endObject()
                .endObject();
        PutMappingRequest request = Requests.putMappingRequest("blog")
                .type("document").source(mappingBuilder);
        client.admin().indices().putMapping(request).get();
    }

    private static void createDocument() throws JsonProcessingException {
        TransportClient client = Connection.getClient();
        Document document = new Document();
//        document.setId(1);
//        document.setTitle("搜索引擎服务器");
//        document.setContent("基于restful的数据风格");
//        document.setComment("我们学习Elasticsearch搜索引擎服务器");

//        document.setId(2);
//        document.setTitle("什么是Elasticsearch");
//        document.setContent("Elasticsearch搜索引擎服务器");
//        document.setComment("Elasticsearch封装了lucene");

        document.setId(3);
        document.setTitle("Elasticsearch的用途");
        document.setContent("Elasticsearch可以用来进行海量数据的检索");
        document.setComment("Elasticsearch真NB");


        ObjectMapper objectMapper = new ObjectMapper();
        String source = objectMapper.writeValueAsString(document);
        System.out.println("source:"+source);

        IndexResponse indexResponse = client.prepareIndex("blog", "document", document.getId().toString()).setSource(source).get();
        // 获取响应的信息
        System.out.println("索引名称:"+indexResponse.getIndex());
        System.out.println("文档类型:"+indexResponse.getType());
        System.out.println("ID:"+indexResponse.getId());
        System.out.println("版本:"+indexResponse.getVersion());
        System.out.println("是否创建成功:"+indexResponse.status());
    }

    private static void search() {
        TransportClient client = Connection.getClient();
        SearchResponse searchResponse = client.prepareSearch("blog").setTypes("document")
                .setQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("title", "搜索"))
                        .should(QueryBuilders.matchQuery("content", "搜索").boost(10))//boost加权，让在content中搜索到的在前面
                        .should(QueryBuilders.matchQuery("comment", "搜索"))
                ).get();

        Iterator<SearchHit> iterator = searchResponse.getHits().iterator();
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next();
            System.out.println("所有的数据JSON的数据格式:"+searchHit.getSourceAsString());
            System.out.println("每条得分:"+searchHit.getScore());
            //获取每个字段的数据
            System.out.println("id:"+searchHit.getSource().get("id"));
            System.out.println("title:"+searchHit.getSource().get("title"));
            System.out.println("content:"+searchHit.getSource().get("content"));
            System.out.println("************************************");
            /*for (Iterator<SearchHitField> ite = searchHit.iterator();ite.hasNext();) {
                SearchHitField next = ite.next();
                System.out.println(next.getValues());
            }*/
        }
    }
}
