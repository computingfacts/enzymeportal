<%-- 
    Document   : search
    Created on : Sep 17, 2012, 4:05:40 PM
    Author     : joseph
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!doctype html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="xchars" uri="http://www.ebi.ac.uk/xchars"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="Fn" uri="/WEB-INF/epTagLibray.tld" %>



<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
<!--[if lt IE 7]> <html class="no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js ie8 oldie" lang="en"> <![endif]-->
<!-- Consider adding an manifest.appcache: h5bp.com/d/Offline -->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->

<c:set var="pageTitle" value="Search results"/>
<%@include file="head.jspf" %>

<body class="level2 ${totalfound eq 0? 'noresults' : ''}">

 <script src="http://code.jquery.com/jquery-2.0.2.min.js"></script>

<!-- <script src="http://code.jquery.com/jquery-1.9.1.js"></script>-->
  <script src="http://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
 
<!--<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css" />-->
 
    <script>
		$(function() {
			$("#accordion").accordion({
				collapsible : true,
				active : false,
				heightStyle : "content"
			});
		});
	</script>
    
    <div id="skip-to">
            <ul>
                <li><a href="#content">Skip to main content</a></li>
                <li><a href="#local-nav">Skip to local navigation</a></li>
                <li><a href="#global-nav">Skip to EBI global navigation menu</a></li>
                <li><a href="#global-nav-expanded">Skip to expanded EBI global navigation menu (includes all sub-sections)</a></li>
            </ul>
        </div>

        <div id="wrapper" class="container_24">

            <%@include file="header.jspf" %>
            
            <div id="content" role="main" class="grid_24 clearfix">

                <!--Global variables-->
                <c:set var="showButton" value="Show more"/>
                <c:set var="searchText" value="${searchModel.searchparams.text}"/>
                <c:set var="searchSequence" value="${searchModel.searchparams.sequence}"/>
                <c:set var="startRecord" value="${pagination.firstResult}"/>
                <c:set var="searchresults" value="${searchModel.searchresults}"/>
                <c:set var="searchFilter" value="${searchresults.searchfilters}"/>
                <c:set var="summaryEntries" value="${searchresults.summaryentries}"/>
                <c:set var="summaryEntriesSize" value="${fn:length(summaryEntries)}"/>
                <c:set var="totalfound" value="${searchresults.totalfound}"/>
                <c:set var="filterSizeDefault" value="${50}"/>
                <script>
                    var  speciesAutocompleteDataSource = [];
                    var compoundsAutoCompleteDataSource = [];
                    var diseaseAutoCompleteDataSource = [];
                    var ecAutoCompleteDataSource = [];
                </script>
                
                
                       <c:choose>
                            <c:when test="${searchModel.searchparams.type eq 'SEQUENCE'}">	
                                <c:set var="searchText" value="${searchModel.searchparams.sequence}"/>	
                            </c:when>

                            <c:otherwise>
                                <c:set var="searchText"
                                       value="${Fn:escapeHTML(searchModel.searchparams.text)}"/>
                            </c:otherwise>
                        </c:choose>

                <!-- Suggested layout containers -->  
                <section >
                    <div class="grid_12zzz" style="display: table; margin-left: 0em;">
                        <%@ include file="breadcrumbs.jsp" %>
                    </div>


                </section>

                <section class="grid_24 clearfix">
                    <section class="grid_18 alpha"  >

                        <c:if test="${totalfound eq 0}">
                            <c:if test="${searchText eq ''}">
                               <c:set var="searchText"
                                       value=" "/> 
                            </c:if>
                            <h2>No results found</h2>
                            <p class="alert">We're sorry but we couldn't find anything that matched your search for " ${searchText} ". Please try another search or use the<a href="advanceSearch"> advanced search</a></p>
                            <script>
                                $(document).ready(function() {
                                    try {
                                        /* The simplest implementation, used on your zero search results pages */
                                        updateSummary({noResults: true});	       
                                    } catch (except_1) {}
                                });
                            </script>
                        </c:if>
                        <c:if test="${totalfound gt 0}">
                               <h2>${summaryEntriesSize} result(s) found</h2>
                        </c:if>
                    </section>
                    <c:if test="${searchModel.searchparams.type ne 'SEQUENCE'}">
                        <script src="resources/javascript/ebi-global-search-run.js"></script>
                        <script src="resources/javascript/ebi-global-search.js"></script>
                        <aside class="grid_6 omega shortcuts expander" id="search-extras">	    	
                        <div id="ebi_search_results"><h3
                            class="slideToggle icon icon-functional"
                            data-icon="u">Show more data from EMBL-EBI</h3>
                        </div>
                    </aside>
                    </c:if>

                </section>

                <section class="grid_6 alpha" id="search-results">


                    <!--                <div class="grid_12 content">-->
                    <c:if test="${ searchresults.totalfound gt 0}">
                        <div class="filter grid_24">
                            <div class="title">
                                Search Filters
                            </div>
                            <div class="line"></div>
                            <form:form id="filtersForm" name="filtersForm" modelAttribute="searchModel" action="search" method="POST">
                                <form:hidden path="searchparams.type" />	
                                <form:hidden path="searchparams.text" />
                                <form:hidden path="searchparams.sequence" />
                                <form:hidden path="searchparams.previoustext" />
                                <input type="hidden" id="filtersFormStart"
                                       name="searchparams.start" value="0"/>
                                <%@ include file="filter-species.jspf"%>
                                <br/>
                                <%@ include file="filter-compounds.jspf"%>
                                <br/>
                                <%@ include file="filter-diseases.jspf"%>
                                <br/>
                                 <%@ include file="filter-family.jsp"%>
                            </form:form>
                        </div> 
                        <%--filter --%>
                    </c:if>
                </section>
                <section class="grid_18" id="keywordSearchResult">
                  <c:if test="${searchModel.searchparams.type eq 'COMPOUND'}">
                  
                    <form id="goBackStructureSearch"
                        action="${pageContext.request.contextPath}/advanceSearch"
                        method="POST" style="text-align: center;">
                        <input type="hidden" name="type" value="COMPOUND"/>

                    <div style="display: table-row;">
                        <figure class="compound structure" style="display: table-cell">
                            <img id="drawnImg" src=""
                                alt="Image not available"/>
                            <figcaption>
                                Your structure search<br/>
                                <button type="submit"
                                    title="Modify the chemical structure to make a new search.">Edit Query</button>
                            </figcaption>
                        </figure>
                        <figure class="compound structure" style="display: table-cell">
                            <img src="${chebiConfig.compoundImgBaseUrl}${searchText}"
                                alt="${searchText}"
                                style="height: 100px; width: 100px"/>
                            <figcaption>
                                <a href="${chebiConfig.compoundBaseUrl}${searchText}"
                                    target="_blank"><span id="chebiNameId"></span></a>
                                <br/>
                                <button type="submit"
                                    title="View the list of results from your chemical structure search."
                                    name="results" value="true">Other matching structures</button>
                            </figcaption>
                        </figure>
                    </div>
                    
                    </form>
                    
                    <script>
                    if (typeof(sessionStorage.drawnImg) != 'undefined'){
                    	$('#drawnImg').attr('src', sessionStorage.drawnImg);
                    }
                    jQuery.ajax({
                    	url: "${chebiConfig.wsTestUrl}${searchText}",
                        success: function(data){
                        	var xmlDoc = jQuery.parseXML(data);
                        	var xmlResult = jQuery(xmlDoc).find('return');
                        	var chebiName = xmlResult.find('chebiAsciiName').text();
                        	$('#chebiNameId').text(
                        			chebiName + ' (' + '${searchText}' + ')');
                        }
                    });
                    </script>
                  </c:if>                                              
                    <c:if test="${totalfound eq -100}">
                        <spring:message code="label.search.empty"/>
                    </c:if>
                    <c:if test="${summaryEntriesSize gt 0 and searchresults.totalfound gt 0}">
                            <div style="width: 100%;">
                                <c:set var="totalPages" value="${pagination.lastPage}"/>
                                <c:set var="maxPages" value="${totalPages}"/>
                                <div class="action-buttons">
                                    <%@include file="basket-buttons.jspf" %>
                                </div>
                                <div id="paginationNav" style="text-align: right;">
                                    <form:form modelAttribute="pagination" >
                                        <c:if test="${totalPages gt pagination.maxDisplayedPages}">
                                            <c:set var="maxPages" value="${pagination.maxDisplayedPages}"/>
                                            <c:set var="showNextButton" value="${true}"/>
                                        </c:if>
                                        <input id="prevStart" type="hidden"
                                               value="${pagination.firstResult - pagination.numberOfResultsPerPage}">
                                        <a id="prevButton" href="javascript:void(0);"
                                           style="display:${pagination.currentPage eq 1? 'none' : 'inline'}">
                                            Previous
                                        </a>
                                        Page ${pagination.currentPage} of ${totalPages}
    
                                        <c:if test="${pagination.lastResult+1 lt summaryEntriesSize}">
                                            <input id ="nextStart" type="hidden"
                                                   value="${startRecord + pagination.numberOfResultsPerPage}">                                    
                                            <a id="nextButton" href="javascript:void(0);">
                                                Next
                                            </a>
                                        </c:if>                         
                                        <%-- Add species filter to this form, don't lose it: --%>
                                        <c:forEach var="filterSp" items="${searchModel.searchresults.searchfilters.species}">
                                            <input type="checkbox" style="display: none;" 
                                                   name="searchparams.species"
                                                   value="${filterSp.scientificname}" />
                                        </c:forEach>
                                        <%-- TODO: add also compounds and disease filters --%>
                                    </form:form>
                                </div><!-- pagination -->
                            </div>
                        <div class="clear"></div>
                        <div class="line"></div>
                        <div class="resultContent">
                            <c:set var="resultItemId" value="${0}"/>
                            <c:forEach items="${summaryEntries}"
                                       begin="${pagination.firstResult}"
                                       end="${pagination.lastResult}" var="enzyme" varStatus="vsEnzymes">
                                    <%@ include file="summary.jspf"%>
                            </c:forEach>
                        </div>
                    </c:if>
                </section>
            </div>

    <%@include file="footer.jspf" %>
    
        </div> <!--! end of #wrapper -->

    </body>
</html>

