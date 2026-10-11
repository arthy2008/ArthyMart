<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><title>Error - ArthyMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<header><h1>ARTHY MART</h1><nav><a href="${pageContext.request.contextPath}/index.jsp">Home</a></nav></header>
<div class="container">
    <h2>Something went wrong</h2>
    <p>Please try again or return home.</p>
    <%
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Object message = request.getAttribute("javax.servlet.error.message");
        Object exceptionObj = request.getAttribute("javax.servlet.error.exception");
        Throwable ex = (exceptionObj instanceof Throwable) ? (Throwable) exceptionObj : exception;
    %>
    <% if (status != null || ex != null) { %>
        <div style="background:#fee2e2; border:1px solid #f87171; border-radius:8px; padding:16px; margin-top:20px; font-family:monospace; font-size:0.85rem; color:#991b1b; text-align:left;">
            <div><strong>Status Code:</strong> <%= status != null ? status : "N/A" %></div>
            <div><strong>Message:</strong> <%= message != null ? message : "N/A" %></div>
            <% if (ex != null) { %>
                <div><strong>Exception:</strong> <%= ex.getClass().getName() %>: <%= ex.getMessage() %></div>
                <pre style="overflow:auto; max-height:200px; margin-top:10px; background:#fff; padding:10px; border-radius:4px;"><%
                    java.io.StringWriter sw = new java.io.StringWriter();
                    ex.printStackTrace(new java.io.PrintWriter(sw));
                    out.print(sw.toString());
                %></pre>
            <% } %>
        </div>
    <% } %>
</div>
</body>
</html>
