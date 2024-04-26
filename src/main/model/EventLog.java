package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

// Represents a log of alarm system events.
// We use the Singleton Design Pattern to ensure that there is only
// one EventLog in the system and that the system has global access
// to the single instance of the EventLog.

public class EventLog implements Iterable<Event> {

    // the only EventLog in the system
    private static EventLog theLog;
    private Collection<Event> events;

    // Prevent external construction
    private EventLog() {
        events = new ArrayList<>();
    }

    //EFFECTS: Gets instance of EventLog - creates it
    //         if it doesn't already exist.
    //         returns  instance of EventLog
    public static EventLog getInstance() {
        if (theLog == null) {
            theLog = new EventLog();
        }
        return theLog;
    }

    //EFFECTS: adds event e to the log
    public void logEvent(Event e) {
        events.add(e);
    }

    //EFFECTS: clears the events and logs the event
    public void clear() {
        events.clear();
        logEvent(new Event("Event log cleared."));
    }

    //EFFECTS: returns an iterator over events
    public Iterator<Event> iterator() {
        return events.iterator();
    }
}
