# Capturable Mobs (Bukkit/Spigot/Paper plugin)

Capture passive or hostile mobs into a **Mob Cage** item, carry them around, and release them later — with two server-configurable modes: **reusable** and **single-use**. Includes a shapeless (keyed) crafting recipe, placement safeguards, clean lore/persistence, and simple admin controls.

---

## Features

### Mob Cage item
- Implemented as a **Spawner** item tagged with persistent data so it’s uniquely identifiable and **unstackable**.
- Clean, padded lore to hide default spawner tooltip.
- Displays **Stored Mob** in the lore once something is captured.

### Crafting
- **Recipe (enabled by default):**  
  ```
  I I I
  I   I     (I = Iron Bars)
  I I I
  ```
- Server operators can enable/disable the recipe at runtime (see Commands).

### Capture mobs
- **Right-click** a mob with the Mob Cage in your **main hand** to capture it.
- Players cannot capture other players.
- Persists useful metadata when present:
  - **Mob type** (EntityType)
  - **Baby/adult** (for ageable mobs)
  - **Custom name**
  - **Sheep color**
  - If the mob has a spawn egg, the cage also stores a **spawner preview** entity id for nicer visuals.

### Release mobs
Two server modes control release behavior and placement rules:

1. **Reusable mode** (default)
   - **Sneak + right-click a block** with a filled cage to release the mob on the adjacent space.
   - The cage is **emptied and retained** (resets lore/flags).
   - **Placement guard:** empty cages can’t be placed; filled cages must be placed while **sneaking** (otherwise placement is cancelled).

2. **Single-use mode**
   - **Placing** a filled cage briefly sets visuals, then **consumes the block** and **spawns the mob** at that spot.
   - Empty cages cannot be placed.
   - Intended as a “deploy and consume” style.

### Quality of life
- **Debounce/cooldowns** to prevent accidental rapid releases.
- All persistent data uses namespaced keys; unique IDs ensure cages do not stack after crafting.
- Works with **passive and hostile** mobs (plugin checks for spawn egg support to enhance previews but does not require it).

---

## Commands & Permissions

| Command | Description | Permission |
|---|---|---|
| `/mobcage` | Gives a Mob Cage **only when the recipe is disabled** (player use). | *(none)* |
| `/mobcagerecipe <enable|disable>` | Toggle the crafting recipe at runtime. | *Op only* |
| `/mobcagemode <reusable|single-use>` | Switch between cage modes. Applies listeners accordingly. | `capturablemobs.mode` |

Notes:
- When the recipe is **enabled**, `/mobcage` is intentionally disabled to avoid bypassing progression.
- Mode switching re-registers listeners to match the chosen mode.

---

## How it works (technical overview)

- **MobCatcherItem**: builds the Spawner-based item, adds namespaced keys (`mob_cage`, `unique_id`), and manages lore.
- **MobCageCraftListener**: intercepts crafting output for cages, injects a unique UUID to make them **unstackable**, and normalizes the display.
- **MobCaptureListener**: handles right-click capture; stores mob metadata (`mob_type`, `is_baby`, `custom_name`, `sheep_color`, optional spawner preview) and removes the entity.
- **MobReleaseListener** (reusable mode): sneak-right-click to release and **retain** an empty cage.
- **MobPlacePreventionListener**: guards block placement rules for reusable vs single-use modes.
- **MobSpawnerUseListener** (single-use mode): consumes the placed block, plays effects, and spawns the stored mob after a short delay.
- **Runtime toggles**: command handlers switch **mode** and **recipe**; the plugin rebinds listeners accordingly so behavior matches the active mode without restarts.

---

## Installation

1. Drop the compiled JAR into your server’s `plugins/` folder.
2. Restart/reload the server.
3. (Optional) Use commands to:
   - `/mobcagerecipe disable` to gate cages behind admin distribution or gameplay events.
   - `/mobcagemode single-use` if you prefer consumable cages.

Tested on modern Paper/Spigot builds for 1.20+ (uses standard Bukkit APIs and PersistentDataContainer).

---

## Usage

1. Craft (or receive) a **Mob Cage**.
2. **Right-click** a mob with the cage in your **main hand** to capture it.
3. Release it later:
   - **Reusable:** sneak + right-click a block to place the mob and keep the cage.
   - **Single-use:** **place** the filled cage; the block is consumed and the mob appears.

Tip: In reusable mode, to place a filled cage as a block (rather than using sneak release), you **must sneak**; empty cages cannot be placed.

---

## License / Credits

- Copyright: All rights reserved.
- Author: birdsprime

Issues and suggestions are welcome via your normal server/plugin workflow.
