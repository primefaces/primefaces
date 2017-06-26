/**
 * PrimeFaces Socket Widget
 */
PrimeFaces.widget.Socket = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this.cfg = cfg;
        var $this = this;

        this.cfg.request = {
            url: this.cfg.url,
            transport: this.cfg.transport,
            fallbackTransport: this.cfg.fallbackTransport,
            enableXDR: false,
            reconnectOnWindowLocationChange: true,
            enableProtocol: true,
            trackMessageLength : true,
            onMessage: function (response) {
                $this.onMessage(response);
            }
        };

        this.cfg.request.onError = this.cfg.onError;
        this.cfg.request.onClose = this.cfg.onClose;
        this.cfg.request.onOpen = this.cfg.onOpen;
        this.cfg.request.onReconnect = this.cfg.onReconnect;
        this.cfg.request.onMessagePublished = this.cfg.onMessagePublished;
        this.cfg.request.onLocalMessage = this.cfg.onLocalMessage;
        this.cfg.request.onTransportFailure = this.cfg.onTransportFailure;
        this.cfg.request.onClientTimeout = this.cfg.onTransportFailure;


        if (this.cfg.autoConnect) {
            this.connect();
        }
    },

    connect: function (uniquePath) {
        if (uniquePath) {
            this.cfg.request.url += uniquePath;
        }

        this.connection = atmosphere.subscribe(this.cfg.request);
    },

    push: function (data) {
        this.connection.push(JSON.stringify(data));
    },

    disconnect: function () {
        // reset url and ignore appended uniquePath
        this.cfg.request.url = this.cfg.url;

        this.connection.close();
    },

    onMessage: function (response) {
        var value = $.parseJSON(response.responseBody);

        if(value.hasOwnProperty('pfpd')) {
            value = value['pfpd'];
        }

        if (this.cfg.onMessage) {
            this.cfg.onMessage.call(this, value);
        }

        if (this.cfg.behaviors && this.cfg.behaviors['message']) {
            this.cfg.behaviors['message'].call(this);
        }
    }

});
