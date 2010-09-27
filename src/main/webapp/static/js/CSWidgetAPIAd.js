var citygrid = {
    common : {
        // sends request to citygrid server
        createwidget : function(data) {
            // fetch css
            var hostname = citygrid.common.getHostName(data.site);

            citygrid.common.scriptInject(hostname + '/getcss');

            // fetch html
            var widgeturl = [];
            for (var e in data) {
                widgeturl.push(e + '=' + escape(data[e]));
            }
            widgeturl = widgeturl.join('&');
            citygrid.common.scriptInject(hostname + '/getwidget?' + widgeturl);
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
           // if (site == "localhost")
                return "http://localhost:8080/ads";
            /*    
            else if (site == "dev")
                return "http://lax1devmcw1.test.cs:8080/ads";
            else if (site == "qa")
                return "http://contentads.qat.citygridmedia.com/ads";
            else if (site == "qalax")
                return "http://lax1qatmcw1.test.cs:8080";
            else
                return "http://contentads.citygridmedia.com/ads";
            */
        },

        checkInput : function(data) {
            // validate user data
		if (!data.what) {
                var sHostUrl = window.location.href;



		if (data.HostingURL != '')
                    sHostUrl = data.HostingURL;
              
                var skynetdata = skynet.common.getData(sHostUrl);
                data.what = skynetdata.what;
                data.where = skynetdata.where;
                //data.what = data.what.split(' ')[0];
				
            	} 
		else
		{
		

				if (!data.what) { data.what = 'Restaurants'; }
				if (!data.adUnitName) { data.adUnitName = 'nearby'; }
				if (!data.adUnitSize) { data.adUnitSize = '300x250'; }
				if (!data.clientIP) { data.clientIP = '127.0.0.1'; }

				// import creative dart data
				data.what = (window.cgWhat) ? window.cgWhat : data.what;
				data.where = (window.cgWhere) ? window.cgWhere : data.where;
		}
				data.adUnitName = (window.cgAdUnitName) ? window.cgAdUnitName : data.adUnitName;
				data.dartClickTrackUrl = (window.cgDartTrackUrl) ? window.cgDartTrackUrl : '';

				// exceptions
				if (data.publisher == 'insiderpages') { data.publisher = 'insider_pages'; }

				citygrid.data = data;
				
           citygrid.common.createwidget(data);
            return data;










        }
    }
};

(function() {
    var scriptSrc = document.getElementById("CSWScript").src;
    var data = eval(scriptSrc.split("?var=")[1]);

    data = citygrid.common.checkInput(data);
    
}());