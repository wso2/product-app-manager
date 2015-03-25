<?php

include('../util/func.php');

$userName = "";

$claims = explode("=.", get_header('X-JWT-Assertion'));
if(isset($claims)){
    if(isset($claims[1])){
        $userClaims = json_decode(base64_decode($claims[1]));
        $userName = $userClaims->Subject;
    }
}else{

}

?>



<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Notifi</title>

    <!-- Bootstrap -->
    <link href="../assets/css/bootstrap.min.css" rel="stylesheet">
     <link href="../assets/css/styles.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
   
      <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <a class="navbar-brand" href="#">Notifi</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">

          <ul class="nav navbar-nav navbar-right">
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Welcome <?echo $userName; ?>! <span class="caret"></span></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="../">Logout</a></li>
              </ul>
            </li>
          </ul>


        </div><!--/.nav-collapse -->
      </div>
    </nav>

    <div class="container">

     <div class="raw">
        <div class="col-md-12">
          <div class='raw'>
              <h2>Please enter your message:</h2> 
          </div>
          <div class='raw'>
              <textarea  id="txt-message" class="form-control" rows="3"></textarea>
          </div>
          <div class='raw raw-seperator pull-right'>
              <button id="btn-send" class="btn btn-default btn-lg">Send</button>
          </div>
        </div>
     </div>

     <div class="raw">
      <div class="col-md-12">
          <div class="raw">
            <h4>Sent Messages:</h4>
          </div>  

          <div class="raw">

            <table class="table table-striped" id="message-table">
              <thead>
                <tr>
                  <th width="80%">Message</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>

              </tbody>
            </table>

          </div>

      </div>  

     </div>

    </div><!-- /.container -->




    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="../assets/js/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="../assets/js/bootstrap.min.js"></script>
    <script src="js/script.js"></script>
    <script type="text/javascript" src="../assets/js/invokeStatistcs.js" ></script>
    <script type="text/javascript">
        invokeStatistics();
    </script>

  </body>
</html>