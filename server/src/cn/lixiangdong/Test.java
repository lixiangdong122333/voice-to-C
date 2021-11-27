package cn.lixiangdong;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import java.io.BufferedOutputStream;
import java.net.Socket;

public class Test {
    /**
     * 要链接的服务器的IP地址
     */
    public static final String IP = "118.190.156.3";
    /**
     * 服务器端口号
     */
    public static final int SERVER_PORT = 8088;

    public static void main(String[] args) throws Exception {
        byte[] data = new byte[1024];
        AudioFormat audioFormat = new AudioFormat(22050, 16, 1, true, false);
        TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
        targetDataLine.open(audioFormat);
        targetDataLine.start();

        Socket socket = new Socket(IP, SERVER_PORT);

        BufferedOutputStream bufferedOutputStream =
                new BufferedOutputStream(
                        socket.getOutputStream());

        while (targetDataLine.read(data, 0, data.length) != -1) {
            bufferedOutputStream.write(data, 0, data.length);
        }
    }
}
