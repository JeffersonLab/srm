# System Readiness Manager User Guide

System Readiness Manager (SRM) tracks whether Jefferson Lab's accelerators are ready to run. It
holds an inventory of the machine, its **systems** and their **components**, and records each
**responsible group**'s **signoff** that a component has been checked and is ready. Operations
and management read the result as a tree and as reports, by system, by group and by **beam
destination**, to decide where beam can be sent. SRM is the tool behind the lab's Hot Checkout
(HCO) process, and it is used all year to show the state of the machine from day to day.

At JLab, SRM is at [ace.jlab.org/srm](https://ace.jlab.org/srm). You can view it without logging
in; log in to make changes.

The [guide's website](https://jeffersonlab.github.io/srm/) has a copy for each release; pick the
version the Help tab shows. On GitHub, this guide describes the `main` branch, which may be newer
than the version you are using.

## Contents

1. [Concepts](concepts.md): the inventory, beam destinations, responsible groups, statuses,
   masks and roles.
2. [Readiness](readiness.md): the tree that shows the state of the machine. For Operations and
   anyone who wants the overall picture.
3. [Signing off](signoff.md): checking, upgrading and downgrading components. For group members
   and group leaders.
4. [Masks](masks.md): taking a component out of the readiness count for a while, and how masked
   differs from unpowered.
5. [Checklists and links](checklists.md): what each group verifies before it signs off, and the
   Links tab.
6. [Reports](reports.md): status, signoff and activity reports.
7. [Administration](administration.md): the Setup tab, settings and email. For admins and
   branch admins.
8. [FAQ](faq.md): common questions about the process.
9. [Hall responsibilities](hall-responsibilities.md): who to contact about the systems that the
   accelerator and the halls share.

## Tabs at a glance

| Tab | What it is for |
|---|---|
| **Readiness** | The machine as a tree of categories, systems, components and groups, each marked with its status. |
| **Signoff** | A group's signoffs for the components of one system; change them here. |
| **Masks** | The components that are masked now, requests to mask more, and candidates for masking. |
| **Checklists** | Each group's checklist for each system it is responsible for. |
| **Links** | Logbooks, task lists, beamline maps and sketches, and hall responsibilities. |
| **Reports** | Status by destination and group, signoff tables, and the history of changes. |
| **Setup** | Admins and branch admins only: the inventory, groups, destinations and settings. |
| **Help** | Version, contacts, documentation and a feedback form. |

The box at the top of every page finds a component: type part of its name or alias and choose
from the suggestions to open its [component detail](reports.md#component-detail). If what you
typed isn't a whole component name, SRM lists the components whose names contain it.

## Quick start for group members

1. Log in, and go to **Signoff**.
2. Choose your **Group**, then one of its **Systems**, and select **Apply**.
3. Read your group's [checklist](checklists.md) for the system, and check each component
   against it.
4. Select the components you checked, in your group's column, and select **Edit Signoff**. Set
   the status to **Checked**, with a comment if there is anything to note.
5. A group leader reviews the checked components and sets them to **Ready**.

When a component you signed off stops being ready, downgrade it the same way, to **Checked** or
**Not Ready**, and say why in the comment. See [Signing off](signoff.md) for the details.

## Quick start for Operations

1. Go to **Readiness**, select **Choose...** and pick the **Beam Destination** you want to send
   beam to, or **Current Run** for all of the run's destinations.
2. A green thumbs-up at the top of the tree means every component for that destination is Ready
   or masked. Otherwise open the nodes that aren't Ready to find the components and the groups
   that are holding it back.
3. Check **Masks › Current** for what is masked and until when.

## Questions and changes

Use the feedback form on the Help tab, or contact the content and technical contacts listed
there. To suggest a change to this guide, open an issue or pull request on
[GitHub](https://github.com/JeffersonLab/srm).
