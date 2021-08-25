package q2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;
import lombok.experimental.Wither;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Q2Main {

    final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            new Q2Main().run();
        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private void run() throws Exception {
        List<Pokemon> pokemonList = objectMapper.readValue(
                Resources.getResource("q2/3_rupert_pokemon.json"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Pokemon.class));

        List<PokemonSpecies> speciesList = objectMapper.readValue(
                Resources.getResource("q2/1_pokemon_species_base_stats.json"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, PokemonSpecies.class));

        speciesByNameAndForm = speciesList.stream()
                .collect(Collectors.toMap(
                        s -> new NameAndForm(s.name, s.form),
                        s -> s));

        List<Level> levelsList = objectMapper.readValue(
                Resources.getResource("q2/2_pokemon_level_cp_multipliers.json"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Level.class));

        levelsByLevel = levelsList.stream()
                .collect(Collectors.toMap(
                        l -> l.getLevel(),
                        l -> l));

        Pokemon snor = pokemonList.get(262);
        assertThat((computeCp(snor, snor.level))).isEqualTo((int)snor.cp);

        outer:
        for (int targetCp = 3155; targetCp <= 3254; targetCp++) {
            for (int i = 0; i < pokemonList.size(); i++) {
                Pokemon p = pokemonList.get(i);
                while (true) {
                    double cp = computeCp(p, p.level);
                    if (cp == targetCp) {
                        System.out.printf("%s,%s,%s,%s\n", targetCp, p.name, (int)p.cp, p.level);
                        continue outer;
                    } else if(cp > targetCp) {
                        break;
                    } else { // (cp < targetCp)
                        p = p.withLevel(p.level + 0.5);
                        pokemonList.set(i, p);
                    }
                }
            }
            System.out.printf("%s,Impossible,,\n", targetCp);
        }
    }

    Map<NameAndForm, PokemonSpecies> speciesByNameAndForm;
    Map<Double, Level> levelsByLevel;

    @Value
    static class NameAndForm {
        String name;
        String form;
    }

    @Value
    static class Pokemon {
        double index;
        String name;
        String form;
        double cp;
        double attack_iv;
        double defense_iv;
        double stamina_iv;
        @Wither
        double level;
    }

    @Value
    static class Level {
        double level;
        double cp_multiplier;
        double stardust;
        double candy;
        double xl_candy;
    }

    public int computeCp(Pokemon p, double level) {
        if (level > 51.0) {
            return Integer.MAX_VALUE;
        }
        PokemonSpecies s = speciesByNameAndForm.get(new NameAndForm(p.name, p.form));

        double totalAttack = s.attack + p.attack_iv;
        double totalDefense = s.defense + p.defense_iv;
        double totalStamina = s.stamina + p.stamina_iv;
        double cpMultiplier = levelsByLevel.get(level).getCp_multiplier();
        return Math.max(10,
                (int)Math.floor((totalAttack * Math.sqrt(totalDefense) * Math.sqrt(totalStamina) * cpMultiplier * cpMultiplier)/ 10.0));
    }

    @Value
    static class PokemonSpecies {
        int number;
        String name;
        String form;
        double attack;
        double defense;
        double stamina;
    }
}
