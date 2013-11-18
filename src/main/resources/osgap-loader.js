var osGap = (function (w) {
    'use strict';
    var
        onLoadCallbacks = [],
        tmp = {
            __url__: "http://127.0.0.1:12701/",

            onLoad: function (callback) {
                onLoadCallbacks.push(callback);
                return tmp;
            },

            sendXDR: function (url, onsuccesscb, onerrorcb, timeout) {
                var xmlhttp,
                    safe = function (f) {
                        return function () {
                            if ((typeof f) === "function") {
                                f(xmlhttp);
                            }
                        }
                    },
                    onsuccesscb_safe = safe(onsuccesscb),
                    onerrorcb_safe = safe(onerrorcb);

                if (w.XDomainRequest) {
                    xmlhttp = new XDomainRequest();
                } else if (w.XMLHttpRequest) {
                    xmlhttp = new XMLHttpRequest();
                }

                if (xmlhttp) {
                    xmlhttp.onload = function () {
                        var status = xmlhttp.status;
                        if (status === undefined || (status >= 200 && status < 300)) {
                            onsuccesscb_safe();
                        } else {
                            onerrorcb_safe();
                        }
                    };
                    xmlhttp.onerror = onerrorcb_safe;
                    xmlhttp.open("GET", url, true);
                    xmlhttp.timeout = timeout || 0;
                    xmlhttp.send();
                }
            }
        };

    tmp.sendXDR(tmp.__url__ + "osgap.js", function (response) {
        eval(response.responseText || "");
    }, null, 500);

    return tmp;
}(window));