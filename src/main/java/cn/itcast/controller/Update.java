package cn.itcast.controller;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import java.io.IOException;
import java.net.InetAddress;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class Update {
    public static void main(String[] args) {
        updateDocument();
        searchUpdate();
    }

    //1 更新整条文档
    private static void updateDocument() {
        Settings settings = Settings.builder().put("cluster.name", "cluster_es").build();
        try {
            TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node1"), 9300));
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index("index1");
            updateRequest.type("index2");
            updateRequest.id("AWgSUPbMZhmqYdZf8L5y");
            updateRequest.doc(jsonBuilder().startObject().field("name","李四").field("age",20).endObject());
            client.update(updateRequest).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //2 查询不到则添加 查询到则修改
    private static void searchUpdate() {
        TransportClient client = Connection.getClient();
        //2.1 查询 查找不到会添加
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index("school");
        indexRequest.type("class");
        indexRequest.id("1");
        try {
            indexRequest.source(jsonBuilder().startObject().field("gender","male")
                    .field("title","ElasticSearch是一个基于Lucene的搜索服务器")
                    .field("content","它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口"
                            +"Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，"
                            +"是当前流行的企业级搜索引擎，设计用于云计算中，能够达到实时搜索，"
                            +"稳定，可靠，快速，安装使用方便").endObject());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //2.2 查找到则修改
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("school");
        updateRequest.type("class");
        updateRequest.id("1");
        try {
            updateRequest.doc(jsonBuilder().startObject().field("gender","female")
                    .field("title","ElasticSearch是一个基于Lucene的搜索服务器")
                    .field("content", "它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。"
                            +"Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，"
                            +"是当前流行的企业级搜索引擎。设计用于云计算中，能够达到实时搜索，"
                            +"稳定，可靠，快速，安装使用方便。").endObject()).upsert(indexRequest);
            //2.3 提交执行
            client.update(updateRequest).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
