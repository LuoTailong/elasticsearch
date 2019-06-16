package cn.itcast.controller;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;

public class DeleteDocument {
    public static void main(String[] args) {
        deleteDocument();
        deleteByQuery();
        async();
    }

    private static void deleteDocument() {
        TransportClient client = Connection.getClient();
        client.prepareDelete("index1", "index1", "AWgSUPbMZhmqYdZf8L5x").get();
    }

    //通过查询的方式进行删除
    private static void deleteByQuery() {
        TransportClient client = Connection.getClient();
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("name", "李四"))
                .source("index1").get();
    }

    //查询删除--异步方式
    private static void async() {
        TransportClient client = Connection.getClient();
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("name", "网二"))
                .source("buildder")
                .execute(new ActionListener<BulkByScrollResponse>() {
                    public void onResponse(BulkByScrollResponse bulkByScrollResponse) {
                        //删除成功后做什么
                    }

                    public void onFailure(Exception e) {
                        //删除失败后做什么
                    }
                });
    }
}
