# Task: Add Geyser Custom Block `connection_rule` Support

## Goal

Patch Geyser so extensions can define Bedrock custom blocks with:

```json
"minecraft:connection_rule": {
  "accepts_connections_from": "none"
}
```

The immediate use case is the Blue-Ice Button Boat-Speed PoC. Java buttons are overridden for Bedrock clients as a non-colliding custom block. Collision is now fixed, but Bedrock glass panes still visually connect to the custom block, making blue-ice highway navigation difficult.

The expected fix is to expose Bedrock's `minecraft:connection_rule` custom block component through the Geyser custom block API and serialize it into the custom block runtime data sent to Bedrock clients.

## Background

Current observations from the extension PoC:

- Java server world must keep the real buttons.
- Bedrock clients receive a Geyser custom block override for Java button block states.
- `collisionBoxes(BoxComponent.emptyBox())` and `selectionBox(BoxComponent.emptyBox())` successfully make the block pass-through.
- Changing geometry does not stop glass panes from connecting.
- Invisible or invalid geometry still leaves pane connection behavior unchanged.
- `placeAir(true)` only emits a dummy `minecraft:on_player_placing` event and does not make existing blocks render or behave as air.
- Geyser's resource pack extension API can provide resource packs, but the current public API does not expose behavior pack registration or raw custom block component injection.

Conclusion: this should be fixed in Geyser's custom block component serialization path, not by custom geometry, resource pack assets, or `CustomBlockProperty`.

## Required investigation

Before editing, inspect the actual checked-out Geyser source. Do not rely on old examples or wiki snippets.

Confirm these current API/core points:

1. `CustomBlockComponents` is the public API object for block components.
2. `GeyserCustomBlockComponents` is the core implementation/builder.
3. `CustomBlockRegistryPopulator.convertComponents(...)` serializes known `CustomBlockComponents` fields to Bedrock NBT.
4. `MappingsReader_v1` parses custom block mapping JSON fields, if JSON mapping support should also expose the component.
5. There is no existing raw passthrough component map or existing `connection_rule` implementation.

## Desired public API

Add a small, typed component API. Exact naming may follow Geyser style, but the extension should be able to express at least this:

```java
CustomBlockComponents components = CustomBlockComponents.builder()
        .selectionBox(BoxComponent.emptyBox())
        .collisionBoxes(BoxComponent.emptyBox())
        .connectionRule(ConnectionRuleComponent.builder()
                .acceptsConnectionsFrom("none")
                .build())
        .build();
```

An enum is preferred over free strings if it matches existing Geyser API style:

```java
ConnectionRuleComponent.AcceptsConnectionsFrom.NONE
ConnectionRuleComponent.AcceptsConnectionsFrom.ALL
ConnectionRuleComponent.AcceptsConnectionsFrom.ONLY_FENCES
```

The Bedrock schema also supports optional enabled directions. Include it only if it remains small and well typed:

```text
east, north, south, west
```

For the Blue-Ice PoC, only `accepts_connections_from = none` is required.

## Required implementation

### API module

Add a component type under the existing custom block component package, for example:

```text
api/src/main/java/org/geysermc/geyser/api/block/custom/component/ConnectionRuleComponent.java
```

Add accessors to:

```text
api/src/main/java/org/geysermc/geyser/api/block/custom/component/CustomBlockComponents.java
```

Required shape:

```java
@Nullable ConnectionRuleComponent connectionRule();
```

and in the builder:

```java
@This Builder connectionRule(@Nullable ConnectionRuleComponent connectionRule);
```

Keep source and binary compatibility in mind. Follow the existing API provider/builder patterns used by `GeometryComponent`, `MaterialInstance`, `BoxComponent`, and `TransformationComponent`.

### Core implementation

Update:

```text
core/src/main/java/org/geysermc/geyser/level/block/GeyserCustomBlockComponents.java
```

Add storage, getter, and builder setter for the new connection rule component.

### Bedrock NBT serialization

Update:

```text
core/src/main/java/org/geysermc/geyser/registry/populator/CustomBlockRegistryPopulator.java
```

In `convertComponents(...)`, serialize the new component as:

```java
builder.putCompound("minecraft:connection_rule", NbtMap.builder()
        .putString("accepts_connections_from", "none")
        .build());
```

If enabled directions are implemented, include the Bedrock field using the current Cloudburst NBT list style used elsewhere in this file.

Do not add this as a block property. It must be under the returned `components` compound.

### JSON mapping support

If Geyser's custom block JSON mapping parser supports component fields, update:

```text
core/src/main/java/org/geysermc/geyser/registry/mappings/versions/MappingsReader_v1.java
```

Recommended JSON shape:

```json
{
  "name": "blueice_ghost_button",
  "components": {
    "connection_rule": {
      "accepts_connections_from": "none"
    }
  }
}
```

or, if the existing mapping file style uses un-namespaced component names, follow that local convention. The generated Bedrock NBT must still use `minecraft:connection_rule`.

This JSON parser update is useful but secondary. The extension API path is required.

## Expected extension usage after patch

The Blue-Ice extension should be able to define:

```java
CustomBlockComponents ghostButtonComponents = CustomBlockComponents.builder()
        .selectionBox(BoxComponent.emptyBox())
        .collisionBoxes(BoxComponent.emptyBox())
        .geometry(GeometryComponent.builder()
                .identifier("minecraft:geometry.full_block")
                .build())
        .materialInstance("*", diagnosticMaterial)
        .connectionRule(ConnectionRuleComponent.builder()
                .acceptsConnectionsFrom(ConnectionRuleComponent.AcceptsConnectionsFrom.NONE)
                .build())
        .displayName("Blue Ice Ghost Button")
        .build();
```

The Java server must still see the real button states. Only Bedrock custom block behavior should change.

## Validation

### Build validation

Build the modified Geyser project using its normal build command.

Do not pin an older API or work around compilation errors by deleting existing functionality.

### Unit or focused validation

Add focused coverage if Geyser has tests for custom block component serialization or mapping parsing.

At minimum, verify that `convertComponents(...)` produces a `components` compound containing:

```text
minecraft:connection_rule
  accepts_connections_from = none
```

for a custom block whose `CustomBlockComponents` includes the new component.

### Runtime validation

Run a local Geyser build with the Blue-Ice extension compiled against the patched API.

Confirm from logs:

- Geyser starts without registry or palette errors.
- The extension registers the custom block and Java button overrides.
- Bedrock client accepts the custom block definition.

In-game Bedrock validation:

1. Place a glass pane adjacent to a Java button that is overridden to the custom ghost button.
2. Confirm the ghost button remains non-colliding.
3. Confirm the glass pane no longer visually connects toward the ghost button.
4. Confirm Java clients and the Java server still see the real button.
5. Re-test a blue-ice boat route and record whether navigation is improved.

Do not claim the boat-speed issue is fixed unless the Bedrock in-game speed behavior is measured.

## Non-goals

- Do not implement packet-level button-to-air translation for this task.
- Do not add behavior pack delivery unless investigation proves `connection_rule` cannot be serialized through custom block components.
- Do not replace Java server buttons with air.
- Do not add region or coordinate filtering.
- Do not implement velocity boosts.
- Do not change Gate, Fabric server code, or production configuration.

## Deliverable

When finished, report:

1. Geyser files changed.
2. Any new public API classes or methods.
3. The exact Bedrock NBT emitted for `minecraft:connection_rule`.
4. Build and test commands with results.
5. Manual Bedrock validation results, especially glass pane connection behavior.
6. Any compatibility risks with current Bedrock format versions.
