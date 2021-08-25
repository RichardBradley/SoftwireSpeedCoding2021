# Additional explainer

## Further notes on CP calculation

Pokémon have:

* a species, e.g. Pikachu, and sometimes a variation or form within the species e.g. there are both Alolan and Kantoan Exeggutors which have different stats but the same species name and number; at time of writing there are 700+ species in the game
* three stats, Attack, Defense and Stamina, which are the total of
  * base attack, defence and stamina values for the species / variation / form; different numbers per stat which range from 1 to about 500 at time of writing.
  * IVs: these are bonus stats for attack, defence and stamina that are unique to the Pokémon. They range from 0-15 and are fixed when the Pokémon first appears in game.
* a level, from 1-51 in 0.5 level steps. Pokémon can be levelled up by feeding them candy and stardust.

The CP can range from 10 to 5000+. To compute it for a given Pokémon you’ll need to

* look up its species in the first spreadsheet attached and find the base attack, defence and stamina values for that species and where relevant the specific form
* add the individual pokémon’s bonus attack, defence and stamina stats to these
* look up the CP multiplier for the pokémon’s level from the second spreadsheet attached
* then CP = (total attack * sqrt(total defence) * sqrt(total stamina) * multiplier^2) / 10, rounded down, and minimum value of 10 if the computed value is less


## Further notes on the data

The third attachment is a recent export from Poké Genie with all of my Pokémon (more or less) that it has data for, cut down to the columns we need, and with various columns renamed. The columns used are:

* Index is the sequence number from Poké Genie (to identify spreadsheet rows only; not used)
* Name and Form to identify the Pokémon species
* CP for the Pokémon’s current CP value
* Atk IV, Def IV and Sta IV for the three bonus stats
* Level Max for the Pokémon’s current level

There are ~6,900 rows here and multiple examples of most Pokémon species. We'll identify Pokémon from this spreadsheet by their
species name and their current CP value, although that's not unique in the data.
You can also use the CP column to check your calculation for the Pokémon's current level.

Other points that might be of interest:

* The Pokémon species data was [scraped from Bulbapedia](https://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_base_stats_(GO)) (CC BY-NC-SA 2.5) and fixed up to use the same form names as Poké Genie.
* The Poké Genie export has been cut down to only the columns we need, plus modified slightly too to keep things simple.
    * I have filtered out rows with incomplete data. This is why the Index number in the spreadsheet has gaps in the sequence. (I left the Index number in because the answer spreadsheet will use it to distinguish between Pokémon with the same species and CP value, although I'm not asking you to.)
    * I have replaced the male and female symbols with M and F (e.g. "Nidoran♀" is now "Nidoran F")
    * I have cleared the form name if it is "Normal", since this isn't consistently applied: where I had scanned a Gyarados for example
      before Mega Gyarados was added to the game it would have a blank form name, whereas afterwards it is now Gyarados Normal form.
      This matches the species data provided.
    * I have recalculated all the CPs. At some point in the past the CP calculation changed, and some rows from the raw
      export have the old calculation.

  You should be able to use your own Poké Genie exports without too much effort.
* Meltan is wrong in the data too, as when it was introduced its final Pokémon number wasn't known. My Meltan are down as #650 Chespin and #494 Victini.
* Poké Genie imports screenshots of Pokémon and doesn't know about the ones I've transferred away, and also doesn't always
  spot where a new Pokémon is a levelled up or evolved version of one it already has. So I don't actually have all of
  these anymore, and in a few rare cases (e.g. screenshots people have posted to WhatsApp groups) I never did in the
  first place.
* The stardust and candy calculation is actually bit more complicated too, but we're ignoring that here. Pokémon can be lucky, shadow or purified:
    * lucky Pokémon only need half as much stardust to level up, but the normal amount of candy
    * purified Pokémon need 10% less stardust and candy per level, rounded up
    * shadow Pokémon need 20% more stardust and candy per level, rounded up.
* And as well as just levelling up Pokémon there's also evolution: many of these Pokémon can be evolved into one or more other species, or temporarily mega-evolved, which will potentially let me reach more CP numbers with the Pokémon I've got. Ditto we're not considering that here.
