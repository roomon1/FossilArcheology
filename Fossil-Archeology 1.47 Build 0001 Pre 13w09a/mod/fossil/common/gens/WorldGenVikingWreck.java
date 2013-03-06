package mod.fossil.common.gens;

import java.util.Random;

import mod.fossil.common.biomes.BiomeJungle1;
import mod.fossil.common.biomes.BiomeOcean1;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBeach;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenOcean;
import net.minecraft.world.chunk.IChunkProvider;

import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenVikingWreck implements IWorldGenerator
{
    @Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) 
	{
		switch (world.provider.dimensionId)
		  {
		   case -1: generateNether(world, random, chunkX*16, chunkZ*16);
		   case 0: generateSurface(world, random, chunkX*16, chunkZ*16);
		  }		
	}

    private void generateSurface(World world, Random random, int blockX, int blockZ) 
    {
    	BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(blockX, blockZ);
    	WorldGeneratorVikingWreck vikingwreck = new WorldGeneratorVikingWreck();
    	if(biome instanceof BiomeOcean1)
   	 	{
    	
    	for (int x = 0;x<2;x++)
        {
            int var6 = blockX + random.nextInt(512);
            int var7 = blockZ + random.nextInt(512);
            int var8 = world.getHeightValue(var6, var7);

            if (random.nextInt(100) == 0 && (world.getBlockMaterial(var6, var8 - 1, var7) == Material.water || world.getBlockId(var6, var8 - 1, var7) == 2 || world.getBlockId(var6, var8 - 1, var7) == Block.sand.blockID))
            {
            	vikingwreck.generate(world, random, var6, var8, var7);
            }
        }
   	 	}
    	
    }
    private void generateNether(World world, Random random, int blockX, int blockZ) 
	{
			  
	}
    
}