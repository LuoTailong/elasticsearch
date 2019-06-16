package cn.itcast.controller;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import sun.applet.Main;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Connection {
    static String cluster_name = "cluster_es";
    static String host = "node1";
    static int port = 9300;

    public static TransportClient getClient(){
        //1 指定集群名称
        Settings settings = Settings.builder().put("cluster.name", cluster_name).build();
        TransportClient client = null;

        //2 构建客户端的连接操作
        try {
            client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
            System.out.println(client.listedNodes());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return client;
    }

    public static void main(String[] args) {
        getClient();
    }
}
