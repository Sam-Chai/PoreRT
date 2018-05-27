/*
 * PoreRT - A Bukkit to Sponge Bridge
 *
 * Copyright (c) 2016, Maxqia <https://github.com/Maxqia> AGPLv3
 * Copyright (c) 2014-2016, Lapis <https://github.com/LapisBlue> MIT
 * Copyright (c) Spigot/Craftbukkit Project <https://hub.spigotmc.org/stash/projects/SPIGOT> LGPLv3
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

package blue.lapis.pore.impl.inventory;

import blue.lapis.pore.converter.type.material.MaterialConverter;
import blue.lapis.pore.impl.inventory.meta.PoreItemMeta;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.api.item.inventory.ItemStack;

public class PoreItemFactory implements ItemFactory {

    @Override
    public ItemMeta getItemMeta(Material material) {
        return new PoreItemMeta(ItemStack.of(MaterialConverter.asItem(material), 1));
    }

    @Override
    public boolean isApplicable(ItemMeta meta, org.bukkit.inventory.ItemStack stack) throws IllegalArgumentException {
        return isApplicable(meta, stack.getType());
    }

    @Override
    public boolean isApplicable(ItemMeta meta, Material material) throws IllegalArgumentException {
        /*if (meta instanceof PoreItemMeta) {
            PoreItemMeta internalMeta = (PoreItemMeta) meta;
            DataTransactionResult result = ItemStack.of(MaterialConverter.asItem(material), 1)
                    .copyFrom(internalMeta.getHandle().copy());
            return result.isSuccessful();
        }
        throw new IllegalArgumentException("ItemMeta not from this ItemFactory!");*/
        return true; //TODO
    }

    @Override
    public boolean equals(ItemMeta meta1, ItemMeta meta2) throws IllegalArgumentException {
        if (meta1 == meta2) {
            return true;
        }
        if (meta1 != null && !(meta1 instanceof PoreItemMeta)) {
            throw new IllegalArgumentException("First meta of " + meta1.getClass().getName() + " does not belong to " + PoreItemFactory.class.getName());
        }
        if (meta2 != null && !(meta2 instanceof PoreItemMeta)) {
            throw new IllegalArgumentException("Second meta " + meta2.getClass().getName() + " does not belong to " + PoreItemFactory.class.getName());
        }
        if (meta1 == null) {
            return ((PoreItemMeta) meta2).isEmpty();
        }
        if (meta2 == null) {
            return ((PoreItemMeta) meta1).isEmpty();
        }
        return equals((PoreItemMeta) meta1, (PoreItemMeta) meta2);
    }

    boolean equals(PoreItemMeta meta1, PoreItemMeta meta2) {
        /*
         * This couldn't be done inside of the objects themselves, else force recursion.
         * This is a fairly clean way of implementing it, by dividing the methods into purposes and letting each method perform its own function.
         *
         * The common and uncommon were split, as both could have variables not applicable to the other, like a skull and book.
         * Each object needs its chance to say "hey wait a minute, we're not equal," but without the redundancy of using the 1.equals(2) && 2.equals(1) checking the 'commons' twice.
         *
         * Doing it this way fills all conditions of the .equals() method.
         */
        return meta1.equalsCommon(meta2) /* && meta1.notUncommon(meta2) && meta2.notUncommon(meta1)*/;
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, org.bukkit.inventory.ItemStack stack) throws IllegalArgumentException {
        return asMetaFor(meta, stack.getType());
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, Material material) throws IllegalArgumentException {
        /*if (meta instanceof PoreItemMeta) {
            PoreItemMeta internalMeta = (PoreItemMeta) meta;
            ItemStack holder = ItemStack.of(MaterialConverter.asItem(material), 1);
            holder.copyFrom(internalMeta.getHandle().copy());
            return new PoreItemMeta(holder);
        }
        throw new IllegalArgumentException("ItemMeta not from this ItemFactory!");*/
        return meta; //TODO
    }

    @Override // Taken from CB
    public Color getDefaultLeatherColor() {
        return Color.fromRGB(0xA06540);
    }
}
