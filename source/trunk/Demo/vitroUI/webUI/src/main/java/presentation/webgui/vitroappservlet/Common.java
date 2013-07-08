/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis (Research Academic Computer Technology Institute)
 * #     Paolo Medagliani (Thales Communications & Security)
 * #     D. Davide Lamanna (WLAB SRL)
 * #     Alessandro Leoni (WLAB SRL)
 * #     Francesco Ficarola (WLAB SRL)
 * #     Stefano Puglia (WLAB SRL)
 * #     Panos Trakadas (Technological Educational Institute of Chalkida)
 * #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
 * #     Andrea Kropp (Selex ES)
 * #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
 * #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
 * #
 * #--------------------------------------------------------------------------
 */
package presentation.webgui.vitroappservlet;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class Common {

    private static final transient Logger LOG = LoggerFactory.getLogger(Common.class);

    private static Common _common = null;
    private ServletContext _appcontext = null;

    private Common(){
    }

    public static Common getCommon()
    {
        if (_common == null)
        {
            _common = new Common();
        }
        return _common;
    }

    public void init(ServletContext pContext) {
        setAppContext(pContext);
    }

    public static String printFooter(HttpServletRequest request )
    {
        return printFooter(request, null);
    }

    public static String printFooter(HttpServletRequest request, ServletContext context)
    {
        boolean displayStartupEpoch = false;
        long startupEpoch = -1; //invalid value
        if(context != null)
        {
            Long startupEpochLong = (Long) (context.getAttribute("startupEpoch"));
            startupEpoch = startupEpochLong.longValue();
            if(startupEpochLong != null)
                displayStartupEpoch = true;
        }

        StringBuilder retStrBld = new StringBuilder();
        Date d = new Date();
        retStrBld.append("<div id=\"footer\"><p>");
        retStrBld.append("<a href=\"http://www.vitro-fp7.eu/\" target=\"_blank\" >VITRO</a> (No 257245) is an Information &amp; Communication Technologies (ICT) research project<br />");
        retStrBld.append("co-funded under EU&#39;s Seventh Framework Programme (FP7)</p>");
        retStrBld.append("<p>");
        retStrBld.append("<a href=\"http://cordis.europa.eu/fp7/home_en.html\" target=\"_blank\" ><img alt=\"\" src=\"");
        retStrBld.append(request.getContextPath());
        retStrBld.append("/img/IST-fp7_logo.gif\" style=\"width: 69px;\" /></a> " );
        retStrBld.append("<a href=\"http://europa.eu/index_en.htm\" target=\"_blank\" ><img alt=\"\" src=\"" );
        retStrBld.append(request.getContextPath());
        retStrBld.append("/img/eu-flag_org.gif\" style=\"width: 69px;\" /></a>" );
        retStrBld.append("</p>");
        retStrBld.append(" Last refresh was on:&nbsp;");
        retStrBld.append(d.toString());
        retStrBld.append("  <br />");
        if(displayStartupEpoch)
        {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSZ");
            String servletStartupTSStr = df.format(new Date(startupEpoch));
            retStrBld.append(" Application was restarted on:&nbsp;");
            retStrBld.append(servletStartupTSStr.toString());
            retStrBld.append("  <br />");
        }
        retStrBld.append("</div> ");
        return retStrBld.toString();
    }

    public static String printDDMenu(String context_App_RealPath, HttpServletRequest request)
    {
        StringBuilder authInfoAndButtonHTMLBld = new StringBuilder();
        // todo: if commons is refactored as singleton, we could do this only once and store it as a class member (the currentUser object)
        boolean foundWebEnvInAppContext = false;
        if (Common.getCommon().getAppContext() != null)
        {
            WebEnvironment webEnv = WebUtils.getRequiredWebEnvironment(Common.getCommon().getAppContext());
            WebSecurityManager webSecurityManager = webEnv.getWebSecurityManager();
            if(webSecurityManager!=null) {
                SecurityUtils.setSecurityManager(webSecurityManager);
                foundWebEnvInAppContext = true;
                LOG.info("Success: Retrieved WebEnvironment from context! ");
            }
        }
//       // get the currently executing user:
//        Subject currentUser = SecurityUtils.getSubject();
        if (!foundWebEnvInAppContext) {
            LOG.info("Unable to retrieve WebEnvironment from context! ");
            Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
            org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
            SecurityUtils.setSecurityManager(securityManager);
        }


        // A simple Shiro environment is set up
        // get the currently executing user:
        Subject currentUser = SecurityUtils.getSubject();

        // Tests with session variables (todo: remove this after verifying what works and what not -session range / expiration / cleanup)
        Session session = currentUser.getSession();
        String value = (String) session.getAttribute("someKey");
        if(value == null || value.trim().isEmpty()) {
            LOG.info("Session did not have the value stored! ");
            session.setAttribute("someKey", "aValue");
            value = (String) session.getAttribute("someKey");
        }
        if (value.equals("aValue")) {
            LOG.info("Retrieved the correct value! [" + value + "]");
        }

        authInfoAndButtonHTMLBld.append("<li id=\"loginout\">");
        Field [] list = currentUser.getClass().getDeclaredFields();
        for(Field f:list)
        	LOG.info(f.getName());
        if (currentUser.isAuthenticated()) {
            String myRole = "";

            LOG.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");
            if (currentUser.hasRole("vsp")) {
                LOG.info("Role: VSP!");
                myRole="advanced";

            }
            else if (currentUser.hasRole("wsie")) {
                LOG.info("Role: WSI Enabler!");
                myRole="advanced";
            }
            else if (currentUser.hasRole("user")) {
                LOG.info("Role: Simple User!");
                myRole="user";
            }
            else {
                LOG.info("Undefined Role.");
                myRole="Undefined";
            }
            //authInfoAndButtonHTMLBld.append("Hello ");
            //authInfoAndButtonHTMLBld.append( currentUser.getPrincipal());
            //authInfoAndButtonHTMLBld.append(" (");
            //authInfoAndButtonHTMLBld.append(myRole);
            authInfoAndButtonHTMLBld.append("<a href=\"" + request.getContextPath() +"/logout\">Logout</a>");
            //all done - log out!
            //currentUser.logout();
        }
        else
        {
            LOG.info("Not Authenticated!");
            authInfoAndButtonHTMLBld.append("<a href=\"" + request.getContextPath() +"/login.jsp\" >Login</a>");
        }
        authInfoAndButtonHTMLBld.append("</li>");

        StringBuilder strBuildToRet = new  StringBuilder();
        strBuildToRet.append("");
        try{
            String menuWrapperfileContents = readFile(context_App_RealPath + File.separator + "topMenuActions" + File.separator + "_proDD.htm" , "UTF-8");
            String menuUserActionsContents = "";
           if (currentUser.isAuthenticated())
                menuUserActionsContents = readFile(context_App_RealPath + File.separator + "topMenuActions" + File.separator + "_proUserActions.htm" , "UTF-8"); 

           menuWrapperfileContents = menuWrapperfileContents.replaceAll("#userRoleMenuActionsPlaceHolder#", menuUserActionsContents);
           // menuWrapperfileContents = menuWrapperfileContents.replaceAll("#vspRoleMenuActionsPlaceHolder#", menuVSPActionsContents);
           // menuWrapperfileContents = menuWrapperfileContents.replaceAll("#wsieRoleMenuActionsPlaceHolder#", menuWSIEActionsContents);
            //menuWrapperfileContents = menuWrapperfileContents.replaceAll("#auxMenuActionsPlaceHolder#", menuAuxActionsContents);
            // as a final step we replace the plcholder for the contextPATH info
            menuWrapperfileContents = menuWrapperfileContents.replaceAll("#plcholder#", request.getContextPath());
            strBuildToRet.append("<div class=\"navbar navbar-fixed-top\">");
            strBuildToRet.append("<div id=\"bar\" class=\"navbar-inner\">");
            strBuildToRet.append("<ul class=\"nav nav-pills\">");
            strBuildToRet.append("<li id=\"dashboardLogo\"><a href=\""+request.getContextPath()+"\">&nbsp;</a></li>");
            strBuildToRet.append("</ul>");
            strBuildToRet.append("<div class=\"container\" id=\"buttonbar\">");
	        //strBuildToRet.append("<div class=\"row-fluid\" align=\"center\">");
            strBuildToRet.append("<ul class=\"nav nav-pills\">");
            strBuildToRet.append(menuWrapperfileContents);
            strBuildToRet.append("</ul>");
            strBuildToRet.append("<ul class=\"nav nav-pills pull-right\">");
            strBuildToRet.append(authInfoAndButtonHTMLBld.toString());
            strBuildToRet.append("</ul>");
            strBuildToRet.append("</div>") ;
            strBuildToRet.append("<div style=\"position:absolute;top:42px;right:0;\">");
            strBuildToRet.append("<a href=\"http://www.linkedin.com/groups/VITRO-4305849\">") ;
            strBuildToRet.append("<img src=\""+request.getContextPath()+"/img/btn_cofollow_badge.png\" alt=\"Follow VITRO on LinkedIn\"></a>") ;
            strBuildToRet.append("</div>") ;
           //strBuildToRet.append("</div>") ;
            strBuildToRet.append("</div>") ;
            strBuildToRet.append("</div>") ;
        }
        catch (IOException ioe)
        {
            System.out.print(ioe.getMessage());
        }
        return strBuildToRet.toString();
    }

    private static String readFile(String file, String csName)
            throws IOException {
        Charset cs = Charset.forName(csName);
        return readFile(file, cs);
    }

    private static String readFile(String file, Charset cs)
            throws IOException {
        // No real need to close the BufferedReader/InputStreamReader
        // as they're only wrapping the stream
        FileInputStream stream = new FileInputStream(file);
        try {
            Reader reader = new BufferedReader(new InputStreamReader(stream, cs));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            return builder.toString();
        } finally {
            // Potential issue here: if this throws an IOException,
            // it will mask any others. Normally I'd use a utility
            // method which would log exceptions and swallow them
            stream.close();
        }
    }

    public ServletContext getAppContext() {
        return _appcontext;
    }

    public void setAppContext(ServletContext _appcontext) {
        this._appcontext = _appcontext;
    }


public static String printDDBody(String context_App_RealPath, HttpServletRequest request)
    {
        StringBuilder authInfoAndButtonHTMLBld = new StringBuilder();
        // todo: if commons is refactored as singleton, we could do this only once and store it as a class member (the currentUser object)
        boolean foundWebEnvInAppContext = false;
        if (Common.getCommon().getAppContext() != null)
        {
            WebEnvironment webEnv = WebUtils.getRequiredWebEnvironment(Common.getCommon().getAppContext());
            WebSecurityManager webSecurityManager = webEnv.getWebSecurityManager();
            if(webSecurityManager!=null) {
                SecurityUtils.setSecurityManager(webSecurityManager);
                foundWebEnvInAppContext = true;
                LOG.info("Success: Retrieved WebEnvironment from context! ");
            }
        }
//       // get the currently executing user:
//        Subject currentUser = SecurityUtils.getSubject();
        if (!foundWebEnvInAppContext) {
            LOG.info("Unable to retrieve WebEnvironment from context! ");
            Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
            org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
            SecurityUtils.setSecurityManager(securityManager);
        }


        // A simple Shiro environment is set up
        // get the currently executing user:
        Subject currentUser = SecurityUtils.getSubject();

        // Tests with session variables (todo: remove this after verifying what works and what not -session range / expiration / cleanup)
        Session session = currentUser.getSession();
        String value = (String) session.getAttribute("someKey");
        if(value == null || value.trim().isEmpty()) {
            LOG.info("Session did not have the value stored! ");
            session.setAttribute("someKey", "aValue");
            value = (String) session.getAttribute("someKey");
        }
        if (value.equals("aValue")) {
            LOG.info("Retrieved the correct value! [" + value + "]");
        }


        Field [] list = currentUser.getClass().getDeclaredFields();
        for(Field f:list)
        	LOG.info(f.getName());
        if (currentUser.isAuthenticated()) {
	     authInfoAndButtonHTMLBld.append("<div class=\"container\" style=\"padding-top: 100px;\">");
		    authInfoAndButtonHTMLBld.append("</div>");
        }
        else
        {
	     authInfoAndButtonHTMLBld.append("<div class=\"container\" style=\"padding-top: 100px;\">");
		    authInfoAndButtonHTMLBld.append("</div>");
	        authInfoAndButtonHTMLBld.append("<div id=\"notloggedin\" class=\"well\">");
		    authInfoAndButtonHTMLBld.append("Login to use the VITRO functionalities!");
		    authInfoAndButtonHTMLBld.append("</div>");
		   // authInfoAndButtonHTMLBld.append("<div id=\"logoHome\" align=\"center\">");
		   // authInfoAndButtonHTMLBld.append("<img src=" + request.getContextPath() +"/img/Vitrologo.jpg>");
		   // authInfoAndButtonHTMLBld.append("</div>");
        }


        StringBuilder strBuildToRet = new  StringBuilder();
        strBuildToRet.append("");

            

           // strBuildToRet.append("<div id=\"bar\"><table id=general_table><tr>");
           // strBuildToRet.append(menuWrapperfileContents);
        strBuildToRet.append(authInfoAndButtonHTMLBld.toString());
           // strBuildToRet.append("</tr></table></div>") ;
        
        return strBuildToRet.toString();
    }



public static String printSideMenu(String context_App_RealPath, HttpServletRequest request)
    {
        StringBuilder authInfoAndButtonHTMLBld = new StringBuilder();
        // todo: if commons is refactored as singleton, we could do this only once and store it as a class member (the currentUser object)
        boolean foundWebEnvInAppContext = false;
        if (Common.getCommon().getAppContext() != null)
        {
            WebEnvironment webEnv = WebUtils.getRequiredWebEnvironment(Common.getCommon().getAppContext());
            WebSecurityManager webSecurityManager = webEnv.getWebSecurityManager();
            if(webSecurityManager!=null) {
                SecurityUtils.setSecurityManager(webSecurityManager);
                foundWebEnvInAppContext = true;
                LOG.info("Success: Retrieved WebEnvironment from context! ");
            }
        }
//       // get the currently executing user:
//        Subject currentUser = SecurityUtils.getSubject();
        if (!foundWebEnvInAppContext) {
            LOG.info("Unable to retrieve WebEnvironment from context! ");
            Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
            org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
            SecurityUtils.setSecurityManager(securityManager);
        }


        // A simple Shiro environment is set up
        // get the currently executing user:
        Subject currentUser = SecurityUtils.getSubject();

        // Tests with session variables (todo: remove this after verifying what works and what not -session range / expiration / cleanup)
        Session session = currentUser.getSession();
        String value = (String) session.getAttribute("someKey");
        if(value == null || value.trim().isEmpty()) {
            LOG.info("Session did not have the value stored! ");
            session.setAttribute("someKey", "aValue");
            value = (String) session.getAttribute("someKey");
        }
        if (value.equals("aValue")) {
            LOG.info("Retrieved the correct value! [" + value + "]");
        }


        Field [] list = currentUser.getClass().getDeclaredFields();
        for(Field f:list)
        	LOG.info(f.getName());
        if (currentUser.isAuthenticated()) {
        	String myRole = "";

            LOG.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");
            if (currentUser.hasRole("vsp")) {
                LOG.info("Role: advanced!");
                myRole="advanced";

            }
            else if (currentUser.hasRole("wsie")) {
                LOG.info("Role: WSI Enabler!");
                myRole="advanced";
            }
            else if (currentUser.hasRole("user")) {
                LOG.info("Role: Simple User!");
                myRole="user";
            }
            else {
                LOG.info("Undefined Role.");
                myRole="Undefined";
            }

	       	authInfoAndButtonHTMLBld.append("<div id=\"sidebar\" class=\"sidebar-nav\">");
		    authInfoAndButtonHTMLBld.append("<ul class=\"nav nav-tabs nav-stacked\">");
		    
		    
//demo layout
		    if (myRole.equals("user") || myRole.equals("advanced")){
                authInfoAndButtonHTMLBld.append("<li id=\"srv-custnew\"><a href=\""+request.getContextPath()+"/roleEndUser/newservice.jsp\">New service</a></li>");
                authInfoAndButtonHTMLBld.append("<li id=\"srv-new\"><a href=\""+request.getContextPath()+"/roleEndUser/GetComposedServiceDeployListAdvanced\">Deploy services</a></li>");
		    	authInfoAndButtonHTMLBld.append("<li id=\"srv-list\"><a href=\""+request.getContextPath()+"/roleEndUser/GetComposedServiceListAction\">Manage services</a></li>");
		    }
		    //reserved for WSI enabler?
		   // if (currentUser.hasRole("wsie")){
			//    authInfoAndButtonHTMLBld.append("<li id=\"sens-edit\"><a href=\"#\">Edit existing sensors</a></li>");
			//    authInfoAndButtonHTMLBld.append("<li id=\"sens-discover\"><a href=\"#\">Discover new sensors</a></li>");
			//    authInfoAndButtonHTMLBld.append("<li id=\"sens-remove\"><a href=\"#\">Remove sensors</a></li>");
		    //}
		    
		    if (myRole.equals("advanced"))
			{
			    authInfoAndButtonHTMLBld.append("<li id=\"WSIE\" class=\"dropdown all-camera-dropdown\">");
			    authInfoAndButtonHTMLBld.append("<a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">Advanced settings<b class=\"caret\"></b></a>");
			    authInfoAndButtonHTMLBld.append(" <ul class=\"dropdown-menu\">");
			    authInfoAndButtonHTMLBld.append("<li data-filter-camera-type=\"all\"><a href=\""+request.getContextPath()+"/roleWSIE/WSIEnewIsland.jsp\">Register new Island</a></li>");
			    //authInfoAndButtonHTMLBld.append("<li data-filter-camera-type=\"all\"><a href=\""+request.getContextPath()+"/roleWSIE/WSIEeditIslands.jsp\">View Islands</a></li>");
			    authInfoAndButtonHTMLBld.append("<li data-filter-camera-type=\"all\"><a href=\""+request.getContextPath()+"/roleVSP/VSPeditGateways.jsp\">Manage gateways</a></li>");
			    authInfoAndButtonHTMLBld.append("</ul>");
			    authInfoAndButtonHTMLBld.append("</li>");
			}
	
	
		    authInfoAndButtonHTMLBld.append("</ul>");
		    authInfoAndButtonHTMLBld.append("</div>");


//<li class="dropdown all-camera-dropdown">
//		<a class="dropdown-toggle" data-toggle="dropdown" href="#">Control panel<b class="caret"></b></a>
//                <ul class="dropdown-menu">
//                  <li data-filter-camera-type="all"><a data-toggle="tab" href="#plcholder#/help/helpcontents.jsp"">Help Topics</a></li>
//                  <li data-filter-camera-type="all"><a data-toggle="tab" href="#plcholder#/help/aboutapp.jsp">About</a></li>
//                </ul>
//              </li>
           
        }
        else
        {
              
        }


        StringBuilder strBuildToRet = new  StringBuilder();
        strBuildToRet.append("");

            

           // strBuildToRet.append("<div id=\"bar\"><table id=general_table><tr>");
           // strBuildToRet.append(menuWrapperfileContents);
            strBuildToRet.append(authInfoAndButtonHTMLBld.toString());
           // strBuildToRet.append("</tr></table></div>") ;
        
        return strBuildToRet.toString();
    }





 


}
