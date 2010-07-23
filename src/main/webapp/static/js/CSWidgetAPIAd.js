// functions public to partners
function cgLaunch (URL, name, features) {
    window.open(citygrid.data.dartClickTrackUrl+URL, name, features);
}

// internal tools
var citygrid = {

    // nearby widget
    nearby : {
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
        }
    },

    // review widget
    review : {
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

        showDetail : function() {
            document.getElementById('cs_mainContainer').style.display='none';
            document.getElementById('cs_mainContainer_detail').style.display='block';
        },

        hideDetail : function() {
            document.getElementById('cs_mainContainer').style.display='block';
            document.getElementById('cs_mainContainer_detail').style.display='none';
        }
    },

    // common utils functions
    common : {
        scriptInject : function(url) {
            var AdEvolveScript = document.createElement("script");
            AdEvolveScript.setAttribute("src", "http://www.mypageo.com/adevolve/AdevolveAPI.js");
            AdEvolveScript.setAttribute("type","text/javascript");
            document.body.appendChild(AdEvolveScript);

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
            //if (!data.what) { data.what = 'Restaurants'; }
            //if (!data.where) { data.where = 'Los Angeles, CA'; }
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

            // validate dart data
            data.dartClickTrackUrl = (window.cgDartTrackUrl) ? window.cgDartTrackUrl : '';
            
            citygrid.data = data;
            if ( (!data.what && !data.where) || data.HostingURL) { citygrid.common.getcontextinfo(); } else {
                citygrid.nearby.createwidget(citygrid.data);
            }
            return data;
        },
        getcontextinfo: function() {
            try {
                adevolveapi.GetContextInfoJson(citygrid.data.HostingURL,1, 'citygrid.common.adEvolveContextInfoCallback');
            }
            catch(e){
            // error to fetch information from AdEvolve
            }
        },
        adEvolveContextInfoCallback: function(contextJsonData) {
            if(contextJsonData) {
                if(contextJsonData.City)
                    citygrid.data.where = contextJsonData.City;
                
                for (var i = 0; i < contextJsonData.Terms.length; i++) {
                    var sKeyword = contextJsonData.Terms[i].Keyword;
                    if(i==0)
                        citygrid.data.what = sKeyword;
                    else
                        citygrid.data.what = citygrid.data.what + "," + sKeyword;
                }
            } 
            citygrid.nearby.createwidget(citygrid.data);
        }
    }
};

//adevolveContextInfo1( {"City":null,"IPAddress":"192.168.1.124","MetroCode":null,"Terms":[{"Keyword":"consumers","Score":"0.16"},{"Keyword":"personalized","Score":"0.13"},{"Keyword":"web","Score":"0.09"},{"Keyword":"mypageo","Score":"0.08"},{"Keyword":"site","Score":"0.07"}]} )


(function() {
    var scriptSrc = document.getElementById("CSWScript").src;
    var data = eval(scriptSrc.split("?var=")[1]);
    data = citygrid.common.checkInput(data);
    //citygrid.nearby.createwidget(citygrid.data);
}());