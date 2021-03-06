package net.nile.magic.entities;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

//ThrownItemEntity
//BlockEntity
//ProjectileEntity

public class TornadoEntity extends OwnedSpellEntity {

    private Entity m_owner;

    private int m_maxAge;

    public int getMaxAge() {
        return m_maxAge;
    }

    public void setMaxAge(int value) {
        m_maxAge = Math.max(value, 0);
    }

    private int m_age;

    public int getAge() {
        return m_age;
    }

    public void setAge(int value) {
        m_age = Math.min(Math.max(value, 0), getMaxAge());
    }

    private float m_damage;

    public float getDamage() {
        return m_damage;
    }

    private static final String ageKey = "age";

    private static final String maxAgeKey = "max_age";

    @Environment(EnvType.CLIENT)
    private static final float ROTATION_SPEED = 10;

    @Environment(EnvType.CLIENT)
    private final float getRotationSpeed() {
        return ROTATION_SPEED + (float) getVelocity().lengthSquared();
    }

    public TornadoEntity(EntityType<?> type, World world) {
        super(type, world);

        m_maxAge = 100;
        m_damage = 10;
    }

    public TornadoEntity(World world, int maxAge, float damage, Entity owner) {
        super(Entities.TORNADO, world);

        m_owner = owner;

        m_maxAge = Math.max(maxAge, 0);
        m_damage = Math.max(0, damage);
    }

    @Override
    protected void initDataTracker() {
        // :/
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        setMaxAge(tag.getInt(maxAgeKey));
        setAge(tag.getInt(ageKey));
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        tag.putInt(maxAgeKey, m_maxAge);
        tag.putInt(ageKey, m_age);
    }

    public void setMovement(Vec3d velocity, float m) {
        setVelocity(velocity.multiply(m));
    }

    @Override
    public void tick() {

        if (this.world.isClient) {
            this.yaw += getRotationSpeed();
            this.yaw = yaw % 360;//lol idk it looks ok but maybe u should change it
        } else {

            List<Entity> entities = this.world.getOtherEntities(this, this.getBoundingBox().stretch(this.getVelocity()),this::isEntityEnemy);

            for (int i = 0; i < entities.size(); i++) {
                entities.get(i).damage(DamageSource.magic(this, m_owner), getDamage());
            }

            if (m_age >= m_maxAge) {
                remove();
            } else {
                m_age++;
            }
        }

        Vec3d movement = getVelocity();

        move(MovementType.SELF, movement);

        super.tick();

    }

    @SuppressWarnings("resource")
    @Override
    public void remove() {
        if (world.isClient) {
            for (int i = 0; i < 10; i++) {
                MinecraftClient.getInstance().particleManager.addParticle(ParticleTypes.CLOUD, getX(), getY() + i / 10f, getZ(),
                        0, 0, 0);
            }
        }
        super.remove();
    }

    @Override
    public Entity getOwner() {
        return m_owner;
    }
}
