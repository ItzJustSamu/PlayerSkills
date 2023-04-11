# PlayerSkills-Reborn
Skills 1.8.8 supported (This is not the official github for PlayerSkills Reborn just a repo)

I'm maintaining only my server with this plugin do not ask for updates, however feel free to pull a request for fixes and ill apply them.

The original resource seems to be dead/inactive 

Link:
https://www.spigotmc.org/resources/▶-playerskills-reborn-◀-upgrade-skills-citizens-support-holograms-suport-with-source-code.59383/

my link:
https://www.spigotmc.org/resources/▶-playerskills-extended-◀-upgrade-skills-citizens-support-holograms-suport-orbs-sourcecode.109080/


For Devs:

getSkillLevel(Player player, Skill skill): Returns the level of the specified skill for the given player.
getSkillPoints(Player player): Returns the number of skill points the given player has.
getTotalPointsSpent(Player player): Returns the total number of points the player has spent on all skills.
setSkillPoints(Player player, int points): Sets the number of skill points the given player has.
setSkillLevel(Player player, Skill skill, int level): Sets the level of the specified skill for the given player.
getSkillLevel(OfflinePlayer player, Skill skill): Returns the level of the specified skill for the given offline player.
getSkillPoints(OfflinePlayer player): Returns the number of skill points the given offline player has.
getPointPrice(Player player): Returns the price of a skill point for the given player, taking into account the current configuration settings.
resetAll(Player player): Resets all of the player's skills and skill points to their default values.
buySkillPoint(Player player): Deducts the cost of a skill point from the player's level and adds a skill point to their total.
getTotalPointsSpent(OfflinePlayer player): Returns the total number of points the offline player has spent on all skills.
setSkillPoints(OfflinePlayer player, int points): Sets the number of skill points the given offline player has.
setSkillLevel(OfflinePlayer player, Skill skill, int level): Sets the level of the specified skill for the given offline player.
getMaximumLevel(Skill skill): Returns the maximum level for the specified skill, as specified in the configuration file.

    </repositories>
        <repository>
            <id>playerskills-reborn-repo</id>
            <url>https://raw.github.com/ItzJustSamu/PlayerSkills-Reborn/master/repo/</url>
        </repository>
    </repositories>
