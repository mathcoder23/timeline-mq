package org.pettyfox.timeline2.store;

import org.pettyfox.timeline2.core.TimelineExchange;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Petty Fox
 * @version 1.0
 */
public class TimelineExchangeMemoryImpl implements TimelineExchange {
    private final ConcurrentHashMap<String, Set<String>> producerCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> consumerCache = new ConcurrentHashMap<>();

    @Override
    public void subscribe(String consumerId, String ... producerIds) {
        for(String producerId : producerIds){
            producerCache.computeIfAbsent(producerId,(key)-> new HashSet<>()).add(consumerId);
            consumerCache.computeIfAbsent(consumerId,(key)->new HashSet<>()).add(producerId);
        }

    }

    @Override
    public void unsubscribe(String consumerId, String producerId) {
        producerCache.computeIfAbsent(producerId,(key)-> new HashSet<>()).remove(consumerId);
        consumerCache.computeIfAbsent(consumerId,(key)->new HashSet<>()).remove(producerId);
    }

    @Override
    public void removeAllSubscribe(String consumerId) {
        consumerCache.remove(consumerId);
    }

    @Override
    public void removeAllSubscribeByBeSubscribe(String producerId) {
        producerCache.remove(producerId);
    }

    @Override
    public List<String> listBySubscribe(String producerId) {
        return new ArrayList<>(producerCache.get(producerId));
    }

    @Override
    public List<String> listByBeSubscribe(String consumerId) {
        return new ArrayList<>(consumerCache.get(consumerId));
    }
}
