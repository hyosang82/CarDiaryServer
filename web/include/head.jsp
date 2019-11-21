<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Car Diary</title>
	<!-- Bootstrap Styles-->
    <link href="assets/css/bootstrap.css" rel="stylesheet" />
     <!-- FontAwesome Styles-->
    <link href="assets/css/font-awesome.css" rel="stylesheet" />
    <!-- Custom Styles-->
    <link href="assets/css/custom-styles.css" rel="stylesheet" />
     <!-- Google Fonts-->
   <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css' />
   
   <!-- jQuery Js -->
    <script src="assets/js/jquery-1.10.2.js"></script>
</head>
<body>
<%!
private String getPageTitle(String main, String sub) {
	StringBuffer sb = new StringBuffer();
	sb.append("<div class=\"row\">")
	.append("<div class=\"col-md-12\">")
    .append("<h1 class=\"page-header\">")
    .append(main).append(" <small>").append(sub).append("</small>")
    .append("</h1></div></div>");
	
	return sb.toString();
}

%>
    <div id="wrapper">
    
    <%@ include file="navi_top.jsp" %>
    <%@ include file="navi_side.jsp" %>
    
    <div id="page-wrapper" >
        <div id="page-inner">
    