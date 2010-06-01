// functions public to partners
function cgLaunch (URL, name, features) {
    window.open("http://ad.doubleclick.net/clk;225291110;48835962;h?"+URL, name, features);
}

// internal tools
var citygrid = {

    // nearby widget
    nearby : {
        path : 'http://contentads.citygridmedia.com/ads/Nearby_Places300x250',

        createwidget : function(objCSW) {
            objCSW = citygrid.common.checkInput(objCSW);

            var widgeturl = this.path;
            widgeturl += '?what='+objCSW.what;
            widgeturl += '&where='+objCSW.where;
            widgeturl += '&publishercode='+objCSW.publisher;
            widgeturl += '&lat='+objCSW.lat;
            widgeturl += '&lon='+objCSW.lon;
            widgeturl += '&tags='+objCSW.tags;
            widgeturl += '&radius='+objCSW.radius;
            widgeturl += '&placement=&apikey=test';
            widgeturl += '&callbackfunction=' + objCSW.callback;
            widgeturl += '&callbackURL=' + objCSW.url;

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
            document.getElementById(citygrid.objCSW.target).innerHTML = widgetHTML;
        },

        checkInput : function(objCSW) {
            if (typeof objCSW.what == "undefined" && typeof objCSW.tags == "undefined" ) { throw 'undefined what/tags'; }
            if (typeof objCSW.url == "undefined" && typeof objCSW.callback == "undefined" ) { throw 'undefined url/callback'; }
            if (typeof objCSW.what == "" && typeof objCSW.tags == "" ) { throw 'empty what/tags'; }
            if (typeof objCSW.url == "" && typeof objCSW.callback == "" ) { throw 'empty url/callback'; }
            if (typeof objCSW.publisher == "undefined" || objCSW.publisher == '') { throw 'undefiend publisher'; }
            if (typeof objCSW.what == "undefined") { objCSW.what = ''; }
            if (typeof objCSW.where == "undefined") { objCSW.where = ''; }
            if (typeof objCSW.lat == "undefined") { objCSW.lat = ''; }
            if (typeof objCSW.lon == "undefined") { objCSW.lon = ''; }
            if (typeof objCSW.tags == "undefined") { objCSW.tags = ''; }
            if (typeof objCSW.radius == "undefined") { objCSW.radius = ''; }
            if (typeof objCSW.url == "undefined") { objCSW.url = ''; }
            if (typeof objCSW.callback == "undefined") { objCSW.callback = ''; }
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