package recv;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Worker {
    //Queue name 선언
    private final static String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws Exception {

        //서버와 연결 만들기
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //대기열 선언
        channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
        System.out.println("[*] Waiting for messages, To exit press CTRL+C" +
                "[*] 메세지를 기다리는 중입니다. 종료하려면 CTRL+C를 누르십시오.");

        //기존 메세지 = 균등하게 적용 -> 현제 메세지 = RabbitMQ가 consumer 에게 한 번에 둘 이상의 메세지를 제공하지 않음.
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);

        //데이터를 받을때까지 버퍼링/ 받게되면 byte 데이터를 String으로 변환.
        //가상 딜레이 적용
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            System.out.println(" [x] Received '" + message + "'");
            try {
                doWork();
            } finally {
                System.out.println(" [x] Done");
                //ack를 전송(메세지의 손실 여부를 판단)
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        //tcp 통신에서의 ack 설정.
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {
        });
    }

    //가상 딜레이 설정
    private static void doWork() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
