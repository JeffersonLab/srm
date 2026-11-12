# Masks

[User Guide](README.md) › Masks

A **mask** takes a component out of the readiness count for a while. Use it for a component that
is broken or out of service and waiting for repair, when the program can run without it. A
masked component shows a mask instead of a status, and it no longer holds back its system, its
category or its beam destinations: the [Readiness](readiness.md) tree and the reports count it
separately.

A mask has a **reason**, the person who set it, and normally an **expiration**: the date by
which the component will be dealt with, or the end of the run, whichever comes first. Components
that are credited controls, or that the run can't do without, are never masked.

Anyone can view the **Masks** tab without logging in.

## Kinds of mask

The kind records how the mask was made, and is shown as the component's status:

| Kind | How it is made | Who can make it |
|---|---|---|
| **Masked (Director)** | An admin accepts a [mask request](#requesting-a-mask). | Any logged-in user can ask; an admin accepts. |
| **Masked (Crew Chief)** | **Mask** on [Candidates](#candidates), or **Edit Mask** on [Current](#current-masks). | A crew chief or an admin. |
| **Masked (Administrator)** | **(Un)mask** on [Setup › Component List](administration.md#component-list). | An admin, or a branch admin in their branch. |

Whichever way a component is masked, each of its group signoffs is set to the mask too, with the
comment "Masked", so the [signoff history](reports.md#signoff-activity) shows when it happened.

## Requesting a mask

1. On the [Signoff](signoff.md) tab, list the system's components and select the ones to mask,
   in your group's column.
2. Select **Request Masking**.
3. Give the **Reason** (up to 512 characters) and the **Mask Expiration**, which must be in the
   future.
4. Select **Save**.

SRM creates one request for each component, emails the people who handle mask requests, and
shows the pending requests. It refuses a component that is already masked or already has a
pending request; nothing is requested if any of the selected components is refused.

### Requests

**Masks › Requests** lists the requests, newest first: the response (PENDING, ACCEPTED or
REJECTED), component, system, reason, mask expiration, who asked and when. It starts with the
pending requests for the current run's destinations; the filter can show the others, by
**Beam Destination**, **Category**, **System**, **Region**, **Group**, **Reason** and
**Request Status**. **Export** saves the list as an image or prints it.

An admin selects a request and then:

- **Accept**: the dialog shows the reason and expiration that were asked for, and either can be
  changed before saving. The component gets a Director mask, with the admin as the person who
  masked it.
- **Reject**: the request is marked REJECTED and nothing else changes. The component can be
  requested again.

SRM doesn't email the requester about the answer, so look at the request on this page, or tell
them.

## Current masks

**Masks › Current** lists the components that are masked now, the most recently masked first:
the masked date, the mask expiration date, the component and its system, who masked it and the
kind of mask, and the reason. Select a component's name for its **Component Information**.

The filter starts with the current run's destinations and with the Director and Crew Chief
masks. To see Administrator masks, add **Administrator** to **Mask Type**. It can also filter by
**Category**, **System**, **Region**, **Group**, **Unpowered** and **Component** name (with `%`
as a wildcard).

A crew chief or an admin can select one or more rows (hold **Ctrl** or **Shift**, **⌘** on a
Mac) and then:

- **Edit Mask**: change the reason and expiration. Saving replaces both, so keep a reason in the
  box and enter the expiration again even if only one of them changes. The mask becomes a Crew
  Chief mask, with you as the person who masked it.
- **Remove Mask**: unmask the components. Choose the **Status** their signoffs go to, **Checked**
  (the default) or **Not Ready**, and optionally a **Comment** for the signoff history.

## Candidates

**Masks › Candidates** lists the components that aren't masked and aren't Ready: by default
those that are Checked or Not Ready, for the current run's destinations. It is the list to work
through when deciding what the run can start without. The filter can change the **Status** and
narrow the list by **Beam Destination**, **Category**, **System**, **Region**, **Group**,
**Unpowered** and **Component** name.

A crew chief or an admin can select components and select **Mask**, giving the **Reason**,
which is required, and the **Expiration**. This makes a Crew Chief mask at once, with no
request.

## When a mask ends

| How it ends | The component's signoffs become |
|---|---|
| The expiration passes | Not Ready, with the comment "Auto-downgrade due to unmasking by expiration". |
| **Remove Mask** on Masks › Current | Checked or Not Ready, as chosen. |
| **(Un)mask** on Setup › Component List | Checked. |

Either way, every responsible group then has to look at the component again and sign off.
Expired masks are removed at midnight, and whenever someone opens the Readiness tab, so a mask
can outlast its expiration time by up to a day in the other pages. A mask with no expiration
stays until someone removes it.

While a component has a Director or Crew Chief mask, its signoffs can't be changed: remove the
mask first.

## Unpowered or masked?

These are different things, and a component can be both.

**Unpowered** means the component isn't meant to be used during the run. It follows the
element's property in the CEBAF Element Database (CED); an admin sets it in SRM with
**(Un)power** on [Setup › Component List](administration.md#component-list), and SRM marks the
component with a ★.

- Unpowered elements don't appear on the control screens.
- The groups still sign off an unpowered component as Not Ready, Checked or Ready. Checking an
  unpowered component is a hot checkout in reverse: verify that it can't affect the beam in any
  way.

**Masked** means the component is broken or out of service, with a repair planned. It exists
only in SRM; CED doesn't set or read it.

- A mask should have an expiration: the date by which the component will be dealt with, or the
  end of the run, whichever comes first.
- While it is masked, all of its group signoffs are masked.
- Credited controls, show-stoppers and critical components are never masked.

Which to use:

| The component | Use |
|---|---|
| works after a standard checkout, but won't be used this run and has had its controls removed | Unpowered, not masked. |
| is broken or out of service, with a repair planned, and should stay on the control screens | Masked. |
| is broken or out of service, with a repair planned, and shouldn't show on the control screens | Unpowered and masked. |

Vacuum elements are never hidden from the control screens, whether or not their controls have
been removed, so they aren't marked unpowered.
