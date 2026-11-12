# Administration

[User Guide](README.md) › Administration

The **Setup** tab is where the inventory, the groups and the settings are maintained. It is
shown to **admins** and **branch admins** only.

## Who can do what

- An **admin** has the `srm-admin` role and can do everything in SRM.
- A **branch admin** administers one branch: a top-level category, such as a hall, with
  everything below it. A branch's admins are the users with the branch's **Admin Role**, which
  an admin sets on the [Category Tree](#category-tree). Any role of the user directory can be
  used, so a new branch needs no change to SRM.
- A **group leader** is a member of the role named as the group's **Leader Workgroup** on the
  [Group List](#group-list).
- A **crew chief** has the `cc` role.

Roles are managed in the lab's user directory, not in SRM. A change to who is an admin or a
branch admin takes effect the next time the person logs in. A change to who leads a group takes
effect when SRM's copy of the directory is refreshed: every night, or at once with **Clear** on
the [Directory Cache](#settings-and-directory-cache) page.

Branch admins see the whole inventory on the Setup pages, and SRM refuses with "Not authorized"
when they try to change something outside their branch. The pages they can use are Category
Tree, Component List, Component Part., System Part., Group Resp. and Bulk Signoff; the rest are
for admins.

Every change to a category, system or component is recorded, with who made it and when, in the
[Inventory Activity report](reports.md#inventory-activity).

## Category Tree

**Setup › Category Tree** shows the categories and systems, and adds, edits and removes them.
Components are on the [Component List](#component-list).

Under **Category** and **System**, **Add...**, **Edit...** and **Remove...** open a dialog. For
Edit and Remove, first choose the category or system; for Add and Edit, fill in:

| Field | Meaning |
|---|---|
| **Parent** | The category it goes under. A category can also go at the top level, which makes it a branch; only an admin can do that. |
| **Name** | Unique among all categories, or among all systems. |
| **Weight** | A whole number that orders it among its siblings: lower first; blank last, by name. |
| **Admin Role** | Top-level categories only, and admins only: the role whose members are the branch's admins. |
| **Description** | Systems only. Shown in the **System Information** dialog. |

Removing a system removes its components and its group responsibilities with it, and removing a
category removes its systems the same way, so SRM asks you to confirm. An admin can rename the
top of the tree with **Root...**.

## Component List

**Setup › Component List** adds and maintains components. Use the filter to find them, by **Beam
Destination**, **Category**, **System**, **Region**, **Group**, **Source**, **Masked**,
**Unpowered** or **Component** name (with `%` as a wildcard). The list can include components
of systems that don't take part in Readiness; see [System participation](#system-participation).

| Button | Does |
|---|---|
| **Add** | Adds a component: its **Name**, **System** and **Region**. Each of the system's responsible groups gets a Not Ready signoff for it. |
| **Bulk Add** | Adds several components to one system and region, one name to a line. If any name is refused, none are added. |
| **Rename** | Changes the name. The signoffs and their history stay with the component. |
| **Remove** | Deletes the component with its signoffs. It no longer appears in the reports, including the activity reports. |
| **(Un)mask** | Sets or removes a **Masked (Administrator)** mask, with a reason and an expiration. Removing the mask sets the component's signoffs to Checked. See [Masks](masks.md). |
| **(Un)power** | Marks the component [unpowered](concepts.md#unpowered-components), or powered again. Changing it sets the component's signoffs to Not Ready. |
| **Edit System** | Moves the component to another system. Groups responsible for both systems keep their signoffs; the others' signoffs are deleted, and groups new to the component start at Not Ready. |
| **Edit Region** | Changes the [region](concepts.md#regions). |
| **Edit Source** | Says where the component's record comes from: **INTERNAL**, or an element database (**CED**, **LED**, **UED**) with the element's ID there. The component's detail then links to that element. |
| **Edit Alias** | Sets another name the component is known by. The search box finds it, and the component's detail shows it. |

A name can't contain an asterisk, and must be unique within its system. For a component whose
source is an element database, SRM warns before a rename or a move, since the name and system
should follow the database.

## Component participation

**Setup › Component Part.** sets which [beam destinations](concepts.md#beam-destinations) each
component takes part in. It is a grid of components (rows) by destinations (columns), where a ✔
means the component is needed to send beam there.

Select a cell to switch it; it is saved at once. The grid shows five destinations and ten
components at a time: use the filters to choose the components and the destination columns, the
**Previous** and **Next** links to move through them, and **10 | 100 | 1000** to show more rows.

A component that takes part in no destination only shows on the other pages when their Beam
Destination filter is empty.

## System participation

**Setup › System Part.** sets which applications each system takes part in. SRM's inventory of
systems and components is shared with other applications, such as Downtime and Problem Report,
and a ✔ under **Readiness** is what makes a system part of SRM's readiness and signoffs. Select
a cell to switch it.

A system without the Readiness ✔, and its components, doesn't appear on the Readiness or Signoff
tabs or in the reports.

## Group responsibilities

**Setup › Group Resp.** sets, for one system, the ordered list of
[groups](concepts.md#responsible-groups) that sign off its components. Choose the system first.

- **Add** a group and say whether a **Checklist Required**. The group goes at the end of the
  order, and every component of the system gets a Not Ready signoff for it.
- **Edit** changes **Checklist Required**. When it is Yes, the group can't sign off the system
  until its [checklist](checklists.md#required-checklists) is published.
- **Remove** deletes the group's signoffs for the system's components. Their history stays in
  the activity reports.
- Drag a row by its handle to change the **Order**; it is saved at once. Existing signoffs don't
  change. The order decides [whose turn it is](signoff.md#whose-turn-it-is) and which groups a
  [downgrade cascades to](signoff.md#downgrades-and-cascades).

## Bulk Signoff

**Setup › Bulk Signoff** sets every signoff of one or more systems, for every responsible group
and every component that isn't masked, to the same status with the same comment. It is for
starting or resetting a checkout, for example setting a whole system to Not Ready at the start
of a maintenance period.

Choose the **System** or systems, the **Status** (Ready, Checked or Not Ready) and a
**Comment**, and select **Signoff**. SRM works through the systems one at a time and shows each
as Queued, Working, Done or Failed.

Bulk signoff skips the usual [signoff rules](signoff.md#signoff-rules): a signoff can go
straight from Not Ready to Ready, required checklists aren't checked, and downgrades don't
cascade. Setting Ready still needs an admin, a leader of each group, or an admin of the system's
branch. There is no undo, short of another bulk signoff.

## Region List, Group List, Beam Destination List and Status List

These are for admins.

### Region List

The [regions](concepts.md#regions) in order, with their aliases. The list is read-only: regions
are added and changed in the database.

### Group List

The responsible groups, including archived ones. **Add** or **Edit** a group's:

| Field | Meaning |
|---|---|
| **Name** | Unique. |
| **Description** | Shown in the **Group Information** dialog. |
| **Leader Workgroup** | The role in the user directory whose members are the group's leaders. |
| **Archived** | Hides the group from the lists where a group is chosen, such as the Signoff tab and the Checklists tab. |

A group can't be removed here; archive it instead.

### Beam Destination List

The [beam destinations](concepts.md#beam-destinations) in order, with a **Target** checkbox for
each. The targets are the **Current Run** destinations: what most pages start with, and what
the **Current Run** link beside a Beam Destination filter chooses. Tick or untick a checkbox to
change them, at the start of each run; it is saved at once. The **Facility** filter shows one
facility's destinations, and **No Target** clears the targets shown.

Destinations themselves are added and changed in the database.

### Status List

The [statuses](concepts.md#statuses), in order from best to worst. Read-only.

## Saved Signoffs

**Setup › Saved Signoffs**, for admins, keeps signoffs that are made again and again, so they
can be repeated with a few clicks. The main use is the forced re-check at the end of a run:
downgrading each group's Ready signoffs to Checked, system by system, so that the groups look at
their components again.

A saved signoff is a filter, as on the Signoff tab, with the status and comment to apply:

| Field | Meaning |
|---|---|
| **Signoff Type** | A label for grouping them: CEBAF or LERF. With **Autofill name and comments**, the name and comment are made from the group, system and type. |
| **Signoff Name** | Unique. |
| **Status**, **Comments** | What to set. With **Downgrade Only**, the choices are Checked and Not Ready. |
| **Group**, **System** | Whose signoffs, on which system. |
| **Region**, **Status**, **Component Name** | Optional: only the components in a region, only the signoffs with a given status now, or only the names that match. |

To make one, select **Add**; or set up the filter on the [Signoff](signoff.md) tab and select
**Save Named Signoff** there. A saved signoff can't be edited: remove it and add it again.

To use them:

- Select one or more rows and select **Signoff** to make them all at once. The normal
  [signoff rules](signoff.md#signoff-rules) apply, and a downgrade cascades: later groups' Ready
  signoffs become Checked.
- Or select the **🔗** button on a row to open the Signoff tab with that saved signoff's filter,
  status and comment filled in, to review the components before saving. The **Max Last Modified
  Date** set on this page is applied too: set it to the start of the maintenance period, so
  that signoffs made since then aren't downgraded.

To downgrade Ready signoffs to Checked without upgrading the ones that are Not Ready, set the
filter's **Status** to Ready; **Downgrade Only** does that for you.

## Email

SRM sends three kinds of email:

| Email | Sent | To |
|---|---|---|
| **Mask request** | When someone [requests a mask](masks.md#requesting-a-mask). | The addresses in the `EMAIL_MASK_REQUEST_LIST` setting. |
| **Activity report** | Daily at 7:00, when `EMAIL_ACTIVITY_ENABLED` is on. | The addresses in the `EMAIL_ACTIVITY_LIST` setting. |
| **Group action report** | Daily at 7:00, when `EMAIL_LEADERS_ENABLED` is on. | The leaders of each group. |

The two daily reports cover the day before, from 7:00 to 7:00. The activity report lists the
signoff changes that left a component Checked or Not Ready: the new outstanding work. The group
report lists the same for one group, each with a link to the Signoff tab for that group and
system. A report with nothing in it isn't sent.

Feedback from the Help tab's form is emailed to the admins.

**Setup › Email**, for admins, shows who receives each email, shows whether the daily emails are
on, and has a **Preview** of each report. **Send Now** sends a report at once, and **Send To
All** sends every group its report. The recipients and the on/off switches are
[settings](#settings-and-directory-cache).

## Settings and Directory Cache

**Setup › Settings** lists the settings, by tag, and an admin can **Edit** a value. A change
takes effect at once unless the setting's description says the app must be redeployed.

| Setting | Meaning |
|---|---|
| `EMAIL_MASK_REQUEST_LIST` | Who is emailed about mask requests: addresses, separated by commas. |
| `EMAIL_ACTIVITY_ENABLED`, `EMAIL_ACTIVITY_LIST` | Whether the daily activity report is sent, and to whom. |
| `EMAIL_LEADERS_ENABLED` | Whether the daily group action reports are sent to group leaders. |
| `EMAIL_ENABLED`, `EMAIL_TESTING_ENABLED`, `EMAIL_SENDER_ADDRESS` | For the Help tab's feedback form: whether it sends email, whether it sends to the test group instead of the admins, and the sender's address. |
| `HELP_CONTENT_CONTACT`, `HELP_TECHNICAL_CONTACT`, `HELP_DOC_CSV` | The contacts and the documentation links on the Help tab. Each documentation link is `URL|Label`. |
| `NOTIFICATION_ENABLED`, `NOTIFICATION_MESSAGE`, `NOTIFICATION_LINK_NAME`, `NOTIFICATION_LINK_URL` | A banner across the top of every page, such as a notice of maintenance, with an optional link. |
| `IP_READ_FILTER_ENABLED`, `IP_READ_ALLOWLIST_PATTERN`, `IP_READ_URL_PATTERN` | Whether viewing without a login is limited to some network addresses, and which. |
| `ADMIN_ROLE_NAME` | The role that can edit settings, is listed on the Help tab and receives feedback. |
| `SMOOTHNESS_CDN_ENABLED`, `SMOOTHNESS_SERVER` | Where shared page resources are served from. |

Values changed directly in the database take effect after **Refresh Cache**.

**Setup › Directory Cache** shows the users and roles SRM has looked up in the user directory,
which it keeps until midnight. Select **Clear** after someone is added to or removed from a
group's leaders, so that it takes effect without waiting for the night.

Server settings, such as the logbook server and which logbooks receive problem reports, are
environment variables; see the [README](https://github.com/JeffersonLab/srm#configure).

## Help tab

The **Help** tab shows the release version and date, the content and technical contacts, the
admins (when you are logged in), links to documentation, including this guide, and a feedback
form that emails the admins.
