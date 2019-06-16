import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by angel on 2018/5/9.
 */
public class Monitor_TCP {
    public static void main(String[] args) throws Exception{
        Socket client = new Socket("hadoop01" , 9876);
        PrintWriter printWriter = new PrintWriter(client.getOutputStream());
        System.out.println("建立连接========");
        for(int i=0;i<20;i++){
            printWriter.println("建立连接发送数据，当前的数据是第"+i+"条");
            printWriter.flush();
        }
    }
}
