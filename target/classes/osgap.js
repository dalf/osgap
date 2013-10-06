var osGap = (function (w) {
    'use strict';

    var keyStorage = "osgap.key",
        localhost = "http://127.0.0.1:12701/",
        localStorage = w.localStorage || {
            put: function (k,v) {  },
            get: function (k) {  }
        };

    function getKey() {
        return localStorage.get(keyStorage);
    }
    
    function getXDomainRequest() {
        var xdr = null;
         
        if (window.XDomainRequest) {
                xdr = new XDomainRequest(); 
        } else if (window.XMLHttpRequest) {
                xdr = new XMLHttpRequest(); 
        }
        return xdr;        
    }

    function callOsGap(path) {
        var
            url = localhost + getKey() + path,
            xmlhttp = getXDomainRequest();
        if (xmlhttp !== null) {
            xmlhttp.open("GET", url, true);
            xmlhttp.send();
        }
    }

    return {
        exists : function () {
            var xmlhttp = new XMLHttpRequest();
            xmlhttp.timeout = 50;
            xmlhttp.open("GET", "/", true);
        },
    
        init : function () {
            var
                keyHash = "#osgap.key=",
                hash = w.location.hash,
                key = "";
            if (hash.startsWith(keyHash)) {
                key = hash.substr(keyHash.length());
                localStorage.put(keyStorage, key);
            }
        },

        openFile: function (filePath) {
            callOsGap("file/open/" + filePath);
        },
        
        editFile: function (filePath) {
            callOsGap("file/edit/" + filePath);
        },

        openIE: function (url) {
            callOsGap("ie/" + url);
        },
        
        closeServer: function() {
            callOsGap("close");        
        }
    };

}(window));

osGap.init();
