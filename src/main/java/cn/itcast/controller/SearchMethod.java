package cn.itcast.controller;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;

import java.util.Iterator;

public class SearchMethod {
    @Test
    public void searchType(){
        TransportClient client = Connection.getClient();
        SearchRequestBuilder builder = client.prepareSearch("player").setTypes("player");
        SearchResponse searchResponse = builder.setQuery(QueryBuilders.matchQuery("name", "于谦"))
//                .setPreference("_local")
//                .setPreference("_primary")
//                .setPreference("_only_nodes:*")
//                .setPreference("_prefer_nodes:SearchMethod")
                .setPreference("_shards:0,1,2")//可以提高查询效率
//                .setPreference("randomizeacross")
                .get();//指定查询方式
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询结果数量有"+hits.getTotalHits()+"条");
        System.out.println("结果中最高分："+hits.getMaxScore());

        //遍历每条数据
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next();
            System.out.println("所有的数据JSON的数据格式："+searchHit.getSourceAsString());
            //获取每个字段的数据
            System.out.println("id:"+searchHit.getSource().get("id"));
            System.out.println("name:"+searchHit.getSource().get("name"));
            System.out.println("age:"+searchHit.getSource().get("age"));
            System.out.println("******************************");
            for(Iterator<SearchHitField> ite = searchHit.iterator(); ite.hasNext();){
                SearchHitField next = ite.next();
                System.out.println(next.getValues());
            }
        }
    }
}
