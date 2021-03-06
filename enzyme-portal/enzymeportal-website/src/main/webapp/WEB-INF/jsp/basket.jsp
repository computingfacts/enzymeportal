<%--
    Document   : basket
    Created on : Oct 2, 2017, 3:46:29 PM
    Author     : <a href="mailto:joseph@ebi.ac.uk">Joseph</a>
--%>

<!--<?xml version="1.0" encoding="UTF-8" ?>-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="epfn" uri="/WEB-INF/epTagLibray.tld" %>

<!DOCTYPE html>
<html>
    <c:set var="pageTitle" value="Compare enzymes"/>
    <%@include file="head.jspf"%>
    <body class="level2">

        <%@include file="skipto.jspf" %>

        <div id="wrapper">

            <%@include file="header.jspf" %>

            <div id="content" role="main" class="clearfix">

                <section>
                    <h2>Basket - Compare enzymes</h2>
                </section>
                <br clear="all"/>

                <form action="compare" method="post" style="font-family: verdana, sans-serif;">
                    <div class="row">
                        <section class="large-12 columns">
                            <div id="basketEmptyMsg" style="display: ${empty basket? 'block':'none'}">
                                <img src="/enzymeportal/resources/images/compare-basket.png">
                            </div>
                            <div id="basketOneMsg"
                                 style="display: ${fn:length(basket) eq 1? 'block':'none'}">
                                You have selected only one enzyme. Please select at least one more in order to compare them.
                            </div>
                            <div id="basketFullMsg"
                                 style="display: ${fn:length(basket) gt 1? 'block':'none'}">
                                You have <span class="basketSize">${fn:length(basket)}</span> enzymes to compare. Please select the species for two of them before proceeding.                  
                            </div>
                        </section>
                        <section class="large-3 columns">
                            <c:if test="${fn:length(basket) gt 1}">
                                <button id="compareButton" disabled
                                        class="button icon icon-functional"
                                        data-icon="O"> Compare selected</button>
                            </c:if>
                        </section>
                        <section class="large-1 columns">
                            <%-- Empty --%>
                        </section>
                    </div>
               

                    <div class="row">
                        <c:forEach var="basketItem" items="${basket}" varStatus="vsEnzymes">
                            <c:set var="enzyme" value="${basketItem.value}"/>
                            <div>

                                <section class="large-8 columns">
                                  
                                    <%@include file="util/prioritiseSpecies.jsp" %>
                                  
                                    <c:set var="showCheckbox" value="false"/>
                                    <%@include file="summary_basket.jspf" %>
                                </section>
                                <section class="large-3 columns">
                                    <select class="toCompare" name="acc"
                                            onchange="updateCompareButton(event)"
                                            title="Select one species to compare.">
                                        <c:forEach var="relSp" items="${enzyme.relatedspecies}">
                                            <option value="${relSp.accession}"
                                                    title="${relSp.species.commonname}">${relSp.species.scientificname}</option>
                                        </c:forEach>
                                        <option selected value=""
                                                title="Deselect this enzyme"></option>
                                    </select>
                                </section>
                                <section class="large-1 columns">
                                    <button class="removeFromBasket icon icon-functional"
                                            title="Remove this summary from your basket."
                                            data-icon="d"
                                            value="${enzyme.proteinView.accession}"
                                            onclick="removeFromBasket(event)"></button>
                                </section>

                            </div>
                        </c:forEach>
                    </div>

                    <script>
                        $(document).ready(function () {
                            updateCompareButton();
                        });
                    </script>

                </form>

            </div>

            <%@include file="footer.jspf" %>

        </div>
    </body>
</html>
