// CSW namespace for Widgets operation, all functionality will be placed or derived in same object.
var CSW = {
	// service root, which points to main URL from which we will get data.
	serviceRoot : 'http://74.208.64.80:8080/citysearch3/PFPSample',
	// element in which we want to dump received data from serviceRoot.
	targetElementId : 'csWidget',
	// width of Ad element, for future use
	width : 250,
	// height of Ad element, for future use
	height : 300,
	// parameters need for webservice
	what : '',
	where : '',
	publishercode : '21EC2020-3AEA-1069-A2DD-08002B30309D',
	callbackURL : 'http://www.insiderpages.com/b/',
	callbackfunction : '',
	placement : '',
	lat : '',
	lon : '',
	apikey : 'gunyay6vkqnvc2geyfedbdt3',
	tags : '',
	radius : '',
	// function to create webservice url with parameters
	buildUrl : function() {
		var widgeturl = this.serviceRoot;
		widgeturl += "?";
		widgeturl += 'what=' + this.what;
		widgeturl += '&where=' + this.where;
		widgeturl += '&publishercode=' + this.publishercode;
		widgeturl += '&apikey=' + this.apikey;
		widgeturl += '&placement=' + this.placement;
		// widgeturl+='&lat='+this.lat;
		// widgeturl+='&lon='+this.lon;
		widgeturl += '&tags=' + this.tags;
		widgeturl += '&radius=' + this.radius;

		widgeturl += '&callbackURL=' + this.callbackURL;

		widgeturl += '&callbackfunction=' + this.callbackfunction;

		return widgeturl;
	},
	// Ajax function to get data from given url and dump it in controlid.
	GetData : function(surl, controlID) {
		var XMLHTTP;
		try {
			XMLHTTP = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				XMLHTTP = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (oc) {
				XMLHTTP = null;
			}
		}
		// Creating object in Mozilla and Safari
		if (!XMLHTTP && typeof XMLHttpRequest != "undefined") {
			XMLHTTP = new XMLHttpRequest();
		}
		var url = surl;
		XMLHTTP.open("GET", url, true);
		XMLHTTP.onreadystatechange = function() {// Call a function when the
													// state changes.
			if (XMLHTTP.readyState == 4
					&& (XMLHTTP.status == 200 || XMLHTTP.status == 0)) {

				try {
					if (XMLHTTP.readyState == 1)
						return;
					if (XMLHTTP.readyState == 4) {
						// Valid Response is received
						if ((XMLHTTP.status == 200 || XMLHTTP.status == 0)) {
							if (controlID && controlID != '') {
								var RsltElem = document
										.getElementById(controlID);
								if (RsltElem) {
									var sScript = "";
									sScript = XMLHTTP.responseText;

									RsltElem.innerHTML = sScript;

									// if (sScript == '') {
									// RsltElem.style.height = '0px';
									// } else {
									// RsltElem.style.height = 'auto';
									// }
								}
							}
							XMLHTTP = null;
						} else // something is wrong
						{
							if (controlID && controlID != '') {
								if (document.getElementById(controlID)) {
									document.getElementById(controlID).innerHTML = '';
								}
							}
						}
					}
				} catch (e) {
				}
				return;
			}

		}
		XMLHTTP.send(null);
	}

};

// default parameters: client will override all this parameters
CSW.cswParameters = {
	targetElementId : 'csWidget',
	width : 250,
	height : 300,
	what : '',
	where : '',
	publishercode : '21EC2020-3AEA-1069-A2DD-08002B30309D',
	callbackURL : 'http://www.insiderpages.com/b/',
	callbackfunction : '',
	placement : '',
	lat : '',
	lon : '',
	apikey : 'gunyay6vkqnvc2geyfedbdt3',
	tags : '',
	radius : ''
};

// xCSWidgets object, which create widgets, user will call method from
// his webpage and set appropriate parameter before it calls createwidget
// method
CSW.xCSWidgets = {
	CreateWidget : function() {
		// assign all parameters from cswparameters to CSW object.
		if (CSW.cswParameters.targetElementId)
			CSW.targetElementId = CSW.cswParameters.targetElementId;
		if (CSW.cswParameters.width)
			CSW.width = CSW.cswParameters.width;
		if (CSW.cswParameters.height)
			CSW.height = CSW.cswParameters.height;
		if (CSW.cswParameters.what)
			CSW.what = CSW.cswParameters.what;
		if (CSW.cswParameters.where)
			CSW.where = CSW.cswParameters.where;
		if (CSW.cswParameters.publishercode)
			CSW.publishercode = CSW.cswParameters.publishercode;
		if (CSW.cswParameters.callbackURL)
			CSW.callbackURL = CSW.cswParameters.callbackURL;
		if (CSW.cswParameters.callbackfunction)
			CSW.callbackfunction = CSW.cswParameters.callbackfunction;

		if (CSW.cswParameters.placement)
			CSW.placement = CSW.cswParameters.placement;
		if (CSW.cswParameters.lat)
			CSW.lat = CSW.cswParameters.lat;
		if (CSW.cswParameters.lon)
			CSW.lon = CSW.cswParameters.lon;
		if (CSW.cswParameters.apikey)
			CSW.apikey = CSW.cswParameters.apikey;
		if (CSW.cswParameters.tags)
			CSW.tags = CSW.cswParameters.tags;
		if (CSW.cswParameters.radius)
			CSW.radius = CSW.cswParameters.radius;

		CSW.GetData(CSW.buildUrl(), CSW.targetElementId);
	}
};
