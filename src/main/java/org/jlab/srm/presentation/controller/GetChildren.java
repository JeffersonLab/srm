package org.jlab.srm.presentation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.srm.business.session.ComponentTreeFacade;
import org.jlab.srm.persistence.enumeration.HcoNodeType;
import org.jlab.srm.persistence.model.HcoNodeData;
import org.jlab.srm.persistence.model.TreeNode;

/**
 * @author ryans
 */
@WebServlet(
    name = "GetChildren",
    urlPatterns = {"/get-children"})
public class GetChildren extends HttpServlet {

  private static final Logger logger = Logger.getLogger(GetChildren.class.getName());

  @EJB ComponentTreeFacade treeFacade;

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String idStr = request.getParameter("id");

    BigInteger nodeId = null;
    HcoNodeType type = null;

    if (idStr != null) {
      String[] tokens = idStr.split("-");
      if (tokens.length == 3) {
        type = HcoNodeType.valueOf(tokens[1]);
        nodeId = new BigInteger(tokens[2]);
      }
    }

    BigInteger[] destinationIdArray =
        ParamConverter.convertBigIntegerArray(request, "destinationId");
    BigInteger categoryId = ParamConverter.convertBigInteger(request, "categoryId");
    BigInteger systemId = ParamConverter.convertBigInteger(request, "systemId");
    BigInteger regionId = ParamConverter.convertBigInteger(request, "regionId");
    BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");
    BigInteger[] statusIdArray = ParamConverter.convertBigIntegerArray(request, "statusId");

    List<TreeNode<HcoNodeData>> children;

    boolean rootNode = false;

    if (nodeId != null && type != null) {
      children =
          treeFacade.findChildren(
              type,
              nodeId,
              destinationIdArray,
              categoryId,
              systemId,
              regionId,
              groupId,
              statusIdArray);
    } else {
      rootNode = true;

      children = new ArrayList<>();

      children.add(
          treeFacade.findRoot(
              destinationIdArray, categoryId, systemId, regionId, groupId, statusIdArray));
    }

    response.setContentType("application/json");

    PrintWriter pw = response.getWriter();

    JsonArrayBuilder json = Json.createArrayBuilder();

    if (children != null) {
      for (TreeNode<HcoNodeData> node : children) {
        JsonObjectBuilder nodeJson = Json.createObjectBuilder();
        String nodeIdStr = "node-" + node.getData().getType().name() + "-" + node.getData().getId();

        /*Group nodes are NOT unique without this extra bit - parent component ID*/
        if (node.getData().getType().equals(HcoNodeType.GROUP)) {
          nodeIdStr = nodeIdStr + "-" + nodeId;
        }

        nodeJson.add("id", nodeIdStr);
        nodeJson.add("text", node.getData().getName());
        nodeJson.add("type", node.getData().getType().name());
        nodeJson.add("children", node.getData().isLazyChildren());

        if (rootNode) {
          JsonObjectBuilder stateJson = Json.createObjectBuilder();
          stateJson.add("opened", true);
          nodeJson.add("state", stateJson);
        }

        JsonObjectBuilder attrJson = Json.createObjectBuilder();
        attrJson.add("data-node-id", node.getData().getId());
        attrJson.add("data-node-type", node.getData().getType().name());
        attrJson.add("data-status", node.getData().getStatus().getName());
        nodeJson.add("li_attr", attrJson);
        json.add(nodeJson);
      }
    }

    pw.write(json.build().toString());

    pw.flush();

    boolean error = pw.checkError();

    if (error) {
      logger.log(Level.SEVERE, "PrintWriter Error");
    }
  }
}
