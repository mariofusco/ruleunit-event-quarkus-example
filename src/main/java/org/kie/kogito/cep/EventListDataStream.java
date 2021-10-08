package org.kie.kogito.cep;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.kogito.factory.KogitoEventFactHandle;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionPseudoClock;
import org.kie.kogito.rules.DataProcessor;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.units.ListDataStream;

public class EventListDataStream<T> implements DataStream<T> {

    private final ArrayList<T> values = new ArrayList<>();
    private final List<DataProcessor> subscribers = new ArrayList<>();

    @SafeVarargs
    public static <T> EventListDataStream<T> create(T... ts) {
        EventListDataStream<T> stream = new EventListDataStream<>();
        for (T t : ts) {
            stream.append(t);
        }
        return stream;
    }

    @Override
    public void append(T t) {
        values.add(t);
        for (DataProcessor subscriber : subscribers) {
            KogitoEventFactHandle fh = (KogitoEventFactHandle) subscriber.insert(null, t);
            long timestamp = fh.getStartTimestamp();
            WorkingMemoryEntryPoint ep = fh.getEntryPoint(null);
            SessionPseudoClock clock = (SessionPseudoClock) ep.getInternalWorkingMemory().getSessionClock();
            long advanceTime = timestamp - clock.getCurrentTime();
            if (advanceTime > 0) {
                clock.advanceTime(advanceTime, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void subscribe(DataProcessor subscriber) {
        subscribers.add(subscriber);
        values.forEach(subscriber::insert);
    }

}
