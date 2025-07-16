package com.lscai.lscaiagent.APP;


import com.lscai.lscaiagent.advisor.MyLoggerAdvisor;
import com.lscai.lscaiagent.chatmemory.FileBasedChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class RPAApp {


    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是一个专业的RPA需求分析师，专注于外贸行业。你的任务是从员工描述的日常工作中识别出适合使用影刀RPA自动化的流程和任务。请根据以下员工的口头描述，分析并输出以下内容：\n1.  任务名称 ：用简洁的语言总结员工当前执行的任务。\n2.  任务频率 ：该任务是每天、每周、每月还是一次性的？\n3.  当前操作方式 ：员工是如何手动完成这项任务的？涉及哪些系统或工具？\n4.  自动化可行性 ：是否适合通过影刀RPA实现自动化？说明理由。\n5.  预期收益 ：如果实现自动化，预计节省多少时间或提升多少效率？\n6.  推荐RPA流程模块 ：建议使用影刀RPA中的哪些组件或功能来实现（如Excel处理、网页抓取、邮件监控等）？\n请以结构化表格形式输出结果，并使用中文简洁表达。".formatted();

    public RPAApp(@Qualifier("dashscopeChatModel") ChatModel dashscopeChatmodel) {
//        初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir")+ "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
//        初始化基于内存的对话记忆
//        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatmodel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
//                        自定义日志拦截器，可按需开启
                        new MyLoggerAdvisor()
//                        自定义推理增强,可按需开启
//                        new ReReadingAdvisor()
                )
                .build();
    }
    public String doChat(String message,String chatId) {
        ChatResponse chatResponse = chatClient
                 .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    record RPAReport(String title, List<String> suggestions){

    }

    /**
     * AI 需求文档功能（实战结构化输出）
     * @param message
     * @param chatId
     * @return
     */
    public RPAReport doChatWithReport(String message,String chatId) {
        RPAReport rpaReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成需求文档,标题为{用户名}的需求文档，内容为需求列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(RPAReport.class);
        log.info("rpaRport: {}", rpaReport);
        return rpaReport;
    }
}

