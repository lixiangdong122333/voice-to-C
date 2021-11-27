package cn.lixiangdong;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class Server {
    /**
     * 广播端口
     */
    public static final int BROADCAST_PORT = 8099;

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

        try {
            System.out.println("监听" + Server.SERVER_PORT + "......");
            serverSocket = new ServerSocket(Server.SERVER_PORT);
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
                new Thread(new Dialogue(accept)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 对话线程，收入采用TCP协议，分发采用UDP协议
 */
class Dialogue implements Runnable {
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


//            //音频处理测试
//            AudioFormat audioFormat = new AudioFormat(22050, 16, 1, true, false);
//            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
//            sourceDataLine.open(audioFormat);
//            sourceDataLine.start();

            while (bufferedInputStream.read(data, 0, data.length) != -1) {
//                sourceDataLine.write(data,0,data.length);//音频处理测试
                bufferedOutputStream.write(data, 0, data.length);

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