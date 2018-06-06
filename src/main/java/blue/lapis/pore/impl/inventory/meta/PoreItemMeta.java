/*
 * PoreRT - A Bukkit to Sponge Bridge
 *
 * Copyright (c) 2016, Maxqia <https://github.com/Maxqia> AGPLv3
 * Copyright (c) 2014-2016, Lapis <https://github.com/LapisBlue> MIT
 * Copyright (c) Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * An exception applies to this license, see the LICENSE file in the main directory for more information.
 */

package blue.lapis.pore.impl.inventory.meta;

import blue.lapis.pore.impl.enchantments.PoreEnchantment;
import blue.lapis.pore.util.PoreText;
import blue.lapis.pore.util.PoreWrapper;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.item.HideData;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.common.item.enchantment.SpongeEnchantment;

import java.util.*;

public class PoreItemMeta extends PoreWrapper<ItemStack> implements ItemMeta {

    private boolean unbreakable;

    public PoreItemMeta(ItemStack holder) {
        super(holder);
    }

    @Override
    public boolean hasDisplayName() {
        Optional<Text> displayName = getHandle().get(Keys.DISPLAY_NAME);
        if (displayName.isPresent()) {
            if (!displayName.get().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDisplayName() {
        Optional<Text> displayName = getHandle().get(Keys.DISPLAY_NAME);
        if (displayName.isPresent()) {
            return PoreText.convert(displayName.get());
        }
        return null;
    }

    @Override
    public void setDisplayName(String name) {
        getHandle().offer(Keys.DISPLAY_NAME, PoreText.convert(name));
    }

    @Override
    public boolean hasLocalizedName() {
        return false;
    }

    @Override
    public String getLocalizedName() {
        return null;
    }

    @Override
    public void setLocalizedName(String name) {

    }

    @Override
    public boolean hasLore() {
        Optional<List<Text>> lore = getHandle().get(Keys.ITEM_LORE);
        if (lore.isPresent()) {
            if (!lore.get().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getLore() {
        List<String> lores = new ArrayList<String>();
        NBTTagCompound nbt = ((net.minecraft.item.ItemStack)(Object)getHandle()).getTagCompound();
        NBTTagList lorenbt = nbt.getCompoundTag("display").getTagList("Lore", 8);
        int count = lorenbt.tagCount();
        for (int i = 0; i < count; i++) {
            lores.add(lorenbt.getStringTagAt(i));
        }
        if (lores.size() == 0){
            lores.add("");
        }
        return lores;
    }

    @Override
    public void setLore(List<String> lore) {
        NBTTagCompound nbt = ((net.minecraft.item.ItemStack)(Object)getHandle()).getTagCompound();
        NBTTagList lorenbt = nbt.getCompoundTag("display").getTagList("Lore", 8);
        while (lorenbt.tagCount() != 0){
            lorenbt.removeTag(lorenbt.tagCount() - 1);
        }
        for (String string : lore) {
            lorenbt.appendTag(new NBTTagString(string));
        }
        nbt.getCompoundTag("display").setTag("Lore", lorenbt);
        ((net.minecraft.item.ItemStack)(Object)getHandle()).setTagCompound(nbt);
    }

    @Override
    public boolean hasEnchants() {
        Optional<List<org.spongepowered.api.item.enchantment.Enchantment>> enchants = getHandle().get(Keys.ITEM_ENCHANTMENTS);
        return enchants.isPresent() && enchants.get().size() > 0 ;
    }

    @Override
    public boolean hasEnchant(Enchantment ench) {
        Optional<List<org.spongepowered.api.item.enchantment.Enchantment>> enchants = getHandle().get(Keys.ITEM_ENCHANTMENTS);
        EnchantmentType target = getEnchant(ench);
        if (enchants.isPresent()) {
            for (org.spongepowered.api.item.enchantment.Enchantment itmEnch : enchants.get()) {
                if (itmEnch.getType().equals(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getEnchantLevel(Enchantment ench) {
        Optional<List<org.spongepowered.api.item.enchantment.Enchantment>> enchants = getHandle().get(Keys.ITEM_ENCHANTMENTS);
        EnchantmentType target = getEnchant(ench);
        if (enchants.isPresent()) {
            for (org.spongepowered.api.item.enchantment.Enchantment itmEnch : enchants.get()) {
                if (itmEnch.getType().equals(target)) {
                    return itmEnch.getLevel();
                }
            }
        }
        return 0;
    }

    @Override
    public Map<Enchantment, Integer> getEnchants() {
        Optional<List<org.spongepowered.api.item.enchantment.Enchantment>> enchants = getHandle().get(Keys.ITEM_ENCHANTMENTS);
        Map<Enchantment, Integer> map = new HashMap<Enchantment, Integer>();
        if (enchants.isPresent()) {
            for (org.spongepowered.api.item.enchantment.Enchantment itmEnch : enchants.get()) {
                map.put(Enchantment.getByName(itmEnch.getType().getName()), itmEnch.getLevel());
            }
        }
        return ImmutableMap.copyOf(map);
    }

    @Override
    public boolean addEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction) {
        Optional<List<org.spongepowered.api.item.enchantment.Enchantment>> enchants = getHandle().get(Keys.ITEM_ENCHANTMENTS);
        if (enchants.isPresent()) {
            if (level > ench.getMaxLevel() && !ignoreLevelRestriction) {
                level = ench.getMaxLevel();
            }

            EnchantmentType copy = getEnchant(ench);
            enchants.get().add(new SpongeEnchantment(copy, level));
            getHandle().offer(Keys.ITEM_ENCHANTMENTS, enchants.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEnchant(Enchantment ench) {
        Optional<List<org.spongepowered.api.item.enchantment.Enchantment>> enchants = getHandle().get(Keys.ITEM_ENCHANTMENTS);
        EnchantmentType target = getEnchant(ench);
        if (enchants.isPresent()) {
            for (org.spongepowered.api.item.enchantment.Enchantment itmEnch : enchants.get()) {
                if (itmEnch.getType().equals(target)) {
                    enchants.get().remove(itmEnch);
                    getHandle().offer(Keys.ITEM_ENCHANTMENTS, enchants.get());;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasConflictingEnchant(Enchantment ench) {
        Optional<List<org.spongepowered.api.item.enchantment.Enchantment>> enchants = getHandle().get(Keys.ITEM_ENCHANTMENTS);
        EnchantmentType target = getEnchant(ench);
        if (enchants.isPresent()) {
            for (org.spongepowered.api.item.enchantment.Enchantment itmEnch : enchants.get()) {
                if (!itmEnch.getType().isCompatibleWith(target)) {
                    return true;
                }
            }
        }
        return false;
    }


    private static EnchantmentType getEnchant(Enchantment ench) {
        if (ench instanceof EnchantmentWrapper) {
            ench = ((EnchantmentWrapper) ench).getEnchantment();
        }
        return ((PoreEnchantment) ench).getHandle();
    }


    @Override
    public void addItemFlags(ItemFlag... itemFlags) {
        for (ItemFlag flag : itemFlags) {
            switch (flag) {
                case HIDE_ATTRIBUTES:
                    getHandle().offer(Keys.HIDE_ATTRIBUTES, true);
                    break;
                case HIDE_DESTROYS:
                    getHandle().offer(Keys.HIDE_CAN_DESTROY, true);
                    break;
                case HIDE_ENCHANTS:
                    getHandle().offer(Keys.HIDE_ENCHANTMENTS, true);
                    break;
                case HIDE_PLACED_ON:
                    getHandle().offer(Keys.HIDE_CAN_PLACE, true);
                    break;
                case HIDE_POTION_EFFECTS:
                    getHandle().offer(Keys.HIDE_MISCELLANEOUS, true);
                    break;
                case HIDE_UNBREAKABLE:
                    getHandle().offer(Keys.HIDE_UNBREAKABLE, true);
                    break;
                default:
                    throw new NotImplementedException("TODO"); //TODO
            }
        }
    }

    @Override
    public void removeItemFlags(ItemFlag... itemFlags) {
        for (ItemFlag flag : itemFlags) {
            switch (flag) {
                case HIDE_ATTRIBUTES:
                    getHandle().offer(Keys.HIDE_ATTRIBUTES, false);
                    break;
                case HIDE_DESTROYS:
                    getHandle().offer(Keys.HIDE_CAN_DESTROY, false);
                    break;
                case HIDE_ENCHANTS:
                    getHandle().offer(Keys.HIDE_ENCHANTMENTS, false);
                    break;
                case HIDE_PLACED_ON:
                    getHandle().offer(Keys.HIDE_CAN_PLACE, false);
                    break;
                case HIDE_POTION_EFFECTS:
                    getHandle().offer(Keys.HIDE_MISCELLANEOUS, false);
                    break;
                case HIDE_UNBREAKABLE:
                    getHandle().offer(Keys.HIDE_UNBREAKABLE, false);
                    break;
                default:
                    throw new NotImplementedException("TODO"); //TODO
            }
        }
    }

    @Override
    public Set<ItemFlag> getItemFlags() {
        HashSet<ItemFlag> set = new HashSet<ItemFlag>();
        if (getHandle().get(Keys.HIDE_ATTRIBUTES).orElse(false))
            set.add(ItemFlag.HIDE_ATTRIBUTES);
        if (getHandle().get(Keys.HIDE_CAN_DESTROY).orElse(false))
            set.add(ItemFlag.HIDE_DESTROYS);
        if (getHandle().get(Keys.HIDE_CAN_PLACE).orElse(false))
            set.add(ItemFlag.HIDE_PLACED_ON);
        if (getHandle().get(Keys.HIDE_ENCHANTMENTS).orElse(false))
            set.add(ItemFlag.HIDE_ENCHANTS);
        if (getHandle().get(Keys.HIDE_MISCELLANEOUS).orElse(false))
            set.add(ItemFlag.HIDE_POTION_EFFECTS);
        if (getHandle().get(Keys.HIDE_UNBREAKABLE).orElse(false))
            set.add(ItemFlag.HIDE_UNBREAKABLE);
        return set;
    }

    @Override
    public boolean hasItemFlag(ItemFlag flag) {
        switch (flag) {
            case HIDE_ATTRIBUTES:
                return getHandle().get(Keys.HIDE_ATTRIBUTES).orElse(false);
            case HIDE_DESTROYS:
                return getHandle().get(Keys.HIDE_CAN_DESTROY).orElse(false);
            case HIDE_ENCHANTS:
                return getHandle().get(Keys.HIDE_ENCHANTMENTS).orElse(false);
            case HIDE_PLACED_ON:
                return getHandle().get(Keys.HIDE_CAN_PLACE).orElse(false);
            case HIDE_POTION_EFFECTS:
                return getHandle().get(Keys.HIDE_MISCELLANEOUS).orElse(false);
            case HIDE_UNBREAKABLE:
                return getHandle().get(Keys.HIDE_UNBREAKABLE).orElse(false);
            default:
                throw new NotImplementedException("TODO"); //TODO
        }
    }

    @Override
    public ItemMeta clone() {
        return new PoreItemMeta(getHandle().copy());
    }

    @Override
    public Map<String, Object> serialize() {
        throw new NotImplementedException("TODO"); //TODO
    }

    @Override
    public boolean isUnbreakable() {
        return this.unbreakable;
    }

    @Override
    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public boolean isEmpty() {
        return !(hasDisplayName() || hasEnchants() || hasLore() || isUnbreakable());
    }

    public boolean equalsCommon(PoreItemMeta that) {
        return ((this.hasDisplayName() == that.hasDisplayName()))
                && (this.hasEnchants() == that.hasEnchants())
                && (this.hasLore() == that.hasLore())
                && (this.isUnbreakable() == that.isUnbreakable());
    }

    @Override
    public Spigot spigot() {
        throw new NotImplementedException("TODO"); //TODO
    }
}
