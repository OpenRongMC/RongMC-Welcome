# RongMC Welcome (Legacy)

[简体中文](./README.md) · English

The older construction-pack welcome screen and optional-mod switch. It displays a welcome message, saves completion state and manages optional client JARs from the pack’s configured directory.

## Download and installation

- Version · [v1.0.0+26.3](https://github.com/OpenRongMC/RongMC-Welcome/releases/tag/v1.0.0%2B26.3) (Release)
- Release asset · `rongmc-welcome-1.0.0.26.3.jar`
- Runtime · Minecraft Java 26.3, Fabric, Java 25
- Fabric Loader · `>=0.19.3`
- Fabric mod ID · `rongmc_welcome`
- Dependencies declared by the JAR · [Fabric API](https://modrinth.com/mod/fabric-api)

Place the release JAR in the instance’s `mods/` directory. Use it with a client pack that explicitly ships this legacy welcome module. RongMC Client supplies the five-tier device setup used by the current low-spec pack.

## How to try it

Test the first-launch message in an instance with a clean config, switch one optional mod, restart, and verify the completion state and enabled list.

## Working from source

```sh
./gradlew tasks
```

Inspect the tasks available in this checkout with Java 25 before selecting a build task.

## Documentation and support

- [Previous README (historical guide, contributors and credits)](https://github.com/OpenRongMC/RongMC-Welcome/blob/fc3c1160e3824082bdac9f8046025029f313e9f7/README.md)
- [RongMC Client and Live Tours](https://github.com/OpenRongMC/RongMC-Client)
- [Report a RongMC port issue](https://github.com/OpenRongMC/RongMC-Welcome/issues) · Include the game version, client and server JAR names, reproduction steps and relevant logs.

## License and credits

See this repository’s [LICENSE](./LICENSE) and the original project’s license, credits and third-party asset notices.
