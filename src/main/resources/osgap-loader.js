var osGap = (function (w) {
   'use strict';
   var
         urlRoot = "http://127.0.0.1:12701/",
         dummy = function () { return false },
         loading,
         xhrSuccessStatus = {
            // file protocol always yields status code 0, assume 200
            0: 200, 
            // Support: sometimes IE returns 1223 when it should be 204
            1223: 204
         };

   function sendXDR(url, callback) {
     var xmlhttp, status;
     if (w.XDomainRequest) {
         xmlhttp = new XDomainRequest();
         xmlhttp.onload = function() {
               callback(xmlhttp.responseText);
         };
         xmlhttp.open("GET", url);
         xmlhttp.send();
         return true;
     } else if (w.XMLHttpRequest) {
         xmlhttp = new XMLHttpRequest();
         xmlhttp.timeout = 1000;
         xmlhttp.onreadystatechange = function () {
               if (xmlhttp.readyState == 4) {
                 status = xhrSuccessStatus[ xmlhttp.status ] || xmlhttp.status;
                 if (status >= 200 && status < 300 || status === 304) {
                   callback(xmlhttp.responseText);
                 }
               }
         };
         xmlhttp.open("GET", url, true);
         xmlhttp.send();
         return true;
     }
     return false;
   }

   loading = sendXDR(urlRoot + "osgap-min.js", function (response) {
         var tmpOsGap = {};
         eval(response.replace("var osGap=", "tmpOsGap="));
         for (var attr in tmpOsGap) {
             if (tmpOsGap.hasOwnProperty(attr)) osGap[attr] = tmpOsGap[attr];
         }
         osGap.loaded = true;
         osGap.loading = false;
   });

   return {
	        url: urlRoot,
            loaded : false,
            loading: loading,
            sendXDR: sendXDR,
            openFile: dummy,
            editFile: dummy,
            openIE: dummy,
            closeServer: dummy
         };
}(window));