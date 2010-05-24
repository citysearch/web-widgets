//Widgets operation, all functionality will be placed or derived in same object.
function createwidget(objCSW)
{
    var serviceRoot = '/' + document.location.hostname + '/' + document.location.pathname.split('/')[1] + '/Nearby_Places300x250';
    var widgeturl= serviceRoot;
    widgeturl += "?";
    if(typeof objCSW.what == "undefined" && typeof objCSW.tags == "undefined" )
    {
    throw 'undefined what/tags';
    return;
    }

    if(typeof objCSW.url == "undefined" && typeof objCSW.callback == "undefined" )
    {
    throw 'undefined url/callback';
    return;
    }
    if(typeof objCSW.what == "" && typeof objCSW.tags == "" )
    {
    throw 'empty what/tags';
    return;
    }
    if(typeof objCSW.url == "" && typeof objCSW.callback == "" )
    {
    throw 'empty url/callback';
    return;
    }

    if(typeof objCSW.publisher == "undefined" || objCSW.publisher == '')
    {
        throw 'undefiend publisher';
        return;
    }

    if(typeof objCSW.what == "undefined")
        objCSW.what = '';
    if(typeof objCSW.where == "undefined")
        objCSW.where = '';
    if(typeof objCSW.lat == "undefined")
        objCSW.lat = '';
    if(typeof objCSW.lon == "undefined")
        objCSW.lon = '';

    if(typeof objCSW.tags == "undefined")
        objCSW.tags = '';
    if(typeof objCSW.radius == "undefined")
        objCSW.radius = '';
    if(typeof objCSW.url == "undefined")
        objCSW.url = '';
    if(typeof objCSW.callback == "undefined")
        objCSW.callback = '';

    widgeturl+='what='+objCSW.what;
    widgeturl+='&where='+objCSW.where;
    widgeturl+='&publishercode='+objCSW.publisher;
    widgeturl+='&lat='+objCSW.lat;
    widgeturl+='&lon='+objCSW.lon;
    widgeturl+='&tags='+objCSW.tags;
    widgeturl+='&radius='+objCSW.radius;
    widgeturl+='&placement=&apikey=test';
    widgeturl+='&callbackfunction=' + objCSW.callback;
    widgeturl+='&callbackURL=' + objCSW.url;
    //document.write(widgeturl);

    var XMLHTTP=null;
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
    XMLHTTP.open("GET", widgeturl , true);
    XMLHTTP.onreadystatechange = function() {//Call a function when the state changes.
        if (XMLHTTP.readyState == 4 && (XMLHTTP.status == 200||XMLHTTP.status == 0) ) {

            try {
                if (XMLHTTP.readyState == 1)
                    return;
                if (XMLHTTP.readyState == 4) {
                    //Valid Response is received
                    if ((XMLHTTP.status == 200||XMLHTTP.status == 0)) {
                        var sScript = "";
                        sScript = XMLHTTP.responseText;

                        document.getElementById(objCSW.target).innerHTML = sScript;
                        XMLHTTP = null;
                    }
                }
            }
            catch (e) {
            }
            return;
        }

    }
    XMLHTTP.send(null);
}
function QSObject(querystring){
    //Create regular expression object to retrieve the qs part
    var qsReg = new RegExp("[?][^#]*","i");
    hRef = unescape(querystring);
    var qsMatch = hRef.match(qsReg);
    //removes the question mark from the url
    qsMatch = new String(qsMatch);
    qsMatch = qsMatch.substr(1, qsMatch.length -1);
    //split it up
    var rootArr = qsMatch.split("&");
    for(i=0;i<rootArr.length;i++){
        var tempArr = rootArr[i].split("=");
        if(tempArr.length ==2){
            tempArr[0] = unescape(tempArr[0]);
            tempArr[1] = unescape(tempArr[1]);
            if(tempArr[0]=='var')
            {
                document.write('<script type=\"text/javascript\">createwidget('+tempArr[1]+');<\/script>');
            }
        }
    }
}
var scriptSrc = document.getElementById("CSWScript").src.toLowerCase();
qs = new QSObject(scriptSrc);
