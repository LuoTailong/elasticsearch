package cn.itcast.controller;

import cn.itcast.bean.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class Document {
    public static void main(String[] args) {
        String json = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        createDocumentByJson(json);

        Map<String, Object> map = new HashMap<String , Object >();
        map.put("name" ,"张三");
        map.put("age" , 18);
        createDocumentByMap(map);

        Person p = new Person();
        p.setUser("kimchy");
        p.setPostDate(new Date());
        p.setMessage("trying out Elasticsearch");
        createDocumentByObj(p);

        createDocuemntByJsonBuilder();

    }

    //1 使用json
    public static void createDocumentByJson(String json){
        //1.1 构建客户端连接
        TransportClient client = Connection.getClient();
        //1.2 指定要插入的文档
        IndexResponse indexResponse = client.prepareIndex("json", "json", "1").setSource(json, XContentType.JSON).get();
        String index = indexResponse.getIndex();
        String type = indexResponse.getType();
        System.out.println(indexResponse.status());
    }
    //2 使用map
    public static void createDocumentByMap(Map<String,Object> map){
        Settings settings = Settings.builder().put("cluster.name", "cluster_es").build();
        try {
            TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node1"), 9300));
            client.prepareIndex("map","map").setSource(map, XContentType.JSON).get();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    //3 对象对接方式
    public static void createDocumentByObj(Person person){
        TransportClient client = Connection.getClient();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(person);
            client.prepareIndex("person","persono","1").setSource(bytes,XContentType.JSON).get();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    //4 jsonBuilder
    public static void createDocuemntByJsonBuilder(){
        TransportClient client = Connection.getClient();
        try {
            client.prepareIndex("buildder","builder").setSource(jsonBuilder().startObject().field("name","网二").field("age",18).endObject()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
