# donnercraft

donnercraft is a open-source [Lightning](https://lightning.network/)-based payment plugin for [spigot](https://www.spigotmc.org/) minecraft servers.

## usage

* download the custom spigot.jar server file (it is needed for updated io.netty)
* run java -jar spigot.jar once
* copy tls.cert and admin.macaroon to the same folder as spigot.jar
* copy donnercraft plugin to /plugins/ folder

* create /plugins/Donnercraft/lnd.yml
* fill lnd.yml according to [sample-lnd.yml](./sample-lnd.yml)

