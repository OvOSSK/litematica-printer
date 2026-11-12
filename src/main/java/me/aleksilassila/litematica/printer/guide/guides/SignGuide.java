package me.aleksilassila.litematica.printer.guide.guides;

import me.aleksilassila.litematica.printer.enums.BlockMatchResult;
import me.aleksilassila.litematica.printer.guide.Guide;
import me.aleksilassila.litematica.printer.guide.Result;
import me.aleksilassila.litematica.printer.config.Configs;
import me.aleksilassila.litematica.printer.printer.SchematicBlockContext;
import me.aleksilassila.litematica.printer.printer.action.Action;
import me.aleksilassila.litematica.printer.utils.InteractionUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

/**
 * 告示牌
 */
public class SignGuide extends Guide {

    public SignGuide(SchematicBlockContext context) {
        super(context);
    }

    @Override
    protected Result onBuildActionMissingBlock(BlockMatchResult state) {
        Direction facing = getProperty(requiredState, HorizontalDirectionalBlock.FACING).orElse(null);

        // 站立告示牌
        if (requiredBlock instanceof StandingSignBlock) {
            int rotation = getProperty(requiredState, StandingSignBlock.ROTATION).orElseThrow();
            return Result.success(new Action()
                    .setSides(Direction.DOWN)
                    .setLookRotation(rotation)
                    .setRequiresSupport());
        }

        // 墙壁告示牌
        if (requiredBlock instanceof WallSignBlock && facing != null) {
            return Result.success(new Action()
                    .setSides(facing.getOpposite())
                    .setLookDirection(facing.getOpposite())
                    .setRequiresSupport());
        }


        return Result.SKIP;
    }

    @Override
    protected Result onBuildActionWrongBlock(BlockMatchResult state) {
        if (Configs.Print.BREAK_WRONG_BLOCK.getBooleanValue() && InteractionUtils.canBreakBlock(blockPos)) {
            boolean isLegitimateSign = currentBlock instanceof StandingSignBlock
                    || currentBlock instanceof WallSignBlock
                    ;
            if (!isLegitimateSign) {
                InteractionUtils.getRuntime().add(context);
            }
        }
        return Result.SKIP;
    }
}
