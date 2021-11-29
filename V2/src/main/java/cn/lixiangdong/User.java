package cn.lixiangdong;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class User {
    /**与客户端的TCP连接*/
    private Socket socket;
    /**连接的客户端的IP地址*/
    InetAddress inetAddress;
    /**连接的客户端的端口号*/
    int port;

    public User(Socket socket) {
        this.socket = socket;
        inetAddress = socket.getInetAddress();
        port = socket.getPort();
    }

    public class controlOut implements Runnable {
        PrintStream outStream;
        /**准备语音输出*/
        public void prepareVoiceOut(){
            try {
                //创建输出流
                outStream = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));
                //实例化语音输出类
                voiceOut out = new voiceOut();
                //创建语音输出线程
                Thread voiceOut = new Thread(out);
                //启动语音输出线程
                voiceOut.start();
                //获取语音输出端口号
                int port = out.port;
                //将语音输出端口号发送到客户端











            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            //准备语音输出服务
            prepareVoiceOut();

            //建立定时器
            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                }
            },100, 100);


        }
    }

    private class controlIn implements Runnable {

        @Override
        public void run() {

        }
    }

    private class voiceOut implements Runnable {
        public int port;

        @Override
        public void run() {
            DatagramSocket ds;
            try {
                ds = new DatagramSocket();
                port = ds.getPort();
            } catch (SocketException e) {
                System.out.println("绑定端口失败");
                e.printStackTrace();
            }


        }
    }

    private class voiceIn implements Runnable {

        @Override
        public void run() {

        }
    }
}

