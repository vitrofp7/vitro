<%@ page import="service.subscription.response.M2MSubscriptionResponseService" %>
<%@ page import="service.subscription.response.SubscriptionResponsePort" %>
<%@ page import="java.net.URL" %>
<%@ page import="javax.xml.namespace.QName" %>
<%@ page import="service.notification.M2MNotificationService" %>
<%@ page import="service.notification.NotificationPort" %>
<%@ page import="service.notification.EventKindType" %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="vitro.vspEngine.service.common.ConfigDetails" %>
<%@ page import="service.command.response.M2MCommandResponseService" %>
<%@ page import="service.command.response.CommandResponsePort" %>
<%@ page import="service.command.response.CommandSensorResultType" %>
<%--
  Created by IntelliJ IDEA.
  User: antoniou
  Date: 17/10/12
  Time: 14:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title></title>
  </head>
  <body>
       <%
           // retrieve an external (-ish) (non-local/ non 192.168.x.x or 127.0.0.1 or localhost).
           // TODO: ofcourse for this particular test case, we can retrieve directly this info from the request object, since the index.jsp is hosted in the same webapp as the webservices
           //   String scheme = request.getScheme();             // http
           //   String serverName = request.getServerName();
           //   int serverPort = request.getServerPort();        // 80
           //   String contextPath = request.getContextPath();   // /mywebapp
           //   String servletPath = request.getServletPath();   // /servlet/MyServlet
           //   String pathInfo = request.getPathInfo();         // /a/b;c=123
           //   String queryString = request.getQueryString();          // d=789
           String hostIP = ConfigDetails.getConfigDetails().getProbableExternalIpAddress();
           int hostWSport = request.getServerPort();
           if(hostIP != null)
           {
               M2MSubscriptionResponseService myservice = new M2MSubscriptionResponseService(
                       new URL("http://"+hostIP+":"+Integer.toString(hostWSport)+"/vitrows/services/SubscriptionResponse?wsdl"),
                       new QName("http://www.telefonica.com/wsdl/UNICA/SOAP/m2m/subscriptionresponse/v1/services", "M2MSubscriptionResponseService"));
               SubscriptionResponsePort dasService = myservice.getSubscriptionResponse();
              // dasService.sayHelloWorldFrom("SOMEONE");
               dasService.subscribeResponse("Test Test", 1, 0, "TEST ERROR on SubscriptionResponse");

               M2MNotificationService myNotifyService = new M2MNotificationService(new URL("http://"+hostIP+":"+Integer.toString(hostWSport)+"/vitrows/services/Notification?wsdl"));
               NotificationPort dasNotifyService =    myNotifyService.getNotification();
               dasNotifyService.notify("Test Test" , EventKindType.fromValue("Observation"),"xml xml xml");


               CommandSensorResultType cmdResType = new CommandSensorResultType();
               cmdResType.setCommandResultML("TEST ML:: TODO REPLACE WITH VALID XML");
               M2MCommandResponseService myCommandResponse = new M2MCommandResponseService(new URL("http://"+hostIP+":"+Integer.toString(hostWSport)+"/vitrows/services/CommandResponse?wsdl"));
               CommandResponsePort dasCommandResponseService =    myCommandResponse.getCommandResponse();
               dasCommandResponseService.commandSensorResult("Test Test Correlator", cmdResType, 0, "Test error onCommandResponse");

               try
               {
                   UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
                   if(ssUN== null){
                        out.println("no ssun in context!");
                   }    else
                   {
                       out.println("FOUND ssun in context!");
                   }
               }
               catch (Exception e)
               {
                   e.printStackTrace();
               }
           }
           else
           {
               out.println("No valid IP was detected to find the published web services!");
           }
       %>
  </body>
</html>