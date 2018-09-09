/*
	Copyright 2017 Marceau Dewilde <m@ceau.be>

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package org.primefaces.model.chartjs.enums;

import java.util.Locale;

/**
 * <p>
 * List of standard DOM events.
 * </p>
 * <p>
 * Adapted from
 * <a href="https://developer.mozilla.org/en-US/docs/Web/Events">MDN</a>.
 * </p>
 */
public enum Event {

	abort,
	afterprint,
	animationend,
	animationiteration,
	animationstart,
	audioprocess,
	audioend,
	audiostart,
	beforeprint,
	beforeunload,
	beginEvent,
	blocked,
	blur,
	boundary,
	cached,
	canplay,
	canplaythrough,
	change,
	chargingchange,
	chargingtimechange,
	checking,
	click,
	close,
	complete,
	compositionend,
	compositionstart,
	compositionupdate,
	contextmenu,
	copy,
	cut,
	dblclick,
	devicechange,
	devicelight,
	devicemotion,
	deviceorientation,
	deviceproximity,
	dischargingtimechange,
	DOMActivate,
	DOMAttributeNameChanged,
	DOMAttrModified,
	DOMCharacterDataModified,
	DOMContentLoaded,
	DOMElementNameChanged,
	DOMNodeInserted,
	DOMNodeInsertedIntoDocument,
	DOMNodeRemoved,
	DOMNodeRemovedFromDocument,
	DOMSubtreeModified,
	downloading,
	drag,
	dragend,
	dragenter,
	dragleave,
	dragover,
	dragstart,
	drop,
	durationchange,
	emptied,
	end,
	ended,
	endEvent,
	error,
	focus,
	fullscreenchange,
	fullscreenerror,
	gamepadconnected,
	gamepaddisconnected,
	gotpointercapture,
	hashchange,
	lostpointercapture,
	input,
	invalid,
	keydown,
	keypress,
	keyup,
	languagechange,
	levelchange,
	load,
	loadeddata,
	loadedmetadata,
	loadend,
	loadstart,
	mark,
	message,
	mousedown,
	mouseenter,
	mouseleave,
	mousemove,
	mouseout,
	mouseover,
	mouseup,
	nomatch,
	notificationclick,
	noupdate,
	obsolete,
	offline,
	online,
	open,
	orientationchange,
	pagehide,
	pageshow,
	paste,
	pause,
	pointercancel,
	pointerdown,
	pointerenter,
	pointerleave,
	pointerlockchange,
	pointerlockerror,
	pointermove,
	pointerout,
	pointerover,
	pointerup,
	play,
	playing,
	popstate,
	progress,
	push,
	pushsubscriptionchange,
	ratechange,
	readystatechange,
	repeatEvent,
	reset,
	resize,
	resourcetimingbufferfull,
	result,
	resume,
	scroll,
	seeked,
	seeking,
	select,
	selectstart,
	selectionchange,
	show,
	soundend,
	soundstart,
	speechend,
	speechstart,
	stalled,
	start,
	storage,
	submit,
	success,
	suspend,
	SVGAbort,
	SVGError,
	SVGLoad,
	SVGResize,
	SVGScroll,
	SVGUnload,
	SVGZoom,
	timeout,
	timeupdate,
	touchcancel,
	touchend,
	touchmove,
	touchstart,
	transitionend,
	unload,
	updateready,
	upgradeneeded,
	userproximity,
	voiceschanged,
	versionchange,
	visibilitychange,
	volumechange,
	vrdisplayconnected,
	vrdisplaydisconnected,
	vrdisplaypresentchange,
	waiting,
	wheel;

	private final String serialized;

	private Event() {
		serialized = name().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String toString() {
		return serialized;
	}

}
