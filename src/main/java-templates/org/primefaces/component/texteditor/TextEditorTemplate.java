import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.event.AjaxBehaviorEvent;

    private static final Collection<String> EVENT_NAMES=Collections.unmodifiableCollection(Arrays.asList("blur","change","click","dblclick","focus","keydown","keypress","keyup","mousedown","mousemove","mouseout","mouseover","mouseup","select"));

    @Override
    public Collection<String> getEventNames(){
        return EVENT_NAMES;
    }

    public String getDefaultEventName() {
        return "change";
    }