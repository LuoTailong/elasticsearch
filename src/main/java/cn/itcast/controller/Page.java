package cn.itcast.controller;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;

public class Page {
    public static void main(String[] args) {
        scrollPage(10);
    }

    public static void page() {
        Settings settings = Settings.builder().put("cluster.name", "cluster_es").build();
        try {
            TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node1"), 9300));
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch("blog2").setTypes("article").setQuery(QueryBuilders.matchAllQuery());
            SearchResponse searchResponse = searchRequestBuilder.get();
            //总数
            long totalHits = searchResponse.getHits().getTotalHits();
            int pageSize = 10;
            long pageNum = totalHits / pageSize;
            for (int i = 0; i < pageNum; i++) {
                System.out.println("========第" + (i + 1) + "页=========");
                SearchResponse response = searchRequestBuilder.setFrom(i * pageSize).setSize(pageSize).get();
                Iterator<SearchHit> iterator = response.getHits().iterator();
                while (iterator.hasNext()) {
                    SearchHit next = iterator.next();
                    System.out.println(next.getSourceAsString());
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    //2:深分页
    public static void scrollPage(int pageSize) {
        TransportClient client = Connection.getClient();
        SearchResponse searchResponse = client.prepareSearch("blog2").setTypes("article").setSize(pageSize).setScroll(TimeValue.timeValueMillis(100)).get();
        long totalHits = searchResponse.getHits().getTotalHits();
        long pageNum = totalHits / pageSize;
        for (int i = 0; i < pageNum; i++) {
            System.out.println("=============当前是第：" + (i + 1) + "页=============");
            SearchResponse response = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(TimeValue.timeValueMillis(100)).get();
            Iterator<SearchHit> iterator = response.getHits().iterator();
            while (iterator.hasNext()) {
                SearchHit next = iterator.next();
                System.out.println(next.getSourceAsString());
            }
        }
    }
}
