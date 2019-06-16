package cn.itcast.controller;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.util.Iterator;

public class Filter {
    public static void main(String[] args) {
        rangeFilter();
        boolFilter();
    }

    private static void rangeFilter() {
        TransportClient client = Connection.getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo").setTypes("dahan").setPostFilter(QueryBuilders.rangeQuery("age").from(18).to(20)).get();
        Iterator<SearchHit> iterator = searchResponse.getHits().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().getSourceAsString());
        }
    }

    private static void boolFilter() {
        TransportClient client = Connection.getClient();
        SearchResponse searchResponse = client.prepareSearch("sanguo").setTypes("dahan").setPostFilter(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("name", "张飞"))
                .mustNot(QueryBuilders.termQuery("male", "女"))
                .should(QueryBuilders.matchQuery("address", "北京"))
        ).get();
        Iterator<SearchHit> iterator = searchResponse.getHits().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().getSourceAsString());
        }
    }
}
