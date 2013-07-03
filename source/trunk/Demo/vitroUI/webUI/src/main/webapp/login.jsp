<%@page session='false' contentType='text/html'
        import="presentation.webgui.vitroappservlet.Common"
        %>
<html>
<head>
    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>Login</title>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/ico/favicon.png">
	<script type="text/javascript">
	$(document).ready(function(){
		$('#loginout').addClass("active");
 	});     	
	</script>
</head>
<body>
    <!-- DDMenu -->
     <%= Common.printDDMenu(application.getRealPath("/"), request) %>
   <div class="container-fluid">

      <form class="form-signin" action="" id="loginForm" method="post">
        <h2 class="form-signin-heading">VITRO login</h2>
        <input id="usernameTxb" type="text" name="usernameTxb" class="input-block-level" placeholder="Username">
        <input id="passwordTxb" name="passwordTxb" type="password" class="input-block-level" placeholder="Password" >
        <label class="checkbox">
          <input type="checkbox" name="rememberMe" id="rememberMe"> Remember me
        </label>
        <button class="btn btn-large btn-primary" type="submit">Login</button>
      </form>

    </div>
    <!-- begin the footer for the application -->
    <%= Common.printFooter(request, application) %>
    <!-- end of footer -->
	
	
</body>
</html>
