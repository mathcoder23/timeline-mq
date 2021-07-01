package org.example.boot;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ChatRoom;
import org.example.dto.ImGroup;
import org.example.dto.ImUser;
import org.pettyfox.timeline2.core.TimelineCursorMq;
import org.pettyfox.timeline2.core.TimelineMqConsumerTimeout;
import org.pettyfox.timeline2.core.TimelineMqFactory;
import org.pettyfox.timeline2.model.TimelineHead;
import org.pettyfox.timeline2.model.TimelineMessage;
import org.pettyfox.timeline2.store.TimelineConsumerCursorStore;
import org.pettyfox.timeline2.store.TimelineExchange;
import org.pettyfox.timeline2.store.impl.TimelineConsumerCursorStoreMemoryImpl;
import org.pettyfox.timeline2.store.impl.TimelineExchangeMemoryImpl;
import org.pettyfox.timeline2.store.impl.TimelineMqStoreMemoryImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/30 16:49
 */
@Slf4j
public class RunnerTest implements TimelineMqConsumerTimeout {
    private final Snowflake snowflake = new Snowflake(1, 1);
    TimelineExchange exchange = new TimelineExchangeMemoryImpl();
    // 初始化存储引擎
    TimelineConsumerCursorStore timelineConsumerCursorStore = new TimelineConsumerCursorStoreMemoryImpl();
    // 初始化Mq
    TimelineCursorMq timelineMq = TimelineMqFactory.createCursorMq(new TimelineMqStoreMemoryImpl(), exchange, timelineConsumerCursorStore);

    public static void main(String[] args) throws InterruptedException {

        new RunnerTest().start();

    }

    public void start() throws InterruptedException {
        timelineMq.setTimeoutListener(this);
        ImUser userZs = createUser("张三");
        ImUser userLs = createUser("李四");
        ImUser userVip = createUser("客户Vip1");
        //创建聊天室
        ChatRoom chatRoom = createChatRoom("买房报价组");
        //加入聊天室
        addChatRoom(chatRoom, userLs);
        addChatRoom(chatRoom, userVip);
        addChatRoom(chatRoom, userZs);
        //发送消息
        ThreadUtil.newThread(() -> {

            try {
                sendMessage(userVip, chatRoom, "你们这个xxx，怎么报价的？");
                Thread.sleep(1000);

                sendMessage(userZs, chatRoom, "@李四，这报价怎么还没出来？");

                Thread.sleep(1000);
                sendMessage(userLs, chatRoom, "稍等，正在进行核算");

                Thread.sleep(1000);
                sendMessage(userVip, chatRoom, "快点儿吧，等了两天了~~~~~~");

                Thread.sleep(1000);
                sendMessage(userLs, chatRoom, "亲报价好了哦，您这套一的房子大约要1700万(*￣︶￣)");

                Thread.sleep(1000);
                sendMessage(userVip, chatRoom, "抱歉，打扰了~");


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, "message").start();

        new CountDownLatch(1).await();
    }

    private ImUser createUser(String name) {
        ImUser imUser = new ImUser();
        imUser.setId(SecureUtil.md5(name));
        imUser.setName(name);
        // 监听用户消息
        timelineMq.registerConsumer(imUser.getId(), 10, queue -> {
            for (TimelineMessage message : queue) {
                log.info("[{}]接收消息，{}", name, message.getBody());
                timelineMq.consumerAck(imUser.getId(), message);
            }
        });
        return imUser;
    }

    /**
     * 创建聊天室
     *
     * @param name
     * @return
     */
    private ChatRoom createChatRoom(String name) {
        ImGroup group = new ImGroup();
        group.setId(SecureUtil.md5(name));
        group.setName(name);
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setGroup(group);
        return chatRoom;
    }

    private synchronized void addChatRoom(ChatRoom chatRoom, ImUser imUser) {
        if (chatRoom.getUserIdList().contains(imUser.getId())) {
            return;
        }
        chatRoom.getUserIdList().add(imUser.getId());
        List<String> exists = exchange.listByBeSubscribe(imUser.getId());
        if (null == exists) {
            exists = new ArrayList<>();
        }
        Set<String> crList = new HashSet<>(exists);
        crList.add(chatRoom.getGroup().getId());
        exchange.subscribe(imUser.getId(), crList);
    }

    private void sendMessage(ImUser imUser, ChatRoom chatRoom, String text) {
        TimelineMessage message = new TimelineMessage();
        message.setId(snowflake.nextId());
        message.setBody(String.format("%s说：%s", imUser.getName(), text));
        message.setTopic(chatRoom.getGroup().getId());
        timelineMq.push(message);
    }

    @Override
    public void timeout(String consumerId, TimelineHead timelineHead) {
        log.info("time out {},{}", consumerId, timelineHead.getId());
    }
}
