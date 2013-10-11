var osGap=(function (w) {
    'use strict';
    
    function callOsGap(service, path, callback) {
        var url = osGap.url + service + "/" + path;
        return osGap.sendXDR(url, callback || dummy);
    }

    return {

        openFile: function (filePath) {
            return callOsGap("file/open", filePath);
        },
        
        editFile: function (filePath) {
            return callOsGap("file/edit", filePath);
        },

        openIE: function (url) {
            return callOsGap("ie", url);
        },
        
        closeServer: function() {
            return callOsGap("close");        
        }

    };

}(window));

