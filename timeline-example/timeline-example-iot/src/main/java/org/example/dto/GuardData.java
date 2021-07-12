package org.example.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 门禁数据
 *
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
@Getter
@Setter
public class GuardData {
    private Long id;
    /**
     * 门禁刷卡id
     */
    private String cardId;

    /**
     * 门禁组id
     */
    private Long guardGroupId;
}
