<%-- 
    Document   : ecnumber
    Created on : Nov 18, 2013, 11:56:35 AM
    Author     : joseph
--%>



<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib  prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="xchars" uri="http://www.ebi.ac.uk/xchars"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="Fn" uri="/WEB-INF/epTagLibray.tld" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>



<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
<!--[if lt IE 7]> <html class="no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js ie8 oldie" lang="en"> <![endif]-->
<!-- Consider adding an manifest.appcache: h5bp.com/d/Offline -->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
    <head>
        <!--        <meta charset="utf-8">-->

        <!-- Use the .htaccess and remove these lines to avoid edge case issues.
             More info: h5bp.com/b/378 -->
        <!-- <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> -->	<!-- Not yet implemented -->

        <title>Search Result  &lt; Enzyme Portal &gt; &lt; EMBL-EBI</title>
        <meta name="description" content="EMBL-EBI"><!-- Describe what this page is about -->
        <meta name="keywords" content="bioinformatics, europe, institute"><!-- A few keywords that relate to the content of THIS PAGE (not the whol project) -->
        <meta name="author" content="EMBL-EBI"><!-- Your [project-name] here -->

        <!-- Mobile viewport optimized: j.mp/bplateviewport -->
        <meta name="viewport" content="width=device-width,initial-scale=1">


        <link href="resources/css/search.css" type="text/css" rel="stylesheet" />
        <!--           <link rel="stylesheet" href="resources/css/enzyme-portal-colours.css" type="text/css" media="screen" />-->
        <link rel="stylesheet" href="resources/css/embl-petrol-colours.css" type="text/css" media="screen" />

        <!--        for production-->
        <link rel="stylesheet" href="//www.ebi.ac.uk/web_guidelines/css/compliance/mini/ebi-fluid-embl.css">

        <!--        javascript was placed here for auto complete otherwise should be place at the bottom for faster page loading-->

        <script src="resources/lib/spineconcept/javascript/jquery-1.5.1.min.js" type="text/javascript"></script>
        <script src="resources/lib/spineconcept/javascript/identification.js" type="text/javascript"></script>
        <script src="resources/javascript/search.js" type="text/javascript"></script>


        <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
        <script src="http://yui.yahooapis.com/3.4.1/build/yui/yui-min.js"></script>



        <!-- Full build -->
        <!-- <script src="//www.ebi.ac.uk/web_guidelines/js/libs/modernizr.minified.2.1.6.js"></script> -->

        <!-- custom build (lacks most of the "advanced" HTML5 support -->
        <script src="//www.ebi.ac.uk/ebisearch/examples/ebisearch-globalSearch-template_files/modernizr.js"></script>		

        <!--<! --------------------------------
        GLOBAL SEARCH TEMPLATE - START
       -------------------------------- >-->

        <script type="text/javascript" src="//www.ebi.ac.uk/ebisearch/examples/ebisearch-globalSearch-template_files/jquery-1.8.0.min.js"></script>
        <script type="text/javascript" src="//www.ebi.ac.uk/ebisearch/examples/ebisearch-globalSearch-template_files/jquery-ui-1.8.23.custom.min.js"></script>

        <!--<! --------------------------------
        GLOBAL SEARCH TEMPLATE - END
       -------------------------------- >-->




        <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
        <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
        <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
        


<!-- History.js -->
<script src="//browserstate.github.io/history.js/scripts/bundled/html4+html5/jquery.history.js"></script>




    </head>

    <body class="level2"><!-- add any of your classes or IDs -->
        <div id="skip-to">
            <ul>
                <li><a href="#content">Skip to main content</a></li>
                <li><a href="#local-nav">Skip to local navigation</a></li>
                <li><a href="#global-nav">Skip to EBI global navigation menu</a></li>
                <li><a href="#global-nav-expanded">Skip to expanded EBI global navigation menu (includes all sub-sections)</a></li>
            </ul>
        </div>

        <div id="wrapper" class="container_24">
            <header>
                <div id="global-masthead" class="masthead grid_24">
                    <!--This has to be one line and no newline characters-->
                    <a href="/" title="Go to the EMBL-EBI homepage"><img src="//www.ebi.ac.uk/web_guidelines/images/logos/EMBL-EBI/EMBL_EBI_Logo_white.png" alt="EMBL European Bioinformatics Institute"></a>

                    <nav>
                        <ul id="global-nav">
                            <!-- set active class as appropriate -->
                            <li class="first active" id="services"><a href="/services">Services</a></li>
                            <li id="research"><a href="/research">Research</a></li>
                            <li id="training"><a href="/training">Training</a></li>
                            <li id="industry"><a href="/industry">Industry</a></li>
                            <li id="about" class="last"><a href="/about">About us</a></li>
                        </ul>
                    </nav>

                </div>

                <div id="local-masthead" class="masthead grid_24 nomenu">

                    <!-- local-title -->

                    <div id="local-title" class="grid_12 alpha logo-title"> 
                        <a href="/enzymeportal" title="Back to Enzyme Portal homepage">
                            <img src="resources/images/enzymeportal_logo.png" alt="Enzyme Portal logo" style="width :64px;height: 64px; margin-right: 0px">
                        </a> <span style="margin-top: 30px"><h1 style="padding-left: 0px">Enzyme Portal</h1></span> </div>



                    <div class="grid_12 omega">




                        <%@ include file="frontierSearchBox.jsp" %>



                    </div>


                    <nav>
                        <ul class="grid_24" id="local-nav">
                            <li  class="first"><a href="/enzymeportal" title="">Home</a></li>
                            <!--					<li><a href="#">Documentation</a></li>-->
                            <li><a href="faq" title="Frequently Asked questions">FAQ</a></li>
                            <li><a href="about" title="About Enzyme Portal">About Enzyme Portal</a></li>
                            <li><a href="browse" title="Browse Disease">Browse Disease</a></li>
                             <li class="last active"><a href="${pageContext.request.contextPath}/browseEcNumber" title="Browse Enzyme">Browse Enzyme</a></li>
                            <!-- If you need to include functional (as opposed to purely navigational) links in your local menu,
                                 add them here, and give them a class of "functional". Remember: you'll need a class of "last" for
                                 whichever one will show up last... 
                                 For example: -->
                            <!--					<li class="functional last"><a href="#" class="icon icon-functional" data-icon="l">Login</a></li>-->
                            <li class="functional"><a href="http://www.ebi.ac.uk/support/index.php?query=Enzyme+portal&referrer=http://www.ebi.ac.uk/enzymeportal/" class="icon icon-static" data-icon="f">Feedback</a></li>
                            <!--                            <li class="functional"><a href="#" class="icon icon-functional" data-icon="r">Share</a></li>-->
                            <li class="functional"> <a href="https://twitter.com/share" class="icon icon-functional" data-icon="r" data-dnt="true" data-count="none" data-via="twitterapi">Share</a></li>

                        </ul>
                    </nav>
                </div>
            </header>
            <div id="content" role="main" class="grid_24 clearfix">
                <div class="grid_24">
                    <div class="clear"></div>

                    <div class="grid_24">
    
   
                                <c:if test="${not empty selectedEc }">
                                    
                                   <c:forEach var="selected" items="${selectedEc}"> 
                                        <c:if test="${not empty selected.name }">
                                       <c:choose>
                                           <c:when test="${fn:length(selectedEc) gt 1}">
                                      
                                           <xchars:translate>
                                               <h2><a href="${pageContext.request.contextPath}/ecnumber?ec=${selected.ec}&amp;ecname=${selected.name}">EC ${selected.ec}</a> - ${selected.name}</h2>    
                                       </xchars:translate> 
                                      
                                           </c:when>
                                       <c:otherwise>
                                           <h2 class="active">EC ${selected.ec} - ${selected.name}</h2>    
                                       </c:otherwise>
                                       </c:choose>
                                         </c:if>
                                        <c:if test="${not empty selected.subclassName }">
                                        <c:choose>
                                           <c:when test="${fn:length(selectedEc) gt 2}">  
                                       
                                           <xchars:translate>
                                           <h2 style="margin-left: 1em"><a href="${pageContext.request.contextPath}/ecnumber?ec=${selected.ec}&amp;subecname=${selected.subclassName}">EC ${selected.ec}</a> - ${selected.subclassName}</h2> 
                                        </xchars:translate> 
                                       
                                           </c:when>
                                        <c:otherwise>
                                      <h2 style="margin-left: 1em">EC ${selected.ec} - ${selected.subclassName}</h2>        
                                        </c:otherwise>
                                        </c:choose>
                                      </c:if>
                                       <c:if test="${not empty selected.entries }">
                                           <xchars:translate>
<!--                                           <h2 style="margin-left: 1.5em"><a href="${pageContext.request.contextPath}/ecnumber?ec=${selected.ec}&amp;subsubecname=${selected.subsubclassName}">EC ${selected.ec}</a> - ${selected.subsubclassName}</h2>-->
                                               <h3 style="margin-left: 2.5em">EC ${selected.ec} - ${selected.subsubclassName}</h3>
                                               <hr/>
                                        </xchars:translate> 
                                       </c:if>

          
                                         
                            
                         
                                   </c:forEach>
                              </c:if>
                                                                  
     
                        
                                               

                        <c:if test="${ empty json.description}">
                            <h3>Description</h3>
                            <p> <i>No description available.</i></p>

                        </c:if>
                        <c:if test="${not empty json.description}">
                            <h3>Description</h3>
                            <p>${json.description}</p>

                        </c:if>
                        <h3>Content</h3>
                        <c:choose>
                            <c:when test="${not empty json.children}">
                                <div class="resultText" style="display: table-row">
                                    <c:forEach var="data" items="${json.children}"> 
                                        
                                        <div class="result"  style="display: table-row;vertical-align: top;padding-left: 1em;padding-top: 1ex; min-height: 10px"><a href="${pageContext.request.contextPath}/ecnumber?ec=${data.ec}&amp;subecname=${data.name}">EC ${data.ec}</a>  - ${data.name}</div>


                                    </c:forEach>

                                </div>                            
                            </c:when>
                             <c:when test="${not empty json.subSubclasses}">
                                <div class="resultText" style="display: table-row">
                                    <c:forEach var="data" items="${json.subSubclasses}"> 
                                        
                                        <div class="result"  style="display: table-row;vertical-align: top;padding-left: 1em;padding-top: 1ex; min-height: 10px"><a href="${pageContext.request.contextPath}/ecnumber?ec=${data.ec}&amp;subsubecname=${data.name}">EC ${data.ec}</a>  - ${data.name}</div>


                                    </c:forEach>

                                </div>                            
                            </c:when>
                               <c:when test="${not empty json.entries}">
                                <div class="resultText" style="display: table-row">
                                    <c:forEach var="data" items="${json.entries}"> 
                                       
                                        <div class="result"  style="display: table-row;vertical-align: top;padding-left: 1em;padding-top: 1ex; min-height: 10px"><a href="${pageContext.request.contextPath}/ecnumber?ec=${data.ec}&amp;entryecname=${data.name}"> ${data.ec}</a>  - ${data.name}</div>


                                    </c:forEach>

                                </div>                            
                            </c:when>
                            <c:otherwise>
                                <div class="resultText" style="display: table-row">

                                     <p> <i>No Active Enzyme Classification Numbers found</i></p>
                                </div>                    
                            </c:otherwise>
                        </c:choose>




                    </div>


                </div>

            </div>
                        
       <script type='text/javascript'>//<![CDATA[ 
  $(function(){
  var History = window.History;
  if (History.enabled) {
      State = History.getState();
      // set initial state to first page that was loaded
      History.pushState({urlPath: window.location.pathname}, $("title").text(), State.urlPath);
  } else {
      return false;
  }

  var loadAjaxContent = function(target, urlBase, selector) {
      $(target).load(urlBase + ' ' + selector);
  };

//  var updateContent = function(State) {
//      alert(State);
//      var selector = '#' + State.data.urlPath.substring(1);
//    if ($(selector).length) { //content is already in #hidden_content
//        $('#content').children().appendTo('#hidden_content');
//        $(selector).appendTo('#content');
//    } else { 
//        $('#content').children().clone().appendTo('#hidden_content');
//        loadAjaxContent('#content', State.url, selector);
//    }
//  };

 var updateContent = function(State) {
        var url = State.url;
        var $target = $(State.data.target);
        //ajaxPost(url, $target);
        console.log(State);
        
                 $.ajax({
        url: url,
        data : null,
        type: "GET",
        //beforeSend: function (xhr) { xhr.setRequestHeader('X-Target', frame); },
        success: function (data) {
            $target.html(data);
        }
    });
  };
  


  // Content update and back/forward button handler
  History.Adapter.bind(window, 'statechange', function() {
      updateContent(History.getState());
      // var State = History.getState();
//console.log(State); 
//       var url = State.data.url;
//        var $target = $(State.data.target);
//        //console.log($target);
//        alert(State.data.target);
//        var url = State.url;


   
//         $.ajax({
//        url: url,
//        data : null,
//        type: "POST",
//        //beforeSend: function (xhr) { xhr.setRequestHeader('X-Target', frame); },
//        success: function (data) {
//            $target.html(data);
//        }
//    });
        
    
  });

  // navigation link handler
//  $('body').on('click', 'a', function(e) {
//      var urlPath = $(this).attr('href');
//      var title = $(this).text();
//      History.pushState({urlPath: urlPath}, title, urlPath);
//      return false; // prevents default click action of <a ...>
//  });
  });//]]>  

  </script>                   
                        
            <footer>
                <!-- Optional local footer (insert citation / project-specific copyright / etc here -->
                <!--
    <div id="local-footer" class="grid_24 clearfix">
                        <p>How to reference this page: ...</p>
                </div>
                -->
                <!-- End optional local footer -->

                <div id="global-footer" class="grid_24">

                    <nav id="global-nav-expanded">

                        <div class="grid_4 alpha">
                            <h3 class="embl-ebi"><a href="/" title="EMBL-EBI">EMBL-EBI</a></h3>
                        </div>

                        <div class="grid_4">
                            <h3 class="services"><a href="/services">Services</a></h3>
                        </div>

                        <div class="grid_4">
                            <h3 class="research"><a href="/research">Research</a></h3>
                        </div>

                        <div class="grid_4">
                            <h3 class="training"><a href="/training">Training</a></h3>
                        </div>

                        <div class="grid_4">
                            <h3 class="industry"><a href="/industry">Industry</a></h3>
                        </div>

                        <div class="grid_4 omega">
                            <h3 class="about"><a href="/about">About us</a></h3>
                        </div>

                    </nav>

                    <section id="ebi-footer-meta">
                        <p class="address">EMBL-EBI, Wellcome Trust Genome Campus, Hinxton, Cambridgeshire, CB10 1SD, UK &nbsp; &nbsp; +44 (0)1223 49 44 44</p>
                        <p class="legal">Copyright &copy; EMBL-EBI 2012 | EBI is an Outstation of the <a href="http://www.embl.org">European Molecular Biology Laboratory</a> | <a href="/about/privacy">Privacy</a> | <a href="/about/cookies">Cookies</a> | <a href="/about/terms-of-use">Terms of use</a></p>	
                    </section>

                </div>

            </footer>
        </div> <!--! end of #wrapper -->


        <!-- JavaScript at the bottom for fast page loading -->

        <c:if test="${pageContext.request.serverName!='www.ebi.ac.uk'}" >
            <script type="text/javascript">var redline = {}; redline.project_id = 185653108;</script><script id="redline_js" src="http://www.redline.cc/assets/button.js" type="text/javascript">
                
            </script>
            <script>
                $(document).ready(function() {
                    setTimeout(function(){
                        // Handler for .ready() called.
                        $("#redline_side_car").css("background-image","url(resources/images/redline_left_button.png)");
                        $("#redline_side_car").css("background-size", "23px auto");
                        $("#redline_side_car").css("display", "block");
                        $("#redline_side_car").css("width", "23px");
                        $("#redline_side_car").css("height", "63px");
                    },1000);
                });
            </script>
        </c:if>

        <!--        add twitter script for twitterapi-->
        <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="https://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>



        <!--    now the frontier js for ebi global result-->
        <script src="//www.ebi.ac.uk/web_guidelines/js/ebi-global-search-run.js"></script>
        <script src="//www.ebi.ac.uk/web_guidelines/js/ebi-global-search.js"></script>



        <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/cookiebanner.js"></script>  
        <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/foot.js"></script>

        <!-- end scripts-->


        <!-- Change UA-XXXXX-X to be your site's ID -->
        <!--
      <script>
          window._gaq = [['_setAccount','UAXXXXXXXX1'],['_trackPageview'],['_trackPageLoadTime']];
          Modernizr.load({
            load: ('https:' == location.protocol ? '//ssl' : '//www') + '.google-analytics.com/ga.js'
          });
        </script>
        -->


        <!-- Prompt IE 6 users to install Chrome Frame. Remove this if you want to support IE 6.
             chromium.org/developers/how-tos/chrome-frame-getting-started -->
        <!--[if lt IE 7 ]>
            <script src="//ajax.googleapis.com/ajax/libs/chrome-frame/1.0.3/CFInstall.min.js"></script>
            <script>window.attachEvent('onload',function(){CFInstall.check({mode:'overlay'})})</script>
          <![endif]-->

    </body>

</html>


