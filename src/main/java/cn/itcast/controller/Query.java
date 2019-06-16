package cn.itcast.controller;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;

public class Query {
    public static void main(String[] args) {
        boolQuery();
    }

    public static TransportClient getClient(){
        Settings settings = Settings.builder().put("cluster.name", "cluster_es").build();
        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node1"),9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return client;
    }

    //1:matchAllQuery
    public static void matchAll(){
        TransportClient client = getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo").setTypes("dahan").setQuery(QueryBuilders.matchAllQuery()).get();
        print(searchResponse);
    }

    //2:queryStringQuery
    public static void queryString(){
        TransportClient client = getClient();
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("sanguo").setTypes("dahan");
        SearchRequestBuilder requestBuilder = searchRequestBuilder.setQuery(QueryBuilders.queryStringQuery("刘备"));
        SearchResponse searchResponse = requestBuilder.get();
        print(searchResponse);
    }

    //3:通配符wildcardQuery
    public static void wildcardQuery(){
        TransportClient client = getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo")
                .setTypes("dahan").setQuery(QueryBuilders.wildcardQuery("address", "上海*")).get();
        print(searchResponse);
    }

    //4:termQuery
    public static void termQuery(){
        TransportClient client = getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo").setTypes("dahan")
                .setQuery(QueryBuilders.termsQuery("name", "张飞蛋", "关羽", "刘备")).get();
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("当前获取到的num:"+totalHits);
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsString());
        }
    }

    //5:matchQuery
    public static void matchQuery(){
        TransportClient client = getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo").setTypes("dahan").setQuery(QueryBuilders.matchQuery("name", "张飞蛋")).get();
        print(searchResponse);
    }

    //6:multiMatchQuery
    public static void multiMatchQuery(){
        TransportClient client = getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo").setTypes("dahan").setQuery(QueryBuilders.multiMatchQuery("北京", "name", "address")).get();
        print(searchResponse);
    }

    //7:idsQuery
    public static void idsQuery(){
        TransportClient client = getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo").setTypes("dahan").setQuery(QueryBuilders.idsQuery().addIds("AWgTw-0sTnCY98iLkr8H")).get();
        print(searchResponse);
    }

    //8:fuzzyQuery
    public static void fuzzyQuery(){
        TransportClient client = getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo").setTypes("dahan").setQuery(QueryBuilders.fuzzyQuery("address", "北京")).get();
        print(searchResponse);
    }

    //9：范围查询
    public static void rangeQuery(){
        TransportClient client = getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo").setTypes("dahan").setQuery(QueryBuilders.rangeQuery("age").from(18).to(22).includeLower(true).includeLower(false)).get();
        print(searchResponse);
    }

    //10:复合查询
    public static void boolQuery(){
        TransportClient client = getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo").setTypes("dahan").setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("address", "深圳")).must(QueryBuilders.termsQuery("male", "女"))).get();
        print(searchResponse);
    }

    //11:排序查询
    public static void sortQuery(){
        TransportClient client = getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo").setTypes("dahan").setQuery(QueryBuilders.matchAllQuery()).addSort("age", SortOrder.DESC).get();
        print(searchResponse);
    }

    public static void print(SearchResponse searchResponse){
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsString());
        }
    }
}
