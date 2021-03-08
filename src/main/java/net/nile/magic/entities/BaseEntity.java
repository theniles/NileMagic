package net.nile.magic.entities;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.nile.magic.client.networking.handlers.EntitySpawnHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public abstract class BaseEntity extends Entity implements IAnimatable {

    public BaseEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {

        //this.move(MovementType.SELF, getVelocity());
            
        //if(!this.world.isClient)NileMagic.logger.info(getPos());

        super.tick();

        Vec3d movement = getVelocity();

        Vec3d newPos = getPos().add(movement);

        updatePosition(newPos.x, newPos.y, newPos.z);
        //this.setVelocity(this.getVelocity().multiply(0.9D));
        //NileMagic.logger.info("BBBB " + getVelocity() + "  " + getPos());
    }

    public PacketByteBuf writeClientSpawnData(PacketByteBuf buf) {

        buf.writeInt(Registry.ENTITY_TYPE.getRawId(getType()));

        buf.writeInt(getEntityId());
        buf.writeUuid(getUuid());

        buf.writeDouble(getX());
        buf.writeDouble(getY());
        buf.writeDouble(getZ());

        Vec3d vel = getVelocity();

        buf.writeDouble(vel.x);
        buf.writeDouble(vel.y);
        buf.writeDouble(vel.z);

        buf.writeInt((int) (pitch * 265f / 360));
        buf.writeInt((int) (yaw * 265f / 360));

        return buf;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return ServerPlayNetworking.createS2CPacket(EntitySpawnHandler.CHANNEL,
                writeClientSpawnData(new PacketByteBuf(Unpooled.buffer())));
    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    @Override
    public AnimationFactory getFactory() {
        return null;
    }
}
