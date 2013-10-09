var osGap = (function (w) {
    'use strict';

    var localhost = "http://127.0.0.1:12701/";
    
    function getXDomainRequest() {
        var xdr = null;
         
        if (window.XDomainRequest) {
                xdr = new XDomainRequest(); 
        } else if (window.XMLHttpRequest) {
                xdr = new XMLHttpRequest(); 
        }
        return xdr;        
    }

    function callOsGap(service, path) {
        var
            url = localhost + service + "/" + path,
            xmlhttp = getXDomainRequest();
        if (xmlhttp !== null) {
            xmlhttp.open("GET", url, true);
            xmlhttp.send();
        }
    }

    return {

        openFile: function (filePath) {
            callOsGap("file/open", filePath);
        },
        
        editFile: function (filePath) {
            callOsGap("file/edit", filePath);
        },

        openIE: function (url) {
            callOsGap("ie", url);
        },
        
        closeServer: function() {
            callOsGap("close");        
        }
    };

}(window));

