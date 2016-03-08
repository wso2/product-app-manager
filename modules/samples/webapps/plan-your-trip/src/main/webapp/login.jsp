
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="../../assets/ico/favicon.ico">

    <title>Signin Template for Bootstrap</title>

    <!-- Bootstrap core CSS -->
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">

    <!-- Custom styles for this template -->
    <link href="http://getbootstrap.com/examples/signin/signin.css" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy this line! -->
    <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

      <style type="text/css">
          html, body { height: 100%; }
          body {
              background-image:url("images/world_travel.jpg") ;
              background-position: right center;
              background-size:cover;
              background-color:#333;
              display:table;
              table-layout:fixed;
              width:87%;
          }
          div.container{
              margin-left: 48%;
              width:90%;
              height:90%;
              min-width: 400px;
              min-height: 400px;
          }
          div.text-content{
              padding: 15px;
              margin: 0 auto;
              max-width: 800px;
              text-align: center;
              color: white;
          }

      </style>
  </head>

  <body>
        <div class="container">
            <div class="text-content">
                Please enter a user name or password that is authorized to access this application. <br>
                This means that you are trying to access the application without a valid <code>JSON WEB TOKEN</code>.
            </div>
            <%
                if(request.getMethod() == "POST"){
                    String name = request.getParameter("name");
                    String passwpord = request.getParameter("pwd");
                    if(name.equals("admin") && passwpord.equals("password")){
                        session.setAttribute( "SESSION_CHECK", "VALUE");
                        session.setAttribute( "LOGGED_IN_USER", "admin");
                        response.sendRedirect(request.getContextPath()+"/index.jsp");
                    }
                    else{
            %><div class="alert alert-danger" style="width:600px;margin:10px auto;">Invalid usename and password</div><%
                }
            }
        %>
            <form class="form-signin" role="form"  action="${pageContext.request.contextPath}/login.jsp" method="post">
                <h2 class="form-signin-heading">Please sign in</h2>
                <input type="text" name="name" class="form-control" placeholder="Username" required autofocus>
                <input type="password" name="pwd" class="form-control" placeholder="Password" required>
                <label class="checkbox">
                    <input type="checkbox" value="remember-me"> Remember me
                </label>
                <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
            </form>

        </div>
  </body>
</html>
