package org.example.timeline;

import org.example.dto.GuardData;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/2
 */
@Component
public class StoreService {
    private final ConcurrentHashMap<Long, GuardData> guardDataMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Long,GuardData> getGuardDataMap(){
        return guardDataMap;
    }
}
