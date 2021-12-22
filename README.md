[![Build](https://github.com/antonilol/redstone_components/actions/workflows/push.yml/badge.svg)](https://github.com/antonilol/redstone_components/actions/workflows/push.yml)

# Redstone Components

## Install and use

This mod runs on [Fabric](https://fabricmc.net/), so make sure you have that installed.

Download the [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files) if you don't have it already. Put it in your mods folder.

Go to [Releases](https://github.com/antonilol/redstone_components/releases) (or compile it, see below) and download the latest release. Also put it in your mods folder.

The `mods` folder can be found in [.minecraft](https://minecraft.fandom.com/wiki/.minecraft#Locating_.minecraft).
If not, create it.

New blocks added by this mod:
- Memory Cell
- Small Memory Cell
- Configurable Redstone Block
- Mega TNT (bugged: ignites multiple times with redstone)
- Curved Repeater (incomplete: item model, parts of block model)
- *Configurable TNT (incomplete)*
- Probable more in the future...

## Compiling

#### Linux and Mac OS

Clone the repo

```bash
git clone https://github.com/antonilol/redstone_components.git
```
or download the [zip](https://github.com/antonilol/redstone_components/archive/refs/heads/master.zip) and unzip it.

Enter the folder (`cd` or double click).

And finally, compile.

```bash
./gradlew build
```

If it says `bash: ./gradlew: Permission denied`, make `gradlew` executable.

```bash
chmod +x gradlew
```

#### Windows

Clone or download like mentioned above and build with

```bash
gradlew.bat build
```

## Developing

Clone the repo.

To get completions in your IDE (if applicable) run `./gradlew genSources` (unix) or `gradlew.bat genSources` (windows).

More on that [here](https://fabricmc.net/wiki/tutorial:setup).

## License

MIT
