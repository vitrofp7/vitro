function getXMLHTTPRequest() {
    var requester = null;
    try {
        requester = new XMLHttpRequest();
    }
    catch (error) {
        var aVersions = [ "MSXML2.XMLHttp.5.0", "MSXML2.XMLHttp.4.0", "MSXML2.XMLHttp.3.0", "MSXML2.XMLHttp", "Microsoft.XMLHTTP"];
        for(var i = 0; i < aVersions.length;i++)
        {
            try 
            {
                 requester = new ActiveXObject(aVersions[i]);
            }
            catch (error) 
            {
                continue;
            }
        }
    }
    return requester;
}