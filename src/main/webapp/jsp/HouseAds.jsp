<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<div class="ctsrch_boxContainer">
    <div class="ctsrch_header" style="">
        <div class="ctsrch_headerText">More to Try</div>
    </div>
    <div class="ctsrch_container">
        <s:iterator value="houseAds" status="hadStatus">
            <div class="ctsrch_leftSide"></div>
            <div class="ctsrch_rightSide">
                <div class="ctsrch_mainLink">
                    <a href='<s:property value="destinationUrl" />'><s:property value="title" /></a>
                </div>
                <div class="ctsrch_descFont">
                    <s:property value="tagLine" />
                </div>
                <div class="ctsrch_displayUrl" >
                    <a href='<s:property value="destinationUrl" />'><s:property value="displayUrl" /></a>
                </div>
            </div>
            <s:if test="%{#hadStatus.index < (houseAds.size() - 1)}">
                  <div class="ctsrch_lineMargin"> </div>
              </s:if>
        </s:iterator>
    </div>
</div>