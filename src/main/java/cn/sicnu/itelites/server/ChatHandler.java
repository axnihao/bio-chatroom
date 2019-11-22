package cn.sicnu.itelites.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatHandler implements Runnable{
    private ChatServer server;
    private Socket socket;

    public ChatHandler(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            //存除新上线用户
            this.server.addClient(socket);

            //读取用户发送的消息
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            String msg = null;
            while ((msg = reader.readLine()) != null) {
                String fwdMsg = "客户端[" + this.socket.getPort() + "]: " + msg + "\n";
                System.out.print(fwdMsg);

                //将消息转发给聊天室里在线的其他用户
                this.server.forwardMessage(socket, fwdMsg);

                //检查用户是否准备退出
                if (this.server.readyToQuit(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.server.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
