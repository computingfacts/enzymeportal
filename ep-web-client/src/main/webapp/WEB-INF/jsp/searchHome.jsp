<%-- 
    Document   : search
    Created on : Mar 31, 2011, 7:57:06 PM
    Author     : hongcao
--%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="xchars" uri="http://www.ebi.ac.uk/xchars"%>

<html>
    <head>
        <title>Enzyme Portal</title>        
        <link media="screen" href="resources/lib/spineconcept/css/960gs/reset.css" type="text/css" rel="stylesheet" />
        <link media="screen" href="resources/lib/spineconcept/css/960gs/text.css" type="text/css" rel="stylesheet" />
        <link media="screen" href="resources/lib/spineconcept/css/960gs/960.css" type="text/css" rel="stylesheet" />
        <link media="screen" href="resources/lib/spineconcept/css/common.css" type="text/css" rel="stylesheet" />
        <link media="screen" href="resources/lib/spineconcept/css/identification.css" type="text/css" rel="stylesheet" />
        <link media="screen" href="resources/lib/spineconcept/css/species.css" type="text/css" rel="stylesheet" />
        <link media="screen" href="resources/lib/spineconcept/javascript/jquery-ui/css/custom-theme/jquery-ui-1.8.11.custom.css" type="text/css" rel="stylesheet" />
        <link href="resources/css/search.css" type="text/css" rel="stylesheet" />
        <script src="resources/lib/spineconcept/javascript/jquery-1.5.1.min.js" type="text/javascript"></script>
        <script src="resources/lib/spineconcept/javascript/jquery-ui/js/jquery-ui-1.8.11.custom.min.js" type="text/javascript"></script>
        <script src="resources/lib/spineconcept/javascript/identification.js" type="text/javascript"></script>

        <!--
       <link media="screen" href="resources/spineconcept/css/common.css" type="text/css" rel="stylesheet" />
        <link href="resources/lib/layout-default-latest.css" type="text/css" rel="stylesheet" />
        <link href="resources/spineconcept/css/epHome.css" type="text/css" rel="stylesheet" />
        <script type="text/javascript" src="resources/lib/jquery-latest.js"></script>
        <script type="text/javascript" src="resources/lib/jquery-ui-latest.js"></script>
        <script type="text/javascript" src="resources/lib/jquery.layout-latest.js"></script>
	<script type="text/javascript" src="resources/lib/js/debug.js"></script>

-->
        <!--
    <link rel="stylesheet" type="text/css" href="resources/lib/extjs4.0.0/ext-all.css" />
    <script type="text/javascript" src="resources/lib/extjs4.0.0/bootstrap.js"></script>
    <script type="text/javascript" src="resources/lib/extjs4.0.0/border.js"></script>
-->
    </head>
    <body>        
        <div class="page container_12">
            <div  class="grid_12">
<div class="headerdiv" id="headerdiv" style="height: 60px;">
    <iframe src="http://www.ebi.ac.uk/inc/homepage_head.html" name="head" id="head" marginwidth="0" marginheight="0" style="height: 125px;" frameborder="0" scrolling="no" width="100%">
