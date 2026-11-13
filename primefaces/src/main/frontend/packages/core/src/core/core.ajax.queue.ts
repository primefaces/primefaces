import { core } from "./core.js";
import { ajax } from "./core.ajax.js";

/**
 * This class contains functionality related to queuing AJAX requests to ensure that they are (a) sent in the
 * proper order and (b) that each response is processed in the same order as the requests were sent.
 */
export class AjaxQueue {
    /**
     * A map between the source ID and  the timeout IDs (as returned by `setTimeout`). Used for AJAX requests
     * with a specified delay (such as remote commands that have a delay set).
     */
    private delays: Record<string, { timeout: number | undefined }> = {};

    /**
     * A list of requests that are waiting to be sent.
     */
    private requests: PrimeFaces.ajax.Configuration[] = [];

    /**
     * A list of sent AJAX requests, i.e. HTTP requests that were already started. This is used, for example, to
     * abort requests that were sent already when that becomes necessary.
     */
    private xhrs: PrimeFaces.ajax.pfXHR[] = [];

    /**
     * Offers an AJAX request to this queue. The request is sent once all other requests in this queue have
     * been sent. If a delay is set on the request configuration, the request is not sent before the specified
     * delay has elapsed.
     * @param request The request to send.
     */
    offer(request: PrimeFaces.ajax.Configuration): void {
        if (request.delay) {
            const sourceId = typeof request.source === "string"
                ? request.source
                : request.source !== undefined ? $(request.source).attr("id") ?? "" : "";
            const createTimeout = () => {
                return core.queueTask(() => {
                    this.requests.push(request);

                    if (this.requests.length === 1) {
                        ajax.Request.send(request);
                    }
                }, request.delay);
            };

            if (this.delays[sourceId]) {
                clearTimeout(this.delays[sourceId].timeout);
                this.delays[sourceId].timeout = createTimeout();
            }
            else {
                this.delays[sourceId] = {
                    timeout: createTimeout()
                };
            }
        }
        else {
            this.requests.push(request);

            if (this.requests.length === 1) {
                ajax.Request.send(request);
            }
        }
    }

    /**
     * Removes the topmost request (the requests that was just sent) from this queue; and starts the second
     * topmost request.
     * @return The topmost request in this queue, or `null` if this queue is empty.
     */
    poll(): PrimeFaces.ajax.Configuration | null {
        if (this.isEmpty()) {
            return null;
        }

        const processed = this.requests.shift();
        const next = this.peek();

        if (next) {
            ajax.Request.send(next);
        }

        return processed ?? null;
    }

    /**
     * Returns the request that is scheduled to be sent next, but does not modify the queue in any way.
     * @return The topmost request in this queue that is to be sent next,
     * or `null` when this queue is empty.
     */
    peek(): PrimeFaces.ajax.Configuration | null {
        if (this.isEmpty()) {
            return null;
        }

        return this.requests[0] ?? null;
    }

    /**
     * Checks whether this queue contains any scheduled AJAX requests.
     * @return `true` if this queue contains no scheduled requests, `false` otherwise.
     */
    isEmpty(): boolean {
        return this.requests.length === 0;
    }

    /**
     * Adds a newly sent XHR request to the list of sent requests ({@link AjaxQueue.xhrs | PrimeFaces.ajax.Queue.xhrs}).
     * @param xhr XHR request to add.
     */
    addXHR(xhr: PrimeFaces.ajax.pfXHR): void {
        this.xhrs.push(xhr);
    }

    /**
     * Removes an XHR request from the list of sent requests ({@link AjaxQueue.xhrs | PrimeFaces.ajax.Queue.xhrs}). Usually
     * called once the AJAX request is done, having resulted in either a success or an error.
     * @param xhr XHR request to remove.
     */
    removeXHR(xhr: PrimeFaces.ajax.pfXHR): void {
        var index = $.inArray(xhr, this.xhrs);
        if (index > -1) {
            this.xhrs.splice(index, 1);
        }
    }

    /**
     * Aborts all requests that were already sent, but have not yet received an answer from the server. Also
     * removes all requests that are waiting in the queue and have not been sent yet.
     */
    abortAll(): void {
        // clear out any pending requests
        this.requests = [];

        // abort any in-flight that are not DONE(4)
        for (const xhr of this.xhrs) {
            if (xhr.readyState !== 4) {
                xhr.abort();
            }
        }

        this.xhrs = [];
    }
}
