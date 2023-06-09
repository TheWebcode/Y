package io.github.thewebcode.yplugin.entity;

import com.mysql.cj.util.StringUtils;
import io.github.thewebcode.yplugin.inventory.ArmorInventory;
import io.github.thewebcode.yplugin.yml.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;

@SerializeOptions(
        configMode = ConfigMode.DEFAULT
)
public class MobData extends YamlConfig {

    @Path("type")
    private EntityType entityType;

    @Path("health")
    private double health = 0;

    @Path("max-health")
    private double maxHealth = 0;

    @Path("baby")
    private boolean baby = false;

    @Path("villager")
    private boolean villager = false;

    @Comments({
            "Determines whether or not the entity is powered",
            "Only valid on creepers"
    })
    @Path("powered")
    private boolean powered = false;

    private Skeleton.SkeletonType skeletonType = Skeleton.SkeletonType.NORMAL;

    @Comments({
            "The entities custom name, which will be",
            "shown when the entity is in sight"
    })
    @Path("name")
    private String name = "";

    @Path("age")
    private int age = 0;

    @Comments({
            "Used when generating attributes",
            "of the creature at random."
    })
    @Path("age-min")
    private int ageMin = 0;

    @Comments({
            "Used when generating attributes",
            "of the creature at random."
    })
    @Path("age-max")
    private int ageMax = 0;

    @Comments({
            "Determines the size of the slime"
    })
    @Path("size")
    private int size = 0;

    @Comments({
            "Used when generating attributes",
            "of the creature at random."
    })
    @Path("size-min")
    private int sizeMin = 0;

    @Comments({
            "Used when generating attributes",
            "of the creature at random."
    })
    @Path("size-max")
    private int sizeMax = 0;

    @Path("armor")
    private ArmorInventory armorInventory = new ArmorInventory();

    public MobData(EntityType type, double health, double maxHealth, boolean baby, boolean villager, boolean powered, String skeletalType, String name, int age, int ageMin, int ageMax, int size, int sizeMin, int sizeMax, ArmorInventory armorInventory) {
        this.entityType = type;
        this.health = health;
        this.maxHealth = maxHealth;
        this.baby = baby;
        this.villager = villager;
        this.powered = powered;


        if (skeletalType != null) {
            this.skeletonType = Skeleton.SkeletonType.valueOf(skeletalType);
        } else {
            this.skeletonType = Skeleton.SkeletonType.NORMAL;
        }

        this.name = name;
        this.age = age;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.size = size;
        this.sizeMin = sizeMin;
        this.sizeMax = sizeMax;
        this.armorInventory = armorInventory;
    }

    public MobData() {

    }

    public CreatureBuilder toBuilder() {
        CreatureBuilder builder = CreatureBuilder.of(entityType);

        if (name != null && !StringUtils.isNullOrEmpty(name)) {
            builder.name(name);
        }

        if (health > 0) {
            builder.health(health);
        }

        if (maxHealth > 0) {
            builder.maxHealth(maxHealth);
        }

        if (age > 0) {
            builder.age(age);
        }

        if (ageMin > 0 && ageMax > ageMin) {
            builder.age(ageMin, ageMax);
        }

        if (size > 0) {
            builder.size(size);
        }

        if (sizeMin > 0 && sizeMax > sizeMin) {
            builder.size(sizeMin, sizeMax);
        }

        if (powered) {
            builder.powered();
        }

        builder.asBaby(baby);

        builder.asVillager(villager);

        builder.armor(armorInventory);

        return builder;
    }


    public EntityType getEntityType() {
        return entityType;
    }

    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public boolean isBaby() {
        return baby;
    }

    public boolean isVillager() {
        return villager;
    }

    public boolean isPowered() {
        return powered;
    }

    public Skeleton.SkeletonType getSkeletonType() {
        return skeletonType;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getAgeMin() {
        return ageMin;
    }

    public int getAgeMax() {
        return ageMax;
    }

    public int getSize() {
        return size;
    }

    public int getSizeMin() {
        return sizeMin;
    }

    public int getSizeMax() {
        return sizeMax;
    }

    public ArmorInventory getArmorInventory() {
        return armorInventory;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setBaby(boolean baby) {
        this.baby = baby;
    }

    public void setVillager(boolean villager) {
        this.villager = villager;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    public void setSkeletonType(Skeleton.SkeletonType skeletonType) {
        this.skeletonType = skeletonType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAgeMin(int ageMin) {
        this.ageMin = ageMin;
    }

    public void setAgeMax(int ageMax) {
        this.ageMax = ageMax;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSizeMin(int sizeMin) {
        this.sizeMin = sizeMin;
    }

    public void setSizeMax(int sizeMax) {
        this.sizeMax = sizeMax;
    }

    public void setArmorInventory(ArmorInventory armorInventory) {
        this.armorInventory = armorInventory;
    }
}
