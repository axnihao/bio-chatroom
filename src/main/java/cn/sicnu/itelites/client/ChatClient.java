package cn.sicnu.itelites.client;

import cn.sicnu.itelites.server.ChatServer;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private final int DEFAULT_SERVER_PORT = 8888;
    private final String QUIT = "quit";

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    //发送消息给服务器
    public void send(String msg) throws IOException {
        if (!socket.isOutputShutdown()) {
            this.writer.write(msg + "\n");
            this.writer.flush();
        }
    }

    //从服务器接收消息
    public String receive() throws IOException {
        String msg = null;
        if (!socket.isInputShutdown()) {
            msg = reader.readLine();
        }
        return msg;
    }

    //检查用户是否准备退出
    public boolean readyToQuit(String msg) {
        return QUIT.equals(msg);
    }

    public void close() {
        if (this.socket != null) {
            try {
                this.reader.close();
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        //创建Socket
        try {
            this.socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);

            //创建IO流
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

            //处理用户的输入 TODO
            new Thread(new UserInputHandler(this)).start();
            //读取服务器转发的消息
            String msg = null;
            while ((msg = receive()) != null) {
                System.out.println(msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    public static void main(String[] args) {
        new ChatClient().start();
    }
}
