# Completed addon events

Public immutable, non-cancellable main-thread events live in me.lidan.griffinAddon.events. Consumers should use soft dependencies, check API availability, and compile without shading this addon.

GriffinBurrowOpenedEvent follows the existing burrow drop selection and records player, rarity, world and selected outcome. The outcome is the rolled type, not proof that a configured item or mob existed. Actual native item/coin awards already publish Core Drop observations and must not be counted again from this summary.

GriffinMobSpawnedEvent follows a successful native mob spawn and identifies the actual entity, summoner, Mythic ID and entity world. Correlate that entity UUID with death observations. It does not guess a party roster or a boss victory.

GriffinItemGrantedEvent follows the direct sand-dust grant that bypasses Core Drop. It reports actual item ID and amount. Existing public void drop methods retain their signatures and delegate to the same single selection path.

Java 21 build passes. The repository has no standalone automated gameplay test suite. Real burrow/spawn/grant validation must be reported separately; API activation alone is not that validation. Plugin versions include source commits.
