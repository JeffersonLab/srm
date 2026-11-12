# Checklists and links

[User Guide](README.md) › Checklists and links

## Checklists

A **checklist** says what a group actually verifies before it signs off the components of a
system. There is one for each pairing of a [responsible group](concepts.md#responsible-groups)
and a system, so two groups that sign off the same system each have their own. Publishing the
checklists means that anyone, from the next group in line to the Crew Chief, can see what a
Ready from that group stands for.

Anyone can read the checklists and their history without logging in.

### Finding a checklist

On the **Checklists** tab, choose a group. The page lists the systems the group is responsible
for:

| Column | Meaning |
|---|---|
| **System** | The system the checklist is for. |
| **Checklist Required** | Whether the group must publish a checklist before it can sign off this system. An admin sets this on [Setup › Group Resp.](administration.md#group-responsibilities) |
| **Published** | Whether it is published, and by whom and when. |
| **Checklist** | **View**, **History** and, depending on who you are, **Create**, **Edit**, **Delete** and **Sign & Publish** or **Unpublish**. |

**View** opens the checklist with its document ID, group, system, author, revision number, date
and comment. **Printer Friendly** opens it as a page of its own for printing. Published
checklists are also linked from the **Group Information**, **System Information** and
**Component Information** dialogs elsewhere in SRM.

### Writing a checklist

1. Log in, choose your group on the **Checklists** tab, and select **Create** beside the system
   (or **Edit** if there is one already).
2. Fill in the **Author**: the person who is responsible for the content, who needn't be the
   person typing.
3. Write the checklist in the editor. Bulleted list items are shown with a checkbox, so write
   each check as a bullet. The **Insert template** button offers two outlines, **Uncredited
   Control Level 1** and **Uncredited Control Level 2**, with headings for system validation and
   for the system's performance specification documents.
4. When editing, give a **Revision Comment** saying what changed.
5. Select **Save**.

The editor keeps text formatting, headings, lists and links. It doesn't keep images or tables.

Every save is kept as a revision. **History** lists them, oldest first, with the revision
number, date, comment, author and who submitted it; **View** shows the checklist as it was. An
old revision can't be restored, but its text can be copied into a new one.

### Publishing

A group leader or an admin publishes a checklist with **Sign & Publish**, which records their
name and the date, and can **Unpublish** or **Delete** it. Deleting removes the checklist with
all of its history.

| | Not published | Published |
|---|---|---|
| **Create**, **Edit** | Any logged-in user | A leader of the group, or an admin |
| **Sign & Publish**, **Unpublish**, **Delete** | A leader of the group, or an admin | A leader of the group, or an admin |

Editing a published checklist leaves it published, with the new revision showing at once.

### Required checklists

Where **Checklist Required** is Yes and the checklist isn't published, the group can't sign off
that system: the [Signoff](signoff.md) tab says that a checklist "must be published before
signoffs can be made" and lists no components. Admins are exempt. To see which checklists are
still missing, use the [Group Responsibility report](reports.md#group-responsibility).

## Links

The **Links** tab collects references that are useful during a checkout:

- **General**: the logbooks, the task lists, the beamline map, the songsheets, the hall and beam
  dump responsibility documents, and the Accelerator Bypassed-Interlocks Log (on site only).
- **Beamline Sketches**: the CEBAF Element Database's sketch of each part of the machine, from
  the injector to the halls and the LERF.
