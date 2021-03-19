package net.nile.magic.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.nile.magic.blocks.entities.TomeTableBlockEntity;
import net.nile.magic.screenhandlers.SpellTomeScreenHandler;

public class TomeTableBlock extends Block implements NamedScreenHandlerFactory, BlockEntityProvider {

    public TomeTableBlock(Settings settings) {
        super(settings);
    }
    
   protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
           BlockEntity blockEntity = world.getBlockEntity(pos);
           if (blockEntity instanceof TomeTableBlockEntity) {
              ((TomeTableBlockEntity)(blockEntity)).setCustomName(itemStack.getName());
           }
        }
  
     }

     @Override
     public boolean hasSidedTransparency(BlockState state) {
         return true;
     }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {

        player.openHandledScreen(this);

                
        return super.onUse(state, world, pos, player, hand, hit);
    }


    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        
        return new SpellTomeScreenHandler(syncId, inv);
    }

    private final Text m_displayName = new LiteralText("Tome Table");

    @Override
    public Text getDisplayName() {
        return m_displayName;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new TomeTableBlockEntity();
    }
}
