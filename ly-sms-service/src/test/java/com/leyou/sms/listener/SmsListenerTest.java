package com.leyou.sms.listener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsListenerTest {
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Test
    public void listenVerfyCode() throws InterruptedException {
        Map<String,Object> map = new HashMap<>();
        map.put("phone","17737532689");
        map.put("code","comeon");
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", map);
        Thread.sleep(5000);
    }
}