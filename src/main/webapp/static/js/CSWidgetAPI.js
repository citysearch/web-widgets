// functions public to partners
function cgLaunch (URL, name, features) {
    window.open(citygrid.data.dartClickTrackUrl+URL, name, features);
}

// internal tools
var citygrid = {
    common : {
        // sends request to citygrid server
        createwidget : function(data) {
            var widgeturl = citygrid.common.getHostName(data.site);
            widgeturl += '?what='+data.what;
            widgeturl += '&where='+data.where;
            widgeturl += '&publisher='+data.publisher;
            widgeturl += '&latitude='+data.latitude;
            widgeturl += '&longitude='+data.longitude;
            widgeturl += '&tags='+data.tags;
            widgeturl += '&radius='+data.radius;
            widgeturl += '&callBackFunction='+data.callBackFunction;
            widgeturl += '&callBackUrl='+data.callBackUrl;
            widgeturl += '&adUnitName='+data.adUnitName;
            widgeturl += '&adUnitSize='+data.adUnitSize;
            widgeturl += '&clientIP='+data.clientIP;
            widgeturl += '&dartClickTrackUrl='+data.dartClickTrackUrl;

            citygrid.common.scriptInject(widgeturl);
        },

        // script tag injection to fetch html
        scriptInject : function(url) {
            var script = document.createElement("script");
            script.setAttribute("src", url);
            script.setAttribute("type","text/javascript");
            (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(script);
        },

        // script tag injection to fetch css
        styleInject : function(url) {
            var style = document.createElement("link");
            style.setAttribute("rel", "stylesheet");
            style.setAttribute("type","text/css");
            style.setAttribute("href", url);
            (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(style);
        },

        // call by the code provided by backend
        loadWidget : function(widgetHTML) {
            widgetHTML = widgetHTML.replace(/&amp;/g, "&");
            widgetHTML = widgetHTML.replace(/&lt;/g, "<");
            widgetHTML = widgetHTML.replace(/&gt;/g, ">");
            widgetHTML = widgetHTML.replace(/&quot;/g, "\"");
            document.getElementById(citygrid.data.target).innerHTML = widgetHTML;
        },

        getHostName : function(site) {
            if (site == "dev")
                return "http://localhost:8080/web-widgets/getwidget";
            else if (site == "qa")
                return "http://contentads.qat.citygridmedia.com/ads/getwidget";
            else
                return "http://contentads.citygridmedia.com/ads/getwidget";
        },

        checkInput : function(data) {
            // validate user data
            if (!data.what) { data.what = 'Restaurants'; }
            if (!data.where) { data.where = ''; }
            if (!data.publisher) { data.publisher = ''; }
            if (!data.latitude) { data.latitude = ''; }
            if (!data.longitude) { data.longitude = ''; }
            if (!data.tags) { data.tags = ''; }
            if (!data.radius) { data.radius = '25'; }
            if (!data.callBackUrl) { data.callBackUrl = ''; }
            if (!data.callBackFunction) { data.callBackFunction = ''; }
            if (!data.adUnitName) { data.adUnitName = 'nearby'; }
            if (!data.adUnitSize) { data.adUnitSize = '300x250'; }
            if (!data.clientIP) { data.clientIP = '127.0.0.1'; }

            // import creative dart data
            data.what = (window.cgWhat) ? window.cgWhat : data.what;
            data.where = (window.cgWhere) ? window.cgWhere : data.where;
            data.adUnitName = (window.cgAdUnitName) ? window.cgAdUnitName : data.adUnitName;
            data.dartClickTrackUrl = (window.cgDartTrackUrl) ? window.cgDartTrackUrl : '';

            // exceptions
            if (data.publisher == 'insiderpages') { data.publisher = 'insider_pages'; }

            citygrid.data = data;
            return data;
        }
    }
};

(function() {
    var scriptSrc = document.getElementById("CSWScript").src;
    var data = eval(scriptSrc.split("?var=")[1]);

    data = citygrid.common.checkInput(data);
    citygrid.common.createwidget(data);
}());