package cn.nukkit.level.biome.impl.jungle;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.level.generator.populator.impl.PopulatorBamboo;
import cn.nukkit.level.generator.populator.impl.PopulatorMelon;
import cn.nukkit.level.generator.populator.impl.tree.JungleBigTreePopulator;
import cn.nukkit.math.NukkitRandom;

/**
 * @author Alemiz112
 */
public class BambooJungleBiome extends JungleBiome {

    private static final SimplexF podzolNoise = new SimplexF(new NukkitRandom(), 2f, 1 / 4f, 1 / 32f);

    public BambooJungleBiome() {

        PopulatorBamboo bamboo = new PopulatorBamboo();
        bamboo.setBaseAmount(64);
        bamboo.setRandomAmount(64);
        this.addPopulator(bamboo);

        JungleBigTreePopulator bigTrees = new JungleBigTreePopulator();
        bigTrees.setBaseAmount(-1);
        bigTrees.setRandomAmount(2);
        this.addPopulator(bigTrees);

        PopulatorMelon melon = new PopulatorMelon();
        melon.setBaseAmount(-65);
        melon.setRandomAmount(70);
        this.addPopulator(melon);
    }

    @Override
    public String getName() {
        return "Bamboo Jungle";
    }

    @Override
    public int getSurfaceId(int x, int y, int z) {
        return podzolNoise.noise2D(x, z, true) < 0f ? PODZOL << Block.DATA_BITS : Block.GRASS << Block.DATA_BITS;
    }
}
