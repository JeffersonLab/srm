package org.jlab.srm.presentation.controller;

import org.jlab.srm.business.params.SignoffParams;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.business.session.*;
import org.jlab.srm.persistence.entity.*;
import org.jlab.srm.persistence.model.SignoffStandIn;
import org.jlab.srm.presentation.params.SignoffUrlParamHandler;
import org.jlab.srm.presentation.util.FilterSelectionMessage;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@WebServlet(name = "Signoff", urlPatterns = {"/signoff"})
public class Signoff extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(
            Signoff.class.getName());

    @EJB
    BeamDestinationFacade destinationFacade;
    @EJB
    SystemFacade systemFacade;
    @EJB
    ComponentFacade componentFacade;
    @EJB
    ResponsibleGroupFacade groupFacade;
    @EJB
    RegionFacade regionFacade;
    @EJB
    StatusFacade statusFacade;
    @EJB
    GroupSignoffFacade signoffFacade;
    @EJB
    CategoryFacade categoryFacade;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SignoffUrlParamHandler paramHandler = new SignoffUrlParamHandler(request);

        SignoffParams params;

        if (paramHandler.qualified()) {
            params = paramHandler.convert();
            paramHandler.validate(params);
            paramHandler.store(params);
        } else {
            params = paramHandler.materialize();
            paramHandler.redirect(response, params);
            return;
        }

        BigInteger readyCascade = ParamConverter.convertBigInteger(request, "readyCascade");
        BigInteger checkedCascade = ParamConverter.convertBigInteger(request, "checkedCascade");

        if (readyCascade == null) {
            readyCascade = BigInteger.valueOf(50L);
        }

        Boolean systemFirst = null;

        try {
            systemFirst
                    = ParamUtil.convertAndValidateYNBoolean(request, "subsystemFirst",
                    false);
        } catch (Exception e) {
            throw new ServletException("Unable to parse boolean (subsystemFirst)");
        }

        List<BeamDestination> destinationList = destinationFacade.findAll(
                new OrderDirective("weight"));
        List<Region> regionList = regionFacade.findAll(new OrderDirective(
                "weight"));
        List<Status> statusList = statusFacade.findAll(new OrderDirective(
                "statusId"));

        List<SystemEntity> systemList;
        List<ResponsibleGroup> groupList;
        Category categoryRoot = null;
        SystemEntity selectedSystem = null;
        List<GroupResponsibility> groupResponsibilityList = null;

        if (params.getSystemId() != null) {
            selectedSystem = systemFacade.findWithResponsibilities(params.getSystemId());
        }

        // Special groupId = -1 means use last responsible group
        if (params.getGroupId() != null && params.getGroupId().longValue() == -1 && selectedSystem != null) {
            groupResponsibilityList = selectedSystem.getGroupResponsibilityList();
            if (groupResponsibilityList != null && !groupResponsibilityList.isEmpty()) {
                params.setGroupId(groupResponsibilityList.get(groupResponsibilityList.size() - 1).getGroup().getGroupId());
            }
        }

        if (systemFirst) {
            categoryRoot = categoryFacade.findBranch(null, BigInteger.ONE);
            systemList = systemFacade.findWithCategory(params.getCategoryId(), null, null,
                    BigInteger.ONE, true, false);
            groupList
                    = groupFacade.findBySystem(params.getSystemId());
        } else {
            systemList = systemFacade.findByGroup(params.getGroupId());
            groupList = groupFacade.findAll(new OrderDirective("name"));
        }

        ResponsibleGroup selectedGroup = null;
        List<Component> componentList = null;
        List<BeamDestination> selectedDestinationList = null;
        List<Region> selectedRegionList = null;
        List<Status> selectedStatusList = null;
        //Map<String, GroupSignoff> signoffMap = null;
        Map<String, SignoffStandIn> signoffMap = null;

        GroupResponsibility groupResponsibilityForSelected = null;

        if (params.getStatusIdArray() != null) {
            selectedStatusList = new ArrayList<>();

            for (BigInteger id : params.getStatusIdArray()) {
                if(id != null) {
                    selectedStatusList.add(Status.FROM_ID(id));
                }
            }
        }

        if (selectedSystem != null && params.getGroupId() != null) {

            selectedGroup = groupFacade.find(params.getGroupId());

            if (groupResponsibilityList != null) {
                for (GroupResponsibility responsibility : groupResponsibilityList) {
                    if (responsibility.getGroup().equals(selectedGroup)) {
                        groupResponsibilityForSelected = responsibility;
                        break;
                    }
                }
            }

            if (!request.isUserInRole("hcoadm") && groupResponsibilityForSelected != null
                    && groupResponsibilityForSelected.isChecklistRequired()
                    && !groupResponsibilityForSelected.isPublished()) {
                // Don't grab the list of components since it will be a waste, but set to non-null to avoid presentation thinking user didn't select criteria
                componentList = new ArrayList<>();
            } else {
                componentList
                        = componentFacade.findWithoutSignoff(params.getDestinationIdArray(),
                        params.getSystemId(), params.getRegionIdArray(), params.getGroupId(),
                        params.getStatusIdArray(),
                        params.isReadyTurn(), params.getComponentName(),
                        params.getMinLastModified(),
                        params.getMaxLastModified());
                if (componentList != null) {
                    //List<GroupSignoff> signoffList = signoffFacade.findByComponentList(componentList);
                    //signoffMap = signoffFacade.createComponentGroupToSignoffMap(signoffList);
                    List<SignoffStandIn> signoffList
                            = signoffFacade.findStandInListByComponentList(
                            componentList);
                    signoffMap
                            = signoffFacade.createComponentGroupToSignoffStandInMap(
                            signoffList);
                }
            }
        }

        String targetCsv = destinationFacade.filterTargetCsv(destinationList);

        if (params.getDestinationIdArray() != null) {
            selectedDestinationList = destinationFacade.findMultiple(params.getDestinationIdArray());
        }

        if (params.getRegionIdArray() != null) {
                selectedRegionList = regionFacade.findMultiple(params.getRegionIdArray());
        }

        String selectionMessage = "Select a group and subsystem to continue";

        if (componentList != null) {
            DecimalFormat formatter = new DecimalFormat("###,###");

            selectionMessage = "Components ";

            String filters = FilterSelectionMessage.getSignoffScreenMessage(
                    selectedGroup, null, selectedSystem, selectedDestinationList,
                    selectedRegionList, selectedStatusList, params.isReadyTurn(), null,
                    params.getComponentName(), params.getMinLastModified(),
                    params.getMaxLastModified());

            if (filters.length() > 0) {
                selectionMessage = filters;
            }

            selectionMessage = selectionMessage + " {" + formatter.format(componentList.size()) + "}";
        }

        statusList.remove(Status.MASKED);
        statusList.remove(Status.MASKED_CC);
        statusList.remove(Status.MASKED_ADMIN);

        request.setAttribute("targetCsv", targetCsv);
        request.setAttribute("selectionMessage", selectionMessage);
        request.setAttribute("selectedGroup", selectedGroup);
        request.setAttribute("selectedSystem", selectedSystem);
        request.setAttribute("selectedRegionList", selectedRegionList);
        request.setAttribute("statusIdArray", params.getStatusIdArray());
        request.setAttribute("destinationList", destinationList);
        request.setAttribute("systemList", systemList);
        request.setAttribute("componentList", componentList);
        request.setAttribute("regionList", regionList);
        request.setAttribute("groupList", groupList);
        request.setAttribute("statusList", statusList);
        request.setAttribute("groupResponsibilityForSelected",
                groupResponsibilityForSelected);
        request.setAttribute("signoffMap", signoffMap);
        request.setAttribute("categoryRoot", categoryRoot);
        request.setAttribute("readyCascade", readyCascade);
        request.setAttribute("checkedCascade", checkedCascade);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/signoff.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    /*@Override
     protected void doPost(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException {
     BigInteger destinationId = HcoParamConverter.convertBigInteger(request, "destinationId");
     BigInteger systemId = HcoParamConverter.convertBigInteger(request, "systemId");
     BigInteger regionId = HcoParamConverter.convertBigInteger(request, "regionId");
     BigInteger statusId = HcoParamConverter.convertBigInteger(request, "statusId");
     String comment = request.getParameter("comment");

     if (statusId == null) {
     throw new IllegalArgumentException("Please select status");
     }

     System.out.println("statusId: " + statusId);
     System.out.println("comment: " + comment);

     String[] componentIdArray = request.getParameterValues("componentId[]");
     String[] groupIdArray = request.getParameterValues("groupId[]");

     if (componentIdArray == null || groupIdArray == null) {
     throw new IllegalArgumentException("Please select group signoff");
     }

     System.out.println("componentIdArray.length: " + componentIdArray.length);
     System.out.println("groupIdArray.length: " + groupIdArray.length);

     if (componentIdArray.length != groupIdArray.length) {
     throw new IllegalArgumentException("componentId[] and groupId[] must be of same length");
     }

     List<BigInteger> componentIdList = new ArrayList<BigInteger>();
     List<BigInteger> groupIdList = new ArrayList<BigInteger>();

     fillInLists(componentIdList, groupIdList, componentIdArray, groupIdArray);

     try {
     signoffFacade.updateSignoff(componentIdList, groupIdList, Status.FROM_ID(statusId), comment, false);
     } catch (UserFriendlyException e) {
     throw new IllegalArgumentException("Unable to signoff", e);
     }

     LinkedHashMap<String, List<String>> paramMap = new LinkedHashMap<String, List<String>>();

     if (destinationId != null) {
     paramMap.put("destinationId", Arrays.asList(new String[]{destinationId.toString()}));
     }

     if (systemId != null) {
     paramMap.put("systemId", Arrays.asList(new String[]{systemId.toString()}));
     }

     if (regionId != null) {
     paramMap.put("regionId", Arrays.asList(new String[]{regionId.toString()}));
     }

     response.sendRedirect(response.encodeRedirectURL(
     request.getContextPath() + "/signoff" + ServletUtil.buildQueryString(paramMap, "UTF-8")));
     }

     private void fillInLists(List<BigInteger> componentIdList, List<BigInteger> groupIdList, String[] componentIdArray, String[] groupIdArray) {
     int i = 0;
     for (String componentIdStr : componentIdArray) {
     String groupIdStr = groupIdArray[i++];

     BigInteger componentId = toBigInteger(componentIdStr);
     BigInteger groupId = toBigInteger(groupIdStr);

     componentIdList.add(componentId);
     groupIdList.add(groupId);
     }
     }

     private BigInteger toBigInteger(String value) {
     if (value == null || value.isEmpty()) {
     throw new IllegalArgumentException("ID cannot be null");
     }

     return new BigInteger(value);
     }*/
}
