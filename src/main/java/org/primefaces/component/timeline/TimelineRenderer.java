/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.timeline;

import java.io.IOException;
import java.util.*;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.model.timeline.TimelineModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.*;

public class TimelineRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Timeline timeline = (Timeline) component;
        encodeMarkup(context, timeline);
        encodeScript(context, timeline);
    }

    protected void encodeMarkup(FacesContext context, Timeline timeline) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = timeline.getClientId(context);
        writer.startElement("div", timeline);

        writer.writeAttribute("id", clientId, "id");
        if (timeline.getStyle() != null) {
            writer.writeAttribute("style", timeline.getStyle(), "style");
        }

        if (timeline.getStyleClass() != null) {
            writer.writeAttribute("class", timeline.getStyleClass(), "styleClass");
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Timeline timeline) throws IOException {
        TimelineModel model = timeline.getValue();
        if (model == null) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        String clientId = timeline.getClientId(context);

        TimeZone targetTZ = ComponentUtils.resolveTimeZone(timeline.getTimeZone());
        TimeZone browserTZ = ComponentUtils.resolveTimeZone(timeline.getBrowserTimeZone());

        FastStringWriter fsw = new FastStringWriter();
        FastStringWriter fswHtml = new FastStringWriter();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Timeline", timeline.resolveWidgetVar(context), clientId);

        writer.write(",data:[");

        Map<String, String> groupsContent = null;
        List<TimelineGroup> groups = model.getGroups();
        UIComponent groupFacet = timeline.getFacet("group");
        if (groups != null && groupFacet != null) {
            // buffer for groups' content
            groupsContent = new HashMap<>();
        }

        List<TimelineEvent> events = model.getEvents();
        int size = events != null ? events.size() : 0;
        for (int i = 0; i < size; i++) {
            // encode events
            writer.write(encodeEvent(context, fsw, fswHtml, timeline, browserTZ, targetTZ, groups, groupFacet, groupsContent,
                    events.get(i)));
            if (i + 1 < size) {
                writer.write(",");
            }
        }

        writer.write("],opts:{");

        // encode options
        writer.write("height:'" + timeline.getHeight() + "'");
        wb.attr("minHeight", timeline.getMinHeight());
        wb.attr("width", timeline.getWidth());
        wb.attr("responsive", timeline.isResponsive());
        wb.attr("axisOnTop", timeline.isAxisOnTop());
        wb.attr("dragAreaWidth", timeline.getDragAreaWidth());
        wb.attr("editable", timeline.isEditable());
        wb.attr("selectable", timeline.isSelectable());
        wb.attr("unselectable", timeline.isUnselectable());
        wb.attr("zoomable", timeline.isZoomable());
        wb.attr("moveable", timeline.isMoveable());
        wb.attr("timeChangeable", timeline.isTimeChangeable());

        if (timeline.getStart() != null) {
            wb.nativeAttr("start", encodeDate(browserTZ, targetTZ, timeline.getStart()));
        }

        if (timeline.getEnd() != null) {
            wb.nativeAttr("end", encodeDate(browserTZ, targetTZ, timeline.getEnd()));
        }

        if (timeline.getMin() != null) {
            wb.nativeAttr("min", encodeDate(browserTZ, targetTZ, timeline.getMin()));
        }

        if (timeline.getMax() != null) {
            wb.nativeAttr("max", encodeDate(browserTZ, targetTZ, timeline.getMax()));
        }

        wb.attr("zoomMin", timeline.getZoomMin());
        wb.attr("zoomMax", timeline.getZoomMax());

        if (timeline.getPreloadFactor() < 0) {
            wb.attr("preloadFactor", 0);
        }
        else {
            wb.attr("preloadFactor", timeline.getPreloadFactor());
        }

        wb.attr("eventMargin", timeline.getEventMargin());
        wb.attr("eventMarginAxis", timeline.getEventMarginAxis());
        wb.attr("style", timeline.getEventStyle());
        wb.attr("groupsChangeable", timeline.isGroupsChangeable());
        wb.attr("groupsOnRight", timeline.isGroupsOnRight());
        wb.attr("groupsOrder", timeline.isGroupsOrder());
        wb.attr("groupMinHeight", timeline.getGroupMinHeight());

        if (timeline.getGroupsWidth() != null) {
            wb.attr("groupsWidth", timeline.getGroupsWidth());
        }

        wb.attr("snapEvents", timeline.isSnapEvents());
        wb.attr("stackEvents", timeline.isStackEvents());

        wb.attr("showCurrentTime", timeline.isShowCurrentTime());
        if (timeline.isShowCurrentTime()) {
            wb.nativeAttr("currentTime", encodeDate(browserTZ, targetTZ, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime()));
        }

        wb.attr("showMajorLabels", timeline.isShowMajorLabels());
        wb.attr("showMinorLabels", timeline.isShowMinorLabels());
        wb.attr("showButtonNew", timeline.isShowButtonNew());
        wb.attr("showNavigation", timeline.isShowNavigation());

        if (timeline.getLocale() != null) {
            wb.attr("locale", timeline.getLocale().toString());
        }

        if (timeline.getDropHoverStyleClass() != null) {
            wb.attr("hoverClass", timeline.getDropHoverStyleClass());
        }

        if (timeline.getDropActiveStyleClass() != null) {
            wb.attr("activeClass", timeline.getDropActiveStyleClass());
        }

        if (timeline.getDropAccept() != null) {
            wb.attr("accept", timeline.getDropAccept());
        }

        if (timeline.getDropScope() != null) {
            wb.attr("scope", timeline.getDropScope());
        }

        wb.attr("animate", timeline.isAnimate());
        wb.attr("animateZoom", timeline.isAnimateZoom());

        writer.write("}");
        encodeClientBehaviors(context, timeline);

        wb.finish();
    }

    public String encodeEvent(FacesContext context, FastStringWriter fsw, FastStringWriter fswHtml, Timeline timeline,
                              TimeZone browserTZ, TimeZone targetTZ, List<TimelineGroup> groups, UIComponent groupFacet,
                              Map<String, String> groupsContent, TimelineEvent event) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (event.getStartDate() != null) {
            fsw.write("{\"start\":" + encodeDate(browserTZ, targetTZ, event.getStartDate()));
        }
        else {
            fsw.write("{\"start\":null");
        }

        if (event.getEndDate() != null) {
            fsw.write(",\"end\":" + encodeDate(browserTZ, targetTZ, event.getEndDate()));
        }
        else {
            fsw.write(",\"end\":null");
        }

        if (event.isEditable() != null) {
            fsw.write(",\"editable\":" + event.isEditable());
        }
        else {
            fsw.write(",\"editable\":null");
        }

        if (groups != null) {
            // there is a list of groups ==> find the group to the event by the group id
            TimelineGroup foundGroup = null;
            int groupOrder = 0;
            for (TimelineGroup group : groups) {
                if (group.getId() != null && group.getId().equals(event.getGroup())) {
                    foundGroup = group;
                    break;
                }

                groupOrder++;
            }

            if (foundGroup != null) {
                String prefix;
                if (timeline.isGroupsOrder()) {
                    // generate hidden HTML for ordering. See:
                    // https://groups.google.com/forum/?fromgroups=#!topic/chap-links-library/Bk2fb99LUh4
                    // http://stackoverflow.com/questions/2236385/how-to-convert-java-longs-as-strings-while-keeping-natural-order
                    // we must also pass the order of the group as workaround (extracted in queueEvent(), Timeline.java).
                    prefix
                            = "<span style='display:none;'>" + String.format("%016x", groupOrder - Long.MIN_VALUE) + "#"
                            + groupOrder + "</span>";
                }
                else {
                    prefix = "<span style='display:none;'>#" + groupOrder + "</span>";
                }

                if (groupFacet != null) {
                    String groupContent = groupsContent.get(foundGroup.getId());
                    if (groupContent != null) {
                        // content of this group was already rendered ==> reuse it
                        fsw.write(",\"group\":\"" + groupContent + "\"");
                    }
                    else {
                        Object data = foundGroup.getData();
                        if (!LangUtils.isValueBlank(timeline.getVarGroup()) && data != null) {
                            context.getExternalContext().getRequestMap().put(timeline.getVarGroup(), data);
                        }

                        ResponseWriter clonedWriter = writer.cloneWithWriter(fswHtml);
                        context.setResponseWriter(clonedWriter);

                        groupFacet.encodeAll(context);

                        // restore writer
                        context.setResponseWriter(writer);
                        // extract the content of the group, first buffer and then render it
                        groupsContent.put(foundGroup.getId(), prefix + EscapeUtils.forJavaScript(fswHtml.toString()));
                        fsw.write(",\"group\":\"" + groupsContent.get(foundGroup.getId()) + "\"");
                        fswHtml.reset();
                    }
                }
                else if (foundGroup.getData() != null) {
                    fsw.write(",\"group\":\"" + prefix + foundGroup.getData().toString() + "\"");
                }
            }
            else {
                // no group for the event
                fsw.write(",\"group\":null");
            }
        }
        else {
            // group's content is coded in the event self
            if (event.getGroup() != null) {
                fsw.write(",\"group\":\"" + event.getGroup() + "\"");
            }
            else {
                // no group for the event
                fsw.write(",\"group\":null");
            }
        }

        if (!LangUtils.isValueBlank(event.getStyleClass())) {
            fsw.write(",\"className\":\"" + event.getStyleClass() + "\"");
        }
        else {
            fsw.write(",\"className\":null");
        }

        fsw.write(",\"content\":\"");
        if (timeline.getChildCount() > 0) {
            Object data = event.getData();
            if (!LangUtils.isValueBlank(timeline.getVar()) && data != null) {
                context.getExternalContext().getRequestMap().put(timeline.getVar(), data);
            }

            ResponseWriter clonedWriter = writer.cloneWithWriter(fswHtml);
            context.setResponseWriter(clonedWriter);

            renderChildren(context, timeline);

            // restore writer
            context.setResponseWriter(writer);
            // extract the content of the event
            fsw.write(EscapeUtils.forJavaScript(fswHtml.toString()));
            fswHtml.reset();
        }
        else if (event.getData() != null) {
            fsw.write(event.getData().toString());
        }

        fsw.write("\"");
        fsw.write("}");

        String eventJson = fsw.toString();
        fsw.reset();

        return eventJson;
    }

    // convert from UTC to locale date
    private String encodeDate(TimeZone browserTZ, TimeZone targetTZ, Date utcDate) {
        return "new Date(" + DateUtils.toLocalDate(browserTZ, targetTZ, utcDate) + ")";
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
