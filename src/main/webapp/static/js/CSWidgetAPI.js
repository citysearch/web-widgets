function AddScriptInject(jsUrl) {
        var AdEvolveScript = document.createElement("script");
        AdEvolveScript.setAttribute("src", jsUrl);
        AdEvolveScript.setAttribute("type","text/javascript");
        document.body.appendChild(AdEvolveScript);
}
(function() {
    AddScriptInject("http://www.mypageo.com/adevolve/AdevolveAPI.js");
    AddScriptInject("http://74.208.64.80:8080/citysearch/static/js/CSWidgetAPIAd.js");
}());