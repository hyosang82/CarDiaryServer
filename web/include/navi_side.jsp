<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
	String _side_highlight = "I";
	String _uri = request.getRequestURI();

	if(_uri.startsWith("/manage.jsp")) {
		_side_highlight = "MN";
	}else if(_uri.startsWith("/setup.jsp")) {
		_side_highlight = "S";
	}else if(_uri.startsWith("/drivelog.jsp")) {
		_side_highlight = "D";
	}else if(_uri.startsWith("/fuel.jsp")) {
		_side_highlight = "F";
	}else if(_uri.startsWith("/maintenance.jsp")) {
		_side_highlight = "MT";
	}
	
%>
<nav class="navbar-default navbar-side" role="navigation">
    <div class="sidebar-collapse">
        <ul class="nav" id="main-menu">
			<!--
            <li>
                <a href="index.html"><i class="fa fa-dashboard"></i> Dashboard</a>
            </li>
            <li>
                <a href="ui-elements.html"><i class="fa fa-desktop"></i> UI Elements</a>
            </li>
            <li>
                <a href="chart.html"><i class="fa fa-bar-chart-o"></i> Charts</a>
            </li>
            <li>
                <a href="tab-panel.html"><i class="fa fa-qrcode"></i> Tabs & Panels</a>
            </li>
            
            <li>
                <a href="table.html"><i class="fa fa-table"></i> Responsive Tables</a>
            </li>
            <li>
                <a href="form.html"><i class="fa fa-edit"></i> Forms </a>
            </li>
            -->
            
            <li>
                <a <%=("MN".equals(_side_highlight) ? "class=\"active-menu\"" : "") %> href="manage.jsp"><i class="fa fa-edit"></i> Manage </a>
            </li>
            
            
            <li>
                <a <%=("S".equals(_side_highlight) ? "class=\"active-menu\"" : "") %> href="setup.jsp"><i class="fa fa-edit"></i> Setup </a>
            </li>
            
            
            <li>
                <a <%=("D".equals(_side_highlight) ? "class=\"active-menu\"" : "") %> href="drivelog.jsp"><i class="fa fa-edit"></i> Drive Log </a>
            </li>

            <li>
                <a <%=("F".equals(_side_highlight) ? "class=\"active-menu\"" : "") %> href="fuel.jsp"><i class="fa fa-edit"></i> Fuel Log </a>
            </li>
            
            <li>
                <a <%=("MT".equals(_side_highlight) ? "class=\"active-menu\"" : "") %> href="maintenance.jsp"><i class="fa fa-edit"></i> Maintenance </a>
            </li>
            
            <!-- 
            <li>
                <a href="#"><i class="fa fa-sitemap"></i> Multi-Level Dropdown<span class="fa arrow"></span></a>
                <ul class="nav nav-second-level">
                    <li>
                        <a href="#">Second Level Link</a>
                    </li>
                    <li>
                        <a href="#">Second Level Link</a>
                    </li>
                    <li>
                        <a href="#">Second Level Link<span class="fa arrow"></span></a>
                        <ul class="nav nav-third-level">
                            <li>
                                <a href="#">Third Level Link</a>
                            </li>
                            <li>
                                <a href="#">Third Level Link</a>
                            </li>
                            <li>
                                <a href="#">Third Level Link</a>
                            </li>

                        </ul>

                    </li>
                </ul>
            </li>
            <li>
                <a href="empty.html"><i class="fa fa-fw fa-file"></i> Empty Page</a>
            </li>
            -->
        </ul>

    </div>

</nav>
<!-- /. NAV SIDE  -->
