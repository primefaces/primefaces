import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Iterator;
import java.util.Map;

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("idle","active"));

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    private boolean isSelfRequest(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }