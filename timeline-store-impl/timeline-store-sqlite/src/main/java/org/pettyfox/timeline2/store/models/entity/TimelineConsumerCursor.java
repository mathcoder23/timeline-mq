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
public class TimelineConsumerCursor {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    private String consumerId;
    private String producerId;
    private Long cursorId;

    private Date createTime;
    private Date updateTime;
}
