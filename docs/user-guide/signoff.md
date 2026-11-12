# Signing off

[User Guide](README.md) › Signing off

A **signoff** is one [responsible group](concepts.md#responsible-groups)'s statement about one
component: Not Ready, Checked, Ready or Not Applicable. The **Signoff** tab is where a group
works through the components of a system. Anyone can view it; log in to make changes.

## Finding your components

The page asks for two things before it lists anything:

1. **Group**: the group you are signing off for.
2. **System**: the list narrows to the systems that group is responsible for.

Select **Apply** and the grid shows the system's components that match. To pick the system first,
tick **Start with Category/System**: choose a category (optional) and a system, and the Group
list narrows to the groups responsible for it. The **All** link beside System points to the
[Readiness](readiness.md) tab and the [Signoff report](reports.md#signoff), which show every
system at once.

Step 3 of the filter is optional and narrows the components:

| Filter | Keeps components |
|---|---|
| **Beam Destination** | that take part in any of the chosen destinations. **Current Run** chooses the current run's destinations. |
| **Region** | in any of the chosen regions. |
| **Status** | where the selected group's signoff is one of the chosen statuses. **Exclude N/A** chooses Ready, Checked and Not Ready. |
| **Ready Turn** | **Yes**: where it is the selected group's [turn](#whose-turn-it-is). **No**: where it isn't. |
| **Component** | whose name matches; use `%` as a wildcard, such as `MQB1A%`. |
| **Min Last Modified**, **Max Last Modified** | where the selected group's signoff was last changed in that time range. |

The filter is part of the page's address, so a filtered Signoff page can be bookmarked or sent
to a colleague.

## The grid

Each row is a component, and each column is one of the system's responsible groups, numbered in
signoff order. The selected group's column is highlighted; the other columns are there so you
can see where the component stands with the groups before and after yours.

- A cell's icon is that group's status for the component. Hover over it for the comment, who
  changed it, when, and the kind of change.
- An icon before a component's name means it is [masked](masks.md), and a ★ after the name
  means it is [unpowered](concepts.md#unpowered-components).
- Select a component's name for its **Component Information** dialog, or a group's name in the
  heading for the group's description and leaders.
- Tick **Show Comments** to show each signoff's comment in the grid. The icon beside a comment
  opens that signoff's history.

If the page says a checklist "must be published before signoffs can be made", the group's
[checklist](checklists.md) for this system is required and not published yet; publish it first.

## Changing signoffs

1. Select the cells to change, in the selected group's column: select one, hold **Ctrl**
   (**⌘** on a Mac) to add more, or drag across several. The icon in the column's heading selects
   the whole column. **Unselect All** starts over.
2. Select **Edit Signoff**. The dialog lists the group and the components.
3. Choose the **Status**, and write a **Comment** saying what you checked or what is wrong.
4. Select **Save**. Every selected component gets the same status and comment, at the same time.

You sign off for one group at a time. To leave the status as it is and only add a note, choose
the status the signoff already has and write the comment.

### Signoff rules

| To set | You must be | And |
|---|---|---|
| **Checked** or **Not Ready** | logged in | |
| **Ready** | a leader of the group, an admin, or an admin of the component's [branch](concepts.md#roles) | the signoff must be Checked first: Not Ready can't go straight to Ready. |
| **Not Applicable** | a leader of the group, an admin, or an admin of the component's branch | at least one other group must have something other than Not Applicable for the component. |

SRM doesn't check which group you belong to when you set Checked or Not Ready, so anyone who
finds a problem can downgrade a component. Sign off only for your own group otherwise.

SRM also refuses:

- a signoff on a component with a Director or Crew Chief [mask](masks.md). The mask has to be
  removed first;
- a signoff for a group whose required [checklist](checklists.md) isn't published, unless you are
  an admin;
- a save that changes nothing: the same status needs a comment.

### Whose turn it is

A system's groups sign off in order, each building on the one before: for example the group that
installs a device, then the group that powers it, then the group that controls it. It is a
group's **turn** on a component when the groups before it are Ready (or Not Applicable) and its
own signoff is still Not Ready or Checked. The **Ready Turn** filter, here and on the
[Signoff report](reports.md#signoff), lists those components.

The order is guidance: SRM doesn't stop a group from signing off before its turn.

### Downgrades and cascades

Downgrade a signoff, to Checked or Not Ready, whenever the component stops being ready: it broke,
it is being worked on, or what you verified no longer holds. Operations relies on this to know
the state of the machine from day to day; see the [FAQ](faq.md#when-should-i-downgrade-a-component).

A downgrade can also pull down the groups after yours, since what they verified rested on your
signoff. This is a **cascade**, and **Downgrade Options...** under the grid sets how far it goes:

| Option | Choices | Default |
|---|---|---|
| **Ready Cascades To** | nothing, Checked, Not Ready | Checked |
| **Checked Cascades To** | nothing, Not Ready | nothing |

With the defaults, downgrading your signoff turns every later group's Ready into Checked, so
those groups look again and confirm, and leaves their Checked signoffs alone. The cascaded
signoffs get your comment and show as a Cascade in the
[activity reports](reports.md#signoff-activity). A downgrade that cascades needs a comment.
Groups before yours are never changed.

### Needs Attention

Tick **Needs Attention (Create OPS-PR)** to also post an operations problem report to the
electronic logbook, with your comment as its text, for a problem that Operations needs to follow
up. SRM shows a link to the new entry. Without the tick, the signoff is recorded in SRM only.

## Requesting a mask

When a component can't be made ready and the program can run without it for now, ask for it to
be [masked](masks.md): select its cells, select **Request Masking**, and give the **Reason** and
the **Mask Expiration**. See [Requesting a mask](masks.md#requesting-a-mask).

## Other ways to sign off

- On the [Readiness](readiness.md#signing-off-from-the-tree) tab, select a group under a
  component to change that one signoff.
- An admin can sign off a whole system at once, or run a saved set of signoffs, from the Setup
  tab: see [Bulk Signoff](administration.md#bulk-signoff) and
  [Saved Signoffs](administration.md#saved-signoffs).
- Group leaders get a [daily email](administration.md#email) of their group's new outstanding
  signoffs, when it is turned on, with links to the Signoff tab.
