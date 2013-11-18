(function (w, osGap, onLoadCallbacks) {
    'use strict';
    var
        attr;

    function parseResponse(xmlhttp, callback) {
        var response;

        if ((typeof callback) !== "function") {
            return;
        }

        if (xmlhttp.response) {
            callback(xmlhttp.response);
        } else {
            if (w.JSON) {
                response = w.JSON.parse(xmlhttp.responseText);
            } else {
                // ugly hack
                response = eval(xmlhttp.responseText);
            }
            callback(response)
        }
    }

    function callOsGap(service, parameters, onsuccess, onerror) {
        var url = [],
            k;
        parameters.t = (new Date()).getTime();
        for (k in parameters) {
            if (parameters.hasOwnProperty(k)) {
                url.push(encodeURIComponent(k) + "=" + (parameters[k] === undefined || parameters[k] === null ? '' : encodeURIComponent(parameters[k])));
            }
        }
        return osGap.sendXDR(osGap.__url__ + service + "?" + url.join('&'), function (xmlhttp) {
            parseResponse(xmlhttp, onsuccess)
        }, onerror);
    }

    var tmp = {
        loaded: true,

        file: {
            open: function (filePath, onsuccess, onerror) {
                return callOsGap("file/open", {
                    path: filePath
                }, onsuccess, onerror);
            },

            edit: function (filePath, onsuccess, onerror) {
                return callOsGap("file/edit", {
                    path: filePath
                }, onsuccess, onerror);
            }
        },

        url: {
            openIE: function (url, onsuccess, onerror) {
                return callOsGap("url/ie", {
                    url: url
                }, onsuccess, onerror);
            }
        },

        clipboard: {
            get: function (onsuccess, onerror) {
                return callOsGap("clipboard/get", {}, onsuccess, onerror);
            },

            setText: function (text, onsuccess, onerror) {
                return callOsGap("clipboard/set", {
                    text: text
                }, onsuccess, onerror);
            }
        },


        server: {
            close: function (onsuccess, onerror) {
                return callOsGap("server/close", {}, onsuccess, onerror);
            }
        }

    };

    // copy new function to osGap
    for (attr in tmp) {
        if (tmp.hasOwnProperty(attr)) {
            osGap[attr] = tmp[attr];
        }
    }

    // call the callbacks
    function iterate(callbacks, length, i) {
        if (i >= length) {
            return;
        }
        try {
            callbacks[i]();
        } catch (e) {
            throw e;
        } finally {
            iterate(callbacks, length, i + 1);
        }
    }

    var c = onLoadCallbacks.slice(0);
    iterate(c, c.length, 0);

}(window, osGap, onLoadCallbacks));