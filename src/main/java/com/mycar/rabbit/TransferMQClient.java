package com.mycar.rabbit;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import com.mycar.globalConfig.EnvironmentConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class TransferMQClient {

    private static final Logger logger = LogManager.getLogger(TransferMQClient.class);

    private static Channel channel = null;

    public TransferMQClient() {

    }

    /**
     * isClearChannel 是否清除通道信息
     * */
    public static synchronized Channel getInstance(){
        ExecutorService threader = Executors.newFixedThreadPool(20);
        if (channel==null){
            // 配置连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(EnvironmentConfig.RABBITMQ_HOST);
            factory.setUsername(EnvironmentConfig.RABBITMQ_USER);
            factory.setPassword(EnvironmentConfig.RABBITMQ_PWD);
            factory.setExceptionHandler(new DefaultExceptionHandler(){
                @Override
                public void handleConfirmListenerException(Channel channel, Throwable exception) {
                    logger.info("=====消息确认发生异常=======");
                    exception.printStackTrace();
                    logger.error("ERROR-Exception::"+exception.getMessage(),exception);
                }
            });
            Connection connection = null;
            try{
                // 建立TCP连接
                try {
                    connection = factory.newConnection(threader);
                } catch (TimeoutException e) {
                    logger.error("ERROR-Exception::"+e.getMessage(),e);
                }
                // 在TCP连接的基础上创建通道
                channel = connection.createChannel();

                //mqtt amqp 不同协议间通信
                //不管有没有它，先删掉，再创建
                channel.exchangeDelete("iot");
                //首先创建一个交换机
                channel.exchangeDeclare("iot", "topic");
                //与MQTT的交换机绑定再一起
                channel.exchangeBind("iot", "amq.topic","st0001");

                //统一设置队列中的所有消息的过期时间，单位毫秒
                Map<String, Object> arguments = new HashMap<String, Object>();
                arguments.put("x-message-ttl", 500);
                //x-max-length:用于指定队列的长度，如果不指定，可以认为是无限长，
                // 例如指定队列的长度是4，当超过4条消息，前面的消息将被删除，给后面的消息腾位
                arguments.put("x-max-length", 2);
                //x-max-priority: 设置消息的优先级，优先级值越大，越被提前消费。
                arguments.put("x-max-priority", 10);
                //照旧，先干掉再创建
                channel.queueDelete(EnvironmentConfig.QUEUE_NAME);
                //创建一个队列
                channel.queueDeclare(EnvironmentConfig.QUEUE_NAME, false, false, false, arguments);
                // 将队列绑定到交换机
                channel.queueBind(EnvironmentConfig.QUEUE_NAME, "iot", "st0001");
                //同一时刻服务器只会发送50条消息给消费者
                channel.basicQos(0, 10, false);

                logger.info(" [TransferMQClient] start..");

                channel.basicAck(0,true);//true 针对整个信道
            }catch(IOException e){
                logger.error("ERROR-Exception::"+e.getMessage(),e);
            }
            return channel;
        }
        return channel;
    }

    public static void execute(Channel channel){

        try {
            // 默认消费者实现
            Channel finalChannel = channel;
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(message);

                }
            };
            // 接收消息
            channel.basicConsume(EnvironmentConfig.QUEUE_NAME, true, consumer);//false 与ack呼应


        } catch (Exception e) {
            logger.error("ERROR-Exception::"+e.getMessage(),e);
        }
    }
}
