package org.jlab.srm.business.session;

import org.jlab.srm.persistence.entity.ResponsibleGroup;
import org.jlab.srm.persistence.entity.Staff;
import org.jlab.smoothness.business.exception.UserFriendlyException;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigInteger;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles({"hcoadm"})
public class GroupLeaderFacade {

    @EJB
    ResponsibleGroupFacade groupFacade;
    @EJB
    StaffFacade staffFacade;

    @RolesAllowed("hcoadm")
    public void add(BigInteger groupId, String username) throws UserFriendlyException {
        if (groupId == null) {
            throw new UserFriendlyException("groupId cannot be null");
        }

        if (username == null || username.isEmpty()) {
            throw new UserFriendlyException("username cannot be null");
        }

        ResponsibleGroup group = groupFacade.find(groupId);
        Staff staff = staffFacade.findByUsername(username);

        if (group == null) {
            throw new UserFriendlyException("Group not found: " + groupId);
        }

        if (staff == null) {
            throw new UserFriendlyException("Staff not found: " + username);
        }

        List<Staff> leaderList = group.getLeaderWorkgroup().getStaffList();

        if (leaderList.contains(staff)) {
            throw new UserFriendlyException("User " + staff.getUsername() + " already is a leader for group " + group.getName());
        }

        leaderList.add(staff);
    }

    @RolesAllowed("hcoadm")
    public void remove(BigInteger groupId, String username) throws UserFriendlyException {
        if (groupId == null) {
            throw new UserFriendlyException("groupId cannot be null");
        }

        if (username == null || username.isEmpty()) {
            throw new UserFriendlyException("username cannot be null");
        }

        ResponsibleGroup group = groupFacade.find(groupId);
        Staff staff = staffFacade.findByUsername(username);

        if (group == null) {
            throw new UserFriendlyException("Group not found: " + groupId);
        }

        if (staff == null) {
            throw new UserFriendlyException("Staff not found: " + username);
        }

        List<Staff> leaderList = group.getLeaderWorkgroup().getStaffList();

        if (!leaderList.contains(staff)) {
            throw new UserFriendlyException("User " + staff.getUsername() + " is not currently a leader for group " + group.getName());
        }

        leaderList.remove(staff);
    }
}
