# Lucky Penny Tracker

Tracks the charges, resources, and Grand Exchange value saved by Ghommal's
lucky penny.

The **Charges** tab shows each item whose charge was saved. The **Resources**
tab uses RuneLite-style item slots and shows the total resources saved. Hover a
resource icon to see its exact amount and its total saved GE value.

## Pricing

Prices and sprites use RuneLite's `ItemManager`. Resource item IDs are kept in
`ResourceItemRegistry` so the Resources tab does not depend on a name search.
The Pharaoh's sceptre artefact IDs are configured directly because those legacy
Pyramid Plunder items do not have generated gameval constants in every RuneLite
release.

`Hide unpriced items` hides a charge save when any resource needed to value that
charge has no GE price.

## Lucky Penny items without resource-cost tracking

The following known Lucky Penny charge or degradation cases are deliberately
not assigned a saved-resource value yet:

- Amulet of the damned
- Enchanted lyre
- Slayer ring
- Skull sceptre (i)
- Blood moon helm, chestplate, and tassets
- Eclipse moon helm, chestplate, and tassets
- Blue moon helm, chestplate, and tassets

These need time-based degradation handling or an additional player choice for
their recharge resource. They may still be detected from the chat message, but
they do not contribute a resource or GP value. This list is kept here so new
cost support can be added deliberately rather than silently estimating a price.
