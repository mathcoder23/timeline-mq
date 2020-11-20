package org.pettyfox.timeline.core;


import org.pettyfox.timeline.model.Timeline;

/**
 * @author Petty Fox
 * @version 1.0
 */
public interface TimelineStore {
    void store(Timeline<?> timeline);
}
