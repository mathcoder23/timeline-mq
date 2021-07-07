package org.example.controller;

import org.example.dto.GuardData;
import org.example.timeline.StoreService;
import org.example.timeline.event.GuardDataEvent;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 门禁数据管理
 *
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
@RestController
@RequestMapping("/guardData")
public class GuardDataController {
    @Resource
    private StoreService storeService;
    @Resource
    private GuardDataEvent guardDataEvent;

    @PostMapping("add")
    public String add(@RequestBody GuardData guardData) {
        GuardData data = storeService.getGuardDataMap().get(guardData.getId());
        if (null != data && !data.getGuardGroupId().equals(guardData.getGuardGroupId())) {
            guardDataEvent.onDataDelGroup(guardData.getId(), data.getGuardGroupId());
        }
        storeService.getGuardDataMap().put(guardData.getId(), guardData);
        guardDataEvent.onDataAddGroup(guardData.getId(), guardData.getGuardGroupId());


        return "ok";
    }

    @PostMapping("del")
    public String del(@RequestBody GuardData guardData) {
        GuardData data = storeService.getGuardDataMap().get(guardData.getId());
        if (null != data) {
            guardDataEvent.onDataDel(guardData.getId());
            storeService.getGuardDataMap().remove(guardData.getId());
        }

        return "ok";
    }

    @PostMapping("modify")
    public String modify(@RequestBody GuardData guardData) {
        GuardData data = storeService.getGuardDataMap().get(guardData.getId());
        if (null != data) {
            guardDataEvent.onDataDelGroup(guardData.getId(), data.getGuardGroupId());
        }
        storeService.getGuardDataMap().put(guardData.getId(), guardData);
        guardDataEvent.onDataAddGroup(guardData.getId(), guardData.getGuardGroupId());

        return "ok";
    }


    @GetMapping("list")
    public Map<Long, GuardData> list() {
        return storeService.getGuardDataMap();
    }


}
