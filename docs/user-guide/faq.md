# Frequently asked questions

[User Guide](README.md) › FAQ

SRM was first called the Hot Checkout (HCO) tool, and many people still call it HCO; the two
names mean the same application.

## When should I downgrade a component?

Whenever you work on a component and you are not its final signoff, so that the groups after
yours are told to look again, and whenever a component won't be operational for more than a
shift. The link in ATLis takes you straight to the component's signoff in SRM. Always write a
comment when you downgrade, and upgrade the component again once it is back in service.
Operations relies on this to understand the state of the machine from day to day. See
[Downgrades and cascades](signoff.md#downgrades-and-cascades).

## Can the accelerator operate when not everything is ready?

Yes. The accelerator is a large machine that will rarely, if ever, have every component working.
At the start of a run the aim is for SRM to be completely ready ("all green"), with the
components that are known to be out of service [masked](masks.md). Once the run starts,
components break and need repair, which is coordinated through the Ops PR process. If a repair
can't be made during the shift, downgrade the component so that the Crew Chief knows the state
of the machine. The program changes during a run too, so some components will be not ready or
waiting for a scheduled repair; what matters is that the Crew Chief can review SRM and see the
actual state of each system.

## What if SRM says my group is responsible for a component we don't work on?

The signoffs were negotiated with system owners and group leaders when the Hot Checkout process
was designed, by [system](concepts.md#responsible-groups): every component of a system has the
same groups. If your group works on some of a system's components and not others, a group leader
can sign off the others as [Not Applicable](concepts.md#statuses). SRM makes sure at least one
group checks every component, so the last group left can't choose Not Applicable. If you think
the assignment itself is wrong, use the feedback form on the Help tab.

## What does a Not Applicable (N/A) signoff mean?

- It belongs to one group on one component: one group can be N/A while another still signs off.
- It is for a component a group never checks, in a system where the group normally does sign
  off.
- It doesn't expire. It stays until someone sets the signoff to Not Ready, Checked or Ready
  again.
- It exists only in SRM; the CEBAF Element Database (CED) doesn't set or read it.

For example, most CEBAF box power supplies are interlocked by the Personnel Safety System, so
the Safety Systems Group signs off the Box Power Supplies system. A few of them aren't
interlocked, and for those the group's signoff is N/A.

## How do I sign off an unpowered component? What about masking?

An unpowered component still needs its signoffs; a masked component doesn't. See
[Unpowered or masked?](masks.md#unpowered-or-masked)

## When does Hot Checkout happen?

SRM is used all year, and Hot Checkout is the part of the year when the machine goes from a
maintenance period back to running. It typically has two stages:

1. **Before tunnel lockup**: the checks that make the machine ready to lock up, such as
   alignment, vacuum work and diagnostic health checks. These are done during the maintenance
   period, up to the week before lockup.
2. **After tunnel lockup**: the checks that need the tunnel locked, such as box power supply and
   RF checks. These are done in the week before beam operations, starting on Monday, with the
   turnover to Operations complete by Friday morning.

At the end of a run, a chosen set of components is downgraded on purpose so that the responsible
groups check them again; see [Saved Signoffs](administration.md#saved-signoffs).

## Who do I ask about SRM, or to get something changed?

Use the feedback form on the **Help** tab. The message goes to the whole team, so questions are
answered and concerns are followed up. To suggest a change to this guide, open an issue or pull
request on [GitHub](https://github.com/JeffersonLab/srm).
