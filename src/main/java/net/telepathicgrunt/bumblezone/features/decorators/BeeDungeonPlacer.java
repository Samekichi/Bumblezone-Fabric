package net.telepathicgrunt.bumblezone.features.decorators;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.datafixers.Dynamic;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;


public class BeeDungeonPlacer extends Placement<NoPlacementConfig>
{
	public BeeDungeonPlacer(Function<Dynamic<?>, ? extends NoPlacementConfig> configFactory)
	{
		super(configFactory);
	}


	@Override
	public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random random, NoPlacementConfig placementConfig, BlockPos pos)
	{
		ArrayList<BlockPos> validPositions = new ArrayList<BlockPos>();
		BlockPos.Mutable mutable = new BlockPos.Mutable(pos);
		boolean validSpot;

		for (int currentAttempt = 0; currentAttempt <= 10; currentAttempt++)
		{
			validSpot = false;
			int x = random.nextInt(8) + pos.getX() + 4;
			int z = random.nextInt(8) + pos.getZ() + 4;
			int y = random.nextInt(chunkGenerator.getMaxHeight() - 10 - chunkGenerator.getSeaLevel()) + chunkGenerator.getSeaLevel() + 2;

			//find a cave air spot
			for (Direction face : Direction.Plane.HORIZONTAL)
			{
				mutable.setPos(x, y, z).move(face, 3);

				if (world.getBlockState(mutable).getBlock() == Blocks.CAVE_AIR)
					validSpot = true;
			}
			
			//make sure we aren't too close to regular air
			for(int xOffset = -6; xOffset <= 6; xOffset += 6)
			{
				for(int zOffset = -6; zOffset <= 6; zOffset += 6)
				{
					for(int yOffset = -3; yOffset <= 9; yOffset += 3)
					{
						mutable.setPos(x, y, z).move(xOffset, yOffset, zOffset);
		
						if (world.getBlockState(mutable).getBlock() == Blocks.AIR)
							validSpot = false;
					}
				}
			}
			
			
			mutable.setPos(x, y, z);
			if (validSpot && world.getBlockState(mutable).isSolid()) {
				validPositions.add(mutable);
				return validPositions.stream();
			}
		}

		return validPositions.stream();
	}
}
