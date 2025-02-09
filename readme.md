## Simple Coin-System plugin

### Player Commands:
- /coins - Shows the player's current balance

### Admin Commands:
- /coins \<player> - Shows the player's current balance
- /coins add \<amount> - Adds coins to your own balance
- /coins add \<amount> \<player> - Adds coins to the player's balance
- /coins remove \<amount> \<player> - Removes coins from your own balance
- /coins remove \<amount> \<player> - Removes coins from the player's balance


All commands with a \<player> argument can be executed by  the console too.

### Permissions:
- coinsystem.use - Allows the player to use the /coins command. Default for all players
- coinsystem.admin - Allows the player to use all the other commands. Default for OPs

### Configuration:
- database can be configured in the plugin.yml

### Dependencies:
- HikariCP (shaded); for db connection pooling