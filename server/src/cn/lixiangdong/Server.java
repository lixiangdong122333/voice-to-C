package cn.lixiangdong;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class Server {

    /**
     * 服务器(本机)监听的端口号
     */
    public static final int SERVER_PORT = 8088;


    public static void main(String[] args) {
        //监听端口
        new Thread(new Monitor()).start();

    }
}

/**
 * 本类用于监听连接并创建对话线程
 */
class Monitor implements Runnable {

    @Override
    public void run() {
        ServerSocket serverSocket = null;
//        DatagramSocket serverSocket = null;

        try {
            System.out.println("监听" + Server.SERVER_PORT + "......");
            serverSocket = new ServerSocket(Server.SERVER_PORT);
//            serverSocket=new DatagramSocket(Server.SERVER_PORT);
            System.out.println("监听成功，等待接入");
        } catch (IOException e) {
            System.out.println("监听" + Server.SERVER_PORT + "失败");
            e.printStackTrace();
        }
        Socket accept;

        while (true) {
            try {
                //阻塞等待连接
                accept = serverSocket.accept();

                //新建聊天线程，将socket传给聊天线程并启动
//                new Thread(new Dialogue(accept)).start();
                ExecutorService executorService = Executors.newFixedThreadPool(6);
                executorService.execute(new Dialogue(accept));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 对话线程，采用TCP协议
 */
class Dialogue implements Runnable {
    private static ArrayList<BufferedOutputStream> outputOfUser = new ArrayList<>();
    Socket socket;
    byte[] data = new byte[1024];

    public Dialogue(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println(socket.getInetAddress() + "：已连接");
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
//        SourceDataLine sourceDataLine = null;
        try {

            //获取输入流
            bufferedInputStream =
                    new BufferedInputStream(
                            socket.getInputStream());

            //输出流

            bufferedOutputStream =
                    new BufferedOutputStream(
                            socket.getOutputStream());
            outputOfUser.add(bufferedOutputStream);


//            //音频处理测试
//            AudioFormat audioFormat = new AudioFormat(22050, 16, 1, true, false);
//            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
//            sourceDataLine.open(audioFormat);
//            sourceDataLine.start();

            while (bufferedInputStream.read(data, 0, data.length) != -1) {
//                sourceDataLine.write(data,0,data.length);//音频处理测试
                for (BufferedOutputStream bos : outputOfUser) {
                    if (bos != bufferedOutputStream)
                        bos.write(data, 0, data.length);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(socket.getInetAddress() + "：已断开");
            try {
//                sourceDataLine.close();
                bufferedInputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 控制线程，采用TCP协议
 */
class Control implements  Runnable{
    Socket socket;

    public Control(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InetAddress inetAddress = socket.getInetAddress();
        System.out.println(inetAddress);
    }

}