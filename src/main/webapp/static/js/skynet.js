// internal tools
var skynet = {
    // common utils functions
    common: {
        CreateXMLHTTPAjax: function() {
            var XMLHTTP = null;
            try {
                XMLHTTP = new ActiveXObject("Msxml2.XMLHTTP");
            }
            catch (e) {
                try {
                    XMLHTTP = new ActiveXObject("Microsoft.XMLHTTP");
                }
                catch (oc) {
                    XMLHTTP = null;
                }
            }
            //Creating object in Mozilla and Safari
            if (!XMLHTTP && typeof XMLHttpRequest != "undefined") {
                XMLHTTP = new XMLHttpRequest();

            }
            return XMLHTTP;
        },
        getData: function(hostingurl) {
            var strKeyword = '';
            var strCity = '';
            var strState = '';
            var strCountry = '';
            
              var skynetdata = {
                        what: '',
                        where: ''
                    };
            var XMLHTTP = null;
            try {
                //var requestUrl = "http://skynet.services.netseer.com/apis/services/skynet/GetResults?userId=citygrid&password=citygrid&url=" + hostingurl;
                //var requestUrl = "http://74.208.64.80:8080/CitySearchWS/getskynet?url=" + hostingurl;
                var requestUrl = "http://localhost:8080/ads/getskynet?url=" + hostingurl;

                XMLHTTP = skynet.common.CreateXMLHTTPAjax();
                if (XMLHTTP) {
                    XMLHTTP.open("GET", requestUrl + "&" + Math.random(), false);
                    XMLHTTP.send(null);
                    if (XMLHTTP.readyState == 4) {
                        //Valid Response is received
                        if (XMLHTTP.status == 200) {
                            var ResponseText = XMLHTTP.responseXML;
                            if (ResponseText) {
                                var skyNetKeywordnodes = ResponseText.getElementsByTagName('relatedKeyword');
                                if (skyNetKeywordnodes.length > 0) {
                                    var ranNum = Math.floor(Math.random() * 5);
                                    if (ranNum < skyNetKeywordnodes.length) {
                                        strKeyword = skyNetKeywordnodes[ranNum].childNodes[0].nodeValue;
                                    } else {
                                        strKeyword = skyNetKeywordnodes[0].childNodes[0].nodeValue;
                                    }
                                }
                                var citynodes = ResponseText.getElementsByTagName('city');
                                if (citynodes.length > 0) {
                                    strCity = citynodes[0].childNodes[0].nodeValue;
                                }
                                var statenodes = ResponseText.getElementsByTagName('state');
                                if (statenodes.length > 0) {
                                    strState = statenodes[0].childNodes[0].nodeValue;
                                }
                                var countrynodes = ResponseText.getElementsByTagName('country');
                                if (countrynodes.length > 0) {
                                    strCountry = countrynodes[0].childNodes[0].nodeValue;
                                }
                                skynetdata.what = strKeyword;
                                skynetdata.where = strCity + ', ' + strState;
                            }
                        }
                        else //something is wrong 
                        {
                            XMLHTTP = null;
                        }

                    }
                }
            }
            catch(e){
             //alert('Error='+ e);
            }
            return skynetdata;
        }
    }
};
