var osGap = (function (w) {
   var
         url = "http://127.0.0.1:12701/osgap-min.js",
         xmlhttp = null,
         dummy = function () {};
   if (w.XDomainRequest) {
         xmlhttp = new XDomainRequest();
   } else if (w.XMLHttpRequest) {
         xmlhttp = new XMLHttpRequest();
   }
   if (xmlhttp !== null) {
         xmlhttp.timeout = 150;
         xmlhttp.onreadystatechange = function () {
               if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                 eval(xmlhttp.responseText.replace("var osGap=", "var tmp="));
                   for (var attr in tmp) {
                       if (tmp.hasOwnProperty(attr)) osGap[attr] = tmp[attr];
                   }
               }
         }
         xmlhttp.open("GET", url, true);
         xmlhttp.send();
   }
   return {
           openFile: dummy,
           editFile: dummy,
           openIE: dummy,
           closeServer: dummy
   }
}(window));
