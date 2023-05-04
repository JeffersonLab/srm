<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Links"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <h3>General</h3>
            <ul>
                <li>
                    <a href="https://logbooks.jlab.org/">Logbooks</a>
                </li>
                <li>
                    <a href="https://tasklists.jlab.org/">Task Lists</a>
                </li>
                <li>
                    <a href="https://ace.jlab.org/cdn/doc/srm/Beamline.pdf">Beamline Map</a>
                </li>
                <li>
                    <a href="https://www.jlab.org/MEgroup/12GeVacc.html">Songsheets</a>
                </li>
                <li>
                    <a href="https://ace.jlab.org/cdn/doc/srm/ACResponsibilities.pdf">Hall
                        A &amp; C Responsibilities</a>
                </li>
                <li>
                    <a href="https://ace.jlab.org/cdn/doc/srm/BResponsibilities.pdf">Hall B Responsibilities</a>
                </li>
                <li>
                    <a href="http://opsweb.acc.jlab.org/abil/pro/">Accelerator Bypassed-Interlocks Log (ABIL) - onsite only</a>
                </li>
            </ul>
            <h3>Beamline Sketches (onsite only)</h3>
            <ul>
                <li>
                    <a href="http://cedweb.acc.jlab.org/zones/INJECTOR_ZONES/sketch">Injector</a>
                </li>
                <li>
                    <a href="http://cedweb.acc.jlab.org/zones/LINAC1/sketch">North LINAC</a>
                </li>
                <li>
                    <a href="http://cedweb.acc.jlab.org/zones/LINAC2/sketch">South LINAC</a>
                </li>
                <li>
                    <a href="http://cedweb.acc.jlab.org/zones/EAST_ARC/sketch">East Arc</a>
                </li>
                <li>
                    <a href="http://cedweb.acc.jlab.org/zones/WEST_ARC/sketch">West Arc</a>
                </li>
                <li>
                    <a href="http://cedweb.acc.jlab.org/zones/BSYS/sketch">BSY</a>
                </li>
                <li>
                    <a href="http://cedweb.acc.jlab.org/zones/HALLA_ENDSTATION/sketch">Hall A</a>
                </li>
                <li>
                    <a href="http://cedweb.acc.jlab.org/zones/HALLB_ENDSTATION/sketch">Hall B</a>
                </li>
                <li>
                    <a href="http://cedweb.acc.jlab.org/zones/HALLC_ENDSTATION/sketch">Hall C</a>
                </li>
                <li>
                    <a href="http://cedweb.acc.jlab.org/zones/HALLD_ENDSTATION/sketch">Hall D</a>
                </li>
                <li>
                    <a href="http://ledweb.acc.jlab.org/zones/LERF/sketch">LERF</a>
                </li>
            </ul>
        </section>
    </jsp:body>
</t:page>