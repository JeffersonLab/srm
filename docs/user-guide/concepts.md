# Concepts

[User Guide](README.md) › Concepts

## Why track readiness

An accelerator is made of thousands of components, looked after by dozens of groups. Before
beam is sent somewhere after a maintenance period, each group has to confirm that its part of
every device on the way is back in working order: this is the lab's **Hot Checkout** (HCO).
System Readiness Manager (SRM) is where that is recorded. It answers two questions:

- For Operations and management: is the machine ready to send beam to this destination, and if
  not, what is left and who has it?
- For each group: which components are waiting on us?

SRM is used all year, not only during a hot checkout. When a component breaks or is worked on
during a run, its group downgrades it, so SRM keeps showing the state of the machine.

## Categories, systems and components

The inventory is a tree:

- A **component** is one thing to be checked: a magnet, a beam position monitor, a valve, a
  server, a procedure.
- A **system** is a set of components of the same kind, such as "Box Power Supplies" or "Beam
  Valves". The components of a system share the same checklist and the same responsible groups,
  which is what makes them a system.
- A **category** groups systems, and other categories, for reporting: for example CEBAF ›
  Magnets › Box Supply. The top-level categories, such as CEBAF, LERF and each hall, are
  **branches**, and a branch can have its own [admins](#roles).

A component also has:

- a **region**: where it is in the machine; see [Regions](#regions);
- the **beam destinations** it takes part in; see [Beam destinations](#beam-destinations);
- optionally an **alias**, another name it is known by, which the search box also finds;
- a **source**: INTERNAL when it was entered in SRM, or the element database it comes from (CED
  for CEBAF, LED for the LERF, UED for the UITF), with a link to the element there.

A component's name is unique within its system.

## Beam destinations

A **beam destination** is somewhere beam can be sent: a hall, a dump, a Faraday cup, a tune-up
line. Each component is marked with the destinations it takes part in, which is every
destination that needs it to be working. An injector magnet takes part in nearly all of them; a
magnet in a hall's transport line only in that hall's.

Filtering by beam destination, on the Readiness tab and in nearly every report, turns "is the
machine ready?" into the question Operations actually asks: "is everything ready that we need to
send beam *there*?"

The **Current Run** link beside a Beam Destination filter chooses the destinations of the
current run, and most pages start with them chosen. An admin sets which destinations those are
on [Setup › Beam Dest. List](administration.md#beam-destination-list).

## Regions

A **region** is a stretch of the machine, such as the Injector, the North Linac, an arc or a
hall's beam line. Every component is in one region. Regions are a second way to cut the
inventory: a system such as "Beam Valves" runs through the whole machine, and the region picks
out its components in one place.

## Responsible groups

A **responsible group** is a team that checks components: Alignment, Vacuum, DC Power, RF,
Safety Systems, Operations and so on. Each system has an ordered list of the groups responsible
for it, and every component of the system needs a signoff from each of them.

The order is the order the work is done in, each group building on the one before: for example
the group that installs a device, then the group that powers it, then the group that controls
it. It matters in two ways: SRM shows each group the components where it is
[its turn](signoff.md#whose-turn-it-is), and a downgrade by one group can
[cascade](signoff.md#downgrades-and-cascades) to the groups after it.

Each group has **leaders**, who make the final Ready signoff. A group can also have a
[checklist](checklists.md) for each system, saying what its signoff means.

## Statuses

A **signoff** is one group's status for one component:

| Status | Meaning | Who sets it |
|---|---|---|
| **Not Ready** | Not checked yet, or known not to work. Every signoff starts here. | Any logged-in user. |
| **Checked** | A member of the group has checked the component, and it is waiting for the leader's review. | Any logged-in user. |
| **Ready** | The group stands behind the component for beam operations. | A leader of the group, an admin or a branch admin. |
| **Not Applicable** | This group has nothing to check on this component, though it does on others in the system. | A leader of the group, an admin or a branch admin. |

Signing off is a two-step process: a technician or engineer checks the component and marks it
Checked, then a group leader reviews and marks it Ready. See
[Signoff rules](signoff.md#signoff-rules).

A **component's status** is the worst of its groups' signoffs, in the order Not Ready, Checked,
Ready; Not Applicable signoffs are passed over. A component is Ready only when every responsible
group has signed it off as Ready or Not Applicable. A [masked](#masks) component shows its mask
instead.

A **system's status** is the worst of its components', and a **category's** the worst of its
systems', leaving masked components out. On the [Readiness](readiness.md) tab these follow the
filter, so the same category can be Ready for one beam destination and Not Ready for another.

## Masks

A **mask** takes a component out of the count for a while: it is broken or out of service, a
repair is planned, and the program can run without it. A masked component no longer holds back
its system. A mask has a reason and, normally, an expiration; when it ends, the component's
groups have to sign it off again. There are three kinds, **Masked (Director)**, **Masked (Crew
Chief)** and **Masked (Administrator)**, named for how the mask was made. See [Masks](masks.md).

## Unpowered components

An **unpowered** component, marked with a ★ after its name, is one that isn't meant to be used
during the run. It still has to be signed off: the groups verify that it can't affect the beam.
Changing a component between powered and unpowered sets its signoffs back to Not Ready, since
what has to be verified changes. See [Unpowered or masked?](masks.md#unpowered-or-masked)

## Roles

Anyone can view SRM without logging in. What you can change depends on who you are:

| Role | Who | Can |
|---|---|---|
| **Logged-in user** | Anyone with a lab account. | Sign off components as Checked or Not Ready, comment, request a mask, and write a checklist that isn't published yet. |
| **Group leader** | A leader or supervisor of a responsible group. | Also sign off as Ready or Not Applicable for the group, and edit, publish and delete the group's checklists. Receives the group's daily email. |
| **Crew Chief** | The Operations crew chiefs. | Also mask components directly, and edit and remove masks. |
| **Branch admin** | The admins of one top-level category, such as a hall. | Also set up their branch on the Setup tab (categories, systems, components, responsibilities), and sign off as Ready or Not Applicable for any group on their branch's components. |
| **Admin** | The SRM administrators, listed on the Help tab. | Everything, including accepting mask requests, the lists of groups and destinations, saved signoffs, and settings. |

Group leaders, crew chiefs and admins are roles in the lab's user directory; see
[Administration](administration.md#who-can-do-what) for how each is set.

The process also has people whose part is to read SRM, not to change it:

- **Geographic integrators**: the liaison for an area of the machine, who works with the groups
  to get the area ready. The **Region** filter gives their view.
- **Management**: the Program Deputy, Operability and the Operations department follow the
  progress, including at the daily meetings, through the [reports](reports.md).
- **Operations**: decides where beam can be sent from the readiness of each beam destination.
