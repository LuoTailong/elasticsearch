package cn.itcast.controller;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class CreateIndex {
    public static void main(String[] args) {
//        createIndex();
        createIndexSetting();
//        deleteIndex();
//        createIndex1();
        createMappings();
    }

    private static void createIndex() {
        TransportClient client = Connection.getClient();
        client.admin().indices().prepareCreate("demo").get();
    }

    private static void createIndexSetting() {
        TransportClient client = Connection.getClient();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("number_of_shards",3);
        map.put("number_of_replicas",2);
        client.admin().indices().prepareCreate("sanguo").setSettings(map).get();
    }

    public static void deleteIndex(){
        TransportClient client = Connection.getClient();
        client.admin().indices().prepareDelete("sanguo").get();

    }

    private static void createIndex1() {
        TransportClient client = Connection.getClient();
        CreateIndexResponse blogs2 = client.admin().indices().prepareCreate("demo1").get();
        System.out.println(blogs2.toString());
    }

    private static void createMappings() {
        TransportClient client = null;
        Settings settings = Settings.builder().put("cluster.name", "cluster_es").build();
        try {
            client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node1"),9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("=========连接成功=========");

        XContentBuilder builder = null;
        try {
            builder = jsonBuilder().startObject().startObject("dahan").field("dynamic", "true")
                    .startObject("properties")
                    .startObject("studentNo").field("type", "string").field("store", "yes").endObject()
                    .startObject("name").field("type", "string").field("store", "yes").field("analyzer", "ik_max_word").endObject()//.field("analyzer", "ik")
                    .startObject("male").field("type", "string").field("store", "yes").endObject()//.field("analyzer", "ik")
                    .startObject("age").field("type", "integer").field("store", "yes").endObject()
                    .startObject("birthday").field("type", "string").field("store", "yes").endObject()
                    .startObject("classNo").field("type", "string").field("store", "yes").endObject()
                    .startObject("address").field("type", "string").field("store", "yes").field("analyzer", "ik_max_word").endObject()
                    .startObject("isLeader").field("type", "boolean").field("store", "yes").field("index", "not_analyzed").endObject()
                    .endObject()
                    .endObject()
                    .endObject();

            PutMappingRequest mapping = Requests.putMappingRequest("sanguo").type("dahan").source(builder);
            client.admin().indices().putMapping(mapping).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
