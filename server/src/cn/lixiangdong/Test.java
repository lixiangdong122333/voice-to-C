package cn.lixiangdong;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Test {
    public static AudioFormat audioFormat = new AudioFormat(22050, 16, 1, true, false);
    public static Socket socket;

    static {
        try {
            socket = new Socket(Test.IP, Test.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 要链接的服务器的IP地址
     */
    public static final String IP = "192.168.50.140";
    /**
     * 服务器端口号
     */
    public static final int SERVER_PORT = 8088;

    public static void main(String[] args) {
        Thread sendOut = new Thread(new sendOut());
        Thread receive = new Thread(new receive());

        sendOut.start();
        receive.start();

    }
}


class sendOut implements Runnable {
    @Override
    public void run() {
        try {

            byte[] data = new byte[1024];
            TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(Test.audioFormat);
            targetDataLine.open(Test.audioFormat);
            targetDataLine.start();


            BufferedOutputStream bufferedOutputStream =
                    new BufferedOutputStream(
                            Test.socket.getOutputStream());

            while (targetDataLine.read(data, 0, data.length) != -1) {
                bufferedOutputStream.write(data, 0, data.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class receive implements Runnable {
    SourceDataLine sourceDataLine = null;
    byte[] data = new byte[1024];

    @Override
    public void run() {
        try {

            sourceDataLine = AudioSystem.getSourceDataLine(Test.audioFormat);
            sourceDataLine.open(Test.audioFormat);
            sourceDataLine.start();

            BufferedInputStream bufferedInputStream=
                    new BufferedInputStream(
                            Test.socket.getInputStream()
                    );

            while (bufferedInputStream.read(data, 0, data.length) != -1){
                sourceDataLine.write(data, 0, data.length);
            }
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}