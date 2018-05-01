/*
 * PoreRT - A Bukkit to Sponge Bridge
 *
 * Copyright (c) 2016, Maxqia <https://github.com/Maxqia> AGPLv3
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

package blue.lapis.pore.mixin.net.minecraft.network;

import blue.lapis.pore.Pore;
import blue.lapis.pore.impl.entity.PorePlayer;

import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At.Shift;

import java.io.IOException;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {

    private static final String CHECK_THREAD = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V";
    @Shadow private static final Logger LOGGER = LogManager.getLogger();
    @Shadow @Final private MinecraftServer serverController;
    @Shadow public EntityPlayerMP player;

    @Inject(method = "processEntityAction", cancellable = true, at = @At(value = "INVOKE", target = CHECK_THREAD, shift = Shift.AFTER))
    public void onEntityActionRecieved(CPacketEntityAction packet, CallbackInfo callback) { // TODO remove when sponge has a event that can replace this
        switch (packet.getAction()) {
            case START_SNEAKING :
            case STOP_SNEAKING :
                PlayerToggleSneakEvent toggleSneak = new PlayerToggleSneakEvent(PorePlayer.of((Player) player), packet.getAction().equals(Action.START_SNEAKING));
                Bukkit.getPluginManager().callEvent(toggleSneak);
                if (toggleSneak.isCancelled()) callback.cancel();
                break;
            case START_SPRINTING:
            case STOP_SPRINTING:
                PlayerToggleSprintEvent toggleSprint = new PlayerToggleSprintEvent(PorePlayer.of((Player) player), packet.getAction().equals(Action.START_SPRINTING));
                Bukkit.getPluginManager().callEvent(toggleSprint);
                if (toggleSprint.isCancelled()) callback.cancel();
                break;
            default:
                break;
        }
    }

    @Inject(method = "processPlayerAbilities", cancellable = true, at = @At(value = "INVOKE", target = CHECK_THREAD, shift = Shift.AFTER))
    public void onAbilitiesRecieved(CPacketPlayerAbilities packetIn, CallbackInfo callback) {
        if (this.player.capabilities.allowFlying && packetIn.isFlying() != this.player.capabilities.isFlying) {
            PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(PorePlayer.of((Player) player), packetIn.isFlying());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendPlayerAbilities();
                callback.cancel();
            }
        }
    }

    @Overwrite
    public void processCustomPayload(CPacketCustomPayload packetIn) {
        String s = packetIn.getChannelName();

        if ("MC|BEdit".equals(s))
        {
            PacketBuffer packetbuffer = packetIn.getBufferData();

            try
            {
                ItemStack itemstack = packetbuffer.readItemStack();

                if (itemstack.isEmpty())
                {
                    return;
                }

                if (!ItemWritableBook.isNBTValid(itemstack.getTagCompound()))
                {
                    throw new IOException("Invalid book tag!");
                }

                ItemStack itemstack1 = this.player.getHeldItemMainhand();

                if (itemstack1.isEmpty())
                {
                    return;
                }

                if (itemstack.getItem() == Items.WRITABLE_BOOK && itemstack.getItem() == itemstack1.getItem())
                {
                    itemstack1.setTagInfo("pages", itemstack.getTagCompound().getTagList("pages", 8));
                }
            }
            catch (Exception exception6)
            {
                LOGGER.error("Couldn't handle book info", (Throwable)exception6);
            }
        }
        else if ("MC|BSign".equals(s))
        {
            PacketBuffer packetbuffer1 = packetIn.getBufferData();

            try
            {
                ItemStack itemstack3 = packetbuffer1.readItemStack();

                if (itemstack3.isEmpty())
                {
                    return;
                }

                if (!ItemWrittenBook.validBookTagContents(itemstack3.getTagCompound()))
                {
                    throw new IOException("Invalid book tag!");
                }

                ItemStack itemstack4 = this.player.getHeldItemMainhand();

                if (itemstack4.isEmpty())
                {
                    return;
                }

                if (itemstack3.getItem() == Items.WRITABLE_BOOK && itemstack4.getItem() == Items.WRITABLE_BOOK)
                {
                    ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK);
                    itemstack2.setTagInfo("author", new NBTTagString(this.player.getName()));
                    itemstack2.setTagInfo("title", new NBTTagString(itemstack3.getTagCompound().getString("title")));
                    NBTTagList nbttaglist = itemstack3.getTagCompound().getTagList("pages", 8);

                    for (int i = 0; i < nbttaglist.tagCount(); ++i)
                    {
                        String s1 = nbttaglist.getStringTagAt(i);
                        ITextComponent itextcomponent = new TextComponentString(s1);
                        s1 = ITextComponent.Serializer.componentToJson(itextcomponent);
                        nbttaglist.set(i, new NBTTagString(s1));
                    }

                    itemstack2.setTagInfo("pages", nbttaglist);
                    this.player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemstack2);
                }
            }
            catch (Exception exception7)
            {
                LOGGER.error("Couldn't sign book", (Throwable)exception7);
            }
        }
        else if ("MC|TrSel".equals(s))
        {
            try
            {
                int k = packetIn.getBufferData().readInt();
                Container container = this.player.openContainer;

                if (container instanceof ContainerMerchant)
                {
                    ((ContainerMerchant)container).setCurrentRecipeIndex(k);
                }
            }
            catch (Exception exception5)
            {
                LOGGER.error("Couldn't select trade", (Throwable)exception5);
            }
        }
        else if ("MC|AdvCmd".equals(s))
        {
            if (!this.serverController.isCommandBlockEnabled())
            {
                this.player.sendMessage(new TextComponentTranslation("advMode.notEnabled", new Object[0]));
                return;
            }

            if (!this.player.canUseCommandBlock())
            {
                this.player.sendMessage(new TextComponentTranslation("advMode.notAllowed", new Object[0]));
                return;
            }

            PacketBuffer packetbuffer2 = packetIn.getBufferData();

            try
            {
                int l = packetbuffer2.readByte();
                CommandBlockBaseLogic commandblockbaselogic1 = null;

                if (l == 0)
                {
                    TileEntity tileentity = this.player.world.getTileEntity(new BlockPos(packetbuffer2.readInt(), packetbuffer2.readInt(), packetbuffer2.readInt()));

                    if (tileentity instanceof TileEntityCommandBlock)
                    {
                        commandblockbaselogic1 = ((TileEntityCommandBlock)tileentity).getCommandBlockLogic();
                    }
                }
                else if (l == 1)
                {
                    Entity entity = this.player.world.getEntityByID(packetbuffer2.readInt());

                    if (entity instanceof EntityMinecartCommandBlock)
                    {
                        commandblockbaselogic1 = ((EntityMinecartCommandBlock)entity).getCommandBlockLogic();
                    }
                }

                String s6 = packetbuffer2.readString(packetbuffer2.readableBytes());
                boolean flag2 = packetbuffer2.readBoolean();

                if (commandblockbaselogic1 != null)
                {
                    commandblockbaselogic1.setCommand(s6);
                    commandblockbaselogic1.setTrackOutput(flag2);

                    if (!flag2)
                    {
                        commandblockbaselogic1.setLastOutput((ITextComponent)null);
                    }

                    commandblockbaselogic1.updateCommand();
                    this.player.sendMessage(new TextComponentTranslation("advMode.setCommand.success", new Object[] {s6}));
                }
            }
            catch (Exception exception4)
            {
                LOGGER.error("Couldn't set command block", (Throwable)exception4);
            }
        }
        else if ("MC|AutoCmd".equals(s))
        {
            if (!this.serverController.isCommandBlockEnabled())
            {
                this.player.sendMessage(new TextComponentTranslation("advMode.notEnabled", new Object[0]));
                return;
            }

            if (!this.player.canUseCommandBlock())
            {
                this.player.sendMessage(new TextComponentTranslation("advMode.notAllowed", new Object[0]));
                return;
            }

            PacketBuffer packetbuffer3 = packetIn.getBufferData();

            try
            {
                CommandBlockBaseLogic commandblockbaselogic = null;
                TileEntityCommandBlock tileentitycommandblock = null;
                BlockPos blockpos1 = new BlockPos(packetbuffer3.readInt(), packetbuffer3.readInt(), packetbuffer3.readInt());
                TileEntity tileentity2 = this.player.world.getTileEntity(blockpos1);

                if (tileentity2 instanceof TileEntityCommandBlock)
                {
                    tileentitycommandblock = (TileEntityCommandBlock)tileentity2;
                    commandblockbaselogic = tileentitycommandblock.getCommandBlockLogic();
                }

                String s7 = packetbuffer3.readString(packetbuffer3.readableBytes());
                boolean flag3 = packetbuffer3.readBoolean();
                TileEntityCommandBlock.Mode tileentitycommandblock$mode = TileEntityCommandBlock.Mode.valueOf(packetbuffer3.readString(16));
                boolean flag = packetbuffer3.readBoolean();
                boolean flag1 = packetbuffer3.readBoolean();

                if (commandblockbaselogic != null)
                {
                    EnumFacing enumfacing = this.player.world.getBlockState(blockpos1).getValue(BlockCommandBlock.FACING);

                    switch (tileentitycommandblock$mode)
                    {
                        case SEQUENCE:
                            IBlockState iblockstate3 = Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
                            this.player.world.setBlockState(blockpos1, iblockstate3.withProperty(BlockCommandBlock.FACING, enumfacing).withProperty(BlockCommandBlock.CONDITIONAL, Boolean.valueOf(flag)), 2);
                            break;
                        case AUTO:
                            IBlockState iblockstate2 = Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
                            this.player.world.setBlockState(blockpos1, iblockstate2.withProperty(BlockCommandBlock.FACING, enumfacing).withProperty(BlockCommandBlock.CONDITIONAL, Boolean.valueOf(flag)), 2);
                            break;
                        case REDSTONE:
                            IBlockState iblockstate = Blocks.COMMAND_BLOCK.getDefaultState();
                            this.player.world.setBlockState(blockpos1, iblockstate.withProperty(BlockCommandBlock.FACING, enumfacing).withProperty(BlockCommandBlock.CONDITIONAL, Boolean.valueOf(flag)), 2);
                    }

                    tileentity2.validate();
                    this.player.world.setTileEntity(blockpos1, tileentity2);
                    commandblockbaselogic.setCommand(s7);
                    commandblockbaselogic.setTrackOutput(flag3);

                    if (!flag3)
                    {
                        commandblockbaselogic.setLastOutput((ITextComponent)null);
                    }

                    tileentitycommandblock.setAuto(flag1);
                    commandblockbaselogic.updateCommand();

                    if (!net.minecraft.util.StringUtils.isNullOrEmpty(s7))
                    {
                        this.player.sendMessage(new TextComponentTranslation("advMode.setCommand.success", new Object[] {s7}));
                    }
                }
            }
            catch (Exception exception3)
            {
                LOGGER.error("Couldn't set command block", (Throwable)exception3);
            }
        }
        else if ("MC|Beacon".equals(s))
        {
            if (this.player.openContainer instanceof ContainerBeacon)
            {
                try
                {
                    PacketBuffer packetbuffer4 = packetIn.getBufferData();
                    int i1 = packetbuffer4.readInt();
                    int k1 = packetbuffer4.readInt();
                    ContainerBeacon containerbeacon = (ContainerBeacon)this.player.openContainer;
                    Slot slot = containerbeacon.getSlot(0);

                    if (slot.getHasStack())
                    {
                        slot.decrStackSize(1);
                        IInventory iinventory = containerbeacon.getTileEntity();
                        iinventory.setField(1, i1);
                        iinventory.setField(2, k1);
                        iinventory.markDirty();
                    }
                }
                catch (Exception exception2)
                {
                    LOGGER.error("Couldn't set beacon", (Throwable)exception2);
                }
            }
        }
        else if ("MC|ItemName".equals(s))
        {
            if (this.player.openContainer instanceof ContainerRepair)
            {
                ContainerRepair containerrepair = (ContainerRepair)this.player.openContainer;

                if (packetIn.getBufferData() != null && packetIn.getBufferData().readableBytes() >= 1)
                {
                    String s5 = ChatAllowedCharacters.filterAllowedCharacters(packetIn.getBufferData().readString(32767));

                    if (s5.length() <= 35)
                    {
                        containerrepair.updateItemName(s5);
                    }
                }
                else
                {
                    containerrepair.updateItemName("");
                }
            }
        }
        else if ("MC|Struct".equals(s))
        {
            if (!this.player.canUseCommandBlock())
            {
                return;
            }

            PacketBuffer packetbuffer5 = packetIn.getBufferData();

            try
            {
                BlockPos blockpos = new BlockPos(packetbuffer5.readInt(), packetbuffer5.readInt(), packetbuffer5.readInt());
                IBlockState iblockstate1 = this.player.world.getBlockState(blockpos);
                TileEntity tileentity1 = this.player.world.getTileEntity(blockpos);

                if (tileentity1 instanceof TileEntityStructure)
                {
                    TileEntityStructure tileentitystructure = (TileEntityStructure)tileentity1;
                    int l1 = packetbuffer5.readByte();
                    String s8 = packetbuffer5.readString(32);
                    tileentitystructure.setMode(TileEntityStructure.Mode.valueOf(s8));
                    tileentitystructure.setName(packetbuffer5.readString(64));
                    int i2 = MathHelper.clamp(packetbuffer5.readInt(), -32, 32);
                    int j2 = MathHelper.clamp(packetbuffer5.readInt(), -32, 32);
                    int k2 = MathHelper.clamp(packetbuffer5.readInt(), -32, 32);
                    tileentitystructure.setPosition(new BlockPos(i2, j2, k2));
                    int l2 = MathHelper.clamp(packetbuffer5.readInt(), 0, 32);
                    int i3 = MathHelper.clamp(packetbuffer5.readInt(), 0, 32);
                    int j = MathHelper.clamp(packetbuffer5.readInt(), 0, 32);
                    tileentitystructure.setSize(new BlockPos(l2, i3, j));
                    String s2 = packetbuffer5.readString(32);
                    tileentitystructure.setMirror(Mirror.valueOf(s2));
                    String s3 = packetbuffer5.readString(32);
                    tileentitystructure.setRotation(Rotation.valueOf(s3));
                    tileentitystructure.setMetadata(packetbuffer5.readString(128));
                    tileentitystructure.setIgnoresEntities(packetbuffer5.readBoolean());
                    tileentitystructure.setShowAir(packetbuffer5.readBoolean());
                    tileentitystructure.setShowBoundingBox(packetbuffer5.readBoolean());
                    tileentitystructure.setIntegrity(MathHelper.clamp(packetbuffer5.readFloat(), 0.0F, 1.0F));
                    tileentitystructure.setSeed(packetbuffer5.readVarLong());
                    String s4 = tileentitystructure.getName();

                    if (l1 == 2)
                    {
                        if (tileentitystructure.save())
                        {
                            this.player.sendStatusMessage(new TextComponentTranslation("structure_block.save_success", new Object[] {s4}), false);
                        }
                        else
                        {
                            this.player.sendStatusMessage(new TextComponentTranslation("structure_block.save_failure", new Object[] {s4}), false);
                        }
                    }
                    else if (l1 == 3)
                    {
                        if (!tileentitystructure.isStructureLoadable())
                        {
                            this.player.sendStatusMessage(new TextComponentTranslation("structure_block.load_not_found", new Object[] {s4}), false);
                        }
                        else if (tileentitystructure.load())
                        {
                            this.player.sendStatusMessage(new TextComponentTranslation("structure_block.load_success", new Object[] {s4}), false);
                        }
                        else
                        {
                            this.player.sendStatusMessage(new TextComponentTranslation("structure_block.load_prepare", new Object[] {s4}), false);
                        }
                    }
                    else if (l1 == 4)
                    {
                        if (tileentitystructure.detectSize())
                        {
                            this.player.sendStatusMessage(new TextComponentTranslation("structure_block.size_success", new Object[] {s4}), false);
                        }
                        else
                        {
                            this.player.sendStatusMessage(new TextComponentTranslation("structure_block.size_failure", new Object[0]), false);
                        }
                    }

                    tileentitystructure.markDirty();
                    this.player.world.notifyBlockUpdate(blockpos, iblockstate1, iblockstate1, 3);
                }
            }
            catch (Exception exception1)
            {
                LOGGER.error("Couldn't set structure block", (Throwable)exception1);
            }
        }
        else if ("MC|PickItem".equals(s)) {
            PacketBuffer packetbuffer6 = packetIn.getBufferData();

            try
            {
                int j1 = packetbuffer6.readVarInt();
                this.player.inventory.pickItem(j1);
                this.player.connection.sendPacket(new SPacketSetSlot(-2, this.player.inventory.currentItem, this.player.inventory.getStackInSlot(this.player.inventory.currentItem)));
                this.player.connection.sendPacket(new SPacketSetSlot(-2, j1, this.player.inventory.getStackInSlot(j1)));
                this.player.connection.sendPacket(new SPacketHeldItemChange(this.player.inventory.currentItem));
            }
            catch (Exception exception)
            {
                LOGGER.error("Couldn't pick item", (Throwable)exception);
            }
        }
        else if ("REGISTER".equals(s)) {
            String channels = packetIn.getBufferData().toString(com.google.common.base.Charsets.UTF_8);
            for (String channel : channels.split("\0")) {
                PorePlayer.of((Player) player).addChannel(channel);
            }
        } else if ("UNREGISTER".equals(s)) {
            String channels = packetIn.getBufferData().toString(com.google.common.base.Charsets.UTF_8);
            for (String channel : channels.split("\0")) {
                PorePlayer.of((Player) player).removeChannel(channel);
            }
        } else {
            byte[] data = new byte[packetIn.getBufferData().readableBytes()];
            packetIn.getBufferData().readBytes(data);
            Pore.getServer().getMessenger().dispatchIncomingMessage(PorePlayer.of((Player) player), s, data);
        }
    }
}