</iframe> </div>

            </div>
            <div class="clear"></div>

            <div class="grid_12">
                <div class="breadcrumbs" id="breadcrumbs">
                    <ul>
                        <li class="first"><a href="">EBI</a></li>
                        <li><a href="">Databases</a></li>
                        <li><a href="">Enzymes</a></li>
                        <li><a href="">Search Results</a></li>
                    </ul>
                </div>
                <div class="basket">
                    <input id ="compareButton" type="button" value="Compare & Download (0)" />
                </div>
            </div>
            <div class="clear"></div>
            <div class="grid_12">
                <div  id="keywordSearch" class="search">
                <form:form modelAttribute="searchParameters" action="showResults" method="get">
                    <p>
                        <form:input path="keywords" cssClass="field"/>  
                        <input type="submit" value="Search" class="button" />
                    </p>
                </form:form>
                 </div>
            </div>
            <form:form modelAttribute="resultSet" action="showResults" method="get">
                <c:set var="searchFilter" value="${resultSet.searchfilter}"/>
                <c:set var="enzymeSummaryCollection" value="${resultSet.enzymesummarycollection}"/>
                <c:set var="totalfound" value="${enzymeSummaryCollection.totalfound}"/>
            <div class="grid_12 content">
                <c:if test="${enzymeSummaryCollection.enzymesummary!=null && enzymeSummaryCollection.totalfound>0}">
                <div class="filter">
                    <c:set var="filterMaxDisplay" value="${10}"/>
                    <div class="title">
                        Search Filters
                    </div>
                    <div class="line"></div>

                    <div class="sublevel1">
                        <div class="subTitle">
                            Chemical Compounds
                        </div>
                        <div class="filterContent">
                            <c:set var="compoundList" value="${searchFilter.compounds}"/>
                            <c:set var="compoundListSize" value="${fn:length(compoundList)}"/>
                            <c:set var="compoundListMaxSize" value="${compoundListSize}"/>

                            <c:if test="${compoundListMaxSize > filterMaxDisplay}">
                                <c:set var="compoundListMaxSize" value="${filterMaxDisplay}"/>
                            </c:if>
                            <c:if test="${compoundListMaxSize > 0}">
                                <c:forEach var="i" begin="0" end="${compoundListMaxSize-1}">
                                    <div class="filterLine">
                                    <div class="text">
                                        <xchars:translate>
                                            <c:out value="${compoundList[i].name}" escapeXml="false"/>
                                        </xchars:translate>
                                    </div>
                                    <div class="checkItem">
                                        <input type="checkbox" name="human" value="human" />
                                     </div>
                                    <div class="clear"></div>
                                    </div>
                                </c:forEach>

                            </c:if>
                        </div>
                    </div>

                    <div class="sublevel1">
                        <div class="subTitle">
                            Species
                        </div>
                        <div class="filterContent">
                            <c:set var="speciesList" value="${searchFilter.species}"/>
                            <c:set var="speciesListSize" value="${fn:length(speciesList)}"/>
                            <c:set var="speciesListMaxSize" value="${speciesListSize}"/>
                            <c:if test="${speciesListMaxSize > filterMaxDisplay}">
                                <c:set var="speciesListMaxSize" value="${filterMaxDisplay}"/>
                            </c:if>
                            <c:forEach var="i" begin="0" end="${speciesListMaxSize-1}">
                                <c:set var="speciesName" value="${speciesList[i].commonname}"/>
                                <c:if test='${speciesName==null || speciesName ==""}'>
                                    <c:set var="speciesName" value="${speciesList[i].scientificname}"/>
                                </c:if>
                                <div class="filterLine">
                                <div class="text">
                                <span>
                                    <c:out value="${speciesName}"/>                                    
                                </span>
                                </div>
                                <div class="checkItem">
                                    <input type="checkbox" name="human" value="human" />
                                 </div>
                                <div class="clear"></div>
                                </div>                                
                            </c:forEach>
                        </div>
                    </div>
                </div>
                </c:if>                
                <div id="keywordSearchResult" class="result">
                    <c:if test="${totalfound==0}">
                        No results found!
                    </c:if>
                    <c:if test="${enzymeSummaryCollection.enzymesummary!=null && enzymeSummaryCollection.totalfound>0}">
                        <div class="resultText">
                            About <c:out value="${totalfound}"/> results found
                        </div>
                        <div id="tnt_pagination">
                            <form:form modelAttribute="pagination">
                                <c:set var="totalPages" value="${pagination.totalPages}"/>
                                <c:set var="maxPages" value="${totalPages}"/>
                                <c:if test="${totalPages>pagination.maxDisplayedPages}">
                                    <c:set var="maxPages" value="${pagination.maxDisplayedPages}"/>
                                    <c:set var="showNextButton" value="${true}"/>
                                </c:if>
                                <c:forEach var="i" begin="1" end="${maxPages}">
                                    <c:set var="start" value="${(i-1)*pagination.numberResultsPerPage}"/>
                                    <a href="showResults?keywords=${searchParameters.keywords}&start=${start}">
                                        <c:out value="${i}"/>
                                    </a>                                    
                                </c:forEach>
                                <c:if test="${showNextButton==true}">
                                    <a href="showResults?keywords=${searchParameters.keywords}&start=${searchParameters.start+pagination.numberResultsPerPage}">
                                        next
                                    </a>
                                </c:if>
                            </form:form>
                        </div>
                    <div class="comparison">
                        Compare & download
                    </div>
                    <div class="clear"></div>
                        <div class="line"></div>
                    <div id ="allButtons">
                        <input type="button" value="Add All"/><input type="button" value="Remove All"/>
                    </div>                        
                        <div class="resultContent">
                            <c:forEach items="${enzymeSummaryCollection.enzymesummary}" var="enzyme">
                             <c:set var="primAcc" value="${enzyme.uniprotaccessions[0]}"/>
                            <div class="resultItem">
                                <div id="proteinImg">
                                    <c:set var="imgFile" value='${enzyme.pdbeaccession[0]}'/>
                                    <c:set var="imgLink" value=""/>
                                    <c:if test='${imgFile != "" && imgFile != null}'>
                                        <c:set var="imgLink" value="http://www.ebi.ac.uk/pdbe-srv/view/images/entry/${imgFile}_cbc600.png"/>
                                    </c:if>                                    
                                    <img src="${imgLink}" alt="Image not available!" width="110" height="90"/>                                 
                                </div>
                                <div id="desc">
                                    <a href="entry/${primAcc}">
                                        <c:set var="showName" value="${fn:substring(enzyme.name, 0, 100)}"/>
                                        <c:out value="${showName}"/>
                                       <!-- [<c:out value="${enzyme.uniprotid}"/>]-->
                                    </a>
                                    <br/>
                                    Function:                                    
                                    <c:out value="${enzyme.function}"/><br/>
                                    Synonyms:
                                    <c:set var="synSize" value="${0}"/>
                                    <c:forEach items="${enzyme.synonym}" var="syn">                                        
                                        <c:set var="nameSize" value="${nameSize+1}"/>
                                    </c:forEach>
                                    <c:set var="counter" value="${0}"/>
                                    <c:forEach items="${enzyme.synonym}" var="syn">                                        
                                        <c:if test="${nameSize>1 && counter>0}">
                                            ; 
                                        </c:if>
                                        <c:out value="${syn}"/>
                                        <c:set var="counter" value="${counter+1}"/>
                                    </c:forEach>

                                </div>
                                    <div id="in">in</div>
                                    <div class="species">
                                        <a href="entry/${primAcc}">
                                        <c:choose>
                                        <c:when test='${enzyme.species.commonname == ""}'>
                                            <c:out value="${enzyme.species.scientificname}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${enzyme.species.commonname}"/>
                                        </c:otherwise>
                                        </c:choose>
                                        </a><br/>
                                        <c:set var="speciesCounter" value="${1}"/>
                                        <c:set var="speciesSize" value="${fn:length(enzyme.relatedspecies)+1}"/>
                                        <c:forEach items="${enzyme.relatedspecies}" var="relspecies">                                            
                                            <c:if test="${speciesCounter < 4}">
                                                <a href="entry/${relspecies.uniprotaccessions[0]}">
                                                <c:choose>
                                                <c:when test='${relspecies.species.commonname == ""}'>                                                
                                                    <c:out value="${relspecies.species.scientificname}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${relspecies.species.commonname}"/>
                                                </c:otherwise>
                                                </c:choose>
                                             </a>
                                             <br/>
                                            </c:if>
                                            <c:set var="speciesCounter" value="${speciesCounter+1}"/>
                                        </c:forEach>
                                                <c:if test="${speciesSize > 3}">
                                                <a href="">See more</a> <br/>
                                            </c:if>
                                    
                                </div>                                    
                            </div>
                            <div id="buttonItems">
                                <br/>
                                <br/>
                                <input type="button" value="Add"/><br/>
                                <input type="button" value="Remove"/>
                                <br/>
                                <br/>
                            </div>
                            <div class="clear"></div>
                            </c:forEach>
                        </div>
                    </c:if>
                </form:form>
            </div>

                </div>
            <div class="grid_12">
                    <div class="footer">&copy;
                      <a target="_top" href="http://www.ebi.ac.uk/" title="European Bioinformatics Institute Home Page">European Bioinformatics Institute</a>
                      2011. EBI is an Outstation of the
                      <a href="http://www.embl.org/" target="_blank" title="European Molecular Biology Laboratory Home Page">European Molecular Biology Laboratory</a>.
                    </div>
            </div>
            <div class="clear"></div>
        </div>

    </body>
</html>