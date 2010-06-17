// functions public to partners
function cgLaunch (URL, name, features) {
    window.open("http://ad.doubleclick.net/clk;225291110;48835962;h?"+URL, name, features);
}

// internal tools
var citygrid = {

    // nearby widget
    nearby : {
        createwidget : function(objCSW) {
            objCSW = citygrid.common.checkInput(objCSW);

            var widgeturl = citygrid.common.getHostName(objCSW.site);
            widgeturl += '?what='+objCSW.what;
            widgeturl += '&where='+objCSW.where;
            widgeturl += '&publisher='+objCSW.publisher;
            widgeturl += '&latitude='+objCSW.latitude;
            widgeturl += '&longitude='+objCSW.longitude;
            widgeturl += '&tags='+objCSW.tags;
            widgeturl += '&radius='+objCSW.radius;
            widgeturl += '&placement=';
            widgeturl += '&apikey=test';
            widgeturl += '&callBackFunction='+objCSW.callBackFunction;
            widgeturl += '&callBackUrl='+objCSW.callBackUrl;
            widgeturl += '&adUnitName='+objCSW.adUnitName;
            widgeturl += '&adUnitSize='+objCSW.adUnitSize;

            citygrid.common.scriptInject(widgeturl);
        }
    },

    // common utils functions
    common : {
        scriptInject : function(url) {
            var script = document.createElement("script");
            script.setAttribute("src", url);
            script.setAttribute("type","text/javascript");
            document.body.appendChild(script);
        },

        loadWidget : function(widgetHTML) {
        	widgetHTML = widgetHTML.replace(/&amp;/g, "&");
            widgetHTML = widgetHTML.replace(/&lt;/g, "<");
            widgetHTML = widgetHTML.replace(/&gt;/g, ">");
            widgetHTML = widgetHTML.replace(/&quot;/g, "\"");
            document.getElementById(citygrid.objCSW.target).innerHTML = widgetHTML;
        },

        getHostName : function(site) {
            if (site == "dev")
                return "http://localhost:8080/web-widgets/getwidget";
            else if (site == "qa")
                return "http://contentads.qat.citygridmedia.com/ads/getwidget";
            else
                return "http://contentads.citygridmedia.com/ads/getwidget";
        },

        checkInput : function(objCSW) {
            if (typeof objCSW.what == "undefined") { objCSW.what = 'Restaurants'; }
            if (typeof objCSW.where == "undefined") { objCSW.where = 'West Hollywood, CA'; }
            if (typeof objCSW.publisher == "undefined") { objCSW.publisher = ''; }
            if (typeof objCSW.latitude == "undefined") { objCSW.latitude = ''; }
            if (typeof objCSW.longitude == "undefined") { objCSW.longitude = ''; }
            if (typeof objCSW.tags == "undefined") { objCSW.tags = ''; }
            if (typeof objCSW.radius == "undefined") { objCSW.radius = '10'; }
            if (typeof objCSW.callBackUrl == "undefined") { objCSW.callBackUrl = ''; }
            if (typeof objCSW.callBackFunction == "undefined") { objCSW.callBackFunction = ''; }
            if (typeof objCSW.adUnitName == "undefined") { objCSW.adUnitName = 'nearby'; }
            if (typeof objCSW.adUnitSize == "undefined") { objCSW.adUnitName = '300x250'; }

            citygrid.objCSW = objCSW;
            return objCSW;
        },

        QSObject : function(querystring) {
            //Create regular expression object to retrieve the qs part
            var qsReg = new RegExp("[?][^#]*","i");
            var hRef = unescape(querystring);
            var qsMatch = hRef.match(qsReg);
            //removes the question mark from the url
            qsMatch = new String(qsMatch);
            qsMatch = qsMatch.substr(1, qsMatch.length -1);
            //split it up
            var rootArr = qsMatch.split("&");
            for (var i=0;i<rootArr.length;i++) {
                var tempArr = rootArr[i].split("=");
                if (tempArr.length ==2) {
                    tempArr[0] = unescape(tempArr[0]);
                    tempArr[1] = unescape(tempArr[1]);
                    if(tempArr[0]=='var') {
                        document.write('<script type=\"text/javascript\">citygrid.nearby.createwidget('+tempArr[1]+');<\/script>');
                    }
                }
            }
        }
    }
};

(function() {
    var scriptSrc = document.getElementById("CSWScript").src.toLowerCase();
    citygrid.common.QSObject(scriptSrc);
}());