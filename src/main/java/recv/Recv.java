package recv;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class Recv {
    //Queue name 선언
    private final static String QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws Exception {
        //서버와 연결 만들기
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //대기열 선언
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("[*] Waiting for messages, To exit press CTRL+C" +
                "[*] 메세지를 기다리는 중입니다. 종료하려면 CTRL+C를 누르십시오.");
        //데이터를 받을때 까지 버퍼링 받게되면 byte 데이터를 String으로 변환.
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [X] Received '" + message + "'");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }
}
