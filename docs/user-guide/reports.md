# Reports

[User Guide](README.md) › Reports

The **Reports** tab has ten reports, listed in the menu on the left. Anyone can read them
without logging in, except Inventory Activity, which is for admins and branch admins.

| Report | Answers |
|---|---|
| [Overall Status](#overall-status) | How much of the machine is signed off? |
| [Group Status](#group-status) | How far along is each group? |
| [Signoff Summary](#signoff-summary) | How many signoffs changed in a period, and which way? |
| [Signoff](#signoff) | What is the state of each group's signoff on each component? |
| [Component](#component) | Which components have a given status, and what is known about one component? |
| [Group Responsibility](#group-responsibility) | Which groups sign off which systems, and are their checklists published? |
| [Group Leader](#group-leader) | Who leads each group? |
| [Signoff Activity](#signoff-activity) | Who changed which signoff, when and why? |
| [Inventory Activity](#inventory-activity) | Who changed the inventory? |
| [All Activity](#all-activity) | What happened, signoffs and inventory together? |

## Using the reports

**Choose...** opens a report's filter, and **Apply** runs it. **Clear** empties the filter;
where there is a **Reset**, it goes back to the report's starting filter. The line under the
title says what is selected and, in braces, how many rows match. Lists show 100 rows a page.

- **Beam Destination** keeps the components that take part in any of the chosen destinations.
  Most reports start with the current run's destinations chosen; the **Current Run** link
  chooses them again. Some reports can also **Exclude** destinations: that leaves out the
  components that take part in any of those.
- **Category** covers the systems in the category and in the categories below it, and narrows
  the **System** list.
- **Component** matches a whole name unless you use `%` as a wildcard: `MQB%` finds the names
  that start with MQB, and `%2L%` those that contain 2L.
- **Date Range** offers periods such as **Today (from 7:00)**, **Past 7 Days**, **Current CC
  Shift** and **Previous Month**; **Custom...** takes a start (included) and an end (not
  included). Days start at 7:00.

A report's address includes its filter, so a report can be bookmarked or sent to someone as it
is. SRM also remembers the last filter you used on each report while your browser session lasts.

Where a report has them, **Full Screen** shows the report alone, for a wall display or a
meeting, and **Export** offers **Image** (a picture of the report) and **Print**.

How the reports count:

- A **signoff** is one group's status for one component. A component with three responsible
  groups has three signoffs, so the status reports count signoffs, not components.
- A signoff that was never made is Not Ready.
- The status charts leave out [masked](masks.md) components and Not Applicable signoffs.
- [Unpowered](concepts.md#unpowered-components) components count like any others.

## Overall Status

A pie chart of the signoffs that are Ready, Checked and Not Ready, with the count and percentage
of each and the total. This is the progress of a hot checkout at a glance: filter it by **Beam
Destination**, **Category**, **System**, **Region** or **Group** to see the progress toward one
destination or of one part of the machine.

## Group Status

The same counts for each responsible group: the group's total signoffs, how many are Ready,
Checked and Not Ready, and each as a percentage, with a small pie chart. Under **Display**,
choose **Bar** to see the groups as stacked bars instead of a table.

Select a count to open the [Signoff report](#signoff) with those signoffs listed.

## Signoff Summary

How many signoffs changed during a period, starting with yesterday 7:00 to today 7:00. **Change
Count** has a row for each kind of change, and **Change Count By Group** has the same counts for
each group that had any:

| Change | Meaning |
|---|---|
| **Upgrade Ready** | Signed off as Ready. |
| **Upgrade Checked** | Raised from Not Ready to Checked. |
| **Downgrade Checked** | Lowered from Ready to Checked. |
| **Downgrade Not Ready** | Lowered to Not Ready. |
| **Cascade** | Lowered to Checked because an earlier group downgraded; see [cascades](signoff.md#downgrades-and-cascades). |
| **Comment** | A comment added without changing the status. |

Select a count to open [Signoff Activity](#signoff-activity) with those changes listed.

## Signoff

The current state of every signoff: **Component**, **Group**, **Modified Date**, **Modified By**,
**Status** and **Comments**, 100 to a page. Beyond the common filters it has:

- **Status**: one or more of Not Applicable, Ready, Checked and Not Ready.
- **Ready Turn**: **Yes** lists the signoffs that are waiting on their own group, because the
  groups before it are done; see [Whose turn it is](signoff.md#whose-turn-it-is).
- **Masked**: **Yes** or **No** to list only the signoffs of masked or unmasked components.

From a row, the component's name opens its **Component Information**, the status opens the
[Signoff tab](signoff.md) at that component and group, and the comment icon opens the signoff's
history.

**Export › Excel** downloads every row that matches the filter, not just the page shown.

## Component

Lists components with their **System**, **Region** and overall **Status**. A component's status
is the worst of its groups' signoffs, or its mask; see [Statuses](concepts.md#statuses). Filter
by **Status** to list, for example, everything that is Not Ready for a destination, or by
**Unpowered** to list the unpowered components.

From a row, the name opens the **Component Information** dialog, the status opens the
[Signoff tab](signoff.md) at that component, and the **→** button opens the component's own
page.

### Component detail

The **Component Information** dialog, and the page with the same content, shows what SRM knows
about one component. It opens from a component's name almost anywhere in SRM and from the search
box at the top of every page.

| Field | Shows |
|---|---|
| **Alias** | Another name the component is known by, if it has one. |
| **Group Signoff** | Each responsible group in signoff order, with its status, when and by whom it was last changed, and the comment. **Signoff** goes to the Signoff tab for that group; **Activity** shows that signoff's history. |
| **Group Responsibilities** | The groups again, each with its **Group Information** and, when published, its [checklist](checklists.md). |
| **Category**, **System**, **Region** | Where the component is in the inventory and in the machine. |
| **Beam Destinations** | The [destinations](concepts.md#beam-destinations) the component takes part in. |
| **Unpowered** | Whether it is [unpowered](concepts.md#unpowered-components). |
| **Masked** | Whether it is [masked](masks.md) and, if so, when, by whom, the kind of mask, the reason and the expiration. |
| **Source** | Where the component's record comes from: **INTERNAL** when it was entered in SRM, or a link to the element in the CEBAF, LERF or UITF element database. |
| **Added Date** | When it was added to SRM. **History** opens its [inventory history](#inventory-activity), for admins. |

Admins and the branch's admins also get a **Modify** link to the component on
[Setup › Component List](administration.md#component-list).

## Group Responsibility

**Group Resp.** in the menu. Which groups are responsible for which systems: **Group**,
**System**, **Checklist Required**, a link to **View** the checklist, **Checklist Published**
(with who published it and when) and **Checklist Modified Date**.

Set **Checklist Missing** to **Yes** to list the responsibilities that require a checklist and
don't have a published one: these are the groups that can't sign off yet. See
[Required checklists](checklists.md#required-checklists).

## Group Leader

The leaders of each group, by last name, first name and username. Leaders are the people who can
sign off as Ready for the group; they come from the lab's user directory, through the role set
for the group on [Setup › Group List](administration.md#group-list).

## Signoff Activity

The history of signoffs, newest first, one row for each component, group and change: **Modified
Date**, **Modified By**, **Change and Status** (such as "Upgrade; Ready"), **Component**,
**Group** and **Comments**. It starts with yesterday 7:00 to today 7:00 for the current run's
destinations. Beyond the common filters it has **User**, **Status** (the status the change set)
and **Change**:

| Change | Meaning |
|---|---|
| **Upgrade** | The signoff moved toward Ready: Not Ready to Checked, or Checked to Ready. A change to Not Applicable is also an upgrade. |
| **Downgrade** | The signoff moved away from Ready, or out of Not Applicable. |
| **Cascade** | The signoff was lowered because an earlier group downgraded. |
| **Comment** | A comment was added and the status stayed the same. |

Masking a component shows here as a Comment on each of its signoffs, with the mask as the status
and "Masked" as the comment; unmasking shows as an upgrade or downgrade of each.

The history of a component that has been deleted isn't shown.

## Inventory Activity

For admins and branch admins: the record of changes to the inventory. **Transactions** lists
each saved change, newest first, with its ID, date, who made it, from which computer, and the
changes in it, such as "MOD Component 0L03" (ADD, MOD or DEL of a Component, System or
Category). Select a change to see that item's revisions side by side, oldest first, with every
field as it was at each revision.

The **Component**, **System** and **Category** links at the top open the same revision pages by
ID. The **History** link on a [component's detail](#component-detail) is the easy way in for a
component.

Signoffs aren't part of the inventory; their history is in [Signoff Activity](#signoff-activity).

## All Activity

One feed, newest first, of signoff changes and inventory changes together. Signoffs made
together (the same person, minute, system, group, status and comment) are one entry, with a link
such as **12 Components** to the individual rows in Signoff Activity.

It has the filters of Signoff Activity, with three kinds of status: **Action Status** is the
status a signoff set, **Component Status** is the component's status now, and **Group Status**
is the status now of the chosen **Group**'s signoff.

Each entry names its kind of change:

| Change | Meaning |
|---|---|
| `UPGRADE`, `DOWNGRADE`, `CASCADE`, `COMMENT` | A signoff change, as in [Signoff Activity](#signoff-activity). |
| `ADD_COMPONENT`, `DELETE_COMPONENT`, `RENAME_COMPONENT` | A component was added, deleted or renamed. |
| `COMPONENT_SYSTEM`, `MODIFY_REGION` | A component moved to another system or region. |
| `UNPOWER`, `POWER` | A component was marked unpowered, or powered again. |
| `MASK`, `UNMASK` | A component was masked or unmasked; the entry shows the reason. |
| `LINK`, `UNLINK` | A component was tied to an element database record, or made internal again. |
| `ADD_SYSTEM`, `DELETE_SYSTEM`, `RENAME_SYSTEM`, `REORGANIZE_SYSTEM` | A system was added, deleted, renamed or moved to another category. |
| `ADD_CATEGORY`, `DELETE_CATEGORY`, `RENAME_CATEGORY`, `REORGANIZE_CATEGORY` | A category was added, deleted, renamed or moved. |

Changes to systems and categories aren't tied to a component, so most of the filters hide them,
including the current run's destinations that the report starts with. **Clear** the filter to
see them.
