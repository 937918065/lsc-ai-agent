package com.lscai.lscaiagent.APP;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;



@SpringBootTest
class RPAAppTest {

    @Resource
    private RPAApp rpaApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();

        String message = "你好，我是赖思成";
        String answer = rpaApp.doChat(message,chatId);

        message = "我是惠天信息科技公司的员工";
        answer =  rpaApp.doChat(message,chatId);
        Assertions.assertNotNull(answer);

        message = "我的公司是哪个，我告诉过你";
        answer =  rpaApp.doChat(message,chatId);
        Assertions.assertNotNull(answer);
    }


    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是文员lsc，我想要做一个自动读取excal数据的程序，但我不知道怎么做";
        RPAApp.RPAReport rpaReport =  rpaApp.doChatWithReport(message,chatId);
        Assertions.assertNotNull(rpaReport);
    }
}