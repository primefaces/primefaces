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
import static java.util.stream.Collectors.toList;

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
        TimelineModel<Object, Object> model = timeline.getValue();
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
        wb.init("Timeline", timeline.resolveWidgetVar(), clientId);

        List<TimelineEvent<Object>> events = model.getEvents();
        List<TimelineGroup<Object>> groups = calculateGroupsFromModel(model);
        Map<String, String> groupsContent = null;
        if (!groups.isEmpty()) {
            // buffer for groups' content
            groupsContent = new HashMap<>();
        }

        UIComponent groupFacet = timeline.getFacet("group");
        int groupsSize = groups.size();
        if (groupsSize > 0) {
            writer.write(",groups:[");
            for (int i = 0; i < groupsSize; i++) {
                //If groups was not set in model then order by content.
                Integer order = model.getGroups() != null ? i : null;
                //encode groups
                writer.write(encodeGroup(context, fsw, fswHtml, timeline, groupFacet, groupsContent, groups.get(i), order));
                if (i + 1 < groupsSize) {
                    writer.write(",");
                }
            }
            writer.write("]");
        }

        writer.write(",data:[");
        int size = events != null ? events.size() : 0;
        for (int i = 0; i < size; i++) {
            // encode events
            writer.write(encodeEvent(context, fsw, fswHtml, timeline, browserTZ, targetTZ, groups, events.get(i), i));
            if (i + 1 < size) {
                writer.write(",");
            }
        }

        writer.write("]");

        if (timeline.isShowCurrentTime()) {
            wb.nativeAttr("currentTime", encodeDate(browserTZ, targetTZ, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime()));
        }

        if (timeline.getPreloadFactor() < 0) {
            wb.attr("preloadFactor", 0);
        }
        else {
            wb.attr("preloadFactor", timeline.getPreloadFactor());
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

        if (timeline.getExtender() != null) {
            wb.nativeAttr("extender", timeline.getExtender());
        }

        writer.write(",opts:{");

        // encode options
        writer.write("autoResize: " + timeline.isResponsive());
        if (timeline.getHeight() != null) {
            wb.attr("height", timeline.getHeight());
        }
        if (timeline.getMinHeight() != null) {
            wb.attr("minHeight", timeline.getMinHeight());
        }
        if (timeline.getMaxHeight() != null) {
            wb.attr("maxHeight", timeline.getMaxHeight());
        }
        wb.attr("width", timeline.getWidth());
        wb.nativeAttr("orientation", "{axis: '" + timeline.getOrientationAxis() + "', "
                + "item: '" + timeline.getOrientationItem() + "' }" );
        wb.nativeAttr("editable", "{add: " + timeline.isEditableAdd() + ", "
                + "remove: " + timeline.isEditableRemove() + ", "
                + "updateTime: " + timeline.isEditableTime() + ", "
                + "updateGroup: " + timeline.isEditableGroup() + ","
                + "overrideItems: " + timeline.isEditableOverrideItems() + " }" );
        wb.attr("selectable", timeline.isSelectable());
        wb.attr("zoomable", timeline.isZoomable());
        wb.attr("moveable", timeline.isMoveable());

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

        wb.nativeAttr("margin", "{axis: " + timeline.getEventMarginAxis() + ", "
                + "item: {horizontal: " + timeline.getEventHorizontalMargin() + ", "
                + "vertical: " + timeline.getEventVerticalMargin() + "}}");

        if (timeline.getEventStyle() != null) {
            wb.attr("type", timeline.getEventStyle());
        }

        if (timeline.isGroupsOrder()) {
            //If groups was setted to model then order by order property, else order by content alphabetically
            List<TimelineGroup<Object>> modelGroups = model.getGroups();
            if (modelGroups != null && !modelGroups.isEmpty()) {
                wb.attr("groupOrder", "order");
            }
            else {
                wb.attr("groupOrder", "content");
            }
        }

        if (timeline.getSnap() != null) {
            wb.nativeAttr("snap", timeline.getSnap());
        }
        wb.attr("stack", timeline.isStackEvents());

        wb.attr("showCurrentTime", timeline.isShowCurrentTime());

        wb.attr("showMajorLabels", timeline.isShowMajorLabels());
        wb.attr("showMinorLabels", timeline.isShowMinorLabels());

        wb.attr("locale", timeline.calculateLocale(context).toString());
        wb.attr("clickToUse", timeline.isClickToUse());

        writer.write("}");
        encodeClientBehaviors(context, timeline);

        wb.finish();
    }

    protected String encodeGroup(FacesContext context, FastStringWriter fsw, FastStringWriter fswHtml, Timeline timeline, UIComponent groupFacet,
            Map<String, String> groupsContent, TimelineGroup<?> group, Integer order) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        fsw.write("{id: \"" + EscapeUtils.forJavaScriptBlock(group.getId()) + "\"");

        Object data = group.getData();
        if (!LangUtils.isValueBlank(timeline.getVarGroup()) && data != null) {
            context.getExternalContext().getRequestMap().put(timeline.getVarGroup(), data);
        }
        if (groupFacet != null) {
            ResponseWriter clonedWriter = writer.cloneWithWriter(fswHtml);
            context.setResponseWriter(clonedWriter);

            groupFacet.encodeAll(context);

            // restore writer
            context.setResponseWriter(writer);
            // extract the content of the group, first buffer and then render it
            groupsContent.put(group.getId(), EscapeUtils.forJavaScript(fswHtml.toString()));
            fsw.write(", content:\"" + groupsContent.get(group.getId()) + "\"");
            fswHtml.reset();
        }
        else if (data != null) {
            groupsContent.put(group.getId(), EscapeUtils.forJavaScriptBlock(data.toString()));
            fsw.write(", content:\"" + groupsContent.get(group.getId()) + "\"");
        }

        if (timeline.getGroupStyle() != null) {
            fsw.write(", style: \"" + timeline.getGroupStyle() + "\"");
        }

        if (group.getStyleClass() != null) {
            fsw.write(", className: \"" + group.getStyleClass() + "\"");
        }

        if (order != null) {
            fsw.write(", order: " + order);
        }
        fsw.write("}");

        String groupJson = fsw.toString();
        fsw.reset();

        return groupJson;
    }

    protected String encodeEvent(FacesContext context, FastStringWriter fsw, FastStringWriter fswHtml, Timeline timeline,
                              TimeZone browserTZ, TimeZone targetTZ, List<TimelineGroup<Object>> groups,
                              TimelineEvent<?> event, int index) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        fsw.write("{id: " + index);

        if (event.getStartDate() != null) {
            fsw.write(", start: " + encodeDate(browserTZ, targetTZ, event.getStartDate()));
        }
        else {
            fsw.write(", start: null");
        }

        if (event.getEndDate() != null) {
            fsw.write(", end: " + encodeDate(browserTZ, targetTZ, event.getEndDate()));
        }
        else {
            fsw.write(", end: null");
        }

        if (event.isEditableTime() != null || event.isEditableGroup() != null || event.isEditableRemove() != null) {
            fsw.write(", editable: {");
            boolean isSet = false;
            if (event.isEditableTime() != null) {
                fsw.write("updateTime: " + event.isEditableTime());
                isSet = true;
            }
            if (event.isEditableGroup() != null) {
                if (isSet) {
                    fsw.write(",");
                }
                fsw.write("updateGroup: " + event.isEditableGroup());
                isSet = true;
            }
            if (event.isEditableRemove() != null) {
                if (isSet) {
                    fsw.write(",");
                }
                fsw.write("remove: " + event.isEditableRemove());
                isSet = true;
            }
            fsw.write("}");
        }

        // there is a list of groups ==> find the group to the event by the group id
        TimelineGroup<?> foundGroup = null;
        if (event.getGroup() != null) {
            for (TimelineGroup<?> group : groups) {
                if (Objects.equals(group.getId(), event.getGroup())) {
                    foundGroup = group;
                    break;
                }
            }
        }

        if (foundGroup != null) {
            fsw.write(", group: \"" + EscapeUtils.forJavaScriptBlock(foundGroup.getId()) + "\"");
        }
        else {
            // no group for the event
            fsw.write(", group: null");
        }

        if (!LangUtils.isValueBlank(event.getStyleClass())) {
            fsw.write(", className: \"" + event.getStyleClass() + "\"");
        }
        else {
            fsw.write(", className: null");
        }

        fsw.write(", content:\"");
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

    List<TimelineGroup<Object>> calculateGroupsFromModel(TimelineModel<Object, Object> model) {
        List<TimelineGroup<Object>> groups = model.getGroups();
        if (groups != null) {
            return groups;
        }

        return model.getEvents()
                .stream()
                .map(TimelineEvent::getGroup)
                .filter(Objects::nonNull)
                .distinct()
                .map(group -> new TimelineGroup<Object>(group, group))
                .collect(toList());
    }
}
