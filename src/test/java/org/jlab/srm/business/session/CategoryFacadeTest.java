package org.jlab.srm.business.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.junit.jupiter.api.Test;

/** The admin role saved with a category: only an admin sets it, and only on a branch. */
class CategoryFacadeTest {

  @Test
  void adminSetsChangesAndRemovesBranchAdminRole() throws UserFriendlyException {
    assertEquals("vtaadm", CategoryFacade.adminRoleToSave(" vtaadm ", null, true, true));
    assertEquals("cryoadm", CategoryFacade.adminRoleToSave("cryoadm", "vtaadm", true, true));
    assertNull(CategoryFacade.adminRoleToSave("", "vtaadm", true, true));
    assertNull(CategoryFacade.adminRoleToSave("  ", "vtaadm", true, true));
  }

  @Test
  void absentRoleKeepsBranchAdminRole() throws UserFriendlyException {
    assertEquals("vtaadm", CategoryFacade.adminRoleToSave(null, "vtaadm", true, true));
    assertEquals("vtaadm", CategoryFacade.adminRoleToSave(null, "vtaadm", true, false));
    assertNull(CategoryFacade.adminRoleToSave(null, null, true, false));
  }

  @Test
  void branchAdminCannotChangeAdminRole() throws UserFriendlyException {
    // Unchanged, as when a form sends the role back
    assertEquals("vtaadm", CategoryFacade.adminRoleToSave("vtaadm", "vtaadm", true, false));

    assertThrows(
        UserFriendlyException.class,
        () -> CategoryFacade.adminRoleToSave("cryoadm", "vtaadm", true, false));
    assertThrows(
        UserFriendlyException.class,
        () -> CategoryFacade.adminRoleToSave("", "vtaadm", true, false));
    assertThrows(
        UserFriendlyException.class,
        () -> CategoryFacade.adminRoleToSave("vtaadm", null, true, false));
  }

  @Test
  void onlyTopLevelCategoryHasAdminRole() throws UserFriendlyException {
    assertThrows(
        UserFriendlyException.class,
        () -> CategoryFacade.adminRoleToSave("vtaadm", null, false, true));

    // A branch moved under another category loses its role, whoever moves it
    assertNull(CategoryFacade.adminRoleToSave(null, "vtaadm", false, true));
    assertNull(CategoryFacade.adminRoleToSave("", "vtaadm", false, false));
  }

  @Test
  void tooLongRoleIsRejected() throws UserFriendlyException {
    String longest = "r".repeat(CategoryFacade.ADMIN_ROLE_MAX_LENGTH);

    assertEquals(longest, CategoryFacade.adminRoleToSave(longest, null, true, true));
    assertThrows(
        UserFriendlyException.class,
        () -> CategoryFacade.adminRoleToSave(longest + "r", null, true, true));
  }
}
