/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

        UIComponent menuFacet = timeline.getFacet("menu");
        if (ComponentUtils.shouldRenderFacet(menuFacet)) {
            writer.startElement("div", null);
            StringBuilder cssMenu = new StringBuilder("timeline-menu");

            if ("top".equals(timeline.getOrientationAxis())) {
                cssMenu.append(" timeline-menu-axis-top");
            }
            else if ("both".equals(timeline.getOrientationAxis())) {
                cssMenu.append(" timeline-menu-axis-both");
            }

            if (ComponentUtils.isRTL(context, timeline)) {
                cssMenu.append(" timeline-menu-rtl");
            }

            writer.writeAttribute("class", cssMenu.toString(), null);
            //It will be displayed when timeline is displayed
            writer.writeAttribute("style", "display:none;", null);
            menuFacet.encodeAll(context);
            writer.endElement("div");
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Timeline timeline) throws IOException {
        TimelineModel<Object, Object> model = timeline.getValue();
        if (model == null) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();

        ZoneId zoneId = CalendarUtils.calculateZoneId(timeline.getTimeZone());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zoneId);

        FastStringWriter fsw = new FastStringWriter();
        FastStringWriter fswHtml = new FastStringWriter();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Timeline", timeline);

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
        UIComponent eventTitleFacet = timeline.getFacet("eventTitle");
        int size = events != null ? events.size() : 0;
        for (int i = 0; i < size; i++) {
            // encode events
            writer.write(encodeEvent(context, fsw, fswHtml, timeline, eventTitleFacet, zoneId, groups, events.get(i)));
            if (i + 1 < size) {
                writer.write(",");
            }
        }

        writer.write("]");

        if (timeline.isShowCurrentTime()) {
            wb.nativeAttr("currentTime", encodeDate(dateTimeFormatter, LocalDateTime.now(CalendarUtils.calculateZoneId(timeline.getClientTimeZone()))));
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
        UIComponent menuFacet = timeline.getFacet("menu");
        if (ComponentUtils.shouldRenderFacet(menuFacet)) {
            wb.attr("isMenuPresent", Boolean.TRUE);
        }

        writer.write(",opts:{");

        // encode options
        writer.write("autoResize: " + timeline.isResponsive());
        if (timeline.getClientTimeZone() != null) {
            ZoneOffset zoneOffset = CalendarUtils.calculateZoneOffset(timeline.getClientTimeZone());
            if (ZoneOffset.UTC.equals(zoneOffset)) {
                wb.callback("moment", "function(date)", "return vis.moment(date).utc();");
            }
            else {
                wb.callback("moment", "function(date)", "return vis.moment(date).utcOffset('" + EscapeUtils.forJavaScript(zoneOffset.toString()) + "');");
            }
        }
        if (timeline.getHeight() != null) {
            wb.attr("height", timeline.getHeight());
        }
        if (timeline.getMinHeight() != null) {
            wb.attr("minHeight", timeline.getMinHeight());
        }
        if (timeline.getMaxHeight() != null) {
            wb.attr("maxHeight", timeline.getMaxHeight());
        }
        wb.attr("horizontalScroll", timeline.isHorizontalScroll(), false);
        wb.attr("verticalScroll", timeline.isVerticalScroll(), false);
        wb.attr("width", timeline.getWidth());
        wb.nativeAttr("orientation", "{axis:'" + timeline.getOrientationAxis() + "',"
                + "item:'" + timeline.getOrientationItem() + "'}" );
        wb.nativeAttr("editable", "{add:" + timeline.isEditableAdd() + ","
                + "remove:" + timeline.isEditableRemove() + ","
                + "updateTime:" + timeline.isEditableTime() + ","
                + "updateGroup:" + timeline.isEditableGroup() + ","
                + "overrideItems:" + timeline.isEditableOverrideItems() + "}" );
        wb.attr("selectable", timeline.isSelectable());
        wb.attr("zoomable", timeline.isZoomable());
        wb.attr("moveable", timeline.isMoveable());

        if (timeline.getStart() != null) {
            wb.nativeAttr("start", encodeDate(dateTimeFormatter, timeline.getStart()));
        }

        if (timeline.getEnd() != null) {
            wb.nativeAttr("end", encodeDate(dateTimeFormatter, timeline.getEnd()));
        }

        if (timeline.getMin() != null) {
            wb.nativeAttr("min", encodeDate(dateTimeFormatter, timeline.getMin()));
        }

        if (timeline.getMax() != null) {
            wb.nativeAttr("max", encodeDate(dateTimeFormatter, timeline.getMax()));
        }

        wb.attr("zoomMin", timeline.getZoomMin());
        wb.attr("zoomMax", timeline.getZoomMax());

        wb.nativeAttr("margin", "{axis:" + timeline.getEventMarginAxis() + ","
                + "item:{horizontal:" + timeline.getEventHorizontalMargin() + ","
                + "vertical:" + timeline.getEventVerticalMargin() + "}}");

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
        wb.attr("showTooltips", timeline.isShowTooltips());

        wb.nativeAttr("tooltip", "{followMouse:" + timeline.isTooltipFollowMouse() + ","
                + "overflowMethod:'" + timeline.getTooltipOverflowMethod() + "',"
                + "delay:" + timeline.getTooltipDelay() + "}");

        if (ComponentUtils.isRTL(context, timeline)) {
            wb.attr("rtl", Boolean.TRUE);
        }

        UIComponent loadingFacet = timeline.getFacet("loading");
        if (ComponentUtils.shouldRenderFacet(loadingFacet)) {
            String loading = encodeAllToString(context, writer, fswHtml, loadingFacet);
            // writing facet content's
            wb.nativeAttr("loadingScreenTemplate", "function() { return \"" + EscapeUtils.forJavaScript(loading) + "\";}");
        }

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
        if (ComponentUtils.shouldRenderFacet(groupFacet)) {
            String groupRender = encodeAllToString(context, writer, fswHtml, groupFacet);
            // extract the content of the group, first buffer and then render it
            groupsContent.put(group.getId(), EscapeUtils.forJavaScript(groupRender));
            fsw.write(", content:\"" + groupsContent.get(group.getId()) + "\"");
        }
        else if (data != null) {
            groupsContent.put(group.getId(), EscapeUtils.forJavaScript(data.toString()));
            fsw.write(", content:\"" + groupsContent.get(group.getId()) + "\"");
        }

        if (group.getTreeLevel() != null) {
            fsw.write(", treeLevel:\"" + group.getTreeLevel() + "\"");

            List<String> nestedGroups = group.getNestedGroups();
            if (nestedGroups != null && !nestedGroups.isEmpty()) {
                fsw.write(", nestedGroups: [");

                for (Iterator<String> iter = nestedGroups.iterator(); iter.hasNext(); ) {
                    fsw.write("\"" + EscapeUtils.forJavaScriptBlock(iter.next()) + "\"");

                    if (iter.hasNext()) {
                        fsw.write(",");
                    }
                }

                fsw.write("]");
            }
        }

        if (timeline.getGroupStyle() != null) {
            fsw.write(", style: \"" + timeline.getGroupStyle() + "\"");
        }

        if (group.getStyleClass() != null) {
            fsw.write(", className: \"" + group.getStyleClass() + "\"");
        }

        if (group.getTitle() != null) {
            fsw.write(", title: \"" + EscapeUtils.forJavaScript(group.getTitle()) + "\"");
        }

        if (order != null) {
            fsw.write(", order: " + order);
        }

        if (!LangUtils.isValueBlank(group.getSubgroupOrder())) {
            fsw.write(", subgroupOrder: \"" + EscapeUtils.forJavaScript(group.getSubgroupOrder()) + "\"");
        }

        if (!LangUtils.isValueBlank(group.getSubgroupStack())) {
            fsw.write(", subgroupStack: " + EscapeUtils.forJavaScript(group.getSubgroupStack()));
        }

        if (!LangUtils.isValueBlank(group.getSubgroupVisibility())) {
            fsw.write(", subgroupVisibility: " + EscapeUtils.forJavaScript(group.getSubgroupVisibility()));
        }

        fsw.write("}");

        String groupJson = fsw.toString();
        fsw.reset();

        return groupJson;
    }

    protected String encodeEvent(FacesContext context, FastStringWriter fsw, FastStringWriter fswHtml, Timeline timeline,
                              UIComponent eventTitleFacet, ZoneId zoneId, List<TimelineGroup<Object>> groups,
                              TimelineEvent<?> event) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zoneId);

        fsw.write("{id: \"" + EscapeUtils.forJavaScript(event.getId()) + "\"");

        if (event.getStartDate() != null) {
            fsw.write(", start: " + encodeDate(dateTimeFormatter, event.getStartDate()));
        }
        else {
            fsw.write(", start: null");
        }

        if (event.getEndDate() != null) {
            fsw.write(", end: " + encodeDate(dateTimeFormatter, event.getEndDate()));
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
            fsw.write(", group: \"" + EscapeUtils.forJavaScript(foundGroup.getId()) + "\"");

            if (!LangUtils.isValueBlank(event.getSubgroup())) {
                fsw.write(", subgroup: \"" + EscapeUtils.forJavaScript(event.getSubgroup()) + "\"");
            }
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

        Object data = event.getData();
        if (!LangUtils.isValueBlank(timeline.getVar()) && data != null) {
            context.getExternalContext().getRequestMap().put(timeline.getVar(), data);
        }

        if (event.getTitle() != null) {
            fsw.write(", title:\"");
            fsw.write(EscapeUtils.forJavaScript(event.getTitle()));
            fsw.write("\"");
        }
        else if (ComponentUtils.shouldRenderFacet(eventTitleFacet)) {
            String title = encodeAllToString(context, writer, fswHtml, eventTitleFacet);
            fsw.write(", title:\"");
            fsw.write(EscapeUtils.forJavaScript(title));
            fsw.write("\"");
        }

        fsw.write(", content:\"");
        if (timeline.getChildCount() > 0) {
            ResponseWriter clonedWriter = writer.cloneWithWriter(fswHtml);
            context.setResponseWriter(clonedWriter);

            renderChildren(context, timeline);

            // restore writer
            context.setResponseWriter(writer);
            // extract the content of the event
            fsw.write(EscapeUtils.forJavaScript(fswHtml.toString()));
            fswHtml.reset();
        }
        else if (data != null) {
            fsw.write(data.toString());
        }

        fsw.write("\"");
        fsw.write("}");

        String eventJson = fsw.toString();
        fsw.reset();

        return eventJson;
    }

    // convert from UTC to locale date
    private String encodeDate(DateTimeFormatter dateTimeFormatter, LocalDateTime date) {
        return "new Date('" + dateTimeFormatter.format(date.atZone(dateTimeFormatter.getZone())) + "')";
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    private String encodeAllToString(FacesContext context, ResponseWriter writer, FastStringWriter fswHtml, UIComponent component) throws IOException {
        ResponseWriter clonedWriter = writer.cloneWithWriter(fswHtml);
        context.setResponseWriter(clonedWriter);

        component.encodeAll(context);
        // restore writer
        context.setResponseWriter(writer);
        String encoded = fswHtml.toString();
        fswHtml.reset();
        return encoded;
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
