import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by angel
 */
public class Test {
    TransportClient client = null;

    @Before
    public void testConn(){

        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", "cluster_es").build();
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("hadoop01"), 9300));
            System.out.println("========连接成功=============");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    /**
     * upsert
     * */
    @org.junit.Test
    public void upsertDocument2() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 10; i++){
            executorService.execute(new Thread1());

        }
        Thread.sleep(10000);
        executorService.shutdown();

    }

    class Thread1 implements Runnable {

        public void run() {
            System.out.println("*************" + Thread.currentThread().getName() + " *************");
            // 设置查询条件, 查找不到则添加
            IndexRequest indexRequest = null;
            try {
                indexRequest = new IndexRequest("website", "blog", "1")
                        .source(XContentFactory.jsonBuilder()
                                .startObject()
                                .field("id", "1")
                                .endObject());
                // 设置更新, 查找到更新下面的设置
                UpdateRequest upsert = new UpdateRequest("website", "blog", "1")
                        .doc(XContentFactory.jsonBuilder()
                                .startObject()
                                .field("process_id", Thread.currentThread().getId())
                                .endObject())
                        .upsert(indexRequest);

                client.update(upsert).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    @After
    public void close(){
        client.close();
    }

}
