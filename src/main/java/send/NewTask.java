package send;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class NewTask {
    //Queue name 지정
    private final static String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {
        //서버와 연결 만들기 (local host)
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //data를 queue에 유지하기.
            channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
            //보낼 message
            String message = String.join(" ", argv);
            //queue name으로 message를 바이트 형태로 보냄.
            //메세지 지속성 MessageProperties.PERSISTENT_TEXT_PLAIN
            channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println(" [x] Send '" + message + "'");
        }
    }
}
