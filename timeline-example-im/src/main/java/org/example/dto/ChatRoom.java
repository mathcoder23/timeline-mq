package org.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/6/30 16:46
 */
@Getter
@Setter
public class ChatRoom {
    private ImGroup group;
    private List<String> userIdList = new CopyOnWriteArrayList<>();
}
