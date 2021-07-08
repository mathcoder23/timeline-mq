package org.pettyfox.timeline2.store.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/7
 */
@Getter
@Setter
public class TimelineMessageRecord {
    /**
     * 严格递增id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * topic主题
     */
    private String topic;


    private String channel;

    /**
     * 对象id
     */
    private String objId;

    private String body;

    /**
     * 优化标记
     */
    private Boolean optimizeFlag;

    private Date createTime;
    private Date updateTime;
}
