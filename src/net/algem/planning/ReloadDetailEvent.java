package net.algem.planning;

import net.algem.util.event.GemEvent;

public class ReloadDetailEvent extends GemEvent {
    public ReloadDetailEvent(Object src) {
        super(src, MODIFICATION, DATE);
    }
}
