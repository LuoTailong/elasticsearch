package cn.itcast.controller;

import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class Bulk {
    public static void main(String[] args) {
        asyncBulk();
//        bulk();
    }

    private static void bulk() {
        TransportClient client = Connection.getClient();
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        try {
            bulkRequestBuilder.add(client.prepareIndex("index1", "index1").setSource(jsonBuilder().startObject().field("name", "张三").field("age", 18).endObject()));
            bulkRequestBuilder.add(client.prepareIndex("index1", "index2").setSource(jsonBuilder().startObject().field("name", "李四").field("age", 19).endObject()));
            bulkRequestBuilder.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void asyncBulk() {
        TransportClient client = Connection.getClient();
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            //在批量操作执行前，会做什么操作
            public void beforeBulk(long l, BulkRequest bulkRequest) {

            }

            //批量执行成功之后做什么
            public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {

            }

            //批量执行异常做什么
            public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {

            }
        })
                // 1w次请求执行一次bulk
                .setBulkActions(10000)
                //1gb的数据刷新一次bulk
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
                //固定5s必须刷新一次
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                //并发请求数量 0不并发 1并发允许执行
                .setConcurrentRequests(1)
                //设置退避 100ms后执行 最大请求3次
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();

        //请求添加数据
        Map<String,Object> m = new HashMap<String, Object>();
        m.put("document","这里是异步批量插入测试");
        bulkProcessor.add(new IndexRequest("testblog","test","1").source(m));
        bulkProcessor.add(new IndexRequest("testblog","test","2").source(m));
        bulkProcessor.flush();
        try {
            bulkProcessor.awaitClose(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
