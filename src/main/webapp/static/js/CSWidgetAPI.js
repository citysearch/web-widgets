function AddScriptInject(jsUrl) {
        var AdEvolveScript = document.createElement("script");
        AdEvolveScript.setAttribute("src", jsUrl);
        AdEvolveScript.setAttribute("type","text/javascript");
        document.body.appendChild(AdEvolveScript);
}
(function() {
    AddScriptInject("http://192.168.0.100:8080/ads/static/js/skynet.js");
    AddScriptInject("http://192.168.0.100:8080/ads/static/js/CSWidgetAPIAd.js");
}());