package cn.itcast.controller;

import org.elasticsearch.action.get.*;
import org.elasticsearch.client.transport.TransportClient;

public class SearchDocument {
    public static void main(String[] args) {
        searchOne();
        mutilSearch();
    }

    //1 单条
    private static void searchOne() {
        TransportClient client = Connection.getClient();
        GetRequestBuilder getRequestBuilder = client.prepareGet("index1", "index1", "AWgSUPbMZhmqYdZf8L5x");
        GetResponse getFields = getRequestBuilder.get();
        System.out.println(getFields.toString());
    }


    //2 批量
    private static void mutilSearch() {
        TransportClient client = Connection.getClient();
        MultiGetRequestBuilder multiGetRequestBuilder = client.prepareMultiGet();
        multiGetRequestBuilder.add("index1","index1","AWgSUPbMZhmqYdZf8L5x");
        multiGetRequestBuilder.add("index1","index2","AWgSUPbMZhmqYdZf8L5y");
        MultiGetResponse multiGetResponse = multiGetRequestBuilder.get();
        for (MultiGetItemResponse response : multiGetResponse) {
            System.out.println(response.getResponse().getSourceAsString());
        }
    }
}
