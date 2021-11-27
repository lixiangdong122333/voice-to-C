package cn.lixiangdong;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class Server {
    public static void main(String[] args) {
        //监听端口
        new Thread(new Monitor()).start();

    }
}

/**
 * 本类用于监听连接并创建对话线程
 */
class Monitor implements Runnable {
    /**
     * 服务器(本机)监听的端口号
     */
    public static final int SERVER_PORT = 8088;

    @Override
    public void run() {
        ServerSocket serverSocket = null;

        try {
            System.out.println("监听"+SERVER_PORT+"......");
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("监听成功，等待接入");
        } catch (IOException e) {
            System.out.println("监听" + SERVER_PORT + "失败");
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
    byte[] data=new byte[1024];
    public Dialogue(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println(socket.getInetAddress()+"：已连接");
        BufferedInputStream bufferedInputStream = null;
        SourceDataLine sourceDataLine = null;
        try {

            //获取输入流
            bufferedInputStream =
                    new BufferedInputStream(
                            socket.getInputStream());


            //音频处理
            AudioFormat audioFormat = new AudioFormat(22050, 16, 1, true, false);
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            while (bufferedInputStream.read(data,0,data.length)!=-1){
                sourceDataLine.write(data,0,data.length);
            }
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }finally {
            System.out.println(socket.getInetAddress()+"：已断开");
            try {
                sourceDataLine.close();
                bufferedInputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}